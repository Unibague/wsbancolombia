package co.edu.unibague.ws.persistence;

import co.edu.unibague.ws.models.ConsultarFacturaRq;
import co.edu.unibague.ws.models.RegistrarPagoRq;
import co.edu.unibague.ws.models.RegistrarPagoRs;
import co.edu.unibague.ws.models.db.Bill;
import co.edu.unibague.ws.models.db.BillMapper;
import co.edu.unibague.ws.models.xml.Statusdata;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static co.edu.unibague.ws.models.RegistrarPagoRq.*;

/**
 * @author Daniel Alejandro Bravo Torres
 *         Universidad de Ibague
 *         Copyright (C) 2016. All Rights Reserved
 */
public class PostgresDbHandler extends RinDbHandler {

    /**
     * Constant to represent the name of the bills table in the database
     */
    private static final String DB_BILLS_TABLE = "SIA_WS_RECIBO_TEMP";

    /**
     * Constant to represent the bill id field in the bills table
     */
    private static final String BILLS_BILL_ID_FIELD = "REFERENCIA";

    /**
     * Constant to represent the client id in the bills table
     */
    private static final String BILLS_CLIENT_ID_FIELD = "CLIENTE";

    /**
     * Constant to represent the due date of the bill in the bills table
     */
    private static final String BILLS_DATE_FIELD = "FECHA_LIMITE_PAGO";

    /**
     * Constant to represent the payments table in the database
     */
    private static final String DB_PAYMENTS_TABLE = "SIA_WS_PAGOS_RIN";

    /**
     * Constant to represent the bill id in the payments table
     */
    private static final String PAYS_BILL_ID_FIELD = "ID_FACTURA";

    /**
     * Constant to represent the status of the payment in the payments table
     */
    private static final String PAYS_STATUS_FIELD = "CODIGO_ESTADO";

    /**
     * Constant to represent the id of the payment in the payments table
     */
    private static final String PAYS_ID_FIELD = "SECUENCIA";

    /**
     * Constant to represent the rquid of the payment in the payments table
     */
    private static final String PAYS_RQUID_FIELD = "ID_TRANSACCION";

    /**
     * Constant to represent the register date in the payments table
     */
    private static final String PAYS_EFFDT_FIELD = "FECHA_REGISTRO";

    /**
     * Constant to represent the oracle status table in the database
     */
    private static final String DB_STATUS_TABLE = "SIA_WS_ORCL_DATA";

    /**
     * Sets the current database jdbc
     *
     * @param db Object of type {@link JdbcTemplate}
     */
    public PostgresDbHandler(JdbcTemplate db) {
        super(db);
    }

    /**
     * Checks the status of the current database for the web service
     */
    @Override
    public void checkDatabaseStatus() {
        db.queryForList("SELECT 1 FROM " + DB_BILLS_TABLE + " LIMIT 1");
        db.queryForList("SELECT 1 FROM " + DB_PAYMENTS_TABLE + " LIMIT 1");
        db.queryForList("SELECT 1 FROM " + DB_STATUS_TABLE + " LIMIT 1");
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
        //Queries only bills that are active and has no payment registered in the db
        String sql = "SELECT bills.* FROM " + DB_BILLS_TABLE + " bills LEFT JOIN "
                + DB_PAYMENTS_TABLE + " pays ON TO_CHAR(bills." + BILLS_BILL_ID_FIELD + ", " +
                "'99999999') = pays." + PAYS_BILL_ID_FIELD + " AND pays." + PAYS_STATUS_FIELD +
                " = 0 WHERE pays." + PAYS_BILL_ID_FIELD + " IS NULL AND ";
        switch (type){
            case ConsultarFacturaRq.BY_BILLID_TYPE:
                String billIds = rq.getBillIdList();

                if(billIds.contains(",")) {
                    sql += "bills." + BILLS_BILL_ID_FIELD + " IN (" + billIds +
                            ") ORDER BY bills." + BILLS_BILL_ID_FIELD + ", bills." + BILLS_DATE_FIELD;
                }else{
                    //When is only 1 bill that's searched, the response must send an error
                    //message if this bill has already been payed. This is handled
                    //in the controller automatically when a DupKeyException is raised
                    List payed = db.queryForList("SELECT 1 FROM " + DB_PAYMENTS_TABLE +
                            " WHERE " + PAYS_BILL_ID_FIELD + " = '" + billIds + "' AND " +
                            PAYS_STATUS_FIELD + " = 0");
                    if(!payed.isEmpty())
                        throw new DuplicateKeyException("Bill already payed");
                    else
                        sql = "SELECT * FROM " + DB_BILLS_TABLE + " WHERE " + BILLS_BILL_ID_FIELD +
                                " = " + billIds + " ORDER BY " + BILLS_DATE_FIELD;
                }
                break;
            case ConsultarFacturaRq.BY_CLIENT_NIT_TYPE:
                sql += "bills." + BILLS_CLIENT_ID_FIELD + " = " + rq.getFormatedNit() + " ORDER BY " +
                        BILLS_BILL_ID_FIELD + ", " + BILLS_DATE_FIELD;
        }
        return db.query(sql, new BillMapper());
    }

    /**
     * Register a new payment in the database, given that the info provided is correct.
     *
     * @param rq Object of {@link RegistrarPagoRq} with the payment info
     * @return Map with the additional info required in the response
     */
    @Override
    public Map<String, Object> registerPayment(RegistrarPagoRq rq) {
        Map<String, Object> params = rq.getInsertParameters();
        Map<String, Object> result = new HashMap<>();

        Timestamp effDt = new Timestamp(System.currentTimeMillis());

        Bill bill = new Bill();
        bill.setReference("ERROR");
        bill.setDueDate(effDt);
        bill.setId(Integer.valueOf((String) params.get(DB_BILL_ID)));

        int statusCode = getStatusCode(params, bill);
        params.put("P_" + PAYS_STATUS_FIELD, statusCode);

        BigDecimal payId = BigDecimal.valueOf(-1);

        SqlRowSet rowSet = insertPayment(params);
        if(rowSet.next()){
            payId = rowSet.getBigDecimal(PAYS_ID_FIELD);
            effDt = rowSet.getTimestamp(PAYS_EFFDT_FIELD);
        }else
            statusCode = Statusdata.ERROR;

        result.put(RegistrarPagoRs.DB_STATUS_CODE, BigDecimal.valueOf(statusCode));
        result.put(RegistrarPagoRs.DB_DUE_DT, bill.getDueDate());
        result.put(RegistrarPagoRs.DB_REFERENCE, bill.getReference());
        result.put(RegistrarPagoRs.DB_PAYMENT_ID, payId);
        result.put(RegistrarPagoRs.DB_EFF_DT, effDt);

        return result;
    }

    /**
     * Checks the status of the oracle database saved in the local informix database. This status
     * is updated by a script that runs every minute.
     *
     * @return true if the oracle database is down, false otherwise
     */
    public boolean useBackupDb() {
        SqlRowSet srs = db.queryForRowSet("select active from " + DB_STATUS_TABLE
                + " where secuencia = 1");
        return srs.next() && !srs.getBoolean(1);
    }

    /**
     * Inserts a new payment register in the database, and returns the info generated in the
     * db automatically by this process (id number and register date)
     *
     * @param params Map of column names and values. The column names have an added 'P_' at
     *               the beginning, since it's necessary for their use in the stored procedure
     *               of the oracle database
     * @return {@link SqlRowSet} with the id number and register date of the payment in the db
     */
    private SqlRowSet insertPayment(Map<String, Object> params){
        String[] keys = {DB_RQUID, DB_BANK_ID, DB_OFFICE_ID, DB_PAY_DT, DB_PAY_AMT,
                DB_PAY_TYPE, DB_PAYER_NIT, DB_BILL_ID, DB_PAY_GRP, "P_" + PAYS_STATUS_FIELD};
        int[] types = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.NUMERIC,
                Types.CHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.NUMERIC};
        Object[] values = new Object[keys.length];

        StringBuilder sql1 = new StringBuilder("INSERT INTO " + DB_PAYMENTS_TABLE + "(");
        StringBuilder sql2 = new StringBuilder(" VALUES (");
        StringBuilder select = new StringBuilder("SELECT " + PAYS_ID_FIELD + ", " +
                PAYS_EFFDT_FIELD + " FROM " + DB_PAYMENTS_TABLE + " WHERE ");

        for (int i = 0; i < keys.length; i++){
            String key = keys[i].substring(2);
            values[i] = params.get(keys[i]);
            sql1.append(key); sql1.append(",");
            sql2.append("?,");
            select.append(key); select.append(" = ? AND ");
        }
        sql1.replace(sql1.length() - 1, sql1.length(), ")");
        sql2.replace(sql2.length() - 1, sql2.length(), ")");

        db.update(sql1.toString() + sql2.toString(), values, types);

        return db.queryForRowSet(select.toString().substring(0, select.length() - 4),
                values, types);
    }

    /**
     * Runs the corresponding validations on the bill to be payed
     *
     * @param params Map with the name of the columns and their values
     * @param bill Default bill object. It gets replaced by the info in the database
     *             if the validations are successful
     * @return {@link Integer} value according to the {@link Statusdata} constants
     */
    private int getStatusCode(Map<String, Object> params, Bill bill){
        int statusCode = Statusdata.PAYMENT_FAILED;
        if(db.queryForRowSet("SELECT " + PAYS_ID_FIELD + " FROM " +
                DB_PAYMENTS_TABLE + " WHERE " + PAYS_BILL_ID_FIELD + " = '" + bill.getId() +
                "' AND " + PAYS_STATUS_FIELD + " = 0").next())
            statusCode = Statusdata.BILL_ALREADY_PAYED;
        else{
            if(db.queryForRowSet("SELECT " + PAYS_ID_FIELD + " FROM " + DB_PAYMENTS_TABLE +
                    " WHERE " + PAYS_RQUID_FIELD + " = '" + params.get(RegistrarPagoRq.DB_RQUID)
                    + "' AND " + PAYS_STATUS_FIELD + " = 0").next())
                statusCode = Statusdata.DUPLICATED_PAY_ID;
            else{
                List<Bill> bills = db.query("SELECT * FROM " + DB_BILLS_TABLE + " WHERE " +
                        BILLS_BILL_ID_FIELD + " = " + bill.getId(), new BillMapper());
                if(!bills.isEmpty()){
                    bill = bills.get(0);
                    if(bill.getValue() == (Double) params.get(DB_PAY_AMT))
                        statusCode = Statusdata.SUCCESS;
                }else
                    statusCode = Statusdata.NO_BILL_EXIST;

            }
        }
        return statusCode;
    }
}
