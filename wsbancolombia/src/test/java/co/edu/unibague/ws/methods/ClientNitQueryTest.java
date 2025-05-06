package co.edu.unibague.ws.methods;

import co.edu.unibague.ws.configurations.ControllersTestConfiguration;
import co.edu.unibague.ws.endpoint.RinService;
import co.edu.unibague.ws.helpers.IFXTestBuilder;
import co.edu.unibague.ws.models.ConsultarFacturaRq;
import co.edu.unibague.ws.models.ConsultarFacturaRs;
import co.edu.unibague.ws.models.xml.PresSvcRs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author Daniel Alejandro Bravo Torres
 *         Universidad de Ibague
 *         Copyright (C) 2016. All Rights Reserved
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ControllersTestConfiguration.class)
public class ClientNitQueryTest {
    @Autowired
    private RinService rinService;

    @Test
    public void validNit(){
        ConsultarFacturaRq cfr = getBillQuery("1110522161");
        ConsultarFacturaRs rs = IFXTestBuilder.getUnmarshalledResult(ConsultarFacturaRs.class,
                rinService.consultarFacturasPorNit(cfr.toString()));
        PresSvcRs.BillInqRs billInqRs = rs.getPresSvcRs().getBillInqRs();

        assertEquals("Status code must be 0", "0", billInqRs.getStatus().getStatusCode());
        assertEquals("Must return 1 bill", 1, billInqRs.getBillRec().size());
    }

    @Test
    public void nitWithNoBills(){
        ConsultarFacturaRq cfr = getBillQuery("1054557490");
        ConsultarFacturaRs rs = IFXTestBuilder.getUnmarshalledResult(ConsultarFacturaRs.class,
                rinService.consultarFacturasPorNit(cfr.toString()));

        assertEquals("Status code must be -003", "-003",
                rs.getPresSvcRs().getBillInqRs().getStatus().getStatusCode());
    }

    @Test
    public void madeUpNit(){
        ConsultarFacturaRq cfr = getBillQuery("1234567");
        ConsultarFacturaRs rs = IFXTestBuilder.getUnmarshalledResult(ConsultarFacturaRs.class,
                rinService.consultarFacturasPorNit(cfr.toString()));

        assertEquals("Status code must be -003", "-003",
                rs.getPresSvcRs().getBillInqRs().getStatus().getStatusCode());
    }

    @Test
    public void wrongQuery(){
        ConsultarFacturaRq cfr = getBillQuery("1234567");
        ConsultarFacturaRs rs = IFXTestBuilder.getUnmarshalledResult(ConsultarFacturaRs.class,
                rinService.consultarFacturasPorNit(cfr.toString().replaceFirst("RqUID", "Fail")));

        assertEquals("Status code must be -099", "-099",
                rs.getPresSvcRs().getBillInqRs().getStatus().getStatusCode());
    }

    private ConsultarFacturaRq getBillQuery(String nit){
        String request = IFXTestBuilder.getStringRequest(getClass()
                .getClassLoader().getResource("rqok/facturanit.xml").getFile());
        ConsultarFacturaRq cfr = IFXTestBuilder.getUnmarshalledResult(
                ConsultarFacturaRq.class, request);

        cfr.getPresSvcRq().getBillInqRq().getBillerId().setBillerNum(nit);

        return cfr;
    }
}
