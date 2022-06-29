
package ru.croc.ugd.ssr.model.integration.dgi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SuperServiceDGPContractPrReadyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SuperServiceDGPContractPrReadyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="person_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="affair_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="order_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pdfContract" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pdfProposal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contractType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contractNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractDateEnd" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="letter_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuperServiceDGPContractPrReadyType", propOrder = {
    "personId",
    "affairId",
    "orderId",
    "pdfContract",
    "pdfProposal",
    "contractStatus",
    "contractType",
    "contractNumber",
    "contractDateEnd",
    "letterId"
})
@XmlRootElement(name = "SuperServiceDGPContractPrReady")
public class SuperServiceDGPContractPrReadyType {

    @XmlElement(name = "person_id", required = true)
    protected String personId;
    @XmlElement(name = "affair_id", required = true)
    protected String affairId;
    @XmlElement(name = "order_id", required = true)
    protected String orderId;
    @XmlElement(required = true)
    protected String pdfContract;
    protected String pdfProposal;
    @XmlElement(required = true)
    protected String contractStatus;
    @XmlElement(required = true)
    protected String contractType;
    protected String contractNumber;
    protected String contractDateEnd;
    @XmlElement(name = "letter_id")
    protected String letterId;

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
     * Gets the value of the pdfContract property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPdfContract() {
        return pdfContract;
    }

    /**
     * Sets the value of the pdfContract property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPdfContract(String value) {
        this.pdfContract = value;
    }

    /**
     * Gets the value of the pdfProposal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPdfProposal() {
        return pdfProposal;
    }

    /**
     * Sets the value of the pdfProposal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPdfProposal(String value) {
        this.pdfProposal = value;
    }

    /**
     * Gets the value of the contractStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractStatus() {
        return contractStatus;
    }

    /**
     * Sets the value of the contractStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractStatus(String value) {
        this.contractStatus = value;
    }

    /**
     * Gets the value of the contractType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractType() {
        return contractType;
    }

    /**
     * Sets the value of the contractType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractType(String value) {
        this.contractType = value;
    }

    /**
     * Gets the value of the contractNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractNumber() {
        return contractNumber;
    }

    /**
     * Sets the value of the contractNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractNumber(String value) {
        this.contractNumber = value;
    }

    /**
     * Gets the value of the contractDateEnd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractDateEnd() {
        return contractDateEnd;
    }

    /**
     * Sets the value of the contractDateEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractDateEnd(String value) {
        this.contractDateEnd = value;
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

}
