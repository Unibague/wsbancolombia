package co.edu.unibague.ws.persistence;

import co.edu.unibague.ws.models.ConsultarFacturaRq;
import co.edu.unibague.ws.models.RegistrarPagoRq;
import co.edu.unibague.ws.models.VerificarEstadoRq;
import co.edu.unibague.ws.models.db.Bill;
import co.edu.unibague.ws.models.db.BillMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Alejandro Bravo Torres
 *         Universidad de Ibague
 *         Copyright (C) 2016. All Rights Reserved
 */
public class OracleDbHandler extends RinDbHandler {

    /**
     * Constant to represent the name of the package that stores the custom functions
     * and procedures of the oracle database used to search and pay bills.
     */
    private final static String DB_FUNCTIONS_PACKAGE = "SIA_WS_QUERY_BILLS";

    /**
     * Constant to represent the name of the parameter passed to the function
     * SEARCH_BILL that sets the query type
     */
    private final static String FUNCTION_TYPE_PARAM = "QTYPE";

    /**
     * Constant to represent the name of the parameter passed to the function
     * SEARCH_BILL that sets the query value
     */
    private final static String FUNCTION_VALUE_PARAM = "QVALUE";

    /**
     * Constant to represent the name of the stored function used to
     * search the bills in the database
     */
    private final static String SEARCH_FUNCTION_NAME = "SEARCH_BILL";

    /**
     * Constant to represent the name of the stored procedure used to register
     * a new payment in the database
     */
    private static final String PAY_PROCEDURE_NAME = "PAY_CURRENT_BILL";

    /**
     * Sets the current database jdbc
     *
     * @param db Object of type {@link JdbcTemplate}
     */
    public OracleDbHandler(JdbcTemplate db) {
        super(db);
    }

    /**
     * Checks the status of the current database for the web service
     */
    @Override
    public void checkDatabaseStatus() {
        SimpleJdbcCall call = new SimpleJdbcCall(db)
                .withCatalogName(DB_FUNCTIONS_PACKAGE)
                .withFunctionName(VerificarEstadoRq.DB_FUNCTION);
        call.executeFunction(String.class, Collections.EMPTY_LIST);
    }

    /**
     * Search a bill or bills on the database, depending on the type of query specified in the request
     *
     * @param rq Object of {@link ConsultarFacturaRq}. Contains the query info send by the bank
     * @param type Type of query, either by bill id or bill owner's nit
     * @return List of {@link Bill}
     * @throws DuplicateKeyException Raised when searching for a bill that's already being payed
     */
    @Override
    public List<Bill> searchBills(ConsultarFacturaRq rq, String type) throws DuplicateKeyException {
        MapSqlParameterSource mps = new MapSqlParameterSource();
        mps.addValue(FUNCTION_TYPE_PARAM, type);

        if(ConsultarFacturaRq.BY_BILLID_TYPE.equals(type))
            mps.addValue(FUNCTION_VALUE_PARAM, rq.getBillIdList());
        else
            mps.addValue(FUNCTION_VALUE_PARAM, rq.getFormatedNit());

        //Object with the stored function package, name and result custom mapper, given
        //that the ResultSet returned is of a custom database type
        SimpleJdbcCall call = new SimpleJdbcCall(db)
                .withCatalogName(DB_FUNCTIONS_PACKAGE)
                .withFunctionName(SEARCH_FUNCTION_NAME)
                .returningResultSet("LIST", new BillMapper());

        //Calls the function with the specified parameters and maps the result to a list of bills
        return (List<Bill>) call.executeFunction(List.class, mps);
    }

    /**
     * Register a new payment in the database, given that the info provided is correct.
     *
     * @param rq Object of {@link RegistrarPagoRq} with the payment info
     * @return Map with the additional info required in the response
     */
    @Override
    public Map<String, Object> registerPayment(RegistrarPagoRq rq){
        //Object with the stored procedure package and name
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(db)
                .withCatalogName(DB_FUNCTIONS_PACKAGE)
                .withProcedureName(PAY_PROCEDURE_NAME);
        //Calls the procedure given the parameters returned by the RegistrarPagoRq object
        return jdbcCall.execute(rq.getInsertParameters());
    }
}
