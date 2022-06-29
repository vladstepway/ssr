package ru.croc.ugd.ssr.model.integration.dgi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SuperServiceDGPContractHandStatusType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SuperServiceDGPContractHandStatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="person_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="affair_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="order_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuperServiceDGPContractHandStatusType", propOrder = {
    "personId",
    "affairId",
    "orderId",
    "status"
})
@XmlRootElement(name = "SuperServiceDGPContractHandStatus")
public class SuperServiceDGPContractHandStatusType {

    @XmlElement(name = "person_id", required = true)
    protected String personId;
    @XmlElement(name = "affair_id", required = true)
    protected String affairId;
    @XmlElement(name = "order_id", required = true)
    protected String orderId;
    @XmlElement(name = "Status", required = true)
    protected String status;

    /**
     * Gets the value of the personId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonId() {
        return personId;
    }

    /**
     * Sets the value of the personId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonId(String value) {
        this.personId = value;
    }

    /**
     * Gets the value of the affairId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getAffairId() {
        return affairId;
    }

    /**
     * Sets the value of the affairId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAffairId(String value) {
        this.affairId = value;
    }

    /**
     * Gets the value of the orderId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Sets the value of the orderId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOrderId(String value) {
        this.orderId = value;
    }

    /**
     * Gets the value of the status property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStatus(String value) {
        this.status = value;
    }

}
