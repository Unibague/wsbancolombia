package co.edu.unibague.ws.configurations.logs;

import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.interceptor.LoggingOutInterceptor;

/**
 * Custom logging interceptor implementation. Used to log the ID and html code of the
 * response given by the server.
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
public class ResponseInterceptor extends LoggingOutInterceptor {

    @Override
    protected String formatLoggingMessage(LoggingMessage loggingMessage) {
        StringBuilder sb = new StringBuilder("Outgoing response:\nID:" );
        sb.append(loggingMessage.getId());
        sb.append("\t|\tResponse Code: ");
        sb.append(loggingMessage.getResponseCode());

        if(loggingMessage.getPayload().length() > 0)
            sb.append(loggingMessage.getPayload());

        return sb.toString();
    }
}
