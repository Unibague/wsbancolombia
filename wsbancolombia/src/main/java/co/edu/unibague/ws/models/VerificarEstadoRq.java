package co.edu.unibague.ws.models;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Dummy request representation
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@XmlRootElement(name = "IFX")
public class VerificarEstadoRq extends IFXRequest{

    /**
     * Name of the stored function to check the database status
     */
    public static final String DB_FUNCTION = "CHECK_STATUS";

    /**
     * Constant to represent the SOAP method called to check the web service status
     */
    public static final String SOAP_METHOD = "VerificarEstado";

    /**
     * Path of the xsd file used for schema verification
     */
    public static final String SCHEMA_PATH = "schema/VerificarEstadoRq.xsd";
}
