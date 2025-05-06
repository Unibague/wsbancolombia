package co.edu.unibague.ws.models.xml;

import java.math.BigDecimal;
import java.math.MathContext;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents an object of type BillSummAmt in the response of the
 * search bill methods.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "amountdata", propOrder = {
    "billSummAmtCode",
    "shortDesc",
    "curAmt",
    "billSummAmtType"
})
public class Amountdata {

    /**
     * Constant to represent a field containing the bill's amount without taxes
     */
    public static final int CHARGES = 0;

    /**
     * Constant to represent a field containing the bill's taxes amount
     */
    public static final int INTEREST = 1;

    /**
     * Constant to represent a field containing the bill's total amount
     */
    public static final int TOTAL = 2;

    @XmlElement(name = "BillSummAmtCode", required = true)
    protected String billSummAmtCode;
    @XmlElement(name = "ShortDesc", required = true)
    protected String shortDesc;
    @XmlElement(name = "CurAmt", required = true)
    protected Amountdata.CurAmt curAmt;
    @XmlElement(name = "BillSummAmtType", required = true)
    protected String billSummAmtType;

    /**
     * Gets the value of the billSummAmtCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillSummAmtCode() {
        return billSummAmtCode;
    }

    /**
     * Sets the value of the billSummAmtCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillSummAmtCode(String value) {
        this.billSummAmtCode = value;
    }

    /**
     * Gets the value of the shortDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortDesc() {
        return shortDesc;
    }

    /**
     * Sets the value of the shortDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortDesc(String value) {
        this.shortDesc = value;
    }

    /**
     * Gets the value of the curAmt property.
     * 
     * @return
     *     possible object is
     *     {@link Amountdata.CurAmt }
     *     
     */
    public Amountdata.CurAmt getCurAmt() {
        return curAmt;
    }

    /**
     * Sets the value of the curAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Amountdata.CurAmt }
     *     
     */
    public void setCurAmt(Amountdata.CurAmt value) {
        this.curAmt = value;
    }

    /**
     * Gets the value of the billSummAmtType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillSummAmtType() {
        return billSummAmtType;
    }

    /**
     * Sets the value of the billSummAmtType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillSummAmtType(String value) {
        this.billSummAmtType = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "amt"
    })
    public static class CurAmt {

        @XmlElement(name = "Amt", required = true)
        protected BigDecimal amt;

        /**
         * Gets the value of the amt property.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getAmt() {
            return amt;
        }

        /**
         * Sets the value of the amt property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setAmt(BigDecimal value) {
            this.amt = value;
        }
    }

}
