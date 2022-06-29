
package ru.croc.ugd.ssr.model.integration.dgi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for SuperServiceDGPDefectEliminationAgreementType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SuperServiceDGPDefectEliminationAgreementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="acceptedDefectsActChedFileId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="affairId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="letterId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="actNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="consentStatusCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="defectsEliminationStatusCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuperServiceDGPDefectEliminationAgreementType", propOrder = {
    "defectsEliminatedNotificationDate",
    "acceptedDefectsDate",
    "acceptedDefectsActChedFileId",
    "personId",
    "affairId",
    "letterId",
    "actNumber",
    "actId",
    "consentStatusCode",
    "defectsEliminationStatusCode"
})
@XmlRootElement(name = "SuperServiceDGPDefectEliminationAgreement")
public class SuperServiceDGPDefectEliminationAgreementType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar defectsEliminatedNotificationDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar acceptedDefectsDate;
    @XmlElement(required = true)
    protected String acceptedDefectsActChedFileId;
    @XmlElement(required = true)
    protected String personId;
    @XmlElement(required = true)
    protected String affairId;
    @XmlElement(required = true)
    protected String letterId;
    @XmlElement(required = true)
    protected String actNumber;
    @XmlElement(required = true)
    protected String actId;
    @XmlElement(required = true)
    protected String consentStatusCode;
    @XmlElement(required = true)
    protected String defectsEliminationStatusCode;

    /**
     * Gets the value of the defectsEliminatedNotificationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDefectsEliminatedNotificationDate() {
        return defectsEliminatedNotificationDate;
    }

    /**
     * Sets the value of the defectsEliminatedNotificationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDefectsEliminatedNotificationDate(XMLGregorianCalendar value) {
        this.defectsEliminatedNotificationDate = value;
    }

    /**
     * Gets the value of the acceptedDefectsDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAcceptedDefectsDate() {
        return acceptedDefectsDate;
    }

    /**
     * Sets the value of the acceptedDefectsDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAcceptedDefectsDate(XMLGregorianCalendar value) {
        this.acceptedDefectsDate = value;
    }

    /**
     * Gets the value of the acceptedDefectsActChedFileId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcceptedDefectsActChedFileId() {
        return acceptedDefectsActChedFileId;
    }

    /**
     * Sets the value of the acceptedDefectsActChedFileId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcceptedDefectsActChedFileId(String value) {
        this.acceptedDefectsActChedFileId = value;
    }

    /**
     * Gets the value of the personId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonId() {
        return personId;
    }

    /**
     * Sets the value of the personId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonId(String value) {
        this.personId = value;
    }

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
     * Gets the value of the actNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActNumber() {
        return actNumber;
    }

    /**
     * Sets the value of the actNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActNumber(String value) {
        this.actNumber = value;
    }

    /**
     * Gets the value of the actId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActId() {
        return actId;
    }

    /**
     * Sets the value of the actId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActId(String value) {
        this.actId = value;
    }

    /**
     * Gets the value of the consentStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConsentStatusCode() {
        return consentStatusCode;
    }

    /**
     * Sets the value of the consentStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConsentStatusCode(String value) {
        this.consentStatusCode = value;
    }

    /**
     * Gets the value of the defectsEliminationStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefectsEliminationStatusCode() {
        return defectsEliminationStatusCode;
    }

    /**
     * Sets the value of the defectsEliminationStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefectsEliminationStatusCode(String value) {
        this.defectsEliminationStatusCode = value;
    }

}
