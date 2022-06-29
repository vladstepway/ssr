
package ru.croc.ugd.ssr.model.integration.dgi;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="SuperServiceDGPDisabledPerson" type="{}SuperServiceDGPDisabledPersonType" maxOccurs="unbounded"/>
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
    "superServiceDGPDisabledPerson"
})
@XmlRootElement(name = "SuperServiceDGPDisabledPersons")
public class SuperServiceDGPDisabledPersons {

    @XmlElement(name = "SuperServiceDGPDisabledPerson", required = true)
    protected List<SuperServiceDGPDisabledPersonType> superServiceDGPDisabledPerson;

    /**
     * Gets the value of the superServiceDGPDisabledPerson property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the superServiceDGPDisabledPerson property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSuperServiceDGPDisabledPerson().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SuperServiceDGPDisabledPersonType }
     * 
     * 
     */
    public List<SuperServiceDGPDisabledPersonType> getSuperServiceDGPDisabledPerson() {
        if (superServiceDGPDisabledPerson == null) {
            superServiceDGPDisabledPerson = new ArrayList<SuperServiceDGPDisabledPersonType>();
        }
        return this.superServiceDGPDisabledPerson;
    }

}
