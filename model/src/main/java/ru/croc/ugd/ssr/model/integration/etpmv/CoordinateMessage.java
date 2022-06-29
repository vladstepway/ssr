package ru.croc.ugd.ssr.model.integration.etpmv;

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
 *         &lt;element name="CoordinateDataMessage" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}CoordinateData" minOccurs="0"/>
 *         &lt;element name="Files" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}ArrayOfCoordinateFile" minOccurs="0"/>
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
    "coordinateDataMessage",
    "files"
})
@XmlRootElement(name = "CoordinateMessage", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
public class CoordinateMessage {

    @XmlElement(name = "CoordinateDataMessage", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
    protected CoordinateData coordinateDataMessage;
    @XmlElement(name = "Files", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
    protected ArrayOfCoordinateFile files;

    /**
     * Gets the value of the coordinateDataMessage property.
     * 
     * @return
     *     possible object is
     *     {@link CoordinateData }
     *     
     */
    public CoordinateData getCoordinateDataMessage() {
        return coordinateDataMessage;
    }

    /**
     * Sets the value of the coordinateDataMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoordinateData }
     *     
     */
    public void setCoordinateDataMessage(CoordinateData value) {
        this.coordinateDataMessage = value;
    }

    /**
     * Gets the value of the files property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCoordinateFile }
     *     
     */
    public ArrayOfCoordinateFile getFiles() {
        return files;
    }

    /**
     * Sets the value of the files property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCoordinateFile }
     *     
     */
    public void setFiles(ArrayOfCoordinateFile value) {
        this.files = value;
    }

}
