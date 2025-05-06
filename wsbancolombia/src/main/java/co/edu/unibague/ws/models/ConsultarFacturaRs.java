package co.edu.unibague.ws.models;

import co.edu.unibague.ws.controllers.RINSoapController;
import co.edu.unibague.ws.models.db.Bill;
import co.edu.unibague.ws.models.xml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents the structure of an XML response to any of the methods exposed
 * in the web service to search for bills.
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@XmlRootElement(name = "IFX")
public class ConsultarFacturaRs extends IFXResult {

    /**
     * {@link Logger} object of the custom ifx logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(RINSoapController.LOG_NAME);

    /**
     * Constant to represent the format of the client id in the response
     */
    protected final static int[] CLIENT_ID_FORMAT = {15};

    /**
     * Object that represents the info of the PresSvcRs field in the XML request
     */
    @XmlElement(name = "PresSvcRs", required = true)
    private PresSvcRs presSvcRs;

    /**
     * Default constructor
     */
    public ConsultarFacturaRs(){
        super();
        presSvcRs = new PresSvcRs();
        PresSvcRs.BillInqRs billInqRs = objectFactory.createPresSvcRsBillInqRs();
        presSvcRs.setBillInqRs(billInqRs);
    }

    /**
     * Sets the ClientDt and RqUID of the response given the ones passed in the request
     *
     * @param clientDt ClientDt if the request
     * @param rqUID RqUID of the request
     */
    public void setRequestData(XMLGregorianCalendar clientDt, String rqUID){
        signonRs.setClientDt(clientDt);
        presSvcRs.setRqUID(rqUID);
        presSvcRs.getBillInqRs().setRqUID(rqUID);
    }

    /**
     * Sets the current status of the object given the code passed.
     *
     * @param statusCode Status type, usually of any int constant found
     *                   in the {@link Statusdata} class
     */
    public void setResultStatus(int statusCode){
        presSvcRs.getBillInqRs().setStatus(objectFactory.buildStatusData(statusCode));
    }

    /**
     * Gets the value of the presSvcRs property.
     *
     * @return possible object is {@link PresSvcRs }
     */
    public PresSvcRs getPresSvcRs() {
        return presSvcRs;
    }

    /**
     * Sets the value of the PresSvcRs property
     *
     * @param presSvcRs allowed object is
     *     {@link PresSvcRs }
     */
    public void setPresSvcRs(PresSvcRs presSvcRs) {
        this.presSvcRs = presSvcRs;
    }

    /**
     * Adds the list of bills passed to the current object and sets the current response
     * status given the bills found and the methods used for the request.
     *
     * @param bills List of bills found in the database
     * @param queryType Methods used to search for the bills
     * @param db Informix Jdbc
     */
    public void setAssociatedBills(List<Bill> bills, String queryType, JdbcTemplate db){
        PresSvcRs.BillInqRs billInqRs = presSvcRs.getBillInqRs();
        for(Bill bill : bills){
            String billId = fillWithZeros(String.valueOf(bill.getId()), UID_FORMAT, '-');
            String billDt = bill.getDueDate().toString().split(" ")[0];
            String billingAcct = fillWithZeros(bill.getClient(), CLIENT_ID_FORMAT, '-');
            BigDecimal value = BigDecimal.valueOf(bill.getValue()).setScale(2, BigDecimal.ROUND_HALF_UP);

            if(validateBill(db, bill))
                billInqRs.addBillData(objectFactory.buildBilldata(billId, billDt, billingAcct, value));
        }

        if(billInqRs.getBillRec().isEmpty()){
            switch (queryType){
                case ConsultarFacturaRq.BY_BILLID_TYPE:
                    setResultStatus(Statusdata.NO_BILL_EXIST);
                    break;
                case ConsultarFacturaRq.BY_CLIENT_NIT_TYPE:
                    setResultStatus(Statusdata.NO_BILLS_NIT);
                    break;
                case ConsultarFacturaRq.BY_CLIENT_ID_TYPE:
                    setResultStatus(Statusdata.NO_BILLS_CLIENT_ID);
                    break;
            }
        }else
            setResultStatus(Statusdata.SUCCESS);
    }

    /**
     * Method to see if the language course of the bill to pay has any spots available
     *
     * @param jdbc Informix Jdbc
     * @param bill Bill data of type {@link Bill}
     * @return true if there are spots available, false otherwise
     */
    private boolean validateBill(JdbcTemplate jdbc, Bill bill){
        try {
            if (Pattern.matches("^IDI[0-9]+$", bill.getReference())) {
                String sql = "select grpo from admcui.prmtrcla where cnsctvo = ? and fcha_rsrva = TODAY";
                String group = jdbc.queryForObject(sql, String.class, bill.getReferenceNumber());
                return group != null && Pattern.matches("^\\s*[0-9]+\\s*$", group);
            }else
                return true;
        }catch (Exception e){
            LOG.error("Error validating bill", e);
            return false;
        }
    }
}
