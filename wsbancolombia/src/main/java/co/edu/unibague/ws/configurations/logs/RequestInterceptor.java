package co.edu.unibague.ws.configurations.logs;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingMessage;

/**
 * Custom logging interceptor implementation. Used to log the ID and headers of the
 * requests send to the server.
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */

public class RequestInterceptor extends LoggingInInterceptor {

    @Override
    protected String formatLoggingMessage(LoggingMessage loggingMessage) {
        StringBuilder sb = new StringBuilder("Incoming request:\nID:" );
        sb.append(loggingMessage.getId());
        sb.append("\t|\tHeaders: ");
        sb.append(loggingMessage.getHeader());

        if(loggingMessage.getPayload().length() > 0)
            sb.append(loggingMessage.getPayload());

        return sb.toString();
    }
}
