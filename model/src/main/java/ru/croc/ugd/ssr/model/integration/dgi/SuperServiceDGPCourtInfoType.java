
package ru.croc.ugd.ssr.model.integration.dgi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for SuperServiceDGPCourtInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SuperServiceDGPCourtInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="affair_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="letter_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="case_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="date_last_court" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="date_last_act" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="date_law" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="result_delo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuperServiceDGPCourtInfoType", propOrder = {
    "affairId",
    "letterId",
    "caseId",
    "dateLastCourt",
    "dateLastAct",
    "dateLaw",
    "resultDelo"
})
@XmlRootElement(name = "SuperServiceDGPCourtInfoType")
public class SuperServiceDGPCourtInfoType {

    @XmlElement(name = "affair_id", required = true)
    protected String affairId;
    @XmlElement(name = "letter_id", required = true)
    protected String letterId;
    @XmlElement(name = "case_id", required = true)
    protected String caseId;
    @XmlElement(name = "date_last_court", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateLastCourt;
    @XmlElement(name = "date_last_act")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateLastAct;
    @XmlElement(name = "date_law")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateLaw;
    @XmlElement(name = "result_delo")
    protected String resultDelo;

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
     * Gets the value of the letterId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLetterId() {
        return letterId;
    }

    /**
     * Sets the value of the letterId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLetterId(String value) {
        this.letterId = value;
    }

    /**
     * Gets the value of the caseId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCaseId() {
        return caseId;
    }

    /**
     * Sets the value of the caseId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCaseId(String value) {
        this.caseId = value;
    }

    /**
     * Gets the value of the dateLastCourt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateLastCourt() {
        return dateLastCourt;
    }

    /**
     * Sets the value of the dateLastCourt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateLastCourt(XMLGregorianCalendar value) {
        this.dateLastCourt = value;
    }

    /**
     * Gets the value of the dateLastAct property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateLastAct() {
        return dateLastAct;
    }

    /**
     * Sets the value of the dateLastAct property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateLastAct(XMLGregorianCalendar value) {
        this.dateLastAct = value;
    }

    /**
     * Gets the value of the dateLaw property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateLaw() {
        return dateLaw;
    }

    /**
     * Sets the value of the dateLaw property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateLaw(XMLGregorianCalendar value) {
        this.dateLaw = value;
    }

    /**
     * Gets the value of the resultDelo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultDelo() {
        return resultDelo;
    }

    /**
     * Sets the value of the resultDelo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultDelo(String value) {
        this.resultDelo = value;
    }

}
