
package ru.croc.ugd.ssr.model.integration.dgi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for SuperServiceDGPDefectEliminationTransferType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SuperServiceDGPDefectEliminationTransferType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="oldPlannedDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="newPlannedDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="personId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="affairId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="letterId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="actNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuperServiceDGPDefectEliminationTransferType", propOrder = {
    "oldPlannedDate",
    "newPlannedDate",
    "personId",
    "affairId",
    "letterId",
    "actNumber",
    "actId"
})
@XmlRootElement(name = "SuperServiceDGPDefectEliminationTransfer")
public class SuperServiceDGPDefectEliminationTransferType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar oldPlannedDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar newPlannedDate;
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

    /**
     * Gets the value of the oldPlannedDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOldPlannedDate() {
        return oldPlannedDate;
    }

    /**
     * Sets the value of the oldPlannedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOldPlannedDate(XMLGregorianCalendar value) {
        this.oldPlannedDate = value;
    }

    /**
     * Gets the value of the newPlannedDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getNewPlannedDate() {
        return newPlannedDate;
    }

    /**
     * Sets the value of the newPlannedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setNewPlannedDate(XMLGregorianCalendar value) {
        this.newPlannedDate = value;
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

}
