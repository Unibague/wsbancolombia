package co.edu.unibague.ws.configurations;

import co.edu.unibague.ws.configurations.logs.RequestInterceptor;
import co.edu.unibague.ws.configurations.logs.ResponseInterceptor;
import co.edu.unibague.ws.endpoint.RinService;
import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.wss4j.dom.WSConstants;
import org.springframework.beans.factory.annotation.Value;
import org.tempuri.RecaudosRINSoap;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;
import java.util.HashMap;
import java.util.Map;

/**
 * Main spring configuration class. It sets the address on which our
 * application will be available, and the web service associated to it.
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@Configuration
public class EndpointConfiguration {

    /**
     * Base URI for the application server
     */
    public static final String BASE_URL = "/comercial";

    /**
     * Base URI for the web service
     */
    public static final String SERVICE_URL = "/wsrin";

    /**
     * Config file for WSSecurity. Contains all the required parameters to
     * load the JKS containing the certificate used to decrypt the request
     * and sign the response of the web service.
     */
    private static final String UNIBAGUE_JKS_CONFIG = "configs/unibague.config";

    /**
     * Config file for WSSecurity. Contains all the required parameters to
     * load the JKS containing the certificate used to validate the request's
     * signature.
     */
    private static final String BANCOLOMBIA_SIGNATURE_CONFIG = "configs/banksign.config";

    /**
     * Config file for WSSecurity. Contains all the required parameters to
     * load the JKS containing the certificate used to encrypt the response of the web service.
     */
    private static final String BANCOLOMBIA_ENCRYPTION_CONFIG = "configs/bankcipher.config";

    /**
     * Selected encryption transport algorithm.
     */
    private static final String ENCRYPTION_TRANSPORT_ALG = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";

    /**
     * Selected encryption symmetric algorithm.
     */
    private static final String ENCRYPTION_SYMMETRIC_ALG = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";

    /**
     * Selected signature algorithm.
     */
    private static final String SIGNATURE_ALGORITHM = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";

    /**
     * Alias of the JKS file configured in UNIBAGUE_JKS_CONFIG
     */
    @Value("${security.out.signature}")
    private String unibagueAlias;

    /**
     * Username used in the basic authentication schema of the web service.
     */
    @Value("${security.in.username}")
    private String authUsername;

    /**
     * Alias of the JKS file configured in BANCOLOMBIA_ENCRYPTION_CONFIG
     */
    @Value("${security.out.encrypt}")
    private String bancolombiaAlias;

    /**
     * Configures the main address with the description of the different endpoints
     * and their respective methods, provided by the application.
     *
     * @return servlet published in the address http://localhost:8080/#{BASE_URL}/
     */
    @Bean
    public ServletRegistrationBean dispatcherServlet() {
        ServletRegistrationBean bean = new ServletRegistrationBean(
                new CXFServlet(), BASE_URL + "/*");
        bean.getInitParameters().put("hide-service-list-page", "true");
        return bean;
    }

    /**
     * Default configuration to enable the spring boot application. Besides, adds
     * our custom logging interceptors in the request and response normal and fault chain.
     *
     * @return Object of type {@link SpringBus}
     */
    @Bean(name= Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        SpringBus springBus = new SpringBus();
        springBus.getInInterceptors().add(logSoapIn());
        springBus.getInFaultInterceptors().add(logSoapIn());
        springBus.getOutInterceptors().add(logSoapOut());
        springBus.getOutFaultInterceptors().add(logSoapOut());
        return springBus;
    }

    /**
     * Initializes a new custom logging in interceptor
     *
     * @return Object if type {@link RequestInterceptor}
     */
    @Bean
    public AbstractLoggingInterceptor logSoapIn() {
        return new RequestInterceptor();
    }

    /**
     * Initializes a new custom logging out interceptor
     *
     * @return Object if type {@link ResponseInterceptor}
     */
    @Bean
    public AbstractLoggingInterceptor logSoapOut() {
        return new ResponseInterceptor();
    }

    /**
     * Configures our web service endpoint to be available in the spring runtime configuration.
     *
     * @return Object of type {@link RinService}
     */
    @Bean
    public RecaudosRINSoap recaudosRINSoap(){
        return new RinService();
    }

    /**
     * Initializes an endpoint with an instance of RecaudosRIN, available in the address
     * http://localhost:#{port}/#{BASE_URL}/#{SERVICE_URL} and with the interceptors to
     * handle the WSSecurity implementation.
     *
     * @return Endpoint configuration to our spring application
     */
    @Bean
    public Endpoint endpoint(){
        EndpointImpl endpoint = new EndpointImpl(springBus(), recaudosRINSoap());
        endpoint.setServiceName(RinService.SERVICE_NAME);
        //endpoint.setWsdlLocation(RinService.WSDL_LOCATION.toString());
        // endpoint.getInInterceptors().add(securityInInterceptor());
        // endpoint.getOutInterceptors().add(securityOutInterceptor());
        endpoint.publish(SERVICE_URL);
        return endpoint;
    }

    /**
     * Configures the timestamp, signature and encryption of the response.
     *
     * @return Object of type {@link WSS4JOutInterceptor}
     */
    @Bean
    public WSS4JOutInterceptor securityOutInterceptor(){
        Map<String, Object> configs = new HashMap<>();
        configs.put(WSHandlerConstants.ACTION, "Timestamp Signature Encrypt");

        configs.put(WSHandlerConstants.SIGNATURE_USER, unibagueAlias);
        configs.put(WSHandlerConstants.SIG_ALGO, SIGNATURE_ALGORITHM);
        configs.put(WSHandlerConstants.PW_CALLBACK_CLASS, PasswordHandler.class.getName());
        configs.put(WSHandlerConstants.SIG_PROP_FILE, UNIBAGUE_JKS_CONFIG);

        configs.put(WSHandlerConstants.ENCRYPTION_USER, bancolombiaAlias);
        configs.put(WSHandlerConstants.ENC_KEY_TRANSPORT, ENCRYPTION_TRANSPORT_ALG);
        configs.put(WSHandlerConstants.ENC_SYM_ALGO, ENCRYPTION_SYMMETRIC_ALG);
        configs.put(WSHandlerConstants.ENC_PROP_FILE, BANCOLOMBIA_ENCRYPTION_CONFIG);

        return new WSS4JOutInterceptor(configs);
    }

    /**
     * Configures the simple authentication, timestamp, signature and encryption validation
     * of the incoming request.
     *
     * @return Object of type {@link WSS4JInInterceptor}
     */
    @Bean
    public WSS4JInInterceptor securityInInterceptor() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(WSHandlerConstants.ACTION, "UsernameToken Timestamp Signature Encrypt");

        configs.put(WSHandlerConstants.USER, authUsername);
        configs.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PASSWORD_TEXT);
        configs.put(WSHandlerConstants.PW_CALLBACK_CLASS, PasswordHandler.class.getName());

        configs.put(WSHandlerConstants.SIG_ALGO, SIGNATURE_ALGORITHM);
        configs.put(WSHandlerConstants.SIG_PROP_FILE, BANCOLOMBIA_SIGNATURE_CONFIG);

        configs.put(WSHandlerConstants.DEC_PROP_FILE, UNIBAGUE_JKS_CONFIG);
        configs.put(WSHandlerConstants.ENC_KEY_TRANSPORT, ENCRYPTION_TRANSPORT_ALG);
        configs.put(WSHandlerConstants.ENC_SYM_ALGO, ENCRYPTION_SYMMETRIC_ALG);

        return new WSS4JInInterceptor(configs);
    }
}
