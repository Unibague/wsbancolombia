package co.edu.unibague.ws.controllers;

import co.edu.unibague.ws.models.*;
import co.edu.unibague.ws.models.xml.Statusdata;
import co.edu.unibague.ws.persistence.OracleDbHandler;
import co.edu.unibague.ws.persistence.PostgresDbHandler;
import co.edu.unibague.ws.persistence.RinDbHandler;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;
import java.io.StringReader;
import java.util.UUID;

/**
 * Main class of the web service. Handles the JDBC connections to our databases,
 * unmarshall the string request to the corresponding JAX-B class object, and returns
 * the corresponding string with the result of the process.
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@Component
public class RINSoapController {

    /**
     * Logger name of the ifx logger, used to log to the Elastic server.
     */
    public static final String LOG_NAME = "ifxLog";

    /**
     * Custom field with an id to identify a request and its response in the logs
     */
    private static final String LOG_ID_FIELD = "transactId";

    /**
     * Custom field to identify if the log corresponds to a request or response
     */
    private static final String LOG_TYPE_FIELD = "soapMsg";

    /**
     * Custom field to identify the ws method that generated the log entry
     */
    private static final String LOG_METHOD_FIELD = "soapMethod";

    /**
     * {@link Logger} object for the custom ifx logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(LOG_NAME);

    /**
     * Loads the default Spring JDBC to access the Iceberg database, configured on the
     * application.properties resource.
     */
    @Autowired
    @Qualifier("jdbcIceberg")
    private JdbcTemplate jdbcIceberg;

    /**
     * Loads the default Spring JDBC to access the Informix database, configured on the
     * application.properties resource.
     */
    @Autowired
    @Qualifier("jdbcIngles")
    private JdbcTemplate jdbcInformix;

    /**
     * Loads the default Spring JDBC to access the Postgres local database, configured on the
     * application.properties resource.
     */
    @Autowired
    @Qualifier("jdbcPostgres")
    private JdbcTemplate jdbcPostgres;

    /**
     * Builds a default response to show that the server is up. Checks the status of the current
     * database and logs an error message in case of trouble. Logs the request and response to
     * the custom ifx logger.
     *
     * @param request Custom XML request of type {@link VerificarEstadoRq}
     * @return Custom XML with default result to the client request
     */
    public String checkStatus(String request){
        String result = "";
        VerificarEstadoRs rs = new VerificarEstadoRs();
        try {
            getCurrentDatabase().checkDatabaseStatus();
        } catch (Exception e) {
            LOG.error("Error checking database status", e);
        }

        try {
            VerificarEstadoRq verificarEstadoRq = unmarshallRequest(
                    VerificarEstadoRq.class, request, VerificarEstadoRq.SCHEMA_PATH);
            rs.setClientDt(verificarEstadoRq.getClientDt());
            result = rs.toString();
        }catch (Exception e){
            LOG.error("Error checking service status", e);
        }
        logIfx(request, result, VerificarEstadoRq.SOAP_METHOD);
        return result;
    }

    /**
     * Search the bill info, given the method called on the request, and returns the
     * data found or the respective error message. Logs the request and response to
     * the custom ifx logger.
     *
     * @param request String sent by the web service client. It must comply with
     *                the {@link ConsultarFacturaRq} structure.
     * @param type Type of the query, either by bill id or client nit, according to the different
     *             constants specified in {@link ConsultarFacturaRq}
     * @return XML representing the resulting {@link ConsultarFacturaRs} based on the request info
     */
    public String searchBillBy(String request, String type){
        ConsultarFacturaRs cfResult = new ConsultarFacturaRs();
        try {
            ConsultarFacturaRq cfRequest = unmarshallRequest(
                    ConsultarFacturaRq.class, request, ConsultarFacturaRq.getSchemaPath(type));
            cfResult.setRequestData(cfRequest.getClientDt(), cfRequest.getRqUID());

            cfResult.setAssociatedBills(
                    getCurrentDatabase().searchBills(cfRequest, type),
                    type, jdbcInformix);
        }catch (DuplicateKeyException e){
            cfResult.setResultStatus(Statusdata.BILL_ALREADY_PAYED);
        } catch (Exception e) {
            cfResult.setResultStatus(Statusdata.ERROR);
            LOG.error("Error searching bill", e);
        }
        String result = cfResult.toString();
        logIfx(request, result, type);
        return result;
    }

    /**
     * Returns a default ConsultarFacturaRs, given that the method used for the
     * search of the bills isn't implemented on the service. Logs the request and response to
     * the custom ifx logger.
     *
     * @param request String sent by the web service client. It must comply with
     *                the ConsultarFacturaRq structure.
     * @return method unimplemented string
     */
    public String noSearchImplemented(String request){
        ConsultarFacturaRs cfResult = new ConsultarFacturaRs();
        try {
            ConsultarFacturaRq cfRequest = unmarshallRequest(ConsultarFacturaRq.class, request,
                    ConsultarFacturaRq.getSchemaPath(ConsultarFacturaRq.BY_CLIENT_ID_TYPE));
            cfResult.setRequestData(cfRequest.getClientDt(), cfRequest.getPresSvcRq().getRqUID());
        }catch (Exception e){
            LOG.error("Error at unimplemented method for search", e);
        }finally {
            cfResult.setResultStatus(Statusdata.UNIMPLEMENTED);
        }
        String result = cfResult.toString();
        logIfx(request, result, ConsultarFacturaRq.BY_CLIENT_ID_TYPE);
        return result;
    }

    /**
     * Register a new payment in the database, given that the info provided is correct.
     * Logs the request and response to the custom ifx logger.
     *
     * @param request String sent by the web service client. It must complies with
     *                the RegistrarPagoRq structure.
     * @return XML representing the resulting RegistrarPagoRs based on the
     * request info
     */
    public String registerPayment(String request){
        RegistrarPagoRs rs = new RegistrarPagoRs();
        try {
            RegistrarPagoRq registrarPagoRq = unmarshallRequest(
                    RegistrarPagoRq.class, request, RegistrarPagoRq.SCHEMA_PATH);
            rs.setRequestData(registrarPagoRq);
            rs.setDatabaseResult(
                    getCurrentDatabase().registerPayment(registrarPagoRq),
                    jdbcInformix);
        } catch (Exception e){
            rs.setResultStatus(Statusdata.ERROR);
            LOG.error("Error making payment", e);
        }
        String result = rs.toString();
        logIfx(request, result, RegistrarPagoRq.SOAP_METHOD);
        return result;
    }

    /**
     * Returns the connection handler depending on the current status of the production database.
     * If the database is down, returns a handler to the local PG backup database. In case of error,
     * returns the production database by default.
     *
     * @return {@link OracleDbHandler} object
     */
    private RinDbHandler getCurrentDatabase(){
        try {
            PostgresDbHandler pg = new PostgresDbHandler(jdbcPostgres);
            if (pg.useBackupDb()) {
                return pg;
            }else
                return new OracleDbHandler(jdbcIceberg);
        }catch (Exception e){
            LOG.error("Error selecting default db", e);
            return new OracleDbHandler(jdbcIceberg);
        }
    }

    /**
     * Unmarshall the corresponding xml into a new custom JAX-B POJO,
     * given the model's class, and verifies that the given xml matches
     * the xsd description of the POJO.
     *
     * @param c Class of the JAX-B model.
     * @param data String containing the object data in xml format.
     * @param schemaPath Path of the xsd validation file
     * @param <T> Type of model required
     * @return new object of class T with its attributes values equal to the
     * ones in the string xml.
     * @throws JAXBException Default exception thrown when the given string
     * doesn't match the XML format of the given class.
     */
    private <T> T unmarshallRequest(Class<T> c, String data, String schemaPath)
            throws JAXBException, SAXException {
        SchemaFactory factory =
                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Unmarshaller unmarshaller = JAXBContext.newInstance(c).createUnmarshaller();
        unmarshaller.setSchema(factory.newSchema(
                RINSoapController.class.getClassLoader().getResource(schemaPath)));
        return (T) unmarshaller.unmarshal(new StringReader(data.trim()));
    }

    /**
     * Logs a request and its response to Elastic.
     *
     * @param rq String with the ifx request
     * @param rs String with the ifx response
     * @param method String with the name of the executed method
     */
    private void logIfx(String rq, String rs, String method){
        //Assigns a random UUID value to the custom id field
        MDC.put(LOG_ID_FIELD, UUID.randomUUID().toString());
        MDC.put(LOG_METHOD_FIELD, method);
        //Sets the type of log to a request message
        MDC.put(LOG_TYPE_FIELD, "request");
        LOG.info(rq.trim());
        //Sets the type of log to a response message
        MDC.put(LOG_TYPE_FIELD, "response");
        LOG.info(rs);
        //Cleans the custom parameters for the next log
        MDC.remove(LOG_TYPE_FIELD);
        MDC.remove(LOG_METHOD_FIELD);
        MDC.remove(LOG_ID_FIELD);
    }
}
