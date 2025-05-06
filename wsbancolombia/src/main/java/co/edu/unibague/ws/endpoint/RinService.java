package co.edu.unibague.ws.endpoint;

import co.edu.unibague.ws.controllers.RINSoapController;
import co.edu.unibague.ws.models.ConsultarFacturaRq;
import org.springframework.beans.factory.annotation.Autowired;
import org.tempuri.RecaudosRINSoap;

import javax.jws.WebParam;
import javax.xml.namespace.QName;
import java.net.URL;

/**
 * Web service endpoint. This class implements the interface RecaudosRINSoap,
 * which is auto-generated from the wsdl and describes the desired methods
 * with its required parameters and return types.
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
public class RinService implements RecaudosRINSoap{

    /**
     * Location of the custom wsdl file wished to be displayed as the description of the web service
     */
    public final static URL WSDL_LOCATION =
            RinService.class.getClassLoader().getResource("schema/RecaudosRIN.wsdl");

    /**
     * Name of the web service
     */
    public static final QName SERVICE_NAME =
            new QName("http://tempuri.org", "RecaudosRIN");

    /**
     * Injected {@link RINSoapController} object
     */
    @Autowired
    private RINSoapController rinSoapController;

    /**
     * Method to check the availability of the server
     *
     * @param infoDummy String representation of an object of type {@link co.edu.unibague.ws.models.VerificarEstadoRq}
     * @return String representation of an object of type {@link co.edu.unibague.ws.models.VerificarEstadoRs}
     */
    @Override
    public String verificarEstadoWebService(@WebParam(name = "infoDummy", targetNamespace = "http://tempuri.org") String infoDummy) {
        return rinSoapController.checkStatus(infoDummy);
    }

    /**
     * Method to search bills given their id
     *
     * @param numeroFactura String representation of an object of type {@link ConsultarFacturaRq}
     * @return String representation of an object of type {@link co.edu.unibague.ws.models.ConsultarFacturaRs}
     */
    @Override
    public String consultarFacturaPorNumero(@WebParam(name = "numeroFactura", targetNamespace = "http://tempuri.org") String numeroFactura) {
        return rinSoapController.searchBillBy(numeroFactura, ConsultarFacturaRq.BY_BILLID_TYPE);
    }

    /**
     * Method to search bills given owner's nit
     *
     * @param nit String representation of an object of type {@link ConsultarFacturaRq}
     * @return String representation of an object of type {@link co.edu.unibague.ws.models.ConsultarFacturaRs}
     */
    @Override
    public String consultarFacturasPorNit(@WebParam(name = "nit", targetNamespace = "http://tempuri.org") String nit) {
        return rinSoapController.searchBillBy(nit, ConsultarFacturaRq.BY_CLIENT_NIT_TYPE);
    }

    /**
     * Method to search bills given their owner's custom id
     *
     * @param numeroNegocio String representation of an object of type {@link ConsultarFacturaRq}
     * @return String representation of an object of type {@link co.edu.unibague.ws.models.ConsultarFacturaRs}
     */
    @Override
    public String consultarFacturasPorNegocio(@WebParam(name = "numeroNegocio", targetNamespace = "http://tempuri.org") String numeroNegocio) {
        return rinSoapController.noSearchImplemented(numeroNegocio);
    }

    /**
     * Method to register a payment
     *
     * @param infoPago String representation of an object of type {@link co.edu.unibague.ws.models.RegistrarPagoRq}
     * @return String representation of an object of type {@link co.edu.unibague.ws.models.RegistrarPagoRs}
     */
    @Override
    public String registrarPagoIFX(@WebParam(name = "infoPago", targetNamespace = "http://tempuri.org") String infoPago) {
        return rinSoapController.registerPayment(infoPago);
    }
}
