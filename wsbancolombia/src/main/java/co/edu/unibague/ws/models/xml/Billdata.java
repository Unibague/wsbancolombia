package co.edu.unibague.ws.models.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * This class represents an object of type BillRec in the response of the
 * search bill methods.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "billdata", propOrder = {
    "billId",
    "billInfo"
})
public class Billdata {

    @XmlElement(name = "BillId", required = true)
    protected String billId;
    @XmlElement(name = "BillInfo", required = true)
    protected Billdata.BillInfo billInfo;

    /**
     * Gets the value of the billId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillId() {
        return billId;
    }

    /**
     * Sets the value of the billId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillId(String value) {
        this.billId = value;
    }

    /**
     * Gets the value of the billInfo property.
     * 
     * @return
     *     possible object is
     *     {@link Billdata.BillInfo }
     *     
     */
    public Billdata.BillInfo getBillInfo() {
        return billInfo;
    }

    /**
     * Sets the value of the billInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Billdata.BillInfo }
     *     
     */
    public void setBillInfo(Billdata.BillInfo value) {
        this.billInfo = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="BillType" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="BillDt" type="{http://www.w3.org/2001/XMLSchema}date"/>
     *         &lt;element name="PresAcctId">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="BillingAcct" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="BillerId">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="SPName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="BillerNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="BillSummAmt" type="{http://example.com/uniws/}amountdata" maxOccurs="3" minOccurs="3"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "billType",
        "presAcctId",
        "billSummAmt",
        "billDt"
    })
    public static class BillInfo {

        @XmlElement(name = "BillType", required = true)
        protected String billType;
        @XmlElement(name = "BillDt", required = true)
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar billDt;
        @XmlElement(name = "PresAcctId", required = true)
        protected Billdata.BillInfo.PresAcctId presAcctId;
        @XmlElement(name = "BillSummAmt", required = true)
        protected List<Amountdata> billSummAmt;

        /**
         * Gets the value of the billType property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBillType() {
            return billType;
        }

        /**
         * Sets the value of the billType property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBillType(String value) {
            this.billType = value;
        }

        /**
         * Gets the value of the billDt property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getBillDt() {
            return billDt;
        }

        /**
         * Sets the value of the billDt property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setBillDt(XMLGregorianCalendar value) {
            this.billDt = value;
        }

        /**
         * Gets the value of the presAcctId property.
         * 
         * @return
         *     possible object is
         *     {@link Billdata.BillInfo.PresAcctId }
         *     
         */
        public Billdata.BillInfo.PresAcctId getPresAcctId() {
            return presAcctId;
        }

        /**
         * Sets the value of the presAcctId property.
         * 
         * @param value
         *     allowed object is
         *     {@link Billdata.BillInfo.PresAcctId }
         *     
         */
        public void setPresAcctId(Billdata.BillInfo.PresAcctId value) {
            this.presAcctId = value;
        }

        /**
         * Gets the value of the billSummAmt property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the billSummAmt property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getBillSummAmt().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Amountdata }
         * 
         * 
         */
        public List<Amountdata> getBillSummAmt() {
            if (billSummAmt == null) {
                billSummAmt = new ArrayList<Amountdata>();
            }
            return this.billSummAmt;
        }

        public void addSummAmt(Amountdata amount){
            if (billSummAmt == null) {
                billSummAmt = new ArrayList<Amountdata>();
            }
            billSummAmt.add(amount);
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="BillingAcct" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="BillerId">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="SPName" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="BillerNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "billingAcct",
            "billerId"
        })
        public static class PresAcctId {

            @XmlElement(name = "BillingAcct", required = true)
            protected String billingAcct;
            @XmlElement(name = "BillerId", required = true)
            protected Billdata.BillInfo.PresAcctId.BillerId billerId;

            /**
             * Gets the value of the billingAcct property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getBillingAcct() {
                return billingAcct;
            }

            /**
             * Sets the value of the billingAcct property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setBillingAcct(String value) {
                this.billingAcct = value;
            }

            /**
             * Gets the value of the billerId property.
             * 
             * @return
             *     possible object is
             *     {@link Billdata.BillInfo.PresAcctId.BillerId }
             *     
             */
            public Billdata.BillInfo.PresAcctId.BillerId getBillerId() {
                return billerId;
            }

            /**
             * Sets the value of the billerId property.
             * 
             * @param value
             *     allowed object is
             *     {@link Billdata.BillInfo.PresAcctId.BillerId }
             *     
             */
            public void setBillerId(Billdata.BillInfo.PresAcctId.BillerId value) {
                this.billerId = value;
            }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="SPName" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="BillerNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "spName",
                "billerNum"
            })
            public static class BillerId {

                @XmlElement(name = "SPName", required = true)
                protected String spName;
                @XmlElement(name = "BillerNum", required = true)
                protected String billerNum;

                /**
                 * Gets the value of the spName property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getSPName() {
                    return spName;
                }

                /**
                 * Sets the value of the spName property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setSPName(String value) {
                    this.spName = value;
                }

                /**
                 * Gets the value of the billerNum property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getBillerNum() {
                    return billerNum;
                }

                /**
                 * Sets the value of the billerNum property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setBillerNum(String value) {
                    this.billerNum = value;
                }

            }

        }

    }

}
