package ru.croc.ugd.ssr.model.integration.dgi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="SuperServiceDGPContractIssueStatus" type="{}SuperServiceDGPContractIssueStatusType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "superServiceDGPContractIssueStatus"
})
@XmlRootElement(name = "SuperServiceDGPContractsIssueStatus")
public class SuperServiceDGPContractsIssueStatus {

    @XmlElement(name = "SuperServiceDGPContractIssueStatus", required = true)
    protected SuperServiceDGPContractIssueStatusType superServiceDGPContractIssueStatus;

    /**
     * Gets the value of the superServiceDGPContractIssueStatus property.
     *
     * @return possible object is
     * {@link SuperServiceDGPContractIssueStatusType }
     */
    public SuperServiceDGPContractIssueStatusType getSuperServiceDGPContractIssueStatus() {
        return superServiceDGPContractIssueStatus;
    }

    /**
     * Sets the value of the superServiceDGPContractIssueStatus property.
     *
     * @param value allowed object is
     *              {@link SuperServiceDGPContractIssueStatusType }
     */
    public void setSuperServiceDGPContractIssueStatus(SuperServiceDGPContractIssueStatusType value) {
        this.superServiceDGPContractIssueStatus = value;
    }

}
