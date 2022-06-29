
package ru.croc.ugd.ssr.model.integration.dgi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SuperServiceDGPFlatLayoutType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SuperServiceDGPFlatLayoutType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uinConstruction" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="adm_area" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="district" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="flat_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="entrance" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="floor" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="floorFlatNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="roomsCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="totalSquare" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="s_all" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="s_gil" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="mgn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="space_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuperServiceDGPFlatLayoutType", propOrder = {
    "uinConstruction",
    "address",
    "admArea",
    "district",
    "flatType",
    "entrance",
    "floor",
    "floorFlatNumber",
    "roomsCount",
    "totalSquare",
    "sAll",
    "sGil",
    "mgn",
    "spaceId"
})
public class SuperServiceDGPFlatLayoutType {

    @XmlElement(required = true)
    protected String uinConstruction;
    @XmlElement(required = true)
    protected String address;
    @XmlElement(name = "adm_area", required = true)
    protected String admArea;
    @XmlElement(required = true)
    protected String district;
    @XmlElement(name = "flat_type", required = true)
    protected String flatType;
    @XmlElement(required = true)
    protected String entrance;
    protected int floor;
    protected int floorFlatNumber;
    protected int roomsCount;
    protected double totalSquare;
    @XmlElement(name = "s_all")
    protected double sAll;
    @XmlElement(name = "s_gil")
    protected double sGil;
    @XmlElement(required = true)
    protected String mgn;
    @XmlElement(name = "space_id", required = true)
    protected String spaceId;

    /**
     * Gets the value of the uinConstruction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUinConstruction() {
        return uinConstruction;
    }

    /**
     * Sets the value of the uinConstruction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUinConstruction(String value) {
        this.uinConstruction = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * Gets the value of the admArea property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdmArea() {
        return admArea;
    }

    /**
     * Sets the value of the admArea property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdmArea(String value) {
        this.admArea = value;
    }

    /**
     * Gets the value of the district property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDistrict() {
        return district;
    }

    /**
     * Sets the value of the district property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDistrict(String value) {
        this.district = value;
    }

    /**
     * Gets the value of the flatType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlatType() {
        return flatType;
    }

    /**
     * Sets the value of the flatType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlatType(String value) {
        this.flatType = value;
    }

    /**
     * Gets the value of the entrance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntrance() {
        return entrance;
    }

    /**
     * Sets the value of the entrance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntrance(String value) {
        this.entrance = value;
    }

    /**
     * Gets the value of the floor property.
     * 
     */
    public int getFloor() {
        return floor;
    }

    /**
     * Sets the value of the floor property.
     * 
     */
    public void setFloor(int value) {
        this.floor = value;
    }

    /**
     * Gets the value of the floorFlatNumber property.
     * 
     */
    public int getFloorFlatNumber() {
        return floorFlatNumber;
    }

    /**
     * Sets the value of the floorFlatNumber property.
     * 
     */
    public void setFloorFlatNumber(int value) {
        this.floorFlatNumber = value;
    }

    /**
     * Gets the value of the roomsCount property.
     * 
     */
    public int getRoomsCount() {
        return roomsCount;
    }

    /**
     * Sets the value of the roomsCount property.
     * 
     */
    public void setRoomsCount(int value) {
        this.roomsCount = value;
    }

    /**
     * Gets the value of the totalSquare property.
     * 
     */
    public double getTotalSquare() {
        return totalSquare;
    }

    /**
     * Sets the value of the totalSquare property.
     * 
     */
    public void setTotalSquare(double value) {
        this.totalSquare = value;
    }

    /**
     * Gets the value of the sAll property.
     * 
     */
    public double getSAll() {
        return sAll;
    }

    /**
     * Sets the value of the sAll property.
     * 
     */
    public void setSAll(double value) {
        this.sAll = value;
    }

    /**
     * Gets the value of the sGil property.
     * 
     */
    public double getSGil() {
        return sGil;
    }

    /**
     * Sets the value of the sGil property.
     * 
     */
    public void setSGil(double value) {
        this.sGil = value;
    }

    /**
     * Gets the value of the mgn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMgn() {
        return mgn;
    }

    /**
     * Sets the value of the mgn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMgn(String value) {
        this.mgn = value;
    }

    /**
     * Gets the value of the spaceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpaceId() {
        return spaceId;
    }

    /**
     * Sets the value of the spaceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpaceId(String value) {
        this.spaceId = value;
    }

}
