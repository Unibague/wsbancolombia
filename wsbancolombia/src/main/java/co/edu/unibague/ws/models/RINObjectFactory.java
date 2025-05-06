package co.edu.unibague.ws.models;

import co.edu.unibague.ws.models.xml.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.Properties;

/**
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
public class RINObjectFactory extends ObjectFactory {

    /**
     * Name of the properties file containing the default values for the
     * project's custom objects
     */
    protected final static String PROP_FILE = "wsdata.properties";

    /**
     * Default value for the SignonRs.CustLangPref field
     */
    protected final static String DEF_LNG = "default-language";

    /**
     * Default value for the SignonRs.ClientApp.Version field
     */
    protected final static String WS_VERSION = "version";

    /**
     * Default value for the SignonRs.ClientApp.Org field
     */
    protected final static String WS_ORG = "entity";

    /**
     * Default value for the SignonRs.ClientApp.Name field
     */
    protected final static String WS_NAME = "ws-name";

    /**
     * Default value for the PresSvcRs.BillInqRs.BillRec.BillInfo.BillType field
     */
    protected final static String BILL_TYPE = "bill-default-type";

    /**
     * Default value for the PresSvcRs.BillInqRs.BillRec.BillInfo.
     * PresAcctID.BillerID.SPName field
     */
    protected final static String BILLER_SPNAME = "biller-spname";

    /**
     * Default value for the PresSvcRs.BillInqRs.BillRec.BillInfo.
     * PresAcctID.BillerID.BillerNum field
     */
    protected final static String BILLER_DEFAULT_NUM = "biller-default-num";

    /**
     * Default value for the Status.StatusCode field
     */
    protected final static String STATUS_CODE = "status-code";

    /**
     * Default value for the Status.Severity field
     */
    protected final static String STATUS_SEVERITY = "status-severity";

    /**
     * Default value for the Status.StatusDesc field
     */
    protected final static String STATUS_DESC = "status-desc";

    /**
     * Default value for the BillSummAmnt.BillSummAmntCode field
     */
    protected final static String AMOUNT_CODE = "amount-code";

    /**
     * Default value for the BillSummAmnt.ShortDesc field
     */
    protected final static String AMOUNT_DESC = "amount-desc";

    /**
     * Default value for the BillSummAmnt.BillSummAmntType field
     */
    protected final static String AMOUNT_TYPE = "amount-type";

    /**
     * Properties file containing the default objects values
     */
    private Properties properties;

    /**
     * Object used to create XMLGregorianCalendars
     */
    private DatatypeFactory datatypeFactory;

    /**
     * Loads the properties file and gets an instance of the DatatypeFactory object.
     *
     * @throws IOException Error loading the properties file
     * @throws DatatypeConfigurationException Error getting the DatatypeFactory object
     */
    public RINObjectFactory() throws IOException, DatatypeConfigurationException {
        super();
        properties = new Properties();
        properties.load(RINObjectFactory.class.getClassLoader().getResourceAsStream(PROP_FILE));
        datatypeFactory = DatatypeFactory.newInstance();
    }

    /**
     * Builds a SingonRs object with the default values set.
     * @return new {@link SignonRs} object
     */
    public SignonRs getDefaultSignonRs(){
        SignonRs signonRs = new SignonRs();
        signonRs.setCustLangPref(properties.getProperty(DEF_LNG));
        signonRs.setLanguage(properties.getProperty(DEF_LNG));

        Wsclient uniWS = new Wsclient();
        uniWS.setVersion(properties.getProperty(WS_VERSION));
        uniWS.setOrg(properties.getProperty(WS_ORG));
        uniWS.setName(properties.getProperty(WS_NAME));

        signonRs.setClientApp(uniWS);
        XMLGregorianCalendar serverDt = datatypeFactory.newXMLGregorianCalendar(new GregorianCalendar());
        serverDt.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
        serverDt.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        signonRs.setServerDt(serverDt);

        return signonRs;
    }

    /**
     * Builds a Statusdata object with default values given the type of the status.
     *
     * @param type status type
     * @return new {@link Statusdata} object
     */
    public Statusdata buildStatusData(int type){
        Statusdata status = new Statusdata();
        status.setStatusCode(properties.getProperty(STATUS_CODE + type));
        status.setSeverity(properties.getProperty(STATUS_SEVERITY + type));
        status.setStatusDesc(properties.getProperty(STATUS_DESC + type));

        return status;
    }

    /**
     * Builds a Billdata object with the bill's info given.
     *
     * @param billId Id of the bill
     * @param billDt Due date of the bill
     * @param billingAcct Id of the owner of the bill
     * @param value Amount to pay
     * @return new {@link Billdata} object
     */
    public Billdata buildBilldata(String billId, String billDt, String billingAcct, BigDecimal value){
        Billdata billdata = new Billdata();
        billdata.setBillId(billId);
        Billdata.BillInfo info = createBilldataBillInfo();
        info.setBillDt(datatypeFactory.newXMLGregorianCalendar(billDt));
        info.setBillType(properties.getProperty(BILL_TYPE));

        Billdata.BillInfo.PresAcctId presAcctId = createBilldataBillInfoPresAcctId();
        presAcctId.setBillingAcct(billingAcct);

        Billdata.BillInfo.PresAcctId.BillerId billerId = createBilldataBillInfoPresAcctIdBillerId();
        billerId.setSPName(properties.getProperty(BILLER_SPNAME));
        billerId.setBillerNum(properties.getProperty(BILLER_DEFAULT_NUM));

        presAcctId.setBillerId(billerId);
        info.setPresAcctId(presAcctId);

        info.addSummAmt(buildAmountdata(Amountdata.INTEREST, new BigDecimal(0)));
        info.addSummAmt(buildAmountdata(Amountdata.CHARGES, value));
        info.addSummAmt(buildAmountdata(Amountdata.TOTAL, value));

        billdata.setBillInfo(info);

        return billdata;
    }

    /**
     * Builds an Amountdata object given the type and ammount
     * @param type type of amount
     * @param value amount to pay
     * @return new {@link Amountdata} object
     */
    protected Amountdata buildAmountdata(int type, BigDecimal value){
        Amountdata amountdata = createAmountdata();
        amountdata.setBillSummAmtCode(properties.getProperty(AMOUNT_CODE + type));
        amountdata.setShortDesc(properties.getProperty(AMOUNT_DESC + type));
        amountdata.setBillSummAmtType(properties.getProperty(AMOUNT_TYPE));
        Amountdata.CurAmt amt = new Amountdata.CurAmt();
        amt.setAmt(value);
        amountdata.setCurAmt(amt);
        return amountdata;
    }

    /**
     * Returns an XML date based on the date obtained in a database consult
     * @param timestamp Database date
     * @return new {@link XMLGregorianCalendar} object
     */
    public XMLGregorianCalendar getXMLDateFromSQLTimestamp(Object timestamp){
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(((Timestamp)timestamp).getTime());
        XMLGregorianCalendar date = datatypeFactory.newXMLGregorianCalendar(gc);
        date.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
        date.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        return date;
    }
}
