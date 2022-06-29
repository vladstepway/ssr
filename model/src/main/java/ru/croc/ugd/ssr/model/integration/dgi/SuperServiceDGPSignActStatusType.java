
package ru.croc.ugd.ssr.model.integration.dgi;

import ru.reinform.cdp.utils.mapper.xml.LocalDateTimeXmlAdapter;
import ru.reinform.cdp.utils.mapper.xml.LocalDateXmlAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for SuperServiceDGPSignActStatusType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SuperServiceDGPSignActStatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="affair_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="order_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pdfSignedAct" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="signActDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="TechInfo" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="userID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="userFIO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="actionDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
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
@XmlType(name = "SuperServiceDGPSignActStatusType", propOrder = {
    "affairId",
    "orderId",
    "pdfSignedAct",
    "signActDate",
    "techInfo"
})
@XmlRootElement(name = "SuperServiceDGPSignActStatusType")
public class SuperServiceDGPSignActStatusType {

    @XmlElement(name = "affair_id", required = true)
    protected String affairId;
    @XmlElement(name = "order_id", required = true)
    protected String orderId;
    @XmlElement(required = true)
    protected String pdfSignedAct;
    @XmlElement(name = "signActDate", required = true, type = String.class)
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    @XmlSchemaType(name = "date")
    protected LocalDate signActDate;
    @XmlElement(name = "TechInfo")
    protected SuperServiceDGPSignActStatusType.TechInfo techInfo;

    /**
     * Gets the value of the affairId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAffairId() {
        return affairId;
    }

    /**
     * Sets the value of the affairId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAffairId(String value) {
        this.affairId = value;
    }

    /**
     * Gets the value of the orderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Sets the value of the orderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderId(String value) {
        this.orderId = value;
    }

    /**
     * Gets the value of the pdfSignedAct property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPdfSignedAct() {
        return pdfSignedAct;
    }

    /**
     * Sets the value of the pdfSignedAct property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPdfSignedAct(String value) {
        this.pdfSignedAct = value;
    }

    /**
     * Gets the value of the signActDate property.
     * 
     * @return
     *     possible object is
     *     {@link LocalDate }
     *     
     */
    public LocalDate getSignActDate() {
        return signActDate;
    }

    /**
     * Sets the value of the signActDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalDate }
     *     
     */
    public void setSignActDate(LocalDate value) {
        this.signActDate = value;
    }

    /**
     * Gets the value of the techInfo property.
     * 
     * @return
     *     possible object is
     *     {@link SuperServiceDGPSignActStatusType.TechInfo }
     *     
     */
    public SuperServiceDGPSignActStatusType.TechInfo getTechInfo() {
        return techInfo;
    }

    /**
     * Sets the value of the techInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SuperServiceDGPSignActStatusType.TechInfo }
     *     
     */
    public void setTechInfo(SuperServiceDGPSignActStatusType.TechInfo value) {
        this.techInfo = value;
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
     *         &lt;element name="userID" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="userFIO" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="actionDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
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
        "userID",
        "userFIO",
        "actionDateTime"
    })
    public static class TechInfo {

        @XmlElement(required = true)
        protected String userID;
        @XmlElement(required = true)
        protected String userFIO;
        @XmlElement(name = "actionDateTime", required = true, type = String.class)
        @XmlJavaTypeAdapter(LocalDateTimeXmlAdapter.class)
        @XmlSchemaType(name = "dateTime")
        protected LocalDateTime actionDateTime;

        /**
         * Gets the value of the userID property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUserID() {
            return userID;
        }

        /**
         * Sets the value of the userID property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUserID(String value) {
            this.userID = value;
        }

        /**
         * Gets the value of the userFIO property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUserFIO() {
            return userFIO;
        }

        /**
         * Sets the value of the userFIO property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUserFIO(String value) {
            this.userFIO = value;
        }

        /**
         * Gets the value of the actionDateTime property.
         * 
         * @return
         *     possible object is
         *     {@link LocalDateTime }
         *     
         */
        public LocalDateTime getActionDateTime() {
            return actionDateTime;
        }

        /**
         * Sets the value of the actionDateTime property.
         * 
         * @param value
         *     allowed object is
         *     {@link LocalDateTime }
         *     
         */
        public void setActionDateTime(LocalDateTime value) {
            this.actionDateTime = value;
        }

    }

}
