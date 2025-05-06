package co.edu.unibague.ws;

import co.edu.unibague.ws.configurations.ControllersTestConfiguration;
import co.edu.unibague.ws.endpoint.RinService;
import co.edu.unibague.ws.helpers.IFXTestBuilder;
import co.edu.unibague.ws.models.ConsultarFacturaRq;
import co.edu.unibague.ws.models.ConsultarFacturaRs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ControllersTestConfiguration.class)
public class SearchTest {

    @Autowired
    protected RinService rinService;

    @Test
    public void searchBills(){
        String request = IFXTestBuilder.getStringRequest(getClass()
                .getClassLoader().getResource("rqok/facturanum.xml").getFile());
        ConsultarFacturaRq cfr = IFXTestBuilder.getUnmarshalledResult(
                ConsultarFacturaRq.class, request);

        List<String> bills = cfr.getPresSvcRq().getBillInqRq().getBillId();
        String s = bills.remove(0).substring(0, 30);

        int [] ids = {
                661931,662053,662759,662762,662871,662873,661575,661589,661617,661679,
                661864,661869,661945,662694,683148,662726,658767,658785,659069,670274,
                659176,659269,659279,659327,659469,689232,685988,663597,689277,669852
        };
        int total = ids.length;

        for(int i : ids){
            bills.add(s + String.valueOf(i));
        }

        long l1 = System.currentTimeMillis();

        String result = rinService.consultarFacturaPorNumero(cfr.toString());
        //System.out.println(result);

        ConsultarFacturaRs rs = IFXTestBuilder.getUnmarshalledResult(ConsultarFacturaRs.class,
                result);

        long l2 = System.currentTimeMillis();

        assertEquals("Wrong number of bills", total,
                rs.getPresSvcRs().getBillInqRs().getBillRec().size());

        assertTrue("Taking too long", (l2 - l1)/1000 < 3);
    }
}
