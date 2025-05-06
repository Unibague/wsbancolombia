package co.edu.unibague.ws.models;

import co.edu.unibague.ws.models.xml.SignonRs;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Base class for XML response of the web service.
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public abstract class IFXResult {

    /**
     * Constant representing the format used to create a custom rqUID.
     */
    protected final static int[] UID_FORMAT = {8, 4, 4, 4, 12};

    /**
     * Object that represents the info of the SignonRs field in the
     * XML request.
     */
    @XmlElement(name = "SignonRs", required = true)
    protected SignonRs signonRs;

    /**
     * Object of the custom RINObjectFactory class, that handles the creation of
     * custom model's objects with default values. This object is not present
     * in the XML response.
     */
    @XmlTransient
    protected RINObjectFactory objectFactory;

    /**
     * Default constructor. Initializes the RINObjectFactory object and assigns
     * the default values for the {@link SignonRs} field.
     */
    public IFXResult(){
        try {
            objectFactory = new RINObjectFactory();
            signonRs = objectFactory.getDefaultSignonRs();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the value of the SignonRs property.
     * @return possible object is {@link SignonRs }
     */
    public SignonRs getSignonRs() {
        return signonRs;
    }

    /**
     * Sets the value of the signonRs property
     * @param signonRs allowed object is {@link SignonRs }
     */
    public void setSignonRs(SignonRs signonRs) {
        this.signonRs = signonRs;
    }

    /**
     * Fills a given number with 0 to the left or splits it, given the size of the
     * int in the format, and joins all the string generated.
     *
     * @param number current number to format
     * @param format size of the different segments of the desired string
     * @param separator character to join the generated string
     * @return number adapted to the specified format
     */
    protected String fillWithZeros(String number, int[] format, char separator){
        String formated = "";
        for(int i = format.length - 1; i >= 0; i--){
            if(number == null){
                formated = String.format("%1$0" + format[i] + "d", 0) + separator + formated;
            }else if(number.length() > format[i]){
                formated = number.substring(number.length() - format[i]) + separator + formated;
                number = number.substring(0, number.length() - format[i]);
            }else{
                formated = String.format("%1$0" + format[i] + ".0f", Double.valueOf(number)) + separator + formated;
                number = null;
            }
        }
        return formated.substring(0, formated.length() - 1);
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
