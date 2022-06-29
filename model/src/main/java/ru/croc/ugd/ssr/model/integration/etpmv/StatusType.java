package ru.croc.ugd.ssr.model.integration.etpmv;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * ������
 * 
 * <p>Java class for StatusType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="StatusCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="StatusTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="StatusDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatusType", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", propOrder = {
    "statusCode",
    "statusTitle",
    "statusDate"
})
public class StatusType {

    @XmlElement(name = "StatusCode", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
    protected int statusCode;
    @XmlElement(name = "StatusTitle", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
    protected String statusTitle;
    @XmlElement(name = "StatusDate", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar statusDate;

    /**
     * Gets the value of the statusCode property.
     * 
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the value of the statusCode property.
     * 
     */
    public void setStatusCode(int value) {
        this.statusCode = value;
    }

    /**
     * Gets the value of the statusTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusTitle() {
        return statusTitle;
    }

    /**
     * Sets the value of the statusTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusTitle(String value) {
        this.statusTitle = value;
    }

    /**
     * Gets the value of the statusDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStatusDate() {
        return statusDate;
    }

    /**
     * Sets the value of the statusDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStatusDate(XMLGregorianCalendar value) {
        this.statusDate = value;
    }

}
