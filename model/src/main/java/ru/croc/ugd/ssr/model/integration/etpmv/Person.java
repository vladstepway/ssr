
package ru.croc.ugd.ssr.model.integration.etpmv;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ����
 * 
 * <p>Java class for Person complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Person">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LastName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FirstName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MiddleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="JobTitle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsiId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SsoId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Person", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", propOrder = {
    "lastName",
    "firstName",
    "middleName",
    "jobTitle",
    "phone",
    "email",
    "isiId",
    "ssoId",
    "mdmId"
})
public class Person {

    @XmlElement(name = "LastName", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", required = true)
    protected String lastName;
    @XmlElement(name = "FirstName", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", required = true)
    protected String firstName;
    @XmlElement(name = "MiddleName", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
    protected String middleName;
    @XmlElement(name = "JobTitle", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", required = true, nillable = true)
    protected String jobTitle;
    @XmlElement(name = "Phone", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
    protected String phone;
    @XmlElement(name = "Email", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
    protected String email;
    @XmlElement(name = "IsiId", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
    protected String isiId;
    @XmlElement(name = "SsoId", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
    protected String ssoId;
    @XmlElement(name = "MdmId", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
    protected String mdmId;

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
     * Gets the value of the jobTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * Sets the value of the jobTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJobTitle(String value) {
        this.jobTitle = value;
    }

    /**
     * Gets the value of the phone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhone(String value) {
        this.phone = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the isiId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsiId() {
        return isiId;
    }

    /**
     * Sets the value of the isiId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsiId(String value) {
        this.isiId = value;
    }

    /**
     * Gets the value of the ssoId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSsoId() {
        return ssoId;
    }

    /**
     * Sets the value of the ssoId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSsoId(String value) {
        this.ssoId = value;
    }

    /**
     * Gets the value of the mdmId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMdmId() {
        return mdmId;
    }

    /**
     * Sets the value of the mdmId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMdmId(String value) {
        this.mdmId = value;
    }

}
