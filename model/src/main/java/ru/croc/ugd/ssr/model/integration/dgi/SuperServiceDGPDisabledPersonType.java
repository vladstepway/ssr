
package ru.croc.ugd.ssr.model.integration.dgi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for SuperServiceDGPDisabledPersonType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SuperServiceDGPDisabledPersonType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="middleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="snils" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="wheelchair" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="adm_area" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="district" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="unom" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="flatNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="deleted" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuperServiceDGPDisabledPersonType", propOrder = {
    "lastName",
    "firstName",
    "middleName",
    "birthDate",
    "snils",
    "wheelchair",
    "address",
    "admArea",
    "district",
    "unom",
    "flatNumber",
    "deleted"
})
public class SuperServiceDGPDisabledPersonType {

    protected String lastName;
    protected String firstName;
    protected String middleName;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar birthDate;
    protected String snils;
    protected String wheelchair;
    @XmlElement(required = true)
    protected String address;
    @XmlElement(name = "adm_area")
    protected String admArea;
    protected String district;
    @XmlElement(required = true)
    protected String unom;
    @XmlElement(required = true)
    protected String flatNumber;
    protected boolean deleted;

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the middleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the value of the middleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleName(String value) {
        this.middleName = value;
    }

    /**
     * Gets the value of the birthDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the value of the birthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthDate(XMLGregorianCalendar value) {
        this.birthDate = value;
    }

    /**
     * Gets the value of the snils property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSnils() {
        return snils;
    }

    /**
     * Sets the value of the snils property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSnils(String value) {
        this.snils = value;
    }

    /**
     * Gets the value of the wheelchair property.
     * 
     */
    public String getWheelchair() {
        return wheelchair;
    }

    /**
     * Sets the value of the wheelchair property.
     * 
     */
    public void setWheelchair(String value) {
        this.wheelchair = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * Gets the value of the admArea property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdmArea() {
        return admArea;
    }

    /**
     * Sets the value of the admArea property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdmArea(String value) {
        this.admArea = value;
    }

    /**
     * Gets the value of the district property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDistrict() {
        return district;
    }

    /**
     * Sets the value of the district property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDistrict(String value) {
        this.district = value;
    }

    /**
     * Gets the value of the unom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnom() {
        return unom;
    }

    /**
     * Sets the value of the unom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnom(String value) {
        this.unom = value;
    }

    /**
     * Gets the value of the flatNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlatNumber() {
        return flatNumber;
    }

    /**
     * Sets the value of the flatNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlatNumber(String value) {
        this.flatNumber = value;
    }

    /**
     * Gets the value of the deleted property.
     * 
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Sets the value of the deleted property.
     * 
     */
    public void setDeleted(boolean value) {
        this.deleted = value;
    }

}
