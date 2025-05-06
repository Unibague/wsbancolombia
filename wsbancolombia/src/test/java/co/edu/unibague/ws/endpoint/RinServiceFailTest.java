package co.edu.unibague.ws.endpoint;

import co.edu.unibague.ws.configurations.ControllersTestConfiguration;
import co.edu.unibague.ws.helpers.IFXTestBuilder;
import co.edu.unibague.ws.models.ConsultarFacturaRs;
import co.edu.unibague.ws.models.xml.SignonRs;
import co.edu.unibague.ws.models.xml.Statusdata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ControllersTestConfiguration.class)
public class RinServiceFailTest {

    @Autowired
    protected RinService rinService;

    @Test
    public void consultarFacturaPorNumeroEmpty(){
        String request = IFXTestBuilder.getStringRequest(getClass()
                .getClassLoader().getResource("rqempty/facturanum.xml").getFile());
        String result = rinService.consultarFacturaPorNumero(request);

        validateEmptyBillResult(result, "-001");
    }

    @Test
    public void consultarFacturasPorNitEmpty(){
        String request = IFXTestBuilder.getStringRequest(getClass()
                .getClassLoader().getResource("rqempty/facturanit.xml").getFile());

        String result = rinService.consultarFacturasPorNit(request);

        validateEmptyBillResult(result, "-003");
    }

    private void validateEmptyBillResult(String result, String statusCode){
        ConsultarFacturaRs rs = IFXTestBuilder.getUnmarshalledResult(ConsultarFacturaRs.class, result);

        assertNotNull("Result must not be null", rs);

        //Validate all fields in the SignOn object are present
        validateGettersNotNull(SignonRs.class, rs.getSignonRs());

        assertNotNull(rs.getPresSvcRs());
        assertNotNull(rs.getPresSvcRs().getBillInqRs());
        assertTrue(rs.getPresSvcRs().getBillInqRs().getBillRec().isEmpty());

        Statusdata statusdata = rs.getPresSvcRs().getBillInqRs().getStatus();
        assertNotNull(statusdata);
        assertEquals("Status value must be of respective error type",
                statusCode, statusdata.getStatusCode());
    }

    /** Gets all getters of the given class, executes them and if the resulting object belongs to one
     * of our custom classes (i.e, class name matches .*unibague.* regexp), does the same process
     * recursively. Asserts that none of the object's value is null.
     *
     * @param c Custom class
     * @param value Object of class c
     */
    private void validateGettersNotNull(Class<?> c, Object value){
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(c).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
                    Object o = pd.getReadMethod().invoke(value);
                    assertNotNull("Value " + pd.getName() + " must not be null", o);
                    if (Pattern.matches(".*unibague.*", o.getClass().getName())) {
                        validateGettersNotNull(o.getClass(), o);
                    }
                }
            }
        }catch (IntrospectionException |IllegalAccessException|InvocationTargetException e){
            e.printStackTrace();
        }
    }
}
