package co.edu.unibague.ws.models.xml;

/**
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
public class WrongSoapMessageException extends Exception {

    public WrongSoapMessageException(String message){
        super(message);
    }
}
