package ru.croc.ugd.ssr.model.integration.etpmv;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfCoordinateFileReference complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCoordinateFileReference">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CoordinateFileReference" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}CoordinateFileReference" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCoordinateFileReference", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", propOrder = {
    "coordinateFileReference"
})
public class ArrayOfCoordinateFileReference {

    @XmlElement(name = "CoordinateFileReference", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", nillable = true)
    protected List<CoordinateFileReference> coordinateFileReference;

    /**
     * Gets the value of the coordinateFileReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the coordinateFileReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCoordinateFileReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CoordinateFileReference }
     * 
     * 
     */
    public List<CoordinateFileReference> getCoordinateFileReference() {
        if (coordinateFileReference == null) {
            coordinateFileReference = new ArrayList<CoordinateFileReference>();
        }
        return this.coordinateFileReference;
    }

}
