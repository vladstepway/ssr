//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.05.07 at 07:31:07 PM MSK 
//


package ru.croc.ugd.ssr.model.integration.dgi;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Тип для 1,2
 * 
 * <p>Java class for SuperServiceDGPAdministrativeDocumentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SuperServiceDGPAdministrativeDocumentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="person_id" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="affair_id" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="letter_id" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="administrativeDocumentLink" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="newFlats" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="newFlat" maxOccurs="unbounded"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="unom" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                             &lt;element name="cadnum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                             &lt;element name="flatNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="legalRepresentatives" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="snils" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuperServiceDGPAdministrativeDocumentType", propOrder = {
    "personId",
    "affairId",
    "letterId",
    "administrativeDocumentLink",
    "newFlats",
    "legalRepresentatives"
})
public class SuperServiceDGPAdministrativeDocumentType {

    @XmlElement(name = "person_id", required = true)
    protected String personId;
    @XmlElement(name = "affair_id", required = true)
    protected String affairId;
    @XmlElement(name = "letter_id", required = true)
    protected String letterId;
    protected String administrativeDocumentLink;
    protected SuperServiceDGPAdministrativeDocumentType.NewFlats newFlats;
    protected SuperServiceDGPAdministrativeDocumentType.LegalRepresentatives legalRepresentatives;

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
     * Gets the value of the administrativeDocumentLink property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAdministrativeDocumentLink() {
        return administrativeDocumentLink;
    }

    /**
     * Sets the value of the administrativeDocumentLink property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAdministrativeDocumentLink(String value) {
        this.administrativeDocumentLink = value;
    }

    /**
     * Gets the value of the newFlats property.
     *
     * @return
     *     possible object is
     *     {@link SuperServiceDGPAdministrativeDocumentType.NewFlats }
     *
     */
    public SuperServiceDGPAdministrativeDocumentType.NewFlats getNewFlats() {
        return newFlats;
    }

    /**
     * Sets the value of the newFlats property.
     *
     * @param value
     *     allowed object is
     *     {@link SuperServiceDGPAdministrativeDocumentType.NewFlats }
     *
     */
    public void setNewFlats(SuperServiceDGPAdministrativeDocumentType.NewFlats value) {
        this.newFlats = value;
    }

    /**
     * Gets the value of the legalRepresentatives property.
     *
     * @return
     *     possible object is
     *     {@link SuperServiceDGPAdministrativeDocumentType.LegalRepresentatives }
     *
     */
    public SuperServiceDGPAdministrativeDocumentType.LegalRepresentatives getLegalRepresentatives() {
        return legalRepresentatives;
    }

    /**
     * Sets the value of the legalRepresentatives property.
     *
     * @param value
     *     allowed object is
     *     {@link SuperServiceDGPAdministrativeDocumentType.LegalRepresentatives }
     *
     */
    public void setLegalRepresentatives(SuperServiceDGPAdministrativeDocumentType.LegalRepresentatives value) {
        this.legalRepresentatives = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="snils" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "snils"
    })
    public static class LegalRepresentatives {

        @XmlElement(required = true)
        protected List<String> snils;

        /**
         * Gets the value of the snils property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the snils property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSnils().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         *
         *
         */
        public List<String> getSnils() {
            if (snils == null) {
                snils = new ArrayList<String>();
            }
            return this.snils;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="newFlat" maxOccurs="unbounded"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="unom" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                   &lt;element name="cadnum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *                   &lt;element name="flatNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "newFlat"
    })
    public static class NewFlats {

        @XmlElement(required = true)
        protected List<SuperServiceDGPAdministrativeDocumentType.NewFlats.NewFlat> newFlat;

        /**
         * Gets the value of the newFlat property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the newFlat property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getNewFlat().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link SuperServiceDGPAdministrativeDocumentType.NewFlats.NewFlat }
         *
         *
         */
        public List<SuperServiceDGPAdministrativeDocumentType.NewFlats.NewFlat> getNewFlat() {
            if (newFlat == null) {
                newFlat = new ArrayList<SuperServiceDGPAdministrativeDocumentType.NewFlats.NewFlat>();
            }
            return this.newFlat;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;sequence&gt;
         *         &lt;element name="unom" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *         &lt;element name="cadnum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
         *         &lt;element name="flatNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
         *       &lt;/sequence&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "unom",
            "cadnum",
            "flatNumber"
        })
        public static class NewFlat {

            @XmlElement(required = true)
            protected String unom;
            protected String cadnum;
            @XmlElement(required = true)
            protected String flatNumber;

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
             * Gets the value of the cadnum property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCadnum() {
                return cadnum;
            }

            /**
             * Sets the value of the cadnum property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCadnum(String value) {
                this.cadnum = value;
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

        }

    }

}
