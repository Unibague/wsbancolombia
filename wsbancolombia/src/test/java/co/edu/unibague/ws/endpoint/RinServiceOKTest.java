package co.edu.unibague.ws.endpoint;

import co.edu.unibague.ws.configurations.ControllersTestConfiguration;
import co.edu.unibague.ws.helpers.IFXTestBuilder;
import co.edu.unibague.ws.models.ConsultarFacturaRq;
import co.edu.unibague.ws.models.ConsultarFacturaRs;
import co.edu.unibague.ws.models.RegistrarPagoRq;
import co.edu.unibague.ws.models.RegistrarPagoRs;
import co.edu.unibague.ws.models.xml.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ControllersTestConfiguration.class)
public class RinServiceOKTest {

    @Autowired
    protected RinService rinService;

    @Test
    public void verificarEstadoWebService(){
        String request = IFXTestBuilder.getStringRequest(getClass()
                .getClassLoader().getResource("rqok/verificarestado.xml").getFile());

        String result = rinService.verificarEstadoWebService(request);

        assertNotNull(result);
    }

    @Test
    public void consultarFacturaPorNumero(){
        String request = IFXTestBuilder.getStringRequest(getClass()
                .getClassLoader().getResource("rqok/facturanum.xml").getFile());
        String result = rinService.consultarFacturaPorNumero(request);
        validateSuccessfulBillResult(request, result);
    }


    @Test
    public void consultarFacturasPorNit(){
        String request = IFXTestBuilder.getStringRequest(getClass()
                .getClassLoader().getResource("rqok/facturanit.xml").getFile());

        String result = rinService.consultarFacturasPorNit(request);
        validateSuccessfulBillResult(request, result);
    }

    @Test
    public void consultarFacturasPorNegocio(){
        String request = IFXTestBuilder.getStringRequest(getClass()
                .getClassLoader().getResource("rqok/facturaneg.xml").getFile());

        String result = rinService.consultarFacturasPorNegocio(request);

        assertNotNull(result);
    }

    @Test
    public void registrarPagoIFX(){
        String request = IFXTestBuilder.getStringRequest(getClass()
                .getClassLoader().getResource("rqok/regpago.xml").getFile());

        String result = rinService.registrarPagoIFX(request);

        RegistrarPagoRq rq = IFXTestBuilder.getUnmarshalledResult(RegistrarPagoRq.class, request);
        RegistrarPagoRs rs = IFXTestBuilder.getUnmarshalledResult(RegistrarPagoRs.class, result);

        List<String> nulls = new ArrayList<>();
        getAllNullValues(RegistrarPagoRs.class, rs, nulls);

        assertNotNull(rs);
        assertTrue("Null values exist - " + String.join(" & ", nulls), nulls.size() == 8);
        assertEquals("RqUID must be equal", rq.getRqUID(), rs.getPaySvcRs().getRqUID());

        PaySvcRs.PmtAddRs pmtAddRs = rs.getPaySvcRs().getPmtAddRs();

        assertEquals("Search must be successful",
                "000", pmtAddRs.getStatus().getStatusCode());

        assertTrue("PaymentID must be a number",
                Pattern.matches("^[0-9]{3,12}$", pmtAddRs.getPmtRec().getPmtId()));
        assertEquals("Payment status must be processed",
                "Processed", pmtAddRs.getPmtRec().getPmtStatus().getPmtStatusCode());

    }

    /**
     * Checks that the response given by the endpoint is ok given the request possed
     *
     * @param request xml request
     * @param result xml response
     */
    private void validateSuccessfulBillResult(String request, String result){
        ConsultarFacturaRq rq = IFXTestBuilder.getUnmarshalledResult(ConsultarFacturaRq.class, request);
        ConsultarFacturaRs rs = IFXTestBuilder.getUnmarshalledResult(ConsultarFacturaRs.class, result);

        //Validate the result is not null and matches the IFX pattern
        assertNotNull("Result must not be null", rs);

        //Validate all fields are present
        validateGettersNotNull(ConsultarFacturaRs.class, rs);

        PresSvcRs.BillInqRs billInqRs = rs.getPresSvcRs().getBillInqRs();

        //Validate result status is OK
        assertEquals("Search must be successful",
                "000", billInqRs.getStatus().getStatusCode());

        //Validate result contains any bill
        assertFalse("Result must have bills data", billInqRs.getBillRec().isEmpty());

        //Validate RqUID to be equal in request and response PresSvc element
        assertEquals("RqUID must be equal to the one in the request",
                rq.getPresSvcRq().getRqUID(), rs.getPresSvcRs().getRqUID());

        Billdata data = billInqRs.getBillRec().get(0);

        //Validates Bill ID matches pattern
        assertTrue("Bill ID must match required pattern",
                Pattern.matches("^[0-9]{8}-[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{12}$", data.getBillId()));

        //Validates bill amount matches the required pattern
        assertEquals("Result must have values for interests, charges and total",
                data.getBillInfo().getBillSummAmt().size(), 3);
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
        }catch (IntrospectionException|IllegalAccessException|InvocationTargetException e){
            e.printStackTrace();
        }
    }

    /**
     * Workaround to validate no nulls values in a RegistrarPago response
     * @param c Custom class
     * @param value Object of class c
     * @param list List of current null objects
     */
    private void getAllNullValues(Class<?> c, Object value, List<String> list){
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(c).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
                    Object o = pd.getReadMethod().invoke(value);
                    if(o == null)
                        list.add(pd.getName());
                    else if (Pattern.matches(".*unibague.*", o.getClass().getName())) {
                        getAllNullValues(o.getClass(), o, list);
                    }
                }
            }
        }catch (IntrospectionException|IllegalAccessException|InvocationTargetException e){
            e.printStackTrace();
        }
    }
}
