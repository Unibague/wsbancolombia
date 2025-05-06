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

import java.util.List;
import static org.junit.Assert.*;

/**
 * @author Daniel Alejandro Bravo Torres
 *         Universidad de Ibague
 *         Copyright (C) 2016. All Rights Reserved
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ControllersTestConfiguration.class)
public class BillIdQueryTest {

    @Autowired
    private RinService rinService;

    @Test
    public void validBill(){
        ConsultarFacturaRq cfr = getBillQuery(698899);
        ConsultarFacturaRs rs = IFXTestBuilder.getUnmarshalledResult(ConsultarFacturaRs.class,
                rinService.consultarFacturaPorNumero(cfr.toString()));
        PresSvcRs.BillInqRs billInqRs = rs.getPresSvcRs().getBillInqRs();

        assertEquals("Status code must be 0", "0", billInqRs.getStatus().getStatusCode());
        assertEquals("Must return 1 bill only", 1, billInqRs.getBillRec().size());
    }

    @Test
    public void billNotExist(){
        ConsultarFacturaRq cfr = getBillQuery(123400);
        ConsultarFacturaRs rs = IFXTestBuilder.getUnmarshalledResult(ConsultarFacturaRs.class,
                rinService.consultarFacturaPorNumero(cfr.toString()));

        assertEquals("Status code must be -001", "-001",
                rs.getPresSvcRs().getBillInqRs().getStatus().getStatusCode());
    }

    @Test
    public void twoValidBills(){
        ConsultarFacturaRq cfr = getBillQuery(698899, 698718);
        ConsultarFacturaRs rs = IFXTestBuilder.getUnmarshalledResult(ConsultarFacturaRs.class,
                rinService.consultarFacturaPorNumero(cfr.toString()));
        PresSvcRs.BillInqRs billInqRs = rs.getPresSvcRs().getBillInqRs();

        assertEquals("Status code must be 0", "0", billInqRs.getStatus().getStatusCode());
        assertEquals("Must return 2 bills",2, billInqRs.getBillRec().size());
    }

    @Test
    public void nBillsOneValid(){
        ConsultarFacturaRq cfr = getBillQuery(123400, 567800, 698718);
        ConsultarFacturaRs rs = IFXTestBuilder.getUnmarshalledResult(ConsultarFacturaRs.class,
                rinService.consultarFacturaPorNumero(cfr.toString()));
        PresSvcRs.BillInqRs billInqRs = rs.getPresSvcRs().getBillInqRs();

        assertEquals("Status code must be 0", "0", billInqRs.getStatus().getStatusCode());
        assertEquals("Must return 1 valid bill",1, billInqRs.getBillRec().size());
    }
    
    @Test
    public void wrongQuery(){
        ConsultarFacturaRq cfr = getBillQuery(123400);
        ConsultarFacturaRs rs = IFXTestBuilder.getUnmarshalledResult(ConsultarFacturaRs.class,
                rinService.consultarFacturaPorNumero(cfr.toString().replaceFirst("RqUID", "Fail")));

        assertEquals("Status code must be -099", "-099",
                rs.getPresSvcRs().getBillInqRs().getStatus().getStatusCode());
    }

    private ConsultarFacturaRq getBillQuery(int... billsId){
        String request = IFXTestBuilder.getStringRequest(getClass()
                .getClassLoader().getResource("rqok/facturanum.xml").getFile());
        ConsultarFacturaRq cfr = IFXTestBuilder.getUnmarshalledResult(
                ConsultarFacturaRq.class, request);
        List<String> bills = cfr.getPresSvcRq().getBillInqRq().getBillId();
        String s = bills.remove(0).substring(0, 30);
        for(int billId: billsId)
            bills.add(s + billId);

        return cfr;
    }
}
