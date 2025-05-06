package co.edu.unibague.ws.configurations;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.Properties;

/**
 * Class required to handle the password validations of the WSS4J In and Out interceptors,
 * mainly the password of the basic authentication mechanism and the password of
 * the different alias to the keystore files.
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */

public class PasswordHandler implements CallbackHandler {

    /**
     * Properties file with the password of the available users. Each user is a property
     * of this file.
     */
    private final static String PROP_FILE = "wssec.properties";

    /**
     * {@link Properties} object of PROP_FILE
     */
    private Properties passwords;

    /**
     * Loads the properties of PROP_FILE to a {@link Properties} object
     */
    public PasswordHandler(){
        passwords = new Properties();
        try {
            passwords.load(PasswordHandler.class.getClassLoader()
                    .getResourceAsStream(PROP_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the different {@link WSPasswordCallback} calls from the interceptors. Searches
     * the specified user in the properties file and assigns the user's password to the callback
     * for validation.
     *
     * @param callbacks Array of type {@link Callback}
     * @throws IOException Error reading the properties file
     * @throws UnsupportedCallbackException Callback related error
     */
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for(Callback c : callbacks){
            WSPasswordCallback pc = (WSPasswordCallback) c;

            String pass = passwords.getProperty(pc.getIdentifier());
            if(pass != null){
                pc.setPassword(pass);
            }
        }
    }
}
