package co.edu.unibague.ws.models;

import co.edu.unibague.ws.controllers.RINSoapController;
import co.edu.unibague.ws.models.xml.PaySvcRs;
import co.edu.unibague.ws.models.xml.PmtData;
import co.edu.unibague.ws.models.xml.Statusdata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the structure of an XML response to the method in the web service
 * used to register a new payment.
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@XmlRootElement(name = "IFX")
public class RegistrarPagoRs extends IFXResult {

    /**
     * {@link Logger} object of the custom ifx logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(RINSoapController.LOG_NAME);

    /**
     * Constant to represent the payment confirmation id stored in the database
     */
    public final static String DB_PAYMENT_ID = "P_SERIAL";

    /**
     * Constant to represent the due date of the bill payed
     */
    public final static String DB_DUE_DT = "P_FECHA_LIMITE_PAGO";

    /**
     * Constant to represent the payment status stored in the database
     */
    public final static String DB_STATUS_CODE = "P_ESTADO";

    /**
     * Constant to represent the payment registration date stored in the database
     */
    public final static String DB_EFF_DT = "P_FECHA_REGISTRO";

    /**
     * Constant to represent the bill id in the Informix database
     */
    public final static String DB_REFERENCE = "P_REFERENCIA";

    /**
     * String value of a successful payment
     */
    private final static String PAYMENT_OK = "Processed";

    /**
     * String value of a unsuccessful payment
     */
    private final static String PAYMENT_FAIL = "Failed";

    /**
     * Object that represents the info of the PaySvcRs field in the
     * XML request.
     */
    @XmlElement(name = "PaySvcRs", required = true)
    private PaySvcRs paySvcRs;

    /**
     * Default constructor.
     */
    public RegistrarPagoRs(){
        super();
        paySvcRs = new PaySvcRs();
        paySvcRs.setPmtAddRs(objectFactory.createPaySvcRsPmtAddRs());
    }

    /**
     * Sets the values of the response associated directly with the request message,
     * plus the SPName value.
     *
     * @param request Request object of type {@link RegistrarPagoRq}
     */
    public void setRequestData(RegistrarPagoRq request){
        signonRs.setClientDt(request.getClientDt());

        String rqUID = request.getRqUID();

        paySvcRs.setRqUID(rqUID);
        paySvcRs.setSPName(request.getPaySvcRq().getSPName());

        PaySvcRs.PmtAddRs pmtAddRs = paySvcRs.getPmtAddRs();
        pmtAddRs.setRqUID(rqUID);
        pmtAddRs.setAsyncRqUID(rqUID);

        PmtData rsData = new PmtData(request.getPmtInfo());
        rsData.setCurAmt(null);
        rsData.getRemitInfo().setPmtId(null);
        rsData.getRemitInfo().setCustPayeeId("");

        pmtAddRs.setPmtInfo(rsData);

        paySvcRs.setPmtAddRs(pmtAddRs);
    }

    /**
     * Sets the relevant info obtained once the payment has been successfully or
     * unsuccessfully stored in the Oracle database.
     *
     * @param result Map obtained by executing a stored procedure in the database
     * @param jdbc Jdbc connector to the Informix database
     */
    public void setDatabaseResult(Map<String, Object> result, JdbcTemplate jdbc){
        PaySvcRs.PmtAddRs pmtAddRs = paySvcRs.getPmtAddRs();

        PaySvcRs.PmtAddRs.PmtRec pmtRec = objectFactory.createPaySvcRsPmtAddRsPmtRec();

        PmtData data = new PmtData(pmtAddRs.getPmtInfo());
        data.setPrcDt(null);
        data.setDueDt(objectFactory.getXMLDateFromSQLTimestamp(result.get(DB_DUE_DT)));
        pmtRec.setPmtInfo(data);

        PaySvcRs.PmtAddRs.PmtRec.PmtStatus recStatus = objectFactory.createPaySvcRsPmtAddRsPmtRecPmtStatus();
        recStatus.setEffDt(objectFactory.getXMLDateFromSQLTimestamp(result.get(DB_EFF_DT)));

        pmtRec.setPmtStatus(recStatus);
        pmtAddRs.setPmtRec(pmtRec);

        int resultCode = ((BigDecimal) result.get(DB_STATUS_CODE)).intValue();

        if(resultCode == Statusdata.SUCCESS){
            BigDecimal payID = (BigDecimal) result.get(DB_PAYMENT_ID);
            pmtRec.setPmtId(payID.toString());
            String ref = (String) result.get(DB_REFERENCE);
            validateBill(jdbc, ref);
        }else {
            setPaymentStatus(PAYMENT_FAIL, resultCode);
            pmtRec.setPmtId("");
            LOG.error("Error making the payments validations. Code -00" + resultCode);
        }
    }

    /**
     * Sets the current status of the object given the code passed.
     *
     * @param statusCode Status type, usually of any int constant found
     *                   in the {@link Statusdata} class
     */
    public void setResultStatus(int statusCode){
        paySvcRs.getPmtAddRs().setStatus(objectFactory.buildStatusData(statusCode));
    }

    /**
     * Sets the status of the XML result according to the success of the payment process.
     *
     * @param statusText Value of the PmtStatus.PmtStatusCode attribute
     * @param statusCode Status code of the response, according to the constants in {@link Statusdata}
     */
    private void setPaymentStatus(String statusText, int statusCode){
        paySvcRs.getPmtAddRs().getPmtRec().getPmtStatus().setPmtStatusCode(statusText);
        setResultStatus(statusCode);
    }

    /**
     * Gets the value of the PaymentId in the Iceberg database.
     *
     * @return Bill id of type {@link String}
     */
    public String getDbPaymentId(){
        return paySvcRs.getPmtAddRs().getPmtRec().getPmtId();
    }

    /**
     * Gets the value of the presSvcRs property.
     * @return possible object is {@link PaySvcRs }
     */
    public PaySvcRs getPaySvcRs() {
        return paySvcRs;
    }

    /**
     * Sets the value of the PresSvcRs property.
     * @param paySvcRs allowed object is
     *     {@link PaySvcRs }
     */
    public void setPaySvcRs(PaySvcRs paySvcRs) {
        this.paySvcRs = paySvcRs;
    }

    /**
     * Checks if the payed bill was for a language course, and if it was, adds a new entry to Informix
     * in order to reserve the spot in the course. If there is an error in this method, it will log it
     * to the custom logger, but it won't change the payment status in Iceberg or the response, so the person
     * in charge of the monitoring will be responsible to solve the issue
     *
     * @param jdbc Informix Jdbc
     * @param ifxReference Bill id on Informix
     */
    private void validateBill(JdbcTemplate jdbc, String ifxReference){
        try {
            Pattern pt = Pattern.compile("^IDI0*([1-9]\\d*)$");
            Matcher mt = pt.matcher(ifxReference);
            if (mt.find()) {
                String sql = "insert into admcui.ntas_ingles (ano_per, cdla, codasig, grpo, fnal, " +
                        "fcha, usrio, brrdo, fcha_curso) select first 1 prm.ano_per, prm.cdla, prm.codasig, " +
                        "prm.grpo, 0, TODAY, 'ICEBERG-WS', 'N', grp.f_grpo from admcui.prmtrcla prm " +
                        "inner join grpos grp on prm.codasig = grp.codasig and prm.ano_per = grp.ano_per " +
                        "and prm.grpo = grp.grpo where prm.cnsctvo = ?";

                jdbc.update(sql, mt.group(1));
            }
        }catch (Exception e){
            LOG.error("Error registering language course on informix", e);
        }
        setPaymentStatus(PAYMENT_OK, Statusdata.SUCCESS);
    }
}
