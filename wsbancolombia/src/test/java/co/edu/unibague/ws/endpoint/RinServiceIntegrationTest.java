package co.edu.unibague.ws.endpoint;


import co.edu.unibague.ws.configurations.RinIntegrationTestConfiguration;
import co.edu.unibague.ws.helpers.IFXTestBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.tempuri.RecaudosRINSoap;

import static org.junit.Assert.assertNotNull;

/**
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RinIntegrationTestConfiguration.class)
public class RinServiceIntegrationTest {

    @Autowired
    private RecaudosRINSoap recaudosRINIntegrationTest;

    @Test
    public void verificarEstadoWebService(){
        String request = "";

        String result = recaudosRINIntegrationTest.verificarEstadoWebService(request);

        assertNotNull(result);
    }

    @Test
    public void consultarFacturaPorNumero(){
        String request = IFXTestBuilder.getStringRequest(getClass()
                .getClassLoader().getResource("rqok/facturanum.xml").getFile());
        String result = recaudosRINIntegrationTest.consultarFacturaPorNumero(request);

        assertNotNull(result);
    }

    @Test
    public void consultarFacturasPorNit(){
        String request = "";

        String result = recaudosRINIntegrationTest.consultarFacturasPorNit(request);

        assertNotNull(result);
    }

    @Test
    public void consultarFacturasPorNegocio(){
        String request = "";

        String result = recaudosRINIntegrationTest.consultarFacturasPorNegocio(request);

        assertNotNull(result);
    }

    @Test
    public void registrarPagoIFX(){
        String request = "";

        String result = recaudosRINIntegrationTest.registrarPagoIFX(request);

        assertNotNull(result);
    }
}
