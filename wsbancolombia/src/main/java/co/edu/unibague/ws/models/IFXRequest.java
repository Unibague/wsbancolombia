package co.edu.unibague.ws.models;

import co.edu.unibague.ws.models.xml.SignonRq;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Base class for XML request of the web service.
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public abstract class IFXRequest {
    /**
     * Object that represents the info of the SignonRq field in the
     * XML request.
     */
    @XmlElement(name = "SignonRq", required = true)
    protected SignonRq signonRq;

    /**
     * Gets the value of the signonRq property.
     * @return possible object is {@link SignonRq }
     */
    public SignonRq getSignonRq() {
        return signonRq;
    }

    /**
     * Sets the value of the signonRq property
     * @param signonRq allowed object is {@link SignonRq }
     */
    public void setSignonRq(SignonRq signonRq) {
        this.signonRq = signonRq;
    }

    /**
     * Gets the value of the SignonRq.clientDt property
     * @return possible object is {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getClientDt(){
        if(signonRq != null)
            return signonRq.getClientDt();
        return null;
    }

    /**
     * Returns the XML marshalling of the current object as a String.
     *
     * @return XML representation of the object
     */
    @Override
    public String toString(){
        try {
            JAXBContext jc = JAXBContext.newInstance(this.getClass());
            StringWriter sw = new StringWriter();
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(this, sw);
            return sw.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return "error";
    }
}
