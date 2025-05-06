package co.edu.unibague.ws.methods;

import co.edu.unibague.ws.configurations.ControllersTestConfiguration;
import co.edu.unibague.ws.endpoint.RinService;
import co.edu.unibague.ws.helpers.IFXTestBuilder;
import co.edu.unibague.ws.models.RegistrarPagoRq;
import co.edu.unibague.ws.models.RegistrarPagoRs;
import co.edu.unibague.ws.models.xml.PaySvcRq;
import co.edu.unibague.ws.models.xml.PmtData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * @author Daniel Alejandro Bravo Torres
 *         Universidad de Ibague
 *         Copyright (C) 2016. All Rights Reserved
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ControllersTestConfiguration.class)
public class PaymentTest {

    private final static String RQUID1 = "00000000-0000-0000-0000-201706138001";
    private final static String RQUID2 = "00000000-0000-0000-0000-201706138002";
    private final static int BILLID = 696793;
    private final static double BILLVALUE = 3830400;

    @Autowired
    private RinService rinService;

    @Test
    public void okPayment(){
        RegistrarPagoRq rq = getPayRq(RQUID1, BILLID, BILLVALUE);
        RegistrarPagoRs rs = IFXTestBuilder.getUnmarshalledResult(RegistrarPagoRs.class,
                rinService.registrarPagoIFX(rq.toString()));

        assertEquals("Status code must be 0", "0",
                rs.getPaySvcRs().getPmtAddRs().getStatus().getStatusCode());
    }

    @Test
    public void billAlreadyPayed(){
        RegistrarPagoRq rq = getPayRq(RQUID2, BILLID, BILLVALUE);
        RegistrarPagoRs rs = IFXTestBuilder.getUnmarshalledResult(RegistrarPagoRs.class,
                rinService.registrarPagoIFX(rq.toString()));

        assertEquals("Status code must be -002", "-002",
                rs.getPaySvcRs().getPmtAddRs().getStatus().getStatusCode());
    }

    @Test
    public void dupRqId(){
        RegistrarPagoRq rq = getPayRq(RQUID1, 123456, BILLVALUE);
        RegistrarPagoRs rs = IFXTestBuilder.getUnmarshalledResult(RegistrarPagoRs.class,
                rinService.registrarPagoIFX(rq.toString()));

        assertEquals("Status code must be -006", "-006",
                rs.getPaySvcRs().getPmtAddRs().getStatus().getStatusCode());
    }

    @Test
    public void billNotExists(){
        RegistrarPagoRq rq = getPayRq(RQUID2, 123456, BILLVALUE);
        RegistrarPagoRs rs = IFXTestBuilder.getUnmarshalledResult(RegistrarPagoRs.class,
                rinService.registrarPagoIFX(rq.toString()));

        assertEquals("Status code must be -001", "-001",
                rs.getPaySvcRs().getPmtAddRs().getStatus().getStatusCode());
    }

    private RegistrarPagoRq getPayRq(String rqid, int bill, double value){
        String request = IFXTestBuilder.getStringRequest(getClass()
                .getClassLoader().getResource("rqok/regpago.xml").getFile());
        RegistrarPagoRq rq = IFXTestBuilder.getUnmarshalledResult(
                RegistrarPagoRq.class, request);

        rq.getPaySvcRq().setRqUID(rqid);

        PaySvcRq.PmtAddRq pmtAddRq= rq.getPaySvcRq().getPmtAddRq();
        pmtAddRq.setRqUID(rqid);
        pmtAddRq.getPmtInfo().getCurAmt().setAmt(BigDecimal.valueOf(value));
        PmtData.RemitInfo remitInfo = pmtAddRq.getPmtInfo().getRemitInfo();
        remitInfo.getCurAmt().setAmt(BigDecimal.valueOf(value));
        remitInfo.setBillId("00000000-0000-0000-0000-000000" + bill);

        return rq;
    }
}
