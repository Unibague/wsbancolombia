package co.edu.unibague.ws.models;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Dummy response representation
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@XmlRootElement(name = "IFX")
public class VerificarEstadoRs extends IFXResult{

    /**
     * Default constructor
     */
    public VerificarEstadoRs(){
        super();
    }

    /**
     * Sets the ClientDt based on the date sent in the request message
     *
     * @param clientDt ClientDt send in the request
     */
    public void setClientDt(XMLGregorianCalendar clientDt){
        signonRs.setClientDt(clientDt);
    }
}
