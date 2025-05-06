package co.edu.unibague.ws.configurations;

import org.tempuri.RecaudosRINSoap;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@Configuration
public class RinIntegrationTestConfiguration {

    @Bean
    public RecaudosRINSoap webServiceIntegrationTestClient(){
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(RecaudosRINSoap.class);
        jaxWsProxyFactoryBean.setAddress("https://localhost:8080" + EndpointConfiguration.BASE_URL +
                EndpointConfiguration.SERVICE_URL);
        return (RecaudosRINSoap) jaxWsProxyFactoryBean.create();
    }
}
