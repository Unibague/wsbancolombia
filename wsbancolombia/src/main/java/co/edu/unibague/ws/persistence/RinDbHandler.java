package co.edu.unibague.ws.persistence;

import co.edu.unibague.ws.models.ConsultarFacturaRq;
import co.edu.unibague.ws.models.RegistrarPagoRq;
import co.edu.unibague.ws.models.db.Bill;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author Daniel Alejandro Bravo Torres
 *         Universidad de Ibague
 *         Copyright (C) 2016. All Rights Reserved
 */
public abstract class RinDbHandler {

    /**
     * Connection object of type {@link JdbcTemplate} to the current database
     */
    protected JdbcTemplate db;

    /**
     * Sets the current database jdbc
     *
     * @param db Object of type {@link JdbcTemplate}
     */
    protected RinDbHandler(JdbcTemplate db){
        this.db = db;
    }

    /**
     * Checks the status of the current database for the web service
     */
    public abstract void checkDatabaseStatus();

    /**
     * Search a bill or bills on the database, depending on the type of query specified in the request
     *
     * @param rq Object of {@link ConsultarFacturaRq}. Contains the query info send by the bank
     * @param type Type of query, either by bill id or bill owner's nit
     * @return List of {@link Bill}
     * @throws DuplicateKeyException Raised when searching for a bill that's already being payed
     */
    public abstract List<Bill> searchBills(ConsultarFacturaRq rq, String type) throws DuplicateKeyException;

    /**
     * Register a new payment in the database, given that the info provided is correct.
     *
     * @param rq Object of {@link RegistrarPagoRq} with the payment info
     * @return Map with the additional info required in the response
     */
    public abstract Map<String, Object> registerPayment(RegistrarPagoRq rq);
}
