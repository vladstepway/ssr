
package ru.croc.ugd.ssr.model.integration.dgi;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for SuperServiceDGPDefectEliminationActType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SuperServiceDGPDefectEliminationActType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="flatNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="newUnom" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="defects" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="defect" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
 *         &lt;element name="signedActChedFileId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="affairId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="letterId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="actNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
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
@XmlType(name = "SuperServiceDGPDefectEliminationActType", propOrder = {
    "flatNumber",
    "newUnom",
    "defects",
    "signedActChedFileId",
    "personId",
    "affairId",
    "letterId",
    "actNumber",
    "filingDate",
    "actId"
})
@XmlRootElement(name = "SuperServiceDGPDefectEliminationAct")
public class SuperServiceDGPDefectEliminationActType {

    @XmlElement(required = true)
    protected String flatNumber;
    @XmlElement(required = true)
    protected String newUnom;
    protected SuperServiceDGPDefectEliminationActType.Defects defects;
    @XmlElement(required = true)
    protected String signedActChedFileId;
    @XmlElement(required = true)
    protected String personId;
    @XmlElement(required = true)
    protected String affairId;
    @XmlElement(required = true)
    protected String letterId;
    @XmlElement(required = true)
    protected String actNumber;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar filingDate;
    @XmlElement(required = true)
    protected String actId;

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
     * Gets the value of the newUnom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewUnom() {
        return newUnom;
    }

    /**
     * Sets the value of the newUnom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewUnom(String value) {
        this.newUnom = value;
    }

    /**
     * Gets the value of the defects property.
     * 
     * @return
     *     possible object is
     *     {@link SuperServiceDGPDefectEliminationActType.Defects }
     *     
     */
    public SuperServiceDGPDefectEliminationActType.Defects getDefects() {
        return defects;
    }

    /**
     * Sets the value of the defects property.
     * 
     * @param value
     *     allowed object is
     *     {@link SuperServiceDGPDefectEliminationActType.Defects }
     *     
     */
    public void setDefects(SuperServiceDGPDefectEliminationActType.Defects value) {
        this.defects = value;
    }

    /**
     * Gets the value of the signedActChedFileId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignedActChedFileId() {
        return signedActChedFileId;
    }

    /**
     * Sets the value of the signedActChedFileId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignedActChedFileId(String value) {
        this.signedActChedFileId = value;
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
     * Gets the value of the filingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFilingDate() {
        return filingDate;
    }

    /**
     * Sets the value of the filingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFilingDate(XMLGregorianCalendar value) {
        this.filingDate = value;
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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="defect" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "defect"
    })
    public static class Defects {

        @XmlElement(required = true)
        protected List<SuperServiceDGPDefectEliminationActType.Defects.Defect> defect;

        /**
         * Gets the value of the defect property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the defect property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDefect().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link SuperServiceDGPDefectEliminationActType.Defects.Defect }
         * 
         * 
         */
        public List<SuperServiceDGPDefectEliminationActType.Defects.Defect> getDefect() {
            if (defect == null) {
                defect = new ArrayList<SuperServiceDGPDefectEliminationActType.Defects.Defect>();
            }
            return this.defect;
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
         *         &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "flatElement",
            "description"
        })
        public static class Defect {

            @XmlElement(required = true)
            protected String flatElement;
            @XmlElement(required = true)
            protected String description;

            /**
             * Gets the value of the flatElement property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFlatElement() {
                return flatElement;
            }

            /**
             * Sets the value of the flatElement property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFlatElement(String value) {
                this.flatElement = value;
            }

            /**
             * Gets the value of the description property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDescription() {
                return description;
            }

            /**
             * Sets the value of the description property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDescription(String value) {
                this.description = value;
            }

        }

    }

}
