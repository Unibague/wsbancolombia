package co.edu.unibague.ws.models.db;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Custom RowMapper. Transform a custom object from the database to a {@link Bill} object
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
public class BillMapper implements RowMapper {

    /**
     * Constant to represent the column name of the BillingAcct field in the database
     */
    private final static String CLIENT = "CLIENTE";

    /**
     * Constant to represent the column name of the bill amount data in the database
     */
    private final static String VALUE = "VALOR";

    /**
     * Constant to represent the column name of the BillDt field in the database
     */
    private final static String DUE_DATE = "FECHA_LIMITE_PAGO";

    /**
     * Constant to represent the column name of the BillId field in the database
     */
    private final static String ID = "REFERENCIA";

    /**
     * Constant to represent the column name of the bill's type
     */
    private final static String TYPE = "TIPO_RECIBO";

    /**
     * Constant to represent the reference in SIA.
     */
    private final static String REFERENCE = "REFERENCIA_ACADEMICO";

    /**
     * Creates a bill object based on the result obtained from calling the SEARCH_BILL
     * function of the database
     *
     * @param resultSet Raw database result
     * @param i Row number
     * @return new object of type {@link Bill}
     * @throws SQLException Error getting data from the {@link ResultSet} object
     */
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        Bill bill = new Bill();
        bill.setClient(resultSet.getString(CLIENT));
        bill.setValue(resultSet.getDouble(VALUE));
        bill.setDueDate(resultSet.getTimestamp(DUE_DATE));
        bill.setId(resultSet.getInt(ID));
        bill.setType(resultSet.getString(TYPE));
        bill.setReference(resultSet.getString(REFERENCE));
        return bill;
    }
}
