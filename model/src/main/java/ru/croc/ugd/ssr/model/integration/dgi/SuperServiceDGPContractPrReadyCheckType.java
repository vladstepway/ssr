
package ru.croc.ugd.ssr.model.integration.dgi;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SuperServiceDGPContractPrReadyCheckType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SuperServiceDGPContractPrReadyCheckType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SSOID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MessageText" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pdfFiles" type="{http://www.w3.org/2001/XMLSchema}base64Binary" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuperServiceDGPContractPrReadyCheckType", propOrder = {
    "ssoid",
    "messageText",
    "pdfFiles"
})
public class SuperServiceDGPContractPrReadyCheckType {

    @XmlElement(name = "SSOID", required = true)
    protected String ssoid;
    @XmlElement(name = "MessageText", required = true)
    protected String messageText;
    @XmlElement(required = true)
    protected List<byte[]> pdfFiles;

    /**
     * Gets the value of the ssoid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSSOID() {
        return ssoid;
    }

    /**
     * Sets the value of the ssoid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSSOID(String value) {
        this.ssoid = value;
    }

    /**
     * Gets the value of the messageText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageText() {
        return messageText;
    }

    /**
     * Sets the value of the messageText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageText(String value) {
        this.messageText = value;
    }

    /**
     * Gets the value of the pdfFiles property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pdfFiles property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPdfFiles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * byte[]
     * 
     */
    public List<byte[]> getPdfFiles() {
        if (pdfFiles == null) {
            pdfFiles = new ArrayList<byte[]>();
        }
        return this.pdfFiles;
    }

}
