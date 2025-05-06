package co.edu.unibague.ws.models;

import co.edu.unibague.ws.models.xml.PaySvcRq;
import co.edu.unibague.ws.models.xml.PmtData;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the structure of an XML request to the method in the web service
 * used to register a new payment.
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@XmlRootElement(name = "IFX")
public class RegistrarPagoRq extends IFXRequest {

    /**
     * Constant to represent the SOAP method called to start the payment process
     */
    public static final String SOAP_METHOD = "RegistrarPagoIFX";

    /**
     * Constant representing the RqUID of the request in the database
     */
    public static final String DB_RQUID = "P_ID_TRANSACCION";

    /**
     * Constant representing the id of the bank on which the payment was made
     */
    public static final String DB_BANK_ID = "P_ID_BANCO";

    /**
     * Constant representing the id of the bank office on which the payment was made
     */
    public static final String DB_OFFICE_ID = "P_ID_SUCURSAL";

    /**
     * Constant representing the date of the payment
     */
    public static final String DB_PAY_DT = "P_FECHA_PAGO";

    /**
     * Constant representing the amount payed
     */
    public static final String DB_PAY_AMT = "P_VALOR_PAGO";

    /**
     * Constant representing the method used to pay the bill
     */
    public static final String DB_PAY_TYPE = "P_FORMA_PAGO";

    /**
     * Constant representing the nit of the entity that payed the bill
     */
    public static final String DB_PAYER_NIT = "P_NIT_PAGADOR";

    /**
     * Constant representing the id of the bill payed
     */
    public static final String DB_BILL_ID = "P_ID_FACTURA";

    /**
     * Constant representing the id of the payment group
     */
    public static final String DB_PAY_GRP = "P_ID_GRUPO_PAGO";

    /**
     * Path to the xsd validation file
     */
    public static final String SCHEMA_PATH = "schema/RegistrarPagoRq.xsd";

    /**
     * Object that represents the info of the PaySvcRq field in the XML request.
     */
    @XmlElement(name = "PaySvcRq", required = true)
    private PaySvcRq paySvcRq;

    /**
     * Gets the value of the presSvcRs property.
     * @return possible object is {@link PaySvcRq }
     */
    public PaySvcRq getPaySvcRq() {
        return paySvcRq;
    }

    /**
     * Sets the value of the PresSvcRs property
     * @param paySvcRq allowed object is
     *     {@link PaySvcRq }
     */
    public void setPaySvcRq(PaySvcRq paySvcRq) {
        this.paySvcRq = paySvcRq;
    }

    /**
     * Returns the RqUID of the XML request
     *
     * @return RqUID string
     */
    public String getRqUID(){
        return paySvcRq.getRqUID();
    }

    /**
     * Returns the PmtInfo object of the current request
     * @return object of type {@link PmtData}
     */
    public PmtData getPmtInfo(){
        return paySvcRq.getPmtAddRq().getPmtInfo();
    }

    /**
     * Returns a hash containing the required parameters to execute the stored procedure that
     * registers a new payment in the database
     *
     * @return An {@link Map} object with the name and value of each of the parameters
     */
    public Map<String, Object> getInsertParameters(){
        Map<String, Object> params = new HashMap<>();

        params.put(DB_RQUID, paySvcRq.getRqUID());
        params.put(DB_BANK_ID, paySvcRq.getMsgRqHdr().getNetworkTrnInfo().getBankId());
        params.put(DB_OFFICE_ID, paySvcRq.getMsgRqHdr().getPointOfServiceData().getPOSLocation());

        PmtData data = paySvcRq.getPmtAddRq().getPmtInfo();

        params.put(DB_PAY_DT, data.getPrcDt().toGregorianCalendar().getTime());
        params.put(DB_PAY_AMT, data.getCurAmt().getAmt().doubleValue());
        params.put(DB_PAY_TYPE, data.getCurAmt().getCurCode());
        params.put(DB_PAYER_NIT, data.getRemitInfo().getCustPayeeId());
        params.put(DB_BILL_ID, data.getRemitInfo().getBillId().replaceFirst("^(0|-)+(?!$)", ""));
        params.put(DB_PAY_GRP, data.getRemitInfo().getPmtId());

        return params;
    }
}
