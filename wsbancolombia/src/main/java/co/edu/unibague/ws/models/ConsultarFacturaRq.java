package co.edu.unibague.ws.models;

import co.edu.unibague.ws.models.xml.PresSvcRq;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.stream.Collectors;

/**
 * Represents the structure of an XML request to any of the methods exposed
 * in the web service to search for bills.
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@XmlRootElement(name = "IFX")
public class ConsultarFacturaRq extends IFXRequest{

    /**
     * Constant to represent a search type by Bill ID. This constant must be the same
     * as the value of the conditional in the stored procedure
     */
    public final static String BY_BILLID_TYPE = "ConsultarFacturaPorNumero";

    /**
     * Constant to represent a search type by Client NIT. This constant must be the same
     * as the value of the conditional in the stored procedure
     */
    public final static String BY_CLIENT_NIT_TYPE = "ConsultaFacturasPorNit";

    /**
     * Constant to represent a search type by Client custom ID
     */
    public final static String BY_CLIENT_ID_TYPE = "ConsultaFacturasPorNegocio";

    /**
     * Object that represents the info of the PresSvcRq field in the
     * XML request.
     */
    @XmlElement(name = "PresSvcRq", required = true)
    private PresSvcRq presSvcRq;

    /**
     * Gets the value of the presSvcRq property.
     * @return possible object is {@link PresSvcRq }
     */
    public PresSvcRq getPresSvcRq() {
        return presSvcRq;
    }

    /**
     * Sets the value of the PresSvcRq property
     * @param presSvcRq allowed object is
     *     {@link PresSvcRq }
     */
    public void setPresSvcRq(PresSvcRq presSvcRq) {
        this.presSvcRq = presSvcRq;
    }

    /**
     * Gets the RqUID of the current object
     * @return rqUID of type {@link String}
     */
    public String getRqUID(){
        return presSvcRq.getRqUID();
    }

    /**
     * Takes all the bill's ids, removes the left 0 and -, collects them on a list
     * and joins them with a coma as separator
     *
     * @return Object of type {@link String}
     */
    public String getBillIdList(){
         return String.join(",", presSvcRq.getBillInqRq().getBillId().stream()
                 .map(s -> s.replaceFirst("^(0|-)+(?!$)", ""))
                 .collect(Collectors.toList()));
    }

    /**
     * Returns the NIT numeric value with no 0 to the left or floating points
     *
     * @return Object of type {@link String}
     */
    public String getFormatedNit(){
        return String.format("%1.0f",
                Double.valueOf(presSvcRq.getBillInqRq().getBillerId().getBillerNum()));
    }

    /**
     * Return the path of the xsd file used to validate the string query send by the client.
     *
     * @param type One of the 3 ways to query bills: BY_BILLID_TYPE, BY_CLIENT_NIT_TYPE or
     *             BY_CLIENT_ID_TYPE
     * @return path of xsd file in the classpath folder
     */
    public static String getSchemaPath(String type){
        switch (type){
            case BY_BILLID_TYPE:
                return "schema/ConsultarPorFacturaRq.xsd";
            case BY_CLIENT_ID_TYPE:
                return "schema/ConsultarPorNegocioRq.xsd";
            case  BY_CLIENT_NIT_TYPE:
                return "schema/ConsultarPorNitRq.xsd";
        }
        return "";
    }
}
