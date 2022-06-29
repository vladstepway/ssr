
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
 *         &lt;element name="CoordinateTaskDataMessages" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}ArrayOfCoordinateTaskData" minOccurs="0"/>
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
    "coordinateTaskDataMessages",
    "files"
})
@XmlRootElement(name = "SendTasksMessage", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
public class SendTasksMessage {

    @XmlElement(name = "CoordinateTaskDataMessages", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
    protected ArrayOfCoordinateTaskData coordinateTaskDataMessages;
    @XmlElement(name = "Files", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
    protected ArrayOfCoordinateFile files;

    /**
     * Gets the value of the coordinateTaskDataMessages property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCoordinateTaskData }
     *     
     */
    public ArrayOfCoordinateTaskData getCoordinateTaskDataMessages() {
        return coordinateTaskDataMessages;
    }

    /**
     * Sets the value of the coordinateTaskDataMessages property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCoordinateTaskData }
     *     
     */
    public void setCoordinateTaskDataMessages(ArrayOfCoordinateTaskData value) {
        this.coordinateTaskDataMessages = value;
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
