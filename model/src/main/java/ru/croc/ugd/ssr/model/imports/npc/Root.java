package ru.croc.ugd.ssr.model.imports.npc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element name="region" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="kvartal" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="snos_buildings">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="snos_building" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="snos_flats">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;sequence>
 *                                                           &lt;element name="snos_flat" maxOccurs="unbounded" minOccurs="0">
 *                                                             &lt;complexType>
 *                                                               &lt;complexContent>
 *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                   &lt;sequence>
 *                                                                     &lt;element name="snos_lic" maxOccurs="unbounded" minOccurs="0">
 *                                                                       &lt;complexType>
 *                                                                         &lt;simpleContent>
 *                                                                           &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                                                                             &lt;attribute name="fls" type="{http://www.w3.org/2001/XMLSchema}long" />
 *                                                                             &lt;attribute name="date_reg" type="{http://www.w3.org/2001/XMLSchema}date" />
 *                                                                             &lt;attribute name="kod_plat" type="{http://www.w3.org/2001/XMLSchema}long" />
 *                                                                             &lt;attribute name="date_fls" type="{http://www.w3.org/2001/XMLSchema}date" />
 *                                                                             &lt;attribute name="type_reg" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                                             &lt;attribute name="sobstv" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                                             &lt;attribute name="osnovanie" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                                             &lt;attribute name="sobstvennik" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                                             &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                                                                             &lt;attribute name="nom_komn" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                                                           &lt;/extension>
 *                                                                         &lt;/simpleContent>
 *                                                                       &lt;/complexType>
 *                                                                     &lt;/element>
 *                                                                     &lt;element name="flat_graf" maxOccurs="unbounded" minOccurs="0">
 *                                                                       &lt;complexType>
 *                                                                         &lt;simpleContent>
 *                                                                           &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                                                                             &lt;attribute name="kvq" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                                                             &lt;attribute name="kmq" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                                                           &lt;/extension>
 *                                                                         &lt;/simpleContent>
 *                                                                       &lt;/complexType>
 *                                                                     &lt;/element>
 *                                                                   &lt;/sequence>
 *                                                                   &lt;attribute name="kvnom" type="{http://www.w3.org/2001/XMLSchema}short" />
 *                                                                   &lt;attribute name="gpl" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                                                                   &lt;attribute name="kmq" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                                                   &lt;attribute name="opl" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                                                                   &lt;attribute name="ppl" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                                                                   &lt;attribute name="unkv" type="{http://www.w3.org/2001/XMLSchema}short" />
 *                                                                   &lt;attribute name="disabled" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                                                   &lt;attribute name="queued" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                                                   &lt;attribute name="resident_num" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                                                   &lt;attribute name="kvnom_floor" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                                                   &lt;attribute name="entrance" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                                                   &lt;attribute name="owner" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                                   &lt;attribute name="pl_kuh" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                                                                   &lt;attribute name="is_kommun" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                                   &lt;attribute name="raschet_kv" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                                                   &lt;attribute name="ruchnoy_vvod" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                                   &lt;attribute name="status_kv" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                                                   &lt;attribute name="type_kv" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                                   &lt;attribute name="floor" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                                                   &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                                                                 &lt;/restriction>
 *                                                               &lt;/complexContent>
 *                                                             &lt;/complexType>
 *                                                           &lt;/element>
 *                                                         &lt;/sequence>
 *                                                       &lt;/restriction>
 *                                                     &lt;/complexContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                               &lt;/sequence>
 *                                               &lt;attribute name="npc_cod" type="{http://www.w3.org/2001/XMLSchema}short" />
 *                                               &lt;attribute name="address" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="floor_max" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                               &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="god_p_dgp" type="{http://www.w3.org/2001/XMLSchema}short" />
 *                                               &lt;attribute name="hab_square" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                                               &lt;attribute name="unhab_square" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                                               &lt;attribute name="is_disabled" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="is_unhab" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="mat_sten" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="monument" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="proc_izn" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                               &lt;attribute name="sost_dgp" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="unom" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                               &lt;attribute name="god_ust_izn" type="{http://www.w3.org/2001/XMLSchema}short" />
 *                                               &lt;attribute name="proc_za" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                                               &lt;attribute name="proc_protiv" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                                               &lt;attribute name="kadastr" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="category" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="lift" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                               &lt;attribute name="podezd" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                               &lt;attribute name="nar_obm_pl" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                                               &lt;attribute name="seria" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="starts_tep">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="start_tep" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="buildobjects">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;sequence>
 *                                                           &lt;element name="buildobject">
 *                                                             &lt;complexType>
 *                                                               &lt;simpleContent>
 *                                                                 &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                                                                   &lt;attribute name="buildobject_id" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                                                   &lt;attribute name="bld_address" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                                   &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                                   &lt;attribute name="s_zhilaya" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                                                                   &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                                                                   &lt;attribute name="ugd_guid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                                 &lt;/extension>
 *                                                               &lt;/simpleContent>
 *                                                             &lt;/complexType>
 *                                                           &lt;/element>
 *                                                         &lt;/sequence>
 *                                                       &lt;/restriction>
 *                                                     &lt;/complexContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                               &lt;/sequence>
 *                                               &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}short" />
 *                                               &lt;attribute name="categorija" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                               &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="is_actual" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="is_invest" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="object_name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="s_zhilaya" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                                               &lt;attribute name="region_code" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                                               &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                                               &lt;attribute name="plan_year" type="{http://www.w3.org/2001/XMLSchema}short" />
 *                                               &lt;attribute name="start_date" type="{http://www.w3.org/2001/XMLSchema}date" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="npc_code" type="{http://www.w3.org/2001/XMLSchema}short" />
 *                           &lt;attribute name="project_name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="is_temporary" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="region_code" type="{http://www.w3.org/2001/XMLSchema}short" />
 *                 &lt;attribute name="short" type="{http://www.w3.org/2001/XMLSchema}short" />
 *                 &lt;attribute name="okrug_code" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                 &lt;attribute name="okrug" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "region"
})
@XmlRootElement(name = "root")
public class Root {

    protected List<Root.Region> region;

    /**
     * Gets the value of the region property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the region property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegion().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Root.Region }
     *
     *
     */
    public List<Root.Region> getRegion() {
        if (region == null) {
            region = new ArrayList<Root.Region>();
        }
        return this.region;
    }


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
     *         &lt;element name="kvartal" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="snos_buildings">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="snos_building" maxOccurs="unbounded" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="snos_flats">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;sequence>
     *                                                 &lt;element name="snos_flat" maxOccurs="unbounded" minOccurs="0">
     *                                                   &lt;complexType>
     *                                                     &lt;complexContent>
     *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                         &lt;sequence>
     *                                                           &lt;element name="snos_lic" maxOccurs="unbounded" minOccurs="0">
     *                                                             &lt;complexType>
     *                                                               &lt;simpleContent>
     *                                                                 &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                                                                   &lt;attribute name="fls" type="{http://www.w3.org/2001/XMLSchema}long" />
     *                                                                   &lt;attribute name="date_reg" type="{http://www.w3.org/2001/XMLSchema}date" />
     *                                                                   &lt;attribute name="kod_plat" type="{http://www.w3.org/2001/XMLSchema}long" />
     *                                                                   &lt;attribute name="date_fls" type="{http://www.w3.org/2001/XMLSchema}date" />
     *                                                                   &lt;attribute name="type_reg" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                                                   &lt;attribute name="sobstv" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                                                   &lt;attribute name="osnovanie" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                                                   &lt;attribute name="sobstvennik" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                                                   &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
     *                                                                   &lt;attribute name="nom_komn" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                                                 &lt;/extension>
     *                                                               &lt;/simpleContent>
     *                                                             &lt;/complexType>
     *                                                           &lt;/element>
     *                                                           &lt;element name="flat_graf" maxOccurs="unbounded" minOccurs="0">
     *                                                             &lt;complexType>
     *                                                               &lt;simpleContent>
     *                                                                 &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                                                                   &lt;attribute name="kvq" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                                                   &lt;attribute name="kmq" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                                                 &lt;/extension>
     *                                                               &lt;/simpleContent>
     *                                                             &lt;/complexType>
     *                                                           &lt;/element>
     *                                                         &lt;/sequence>
     *                                                         &lt;attribute name="kvnom" type="{http://www.w3.org/2001/XMLSchema}short" />
     *                                                         &lt;attribute name="gpl" type="{http://www.w3.org/2001/XMLSchema}float" />
     *                                                         &lt;attribute name="kmq" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                                         &lt;attribute name="opl" type="{http://www.w3.org/2001/XMLSchema}float" />
     *                                                         &lt;attribute name="ppl" type="{http://www.w3.org/2001/XMLSchema}float" />
     *                                                         &lt;attribute name="unkv" type="{http://www.w3.org/2001/XMLSchema}short" />
     *                                                         &lt;attribute name="disabled" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                                         &lt;attribute name="queued" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                                         &lt;attribute name="resident_num" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                                         &lt;attribute name="kvnom_floor" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                                         &lt;attribute name="entrance" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                                         &lt;attribute name="owner" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                                         &lt;attribute name="pl_kuh" type="{http://www.w3.org/2001/XMLSchema}float" />
     *                                                         &lt;attribute name="is_kommun" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                                         &lt;attribute name="raschet_kv" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                                         &lt;attribute name="ruchnoy_vvod" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                                         &lt;attribute name="status_kv" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                                         &lt;attribute name="type_kv" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                                         &lt;attribute name="floor" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                                         &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
     *                                                       &lt;/restriction>
     *                                                     &lt;/complexContent>
     *                                                   &lt;/complexType>
     *                                                 &lt;/element>
     *                                               &lt;/sequence>
     *                                             &lt;/restriction>
     *                                           &lt;/complexContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                     &lt;/sequence>
     *                                     &lt;attribute name="npc_cod" type="{http://www.w3.org/2001/XMLSchema}short" />
     *                                     &lt;attribute name="address" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="floor_max" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                     &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="god_p_dgp" type="{http://www.w3.org/2001/XMLSchema}short" />
     *                                     &lt;attribute name="hab_square" type="{http://www.w3.org/2001/XMLSchema}float" />
     *                                     &lt;attribute name="unhab_square" type="{http://www.w3.org/2001/XMLSchema}float" />
     *                                     &lt;attribute name="is_disabled" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="is_unhab" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="mat_sten" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="monument" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="proc_izn" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                     &lt;attribute name="sost_dgp" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="unom" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                     &lt;attribute name="god_ust_izn" type="{http://www.w3.org/2001/XMLSchema}short" />
     *                                     &lt;attribute name="proc_za" type="{http://www.w3.org/2001/XMLSchema}float" />
     *                                     &lt;attribute name="proc_protiv" type="{http://www.w3.org/2001/XMLSchema}float" />
     *                                     &lt;attribute name="kadastr" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="category" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="lift" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                     &lt;attribute name="podezd" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                     &lt;attribute name="nar_obm_pl" type="{http://www.w3.org/2001/XMLSchema}float" />
     *                                     &lt;attribute name="seria" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="starts_tep">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="start_tep" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="buildobjects">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;sequence>
     *                                                 &lt;element name="buildobject">
     *                                                   &lt;complexType>
     *                                                     &lt;simpleContent>
     *                                                       &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                                                         &lt;attribute name="buildobject_id" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                                         &lt;attribute name="bld_address" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                                         &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                                         &lt;attribute name="s_zhilaya" type="{http://www.w3.org/2001/XMLSchema}float" />
     *                                                         &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
     *                                                         &lt;attribute name="ugd_guid" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                                       &lt;/extension>
     *                                                     &lt;/simpleContent>
     *                                                   &lt;/complexType>
     *                                                 &lt;/element>
     *                                               &lt;/sequence>
     *                                             &lt;/restriction>
     *                                           &lt;/complexContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                     &lt;/sequence>
     *                                     &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}short" />
     *                                     &lt;attribute name="categorija" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                     &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="is_actual" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="is_invest" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="object_name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="s_zhilaya" type="{http://www.w3.org/2001/XMLSchema}float" />
     *                                     &lt;attribute name="region_code" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *                                     &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
     *                                     &lt;attribute name="plan_year" type="{http://www.w3.org/2001/XMLSchema}short" />
     *                                     &lt;attribute name="start_date" type="{http://www.w3.org/2001/XMLSchema}date" />
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="npc_code" type="{http://www.w3.org/2001/XMLSchema}short" />
     *                 &lt;attribute name="project_name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="is_temporary" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="region_code" type="{http://www.w3.org/2001/XMLSchema}short" />
     *       &lt;attribute name="short" type="{http://www.w3.org/2001/XMLSchema}short" />
     *       &lt;attribute name="okrug_code" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *       &lt;attribute name="okrug" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "kvartal"
    })
    public static class Region {

        protected List<Root.Region.Kvartal> kvartal;
        @XmlAttribute(name = "name")
        protected String name;
        @XmlAttribute(name = "region_code")
        protected Short regionCode;
        @XmlAttribute(name = "short")
        protected Short _short;
        @XmlAttribute(name = "okrug_code")
        protected Byte okrugCode;
        @XmlAttribute(name = "okrug")
        protected String okrug;

        /**
         * Gets the value of the kvartal property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the kvartal property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getKvartal().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Root.Region.Kvartal }
         *
         *
         */
        public List<Root.Region.Kvartal> getKvartal() {
            if (kvartal == null) {
                kvartal = new ArrayList<Root.Region.Kvartal>();
            }
            return this.kvartal;
        }

        /**
         * Gets the value of the name property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the regionCode property.
         *
         * @return
         *     possible object is
         *     {@link Short }
         *
         */
        public Short getRegionCode() {
            return regionCode;
        }

        /**
         * Sets the value of the regionCode property.
         *
         * @param value
         *     allowed object is
         *     {@link Short }
         *
         */
        public void setRegionCode(Short value) {
            this.regionCode = value;
        }

        /**
         * Gets the value of the short property.
         *
         * @return
         *     possible object is
         *     {@link Short }
         *
         */
        public Short getShort() {
            return _short;
        }

        /**
         * Sets the value of the short property.
         *
         * @param value
         *     allowed object is
         *     {@link Short }
         *
         */
        public void setShort(Short value) {
            this._short = value;
        }

        /**
         * Gets the value of the okrugCode property.
         *
         * @return
         *     possible object is
         *     {@link Byte }
         *
         */
        public Byte getOkrugCode() {
            return okrugCode;
        }

        /**
         * Sets the value of the okrugCode property.
         *
         * @param value
         *     allowed object is
         *     {@link Byte }
         *
         */
        public void setOkrugCode(Byte value) {
            this.okrugCode = value;
        }

        /**
         * Gets the value of the okrug property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getOkrug() {
            return okrug;
        }

        /**
         * Sets the value of the okrug property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setOkrug(String value) {
            this.okrug = value;
        }


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
         *         &lt;element name="snos_buildings">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="snos_building" maxOccurs="unbounded" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="snos_flats">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;sequence>
         *                                       &lt;element name="snos_flat" maxOccurs="unbounded" minOccurs="0">
         *                                         &lt;complexType>
         *                                           &lt;complexContent>
         *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                               &lt;sequence>
         *                                                 &lt;element name="snos_lic" maxOccurs="unbounded" minOccurs="0">
         *                                                   &lt;complexType>
         *                                                     &lt;simpleContent>
         *                                                       &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *                                                         &lt;attribute name="fls" type="{http://www.w3.org/2001/XMLSchema}long" />
         *                                                         &lt;attribute name="date_reg" type="{http://www.w3.org/2001/XMLSchema}date" />
         *                                                         &lt;attribute name="kod_plat" type="{http://www.w3.org/2001/XMLSchema}long" />
         *                                                         &lt;attribute name="date_fls" type="{http://www.w3.org/2001/XMLSchema}date" />
         *                                                         &lt;attribute name="type_reg" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                                         &lt;attribute name="sobstv" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                                         &lt;attribute name="osnovanie" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                                         &lt;attribute name="sobstvennik" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                                         &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
         *                                                         &lt;attribute name="nom_komn" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                                                       &lt;/extension>
         *                                                     &lt;/simpleContent>
         *                                                   &lt;/complexType>
         *                                                 &lt;/element>
         *                                                 &lt;element name="flat_graf" maxOccurs="unbounded" minOccurs="0">
         *                                                   &lt;complexType>
         *                                                     &lt;simpleContent>
         *                                                       &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *                                                         &lt;attribute name="kvq" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                                                         &lt;attribute name="kmq" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                                                       &lt;/extension>
         *                                                     &lt;/simpleContent>
         *                                                   &lt;/complexType>
         *                                                 &lt;/element>
         *                                               &lt;/sequence>
         *                                               &lt;attribute name="kvnom" type="{http://www.w3.org/2001/XMLSchema}short" />
         *                                               &lt;attribute name="gpl" type="{http://www.w3.org/2001/XMLSchema}float" />
         *                                               &lt;attribute name="kmq" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                                               &lt;attribute name="opl" type="{http://www.w3.org/2001/XMLSchema}float" />
         *                                               &lt;attribute name="ppl" type="{http://www.w3.org/2001/XMLSchema}float" />
         *                                               &lt;attribute name="unkv" type="{http://www.w3.org/2001/XMLSchema}short" />
         *                                               &lt;attribute name="disabled" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                                               &lt;attribute name="queued" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                                               &lt;attribute name="resident_num" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                                               &lt;attribute name="kvnom_floor" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                                               &lt;attribute name="entrance" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                                               &lt;attribute name="owner" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                               &lt;attribute name="pl_kuh" type="{http://www.w3.org/2001/XMLSchema}float" />
         *                                               &lt;attribute name="is_kommun" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                               &lt;attribute name="raschet_kv" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                                               &lt;attribute name="ruchnoy_vvod" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                               &lt;attribute name="status_kv" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                                               &lt;attribute name="type_kv" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                               &lt;attribute name="floor" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                                               &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
         *                                             &lt;/restriction>
         *                                           &lt;/complexContent>
         *                                         &lt;/complexType>
         *                                       &lt;/element>
         *                                     &lt;/sequence>
         *                                   &lt;/restriction>
         *                                 &lt;/complexContent>
         *                               &lt;/complexType>
         *                             &lt;/element>
         *                           &lt;/sequence>
         *                           &lt;attribute name="npc_cod" type="{http://www.w3.org/2001/XMLSchema}short" />
         *                           &lt;attribute name="address" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="floor_max" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                           &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="god_p_dgp" type="{http://www.w3.org/2001/XMLSchema}short" />
         *                           &lt;attribute name="hab_square" type="{http://www.w3.org/2001/XMLSchema}float" />
         *                           &lt;attribute name="unhab_square" type="{http://www.w3.org/2001/XMLSchema}float" />
         *                           &lt;attribute name="is_disabled" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="is_unhab" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="mat_sten" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="monument" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="proc_izn" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                           &lt;attribute name="sost_dgp" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="unom" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                           &lt;attribute name="god_ust_izn" type="{http://www.w3.org/2001/XMLSchema}short" />
         *                           &lt;attribute name="proc_za" type="{http://www.w3.org/2001/XMLSchema}float" />
         *                           &lt;attribute name="proc_protiv" type="{http://www.w3.org/2001/XMLSchema}float" />
         *                           &lt;attribute name="kadastr" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="category" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="lift" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                           &lt;attribute name="podezd" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                           &lt;attribute name="nar_obm_pl" type="{http://www.w3.org/2001/XMLSchema}float" />
         *                           &lt;attribute name="seria" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="starts_tep">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="start_tep" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="buildobjects">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;sequence>
         *                                       &lt;element name="buildobject">
         *                                         &lt;complexType>
         *                                           &lt;simpleContent>
         *                                             &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *                                               &lt;attribute name="buildobject_id" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                                               &lt;attribute name="bld_address" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                               &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                               &lt;attribute name="s_zhilaya" type="{http://www.w3.org/2001/XMLSchema}float" />
         *                                               &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
         *                                               &lt;attribute name="ugd_guid" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                             &lt;/extension>
         *                                           &lt;/simpleContent>
         *                                         &lt;/complexType>
         *                                       &lt;/element>
         *                                     &lt;/sequence>
         *                                   &lt;/restriction>
         *                                 &lt;/complexContent>
         *                               &lt;/complexType>
         *                             &lt;/element>
         *                           &lt;/sequence>
         *                           &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}short" />
         *                           &lt;attribute name="categorija" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                           &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="is_actual" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="is_invest" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="object_name" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="s_zhilaya" type="{http://www.w3.org/2001/XMLSchema}float" />
         *                           &lt;attribute name="region_code" type="{http://www.w3.org/2001/XMLSchema}byte" />
         *                           &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
         *                           &lt;attribute name="plan_year" type="{http://www.w3.org/2001/XMLSchema}short" />
         *                           &lt;attribute name="start_date" type="{http://www.w3.org/2001/XMLSchema}date" />
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="npc_code" type="{http://www.w3.org/2001/XMLSchema}short" />
         *       &lt;attribute name="project_name" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="is_temporary" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "snosBuildings",
            "startsTep"
        })
        public static class Kvartal {

            @XmlElement(name = "snos_buildings", required = true)
            protected Root.Region.Kvartal.SnosBuildings snosBuildings;
            @XmlElement(name = "starts_tep", required = true)
            protected Root.Region.Kvartal.StartsTep startsTep;
            @XmlAttribute(name = "geoloc")
            protected String geoloc;
            @XmlAttribute(name = "npc_code")
            protected Short npcCode;
            @XmlAttribute(name = "project_name")
            protected String projectName;
            @XmlAttribute(name = "is_temporary")
            protected String isTemporary;
            @XmlAttribute(name = "last_updated")
            @XmlSchemaType(name = "dateTime")
            protected XMLGregorianCalendar lastUpdated;

            /**
             * Gets the value of the snosBuildings property.
             *
             * @return
             *     possible object is
             *     {@link Root.Region.Kvartal.SnosBuildings }
             *
             */
            public Root.Region.Kvartal.SnosBuildings getSnosBuildings() {
                return snosBuildings;
            }

            /**
             * Sets the value of the snosBuildings property.
             *
             * @param value
             *     allowed object is
             *     {@link Root.Region.Kvartal.SnosBuildings }
             *
             */
            public void setSnosBuildings(Root.Region.Kvartal.SnosBuildings value) {
                this.snosBuildings = value;
            }

            /**
             * Gets the value of the startsTep property.
             *
             * @return
             *     possible object is
             *     {@link Root.Region.Kvartal.StartsTep }
             *
             */
            public Root.Region.Kvartal.StartsTep getStartsTep() {
                return startsTep;
            }

            /**
             * Sets the value of the startsTep property.
             *
             * @param value
             *     allowed object is
             *     {@link Root.Region.Kvartal.StartsTep }
             *
             */
            public void setStartsTep(Root.Region.Kvartal.StartsTep value) {
                this.startsTep = value;
            }

            /**
             * Gets the value of the geoloc property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getGeoloc() {
                return geoloc;
            }

            /**
             * Sets the value of the geoloc property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setGeoloc(String value) {
                this.geoloc = value;
            }

            /**
             * Gets the value of the npcCode property.
             *
             * @return
             *     possible object is
             *     {@link Short }
             *
             */
            public Short getNpcCode() {
                return npcCode;
            }

            /**
             * Sets the value of the npcCode property.
             *
             * @param value
             *     allowed object is
             *     {@link Short }
             *
             */
            public void setNpcCode(Short value) {
                this.npcCode = value;
            }

            /**
             * Gets the value of the projectName property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getProjectName() {
                return projectName;
            }

            /**
             * Sets the value of the projectName property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setProjectName(String value) {
                this.projectName = value;
            }

            /**
             * Gets the value of the isTemporary property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getIsTemporary() {
                return isTemporary;
            }

            /**
             * Sets the value of the isTemporary property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setIsTemporary(String value) {
                this.isTemporary = value;
            }

            /**
             * Gets the value of the lastUpdated property.
             *
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *
             */
            public XMLGregorianCalendar getLastUpdated() {
                return lastUpdated;
            }

            /**
             * Sets the value of the lastUpdated property.
             *
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *
             */
            public void setLastUpdated(XMLGregorianCalendar value) {
                this.lastUpdated = value;
            }


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
             *         &lt;element name="snos_building" maxOccurs="unbounded" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="snos_flats">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;sequence>
             *                             &lt;element name="snos_flat" maxOccurs="unbounded" minOccurs="0">
             *                               &lt;complexType>
             *                                 &lt;complexContent>
             *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                     &lt;sequence>
             *                                       &lt;element name="snos_lic" maxOccurs="unbounded" minOccurs="0">
             *                                         &lt;complexType>
             *                                           &lt;simpleContent>
             *                                             &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
             *                                               &lt;attribute name="fls" type="{http://www.w3.org/2001/XMLSchema}long" />
             *                                               &lt;attribute name="date_reg" type="{http://www.w3.org/2001/XMLSchema}date" />
             *                                               &lt;attribute name="kod_plat" type="{http://www.w3.org/2001/XMLSchema}long" />
             *                                               &lt;attribute name="date_fls" type="{http://www.w3.org/2001/XMLSchema}date" />
             *                                               &lt;attribute name="type_reg" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                                               &lt;attribute name="sobstv" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                                               &lt;attribute name="osnovanie" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                                               &lt;attribute name="sobstvennik" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                                               &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
             *                                               &lt;attribute name="nom_komn" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                                             &lt;/extension>
             *                                           &lt;/simpleContent>
             *                                         &lt;/complexType>
             *                                       &lt;/element>
             *                                       &lt;element name="flat_graf" maxOccurs="unbounded" minOccurs="0">
             *                                         &lt;complexType>
             *                                           &lt;simpleContent>
             *                                             &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
             *                                               &lt;attribute name="kvq" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                                               &lt;attribute name="kmq" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                                             &lt;/extension>
             *                                           &lt;/simpleContent>
             *                                         &lt;/complexType>
             *                                       &lt;/element>
             *                                     &lt;/sequence>
             *                                     &lt;attribute name="kvnom" type="{http://www.w3.org/2001/XMLSchema}short" />
             *                                     &lt;attribute name="gpl" type="{http://www.w3.org/2001/XMLSchema}float" />
             *                                     &lt;attribute name="kmq" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                                     &lt;attribute name="opl" type="{http://www.w3.org/2001/XMLSchema}float" />
             *                                     &lt;attribute name="ppl" type="{http://www.w3.org/2001/XMLSchema}float" />
             *                                     &lt;attribute name="unkv" type="{http://www.w3.org/2001/XMLSchema}short" />
             *                                     &lt;attribute name="disabled" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                                     &lt;attribute name="queued" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                                     &lt;attribute name="resident_num" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                                     &lt;attribute name="kvnom_floor" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                                     &lt;attribute name="entrance" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                                     &lt;attribute name="owner" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                                     &lt;attribute name="pl_kuh" type="{http://www.w3.org/2001/XMLSchema}float" />
             *                                     &lt;attribute name="is_kommun" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                                     &lt;attribute name="raschet_kv" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                                     &lt;attribute name="ruchnoy_vvod" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                                     &lt;attribute name="status_kv" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                                     &lt;attribute name="type_kv" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                                     &lt;attribute name="floor" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                                     &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
             *                                   &lt;/restriction>
             *                                 &lt;/complexContent>
             *                               &lt;/complexType>
             *                             &lt;/element>
             *                           &lt;/sequence>
             *                         &lt;/restriction>
             *                       &lt;/complexContent>
             *                     &lt;/complexType>
             *                   &lt;/element>
             *                 &lt;/sequence>
             *                 &lt;attribute name="npc_cod" type="{http://www.w3.org/2001/XMLSchema}short" />
             *                 &lt;attribute name="address" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="floor_max" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                 &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="god_p_dgp" type="{http://www.w3.org/2001/XMLSchema}short" />
             *                 &lt;attribute name="hab_square" type="{http://www.w3.org/2001/XMLSchema}float" />
             *                 &lt;attribute name="unhab_square" type="{http://www.w3.org/2001/XMLSchema}float" />
             *                 &lt;attribute name="is_disabled" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="is_unhab" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="mat_sten" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="monument" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="proc_izn" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                 &lt;attribute name="sost_dgp" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="unom" type="{http://www.w3.org/2001/XMLSchema}int" />
             *                 &lt;attribute name="god_ust_izn" type="{http://www.w3.org/2001/XMLSchema}short" />
             *                 &lt;attribute name="proc_za" type="{http://www.w3.org/2001/XMLSchema}float" />
             *                 &lt;attribute name="proc_protiv" type="{http://www.w3.org/2001/XMLSchema}float" />
             *                 &lt;attribute name="kadastr" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="category" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="lift" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                 &lt;attribute name="podezd" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                 &lt;attribute name="nar_obm_pl" type="{http://www.w3.org/2001/XMLSchema}float" />
             *                 &lt;attribute name="seria" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
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
                "snosBuilding"
            })
            public static class SnosBuildings {

                @XmlElement(name = "snos_building")
                protected List<Root.Region.Kvartal.SnosBuildings.SnosBuilding> snosBuilding;

                /**
                 * Gets the value of the snosBuilding property.
                 *
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the snosBuilding property.
                 *
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getSnosBuilding().add(newItem);
                 * </pre>
                 *
                 *
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Root.Region.Kvartal.SnosBuildings.SnosBuilding }
                 *
                 *
                 */
                public List<Root.Region.Kvartal.SnosBuildings.SnosBuilding> getSnosBuilding() {
                    if (snosBuilding == null) {
                        snosBuilding = new ArrayList<Root.Region.Kvartal.SnosBuildings.SnosBuilding>();
                    }
                    return this.snosBuilding;
                }


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
                 *         &lt;element name="snos_flats">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;sequence>
                 *                   &lt;element name="snos_flat" maxOccurs="unbounded" minOccurs="0">
                 *                     &lt;complexType>
                 *                       &lt;complexContent>
                 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                           &lt;sequence>
                 *                             &lt;element name="snos_lic" maxOccurs="unbounded" minOccurs="0">
                 *                               &lt;complexType>
                 *                                 &lt;simpleContent>
                 *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
                 *                                     &lt;attribute name="fls" type="{http://www.w3.org/2001/XMLSchema}long" />
                 *                                     &lt;attribute name="date_reg" type="{http://www.w3.org/2001/XMLSchema}date" />
                 *                                     &lt;attribute name="kod_plat" type="{http://www.w3.org/2001/XMLSchema}long" />
                 *                                     &lt;attribute name="date_fls" type="{http://www.w3.org/2001/XMLSchema}date" />
                 *                                     &lt;attribute name="type_reg" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                                     &lt;attribute name="sobstv" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                                     &lt;attribute name="osnovanie" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                                     &lt;attribute name="sobstvennik" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                                     &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
                 *                                     &lt;attribute name="nom_komn" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *                                   &lt;/extension>
                 *                                 &lt;/simpleContent>
                 *                               &lt;/complexType>
                 *                             &lt;/element>
                 *                             &lt;element name="flat_graf" maxOccurs="unbounded" minOccurs="0">
                 *                               &lt;complexType>
                 *                                 &lt;simpleContent>
                 *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
                 *                                     &lt;attribute name="kvq" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *                                     &lt;attribute name="kmq" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *                                   &lt;/extension>
                 *                                 &lt;/simpleContent>
                 *                               &lt;/complexType>
                 *                             &lt;/element>
                 *                           &lt;/sequence>
                 *                           &lt;attribute name="kvnom" type="{http://www.w3.org/2001/XMLSchema}short" />
                 *                           &lt;attribute name="gpl" type="{http://www.w3.org/2001/XMLSchema}float" />
                 *                           &lt;attribute name="kmq" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *                           &lt;attribute name="opl" type="{http://www.w3.org/2001/XMLSchema}float" />
                 *                           &lt;attribute name="ppl" type="{http://www.w3.org/2001/XMLSchema}float" />
                 *                           &lt;attribute name="unkv" type="{http://www.w3.org/2001/XMLSchema}short" />
                 *                           &lt;attribute name="disabled" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *                           &lt;attribute name="queued" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *                           &lt;attribute name="resident_num" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *                           &lt;attribute name="kvnom_floor" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *                           &lt;attribute name="entrance" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *                           &lt;attribute name="owner" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                           &lt;attribute name="pl_kuh" type="{http://www.w3.org/2001/XMLSchema}float" />
                 *                           &lt;attribute name="is_kommun" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                           &lt;attribute name="raschet_kv" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *                           &lt;attribute name="ruchnoy_vvod" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                           &lt;attribute name="status_kv" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *                           &lt;attribute name="type_kv" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                           &lt;attribute name="floor" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *                           &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
                 *                         &lt;/restriction>
                 *                       &lt;/complexContent>
                 *                     &lt;/complexType>
                 *                   &lt;/element>
                 *                 &lt;/sequence>
                 *               &lt;/restriction>
                 *             &lt;/complexContent>
                 *           &lt;/complexType>
                 *         &lt;/element>
                 *       &lt;/sequence>
                 *       &lt;attribute name="npc_cod" type="{http://www.w3.org/2001/XMLSchema}short" />
                 *       &lt;attribute name="address" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="floor_max" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *       &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="god_p_dgp" type="{http://www.w3.org/2001/XMLSchema}short" />
                 *       &lt;attribute name="hab_square" type="{http://www.w3.org/2001/XMLSchema}float" />
                 *       &lt;attribute name="unhab_square" type="{http://www.w3.org/2001/XMLSchema}float" />
                 *       &lt;attribute name="is_disabled" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="is_unhab" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="mat_sten" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="monument" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="proc_izn" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *       &lt;attribute name="sost_dgp" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="unom" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *       &lt;attribute name="god_ust_izn" type="{http://www.w3.org/2001/XMLSchema}short" />
                 *       &lt;attribute name="proc_za" type="{http://www.w3.org/2001/XMLSchema}float" />
                 *       &lt;attribute name="proc_protiv" type="{http://www.w3.org/2001/XMLSchema}float" />
                 *       &lt;attribute name="kadastr" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="category" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="lift" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *       &lt;attribute name="podezd" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *       &lt;attribute name="nar_obm_pl" type="{http://www.w3.org/2001/XMLSchema}float" />
                 *       &lt;attribute name="seria" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 *
                 *
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "snosFlats"
                })
                public static class SnosBuilding {

                    @XmlElement(name = "snos_flats", required = true)
                    protected Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats snosFlats;
                    @XmlAttribute(name = "npc_cod")
                    protected Short npcCod;
                    @XmlAttribute(name = "address")
                    protected String address;
                    @XmlAttribute(name = "floor_max")
                    protected Byte floorMax;
                    @XmlAttribute(name = "geoloc")
                    protected String geoloc;
                    @XmlAttribute(name = "god_p_dgp")
                    protected Short godPDgp;
                    @XmlAttribute(name = "hab_square")
                    protected Float habSquare;
                    @XmlAttribute(name = "unhab_square")
                    protected Float unhabSquare;
                    @XmlAttribute(name = "is_disabled")
                    protected String isDisabled;
                    @XmlAttribute(name = "is_unhab")
                    protected String isUnhab;
                    @XmlAttribute(name = "mat_sten")
                    protected String matSten;
                    @XmlAttribute(name = "monument")
                    protected String monument;
                    @XmlAttribute(name = "proc_izn")
                    protected Byte procIzn;
                    @XmlAttribute(name = "sost_dgp")
                    protected String sostDgp;
                    @XmlAttribute(name = "unom")
                    protected Integer unom;
                    @XmlAttribute(name = "god_ust_izn")
                    protected Short godUstIzn;
                    @XmlAttribute(name = "proc_za")
                    protected Float procZa;
                    @XmlAttribute(name = "proc_protiv")
                    protected Float procProtiv;
                    @XmlAttribute(name = "kadastr")
                    protected String kadastr;
                    @XmlAttribute(name = "category")
                    protected String category;
                    @XmlAttribute(name = "lift")
                    protected Byte lift;
                    @XmlAttribute(name = "podezd")
                    protected Byte podezd;
                    @XmlAttribute(name = "nar_obm_pl")
                    protected Float narObmPl;
                    @XmlAttribute(name = "seria")
                    protected String seria;
                    @XmlAttribute(name = "last_updated")
                    @XmlSchemaType(name = "dateTime")
                    protected XMLGregorianCalendar lastUpdated;

                    /**
                     * Gets the value of the snosFlats property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats }
                     *
                     */
                    public Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats getSnosFlats() {
                        return snosFlats;
                    }

                    /**
                     * Sets the value of the snosFlats property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats }
                     *
                     */
                    public void setSnosFlats(Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats value) {
                        this.snosFlats = value;
                    }

                    /**
                     * Gets the value of the npcCod property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Short }
                     *
                     */
                    public Short getNpcCod() {
                        return npcCod;
                    }

                    /**
                     * Sets the value of the npcCod property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Short }
                     *
                     */
                    public void setNpcCod(Short value) {
                        this.npcCod = value;
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
                     * Gets the value of the floorMax property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Byte }
                     *
                     */
                    public Byte getFloorMax() {
                        return floorMax;
                    }

                    /**
                     * Sets the value of the floorMax property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Byte }
                     *
                     */
                    public void setFloorMax(Byte value) {
                        this.floorMax = value;
                    }

                    /**
                     * Gets the value of the geoloc property.
                     *
                     * @return
                     *     possible object is
                     *     {@link String }
                     *
                     */
                    public String getGeoloc() {
                        return geoloc;
                    }

                    /**
                     * Sets the value of the geoloc property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *
                     */
                    public void setGeoloc(String value) {
                        this.geoloc = value;
                    }

                    /**
                     * Gets the value of the godPDgp property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Short }
                     *
                     */
                    public Short getGodPDgp() {
                        return godPDgp;
                    }

                    /**
                     * Sets the value of the godPDgp property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Short }
                     *
                     */
                    public void setGodPDgp(Short value) {
                        this.godPDgp = value;
                    }

                    /**
                     * Gets the value of the habSquare property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Float }
                     *
                     */
                    public Float getHabSquare() {
                        return habSquare;
                    }

                    /**
                     * Sets the value of the habSquare property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Float }
                     *
                     */
                    public void setHabSquare(Float value) {
                        this.habSquare = value;
                    }

                    /**
                     * Gets the value of the unhabSquare property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Float }
                     *
                     */
                    public Float getUnhabSquare() {
                        return unhabSquare;
                    }

                    /**
                     * Sets the value of the unhabSquare property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Float }
                     *
                     */
                    public void setUnhabSquare(Float value) {
                        this.unhabSquare = value;
                    }

                    /**
                     * Gets the value of the isDisabled property.
                     *
                     * @return
                     *     possible object is
                     *     {@link String }
                     *
                     */
                    public String getIsDisabled() {
                        return isDisabled;
                    }

                    /**
                     * Sets the value of the isDisabled property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *
                     */
                    public void setIsDisabled(String value) {
                        this.isDisabled = value;
                    }

                    /**
                     * Gets the value of the isUnhab property.
                     *
                     * @return
                     *     possible object is
                     *     {@link String }
                     *
                     */
                    public String getIsUnhab() {
                        return isUnhab;
                    }

                    /**
                     * Sets the value of the isUnhab property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *
                     */
                    public void setIsUnhab(String value) {
                        this.isUnhab = value;
                    }

                    /**
                     * Gets the value of the matSten property.
                     *
                     * @return
                     *     possible object is
                     *     {@link String }
                     *
                     */
                    public String getMatSten() {
                        return matSten;
                    }

                    /**
                     * Sets the value of the matSten property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *
                     */
                    public void setMatSten(String value) {
                        this.matSten = value;
                    }

                    /**
                     * Gets the value of the monument property.
                     *
                     * @return
                     *     possible object is
                     *     {@link String }
                     *
                     */
                    public String getMonument() {
                        return monument;
                    }

                    /**
                     * Sets the value of the monument property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *
                     */
                    public void setMonument(String value) {
                        this.monument = value;
                    }

                    /**
                     * Gets the value of the procIzn property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Byte }
                     *
                     */
                    public Byte getProcIzn() {
                        return procIzn;
                    }

                    /**
                     * Sets the value of the procIzn property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Byte }
                     *
                     */
                    public void setProcIzn(Byte value) {
                        this.procIzn = value;
                    }

                    /**
                     * Gets the value of the sostDgp property.
                     *
                     * @return
                     *     possible object is
                     *     {@link String }
                     *
                     */
                    public String getSostDgp() {
                        return sostDgp;
                    }

                    /**
                     * Sets the value of the sostDgp property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *
                     */
                    public void setSostDgp(String value) {
                        this.sostDgp = value;
                    }

                    /**
                     * Gets the value of the unom property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Integer }
                     *
                     */
                    public Integer getUnom() {
                        return unom;
                    }

                    /**
                     * Sets the value of the unom property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Integer }
                     *
                     */
                    public void setUnom(Integer value) {
                        this.unom = value;
                    }

                    /**
                     * Gets the value of the godUstIzn property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Short }
                     *
                     */
                    public Short getGodUstIzn() {
                        return godUstIzn;
                    }

                    /**
                     * Sets the value of the godUstIzn property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Short }
                     *
                     */
                    public void setGodUstIzn(Short value) {
                        this.godUstIzn = value;
                    }

                    /**
                     * Gets the value of the procZa property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Float }
                     *
                     */
                    public Float getProcZa() {
                        return procZa;
                    }

                    /**
                     * Sets the value of the procZa property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Float }
                     *
                     */
                    public void setProcZa(Float value) {
                        this.procZa = value;
                    }

                    /**
                     * Gets the value of the procProtiv property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Float }
                     *
                     */
                    public Float getProcProtiv() {
                        return procProtiv;
                    }

                    /**
                     * Sets the value of the procProtiv property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Float }
                     *
                     */
                    public void setProcProtiv(Float value) {
                        this.procProtiv = value;
                    }

                    /**
                     * Gets the value of the kadastr property.
                     *
                     * @return
                     *     possible object is
                     *     {@link String }
                     *
                     */
                    public String getKadastr() {
                        return kadastr;
                    }

                    /**
                     * Sets the value of the kadastr property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *
                     */
                    public void setKadastr(String value) {
                        this.kadastr = value;
                    }

                    /**
                     * Gets the value of the category property.
                     *
                     * @return
                     *     possible object is
                     *     {@link String }
                     *
                     */
                    public String getCategory() {
                        return category;
                    }

                    /**
                     * Sets the value of the category property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *
                     */
                    public void setCategory(String value) {
                        this.category = value;
                    }

                    /**
                     * Gets the value of the lift property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Byte }
                     *
                     */
                    public Byte getLift() {
                        return lift;
                    }

                    /**
                     * Sets the value of the lift property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Byte }
                     *
                     */
                    public void setLift(Byte value) {
                        this.lift = value;
                    }

                    /**
                     * Gets the value of the podezd property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Byte }
                     *
                     */
                    public Byte getPodezd() {
                        return podezd;
                    }

                    /**
                     * Sets the value of the podezd property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Byte }
                     *
                     */
                    public void setPodezd(Byte value) {
                        this.podezd = value;
                    }

                    /**
                     * Gets the value of the narObmPl property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Float }
                     *
                     */
                    public Float getNarObmPl() {
                        return narObmPl;
                    }

                    /**
                     * Sets the value of the narObmPl property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Float }
                     *
                     */
                    public void setNarObmPl(Float value) {
                        this.narObmPl = value;
                    }

                    /**
                     * Gets the value of the seria property.
                     *
                     * @return
                     *     possible object is
                     *     {@link String }
                     *
                     */
                    public String getSeria() {
                        return seria;
                    }

                    /**
                     * Sets the value of the seria property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *
                     */
                    public void setSeria(String value) {
                        this.seria = value;
                    }

                    /**
                     * Gets the value of the lastUpdated property.
                     *
                     * @return
                     *     possible object is
                     *     {@link XMLGregorianCalendar }
                     *
                     */
                    public XMLGregorianCalendar getLastUpdated() {
                        return lastUpdated;
                    }

                    /**
                     * Sets the value of the lastUpdated property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link XMLGregorianCalendar }
                     *
                     */
                    public void setLastUpdated(XMLGregorianCalendar value) {
                        this.lastUpdated = value;
                    }


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
                     *         &lt;element name="snos_flat" maxOccurs="unbounded" minOccurs="0">
                     *           &lt;complexType>
                     *             &lt;complexContent>
                     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                 &lt;sequence>
                     *                   &lt;element name="snos_lic" maxOccurs="unbounded" minOccurs="0">
                     *                     &lt;complexType>
                     *                       &lt;simpleContent>
                     *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
                     *                           &lt;attribute name="fls" type="{http://www.w3.org/2001/XMLSchema}long" />
                     *                           &lt;attribute name="date_reg" type="{http://www.w3.org/2001/XMLSchema}date" />
                     *                           &lt;attribute name="kod_plat" type="{http://www.w3.org/2001/XMLSchema}long" />
                     *                           &lt;attribute name="date_fls" type="{http://www.w3.org/2001/XMLSchema}date" />
                     *                           &lt;attribute name="type_reg" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *                           &lt;attribute name="sobstv" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *                           &lt;attribute name="osnovanie" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *                           &lt;attribute name="sobstvennik" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *                           &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
                     *                           &lt;attribute name="nom_komn" type="{http://www.w3.org/2001/XMLSchema}byte" />
                     *                         &lt;/extension>
                     *                       &lt;/simpleContent>
                     *                     &lt;/complexType>
                     *                   &lt;/element>
                     *                   &lt;element name="flat_graf" maxOccurs="unbounded" minOccurs="0">
                     *                     &lt;complexType>
                     *                       &lt;simpleContent>
                     *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
                     *                           &lt;attribute name="kvq" type="{http://www.w3.org/2001/XMLSchema}byte" />
                     *                           &lt;attribute name="kmq" type="{http://www.w3.org/2001/XMLSchema}byte" />
                     *                         &lt;/extension>
                     *                       &lt;/simpleContent>
                     *                     &lt;/complexType>
                     *                   &lt;/element>
                     *                 &lt;/sequence>
                     *                 &lt;attribute name="kvnom" type="{http://www.w3.org/2001/XMLSchema}short" />
                     *                 &lt;attribute name="gpl" type="{http://www.w3.org/2001/XMLSchema}float" />
                     *                 &lt;attribute name="kmq" type="{http://www.w3.org/2001/XMLSchema}byte" />
                     *                 &lt;attribute name="opl" type="{http://www.w3.org/2001/XMLSchema}float" />
                     *                 &lt;attribute name="ppl" type="{http://www.w3.org/2001/XMLSchema}float" />
                     *                 &lt;attribute name="unkv" type="{http://www.w3.org/2001/XMLSchema}short" />
                     *                 &lt;attribute name="disabled" type="{http://www.w3.org/2001/XMLSchema}byte" />
                     *                 &lt;attribute name="queued" type="{http://www.w3.org/2001/XMLSchema}byte" />
                     *                 &lt;attribute name="resident_num" type="{http://www.w3.org/2001/XMLSchema}byte" />
                     *                 &lt;attribute name="kvnom_floor" type="{http://www.w3.org/2001/XMLSchema}byte" />
                     *                 &lt;attribute name="entrance" type="{http://www.w3.org/2001/XMLSchema}byte" />
                     *                 &lt;attribute name="owner" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *                 &lt;attribute name="pl_kuh" type="{http://www.w3.org/2001/XMLSchema}float" />
                     *                 &lt;attribute name="is_kommun" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *                 &lt;attribute name="raschet_kv" type="{http://www.w3.org/2001/XMLSchema}byte" />
                     *                 &lt;attribute name="ruchnoy_vvod" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *                 &lt;attribute name="status_kv" type="{http://www.w3.org/2001/XMLSchema}byte" />
                     *                 &lt;attribute name="type_kv" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *                 &lt;attribute name="floor" type="{http://www.w3.org/2001/XMLSchema}byte" />
                     *                 &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
                     *               &lt;/restriction>
                     *             &lt;/complexContent>
                     *           &lt;/complexType>
                     *         &lt;/element>
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
                        "snosFlat"
                    })
                    public static class SnosFlats {

                        @XmlElement(name = "snos_flat")
                        protected List<Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat> snosFlat;

                        /**
                         * Gets the value of the snosFlat property.
                         *
                         * <p>
                         * This accessor method returns a reference to the live list,
                         * not a snapshot. Therefore any modification you make to the
                         * returned list will be present inside the JAXB object.
                         * This is why there is not a <CODE>set</CODE> method for the snosFlat property.
                         *
                         * <p>
                         * For example, to add a new item, do as follows:
                         * <pre>
                         *    getSnosFlat().add(newItem);
                         * </pre>
                         *
                         *
                         * <p>
                         * Objects of the following type(s) are allowed in the list
                         * {@link Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat }
                         *
                         *
                         */
                        public List<Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat> getSnosFlat() {
                            if (snosFlat == null) {
                                snosFlat = new ArrayList<Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat>();
                            }
                            return this.snosFlat;
                        }


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
                         *         &lt;element name="snos_lic" maxOccurs="unbounded" minOccurs="0">
                         *           &lt;complexType>
                         *             &lt;simpleContent>
                         *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
                         *                 &lt;attribute name="fls" type="{http://www.w3.org/2001/XMLSchema}long" />
                         *                 &lt;attribute name="date_reg" type="{http://www.w3.org/2001/XMLSchema}date" />
                         *                 &lt;attribute name="kod_plat" type="{http://www.w3.org/2001/XMLSchema}long" />
                         *                 &lt;attribute name="date_fls" type="{http://www.w3.org/2001/XMLSchema}date" />
                         *                 &lt;attribute name="type_reg" type="{http://www.w3.org/2001/XMLSchema}string" />
                         *                 &lt;attribute name="sobstv" type="{http://www.w3.org/2001/XMLSchema}string" />
                         *                 &lt;attribute name="osnovanie" type="{http://www.w3.org/2001/XMLSchema}string" />
                         *                 &lt;attribute name="sobstvennik" type="{http://www.w3.org/2001/XMLSchema}string" />
                         *                 &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
                         *                 &lt;attribute name="nom_komn" type="{http://www.w3.org/2001/XMLSchema}byte" />
                         *               &lt;/extension>
                         *             &lt;/simpleContent>
                         *           &lt;/complexType>
                         *         &lt;/element>
                         *         &lt;element name="flat_graf" maxOccurs="unbounded" minOccurs="0">
                         *           &lt;complexType>
                         *             &lt;simpleContent>
                         *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
                         *                 &lt;attribute name="kvq" type="{http://www.w3.org/2001/XMLSchema}byte" />
                         *                 &lt;attribute name="kmq" type="{http://www.w3.org/2001/XMLSchema}byte" />
                         *               &lt;/extension>
                         *             &lt;/simpleContent>
                         *           &lt;/complexType>
                         *         &lt;/element>
                         *       &lt;/sequence>
                         *       &lt;attribute name="kvnom" type="{http://www.w3.org/2001/XMLSchema}short" />
                         *       &lt;attribute name="gpl" type="{http://www.w3.org/2001/XMLSchema}float" />
                         *       &lt;attribute name="kmq" type="{http://www.w3.org/2001/XMLSchema}byte" />
                         *       &lt;attribute name="opl" type="{http://www.w3.org/2001/XMLSchema}float" />
                         *       &lt;attribute name="ppl" type="{http://www.w3.org/2001/XMLSchema}float" />
                         *       &lt;attribute name="unkv" type="{http://www.w3.org/2001/XMLSchema}short" />
                         *       &lt;attribute name="disabled" type="{http://www.w3.org/2001/XMLSchema}byte" />
                         *       &lt;attribute name="queued" type="{http://www.w3.org/2001/XMLSchema}byte" />
                         *       &lt;attribute name="resident_num" type="{http://www.w3.org/2001/XMLSchema}byte" />
                         *       &lt;attribute name="kvnom_floor" type="{http://www.w3.org/2001/XMLSchema}byte" />
                         *       &lt;attribute name="entrance" type="{http://www.w3.org/2001/XMLSchema}byte" />
                         *       &lt;attribute name="owner" type="{http://www.w3.org/2001/XMLSchema}string" />
                         *       &lt;attribute name="pl_kuh" type="{http://www.w3.org/2001/XMLSchema}float" />
                         *       &lt;attribute name="is_kommun" type="{http://www.w3.org/2001/XMLSchema}string" />
                         *       &lt;attribute name="raschet_kv" type="{http://www.w3.org/2001/XMLSchema}byte" />
                         *       &lt;attribute name="ruchnoy_vvod" type="{http://www.w3.org/2001/XMLSchema}string" />
                         *       &lt;attribute name="status_kv" type="{http://www.w3.org/2001/XMLSchema}byte" />
                         *       &lt;attribute name="type_kv" type="{http://www.w3.org/2001/XMLSchema}string" />
                         *       &lt;attribute name="floor" type="{http://www.w3.org/2001/XMLSchema}byte" />
                         *       &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
                         *     &lt;/restriction>
                         *   &lt;/complexContent>
                         * &lt;/complexType>
                         * </pre>
                         *
                         *
                         */
                        @XmlAccessorType(XmlAccessType.FIELD)
                        @XmlType(name = "", propOrder = {
                            "snosLic",
                            "flatGraf"
                        })
                        public static class SnosFlat {

                            @XmlElement(name = "snos_lic")
                            protected List<Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat.SnosLic> snosLic;
                            @XmlElement(name = "flat_graf")
                            protected List<Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat.FlatGraf> flatGraf;
                            @XmlAttribute(name = "kvnom")
                            protected String kvnom;
                            @XmlAttribute(name = "gpl")
                            protected Float gpl;
                            @XmlAttribute(name = "kmq")
                            protected Byte kmq;
                            @XmlAttribute(name = "opl")
                            protected Float opl;
                            @XmlAttribute(name = "ppl")
                            protected Float ppl;
                            @XmlAttribute(name = "unkv")
                            protected Short unkv;
                            @XmlAttribute(name = "disabled")
                            protected Byte disabled;
                            @XmlAttribute(name = "queued")
                            protected Byte queued;
                            @XmlAttribute(name = "resident_num")
                            protected Byte residentNum;
                            @XmlAttribute(name = "kvnom_floor")
                            protected Byte kvnomFloor;
                            @XmlAttribute(name = "entrance")
                            protected Byte entrance;
                            @XmlAttribute(name = "owner")
                            protected String owner;
                            @XmlAttribute(name = "pl_kuh")
                            protected Float plKuh;
                            @XmlAttribute(name = "is_kommun")
                            protected String isKommun;
                            @XmlAttribute(name = "raschet_kv")
                            protected Byte raschetKv;
                            @XmlAttribute(name = "ruchnoy_vvod")
                            protected String ruchnoyVvod;
                            @XmlAttribute(name = "status_kv")
                            protected Byte statusKv;
                            @XmlAttribute(name = "type_kv")
                            protected String typeKv;
                            @XmlAttribute(name = "floor")
                            protected Byte floor;
                            @XmlAttribute(name = "last_updated")
                            @XmlSchemaType(name = "dateTime")
                            protected XMLGregorianCalendar lastUpdated;

                            /**
                             * Gets the value of the snosLic property.
                             *
                             * <p>
                             * This accessor method returns a reference to the live list,
                             * not a snapshot. Therefore any modification you make to the
                             * returned list will be present inside the JAXB object.
                             * This is why there is not a <CODE>set</CODE> method for the snosLic property.
                             *
                             * <p>
                             * For example, to add a new item, do as follows:
                             * <pre>
                             *    getSnosLic().add(newItem);
                             * </pre>
                             *
                             *
                             * <p>
                             * Objects of the following type(s) are allowed in the list
                             * {@link Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat.SnosLic }
                             *
                             *
                             */
                            public List<Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat.SnosLic> getSnosLic() {
                                if (snosLic == null) {
                                    snosLic = new ArrayList<Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat.SnosLic>();
                                }
                                return this.snosLic;
                            }

                            /**
                             * Gets the value of the flatGraf property.
                             *
                             * <p>
                             * This accessor method returns a reference to the live list,
                             * not a snapshot. Therefore any modification you make to the
                             * returned list will be present inside the JAXB object.
                             * This is why there is not a <CODE>set</CODE> method for the flatGraf property.
                             *
                             * <p>
                             * For example, to add a new item, do as follows:
                             * <pre>
                             *    getFlatGraf().add(newItem);
                             * </pre>
                             *
                             *
                             * <p>
                             * Objects of the following type(s) are allowed in the list
                             * {@link Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat.FlatGraf }
                             *
                             *
                             */
                            public List<Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat.FlatGraf> getFlatGraf() {
                                if (flatGraf == null) {
                                    flatGraf = new ArrayList<Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat.FlatGraf>();
                                }
                                return this.flatGraf;
                            }

                            /**
                             * Gets the value of the kvnom property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Short }
                             *
                             */
                            public String getKvnom() {
                                return kvnom;
                            }

                            /**
                             * Sets the value of the kvnom property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Short }
                             *
                             */
                            public void setKvnom(String value) {
                                this.kvnom = value;
                            }

                            /**
                             * Gets the value of the gpl property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Float }
                             *
                             */
                            public Float getGpl() {
                                return gpl;
                            }

                            /**
                             * Sets the value of the gpl property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Float }
                             *
                             */
                            public void setGpl(Float value) {
                                this.gpl = value;
                            }

                            /**
                             * Gets the value of the kmq property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Byte }
                             *
                             */
                            public Byte getKmq() {
                                return kmq;
                            }

                            /**
                             * Sets the value of the kmq property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Byte }
                             *
                             */
                            public void setKmq(Byte value) {
                                this.kmq = value;
                            }

                            /**
                             * Gets the value of the opl property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Float }
                             *
                             */
                            public Float getOpl() {
                                return opl;
                            }

                            /**
                             * Sets the value of the opl property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Float }
                             *
                             */
                            public void setOpl(Float value) {
                                this.opl = value;
                            }

                            /**
                             * Gets the value of the ppl property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Float }
                             *
                             */
                            public Float getPpl() {
                                return ppl;
                            }

                            /**
                             * Sets the value of the ppl property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Float }
                             *
                             */
                            public void setPpl(Float value) {
                                this.ppl = value;
                            }

                            /**
                             * Gets the value of the unkv property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Short }
                             *
                             */
                            public Short getUnkv() {
                                return unkv;
                            }

                            /**
                             * Sets the value of the unkv property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Short }
                             *
                             */
                            public void setUnkv(Short value) {
                                this.unkv = value;
                            }

                            /**
                             * Gets the value of the disabled property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Byte }
                             *
                             */
                            public Byte getDisabled() {
                                return disabled;
                            }

                            /**
                             * Sets the value of the disabled property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Byte }
                             *
                             */
                            public void setDisabled(Byte value) {
                                this.disabled = value;
                            }

                            /**
                             * Gets the value of the queued property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Byte }
                             *
                             */
                            public Byte getQueued() {
                                return queued;
                            }

                            /**
                             * Sets the value of the queued property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Byte }
                             *
                             */
                            public void setQueued(Byte value) {
                                this.queued = value;
                            }

                            /**
                             * Gets the value of the residentNum property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Byte }
                             *
                             */
                            public Byte getResidentNum() {
                                return residentNum;
                            }

                            /**
                             * Sets the value of the residentNum property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Byte }
                             *
                             */
                            public void setResidentNum(Byte value) {
                                this.residentNum = value;
                            }

                            /**
                             * Gets the value of the kvnomFloor property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Byte }
                             *
                             */
                            public Byte getKvnomFloor() {
                                return kvnomFloor;
                            }

                            /**
                             * Sets the value of the kvnomFloor property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Byte }
                             *
                             */
                            public void setKvnomFloor(Byte value) {
                                this.kvnomFloor = value;
                            }

                            /**
                             * Gets the value of the entrance property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Byte }
                             *
                             */
                            public Byte getEntrance() {
                                return entrance;
                            }

                            /**
                             * Sets the value of the entrance property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Byte }
                             *
                             */
                            public void setEntrance(Byte value) {
                                this.entrance = value;
                            }

                            /**
                             * Gets the value of the owner property.
                             *
                             * @return
                             *     possible object is
                             *     {@link String }
                             *
                             */
                            public String getOwner() {
                                return owner;
                            }

                            /**
                             * Sets the value of the owner property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *
                             */
                            public void setOwner(String value) {
                                this.owner = value;
                            }

                            /**
                             * Gets the value of the plKuh property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Float }
                             *
                             */
                            public Float getPlKuh() {
                                return plKuh;
                            }

                            /**
                             * Sets the value of the plKuh property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Float }
                             *
                             */
                            public void setPlKuh(Float value) {
                                this.plKuh = value;
                            }

                            /**
                             * Gets the value of the isKommun property.
                             *
                             * @return
                             *     possible object is
                             *     {@link String }
                             *
                             */
                            public String getIsKommun() {
                                return isKommun;
                            }

                            /**
                             * Sets the value of the isKommun property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *
                             */
                            public void setIsKommun(String value) {
                                this.isKommun = value;
                            }

                            /**
                             * Gets the value of the raschetKv property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Byte }
                             *
                             */
                            public Byte getRaschetKv() {
                                return raschetKv;
                            }

                            /**
                             * Sets the value of the raschetKv property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Byte }
                             *
                             */
                            public void setRaschetKv(Byte value) {
                                this.raschetKv = value;
                            }

                            /**
                             * Gets the value of the ruchnoyVvod property.
                             *
                             * @return
                             *     possible object is
                             *     {@link String }
                             *
                             */
                            public String getRuchnoyVvod() {
                                return ruchnoyVvod;
                            }

                            /**
                             * Sets the value of the ruchnoyVvod property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *
                             */
                            public void setRuchnoyVvod(String value) {
                                this.ruchnoyVvod = value;
                            }

                            /**
                             * Gets the value of the statusKv property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Byte }
                             *
                             */
                            public Byte getStatusKv() {
                                return statusKv;
                            }

                            /**
                             * Sets the value of the statusKv property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Byte }
                             *
                             */
                            public void setStatusKv(Byte value) {
                                this.statusKv = value;
                            }

                            /**
                             * Gets the value of the typeKv property.
                             *
                             * @return
                             *     possible object is
                             *     {@link String }
                             *
                             */
                            public String getTypeKv() {
                                return typeKv;
                            }

                            /**
                             * Sets the value of the typeKv property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *
                             */
                            public void setTypeKv(String value) {
                                this.typeKv = value;
                            }

                            /**
                             * Gets the value of the floor property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Byte }
                             *
                             */
                            public Byte getFloor() {
                                return floor;
                            }

                            /**
                             * Sets the value of the floor property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Byte }
                             *
                             */
                            public void setFloor(Byte value) {
                                this.floor = value;
                            }

                            /**
                             * Gets the value of the lastUpdated property.
                             *
                             * @return
                             *     possible object is
                             *     {@link XMLGregorianCalendar }
                             *
                             */
                            public XMLGregorianCalendar getLastUpdated() {
                                return lastUpdated;
                            }

                            /**
                             * Sets the value of the lastUpdated property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link XMLGregorianCalendar }
                             *
                             */
                            public void setLastUpdated(XMLGregorianCalendar value) {
                                this.lastUpdated = value;
                            }


                            /**
                             * <p>Java class for anonymous complex type.
                             *
                             * <p>The following schema fragment specifies the expected content contained within this class.
                             *
                             * <pre>
                             * &lt;complexType>
                             *   &lt;simpleContent>
                             *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
                             *       &lt;attribute name="kvq" type="{http://www.w3.org/2001/XMLSchema}byte" />
                             *       &lt;attribute name="kmq" type="{http://www.w3.org/2001/XMLSchema}byte" />
                             *     &lt;/extension>
                             *   &lt;/simpleContent>
                             * &lt;/complexType>
                             * </pre>
                             *
                             *
                             */
                            @XmlAccessorType(XmlAccessType.FIELD)
                            @XmlType(name = "", propOrder = {
                                "value"
                            })
                            public static class FlatGraf {

                                @XmlValue
                                protected String value;
                                @XmlAttribute(name = "kvq")
                                protected Byte kvq;
                                @XmlAttribute(name = "kmq")
                                protected Byte kmq;

                                /**
                                 * Gets the value of the value property.
                                 *
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *
                                 */
                                public String getValue() {
                                    return value;
                                }

                                /**
                                 * Sets the value of the value property.
                                 *
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *
                                 */
                                public void setValue(String value) {
                                    this.value = value;
                                }

                                /**
                                 * Gets the value of the kvq property.
                                 *
                                 * @return
                                 *     possible object is
                                 *     {@link Byte }
                                 *
                                 */
                                public Byte getKvq() {
                                    return kvq;
                                }

                                /**
                                 * Sets the value of the kvq property.
                                 *
                                 * @param value
                                 *     allowed object is
                                 *     {@link Byte }
                                 *
                                 */
                                public void setKvq(Byte value) {
                                    this.kvq = value;
                                }

                                /**
                                 * Gets the value of the kmq property.
                                 *
                                 * @return
                                 *     possible object is
                                 *     {@link Byte }
                                 *
                                 */
                                public Byte getKmq() {
                                    return kmq;
                                }

                                /**
                                 * Sets the value of the kmq property.
                                 *
                                 * @param value
                                 *     allowed object is
                                 *     {@link Byte }
                                 *
                                 */
                                public void setKmq(Byte value) {
                                    this.kmq = value;
                                }

                            }


                            /**
                             * <p>Java class for anonymous complex type.
                             *
                             * <p>The following schema fragment specifies the expected content contained within this class.
                             *
                             * <pre>
                             * &lt;complexType>
                             *   &lt;simpleContent>
                             *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
                             *       &lt;attribute name="fls" type="{http://www.w3.org/2001/XMLSchema}long" />
                             *       &lt;attribute name="date_reg" type="{http://www.w3.org/2001/XMLSchema}date" />
                             *       &lt;attribute name="kod_plat" type="{http://www.w3.org/2001/XMLSchema}long" />
                             *       &lt;attribute name="date_fls" type="{http://www.w3.org/2001/XMLSchema}date" />
                             *       &lt;attribute name="type_reg" type="{http://www.w3.org/2001/XMLSchema}string" />
                             *       &lt;attribute name="sobstv" type="{http://www.w3.org/2001/XMLSchema}string" />
                             *       &lt;attribute name="osnovanie" type="{http://www.w3.org/2001/XMLSchema}string" />
                             *       &lt;attribute name="sobstvennik" type="{http://www.w3.org/2001/XMLSchema}string" />
                             *       &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
                             *       &lt;attribute name="nom_komn" type="{http://www.w3.org/2001/XMLSchema}byte" />
                             *     &lt;/extension>
                             *   &lt;/simpleContent>
                             * &lt;/complexType>
                             * </pre>
                             *
                             *
                             */
                            @XmlAccessorType(XmlAccessType.FIELD)
                            @XmlType(name = "", propOrder = {
                                "value"
                            })
                            public static class SnosLic {

                                @XmlValue
                                protected String value;
                                @XmlAttribute(name = "fls")
                                protected String fls;
                                @XmlAttribute(name = "date_reg")
                                @XmlSchemaType(name = "date")
                                protected XMLGregorianCalendar dateReg;
                                @XmlAttribute(name = "kod_plat")
                                protected String kodPlat;
                                @XmlAttribute(name = "date_fls")
                                @XmlSchemaType(name = "date")
                                protected XMLGregorianCalendar dateFls;
                                @XmlAttribute(name = "type_reg")
                                protected String typeReg;
                                @XmlAttribute(name = "sobstv")
                                protected String sobstv;
                                @XmlAttribute(name = "osnovanie")
                                protected String osnovanie;
                                @XmlAttribute(name = "sobstvennik")
                                protected String sobstvennik;
                                @XmlAttribute(name = "last_updated")
                                @XmlSchemaType(name = "dateTime")
                                protected XMLGregorianCalendar lastUpdated;
                                @XmlAttribute(name = "nom_komn")
                                protected Byte nomKomn;

                                /**
                                 * Gets the value of the value property.
                                 *
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *
                                 */
                                public String getValue() {
                                    return value;
                                }

                                /**
                                 * Sets the value of the value property.
                                 *
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *
                                 */
                                public void setValue(String value) {
                                    this.value = value;
                                }

                                /**
                                 * Gets the value of the fls property.
                                 *
                                 * @return
                                 *     possible object is
                                 *     {@link Long }
                                 *
                                 */
                                public String getFls() {
                                    return fls;
                                }

                                /**
                                 * Sets the value of the fls property.
                                 *
                                 * @param value
                                 *     allowed object is
                                 *     {@link Long }
                                 *
                                 */
                                public void setFls(String value) {
                                    this.fls = value;
                                }

                                /**
                                 * Gets the value of the dateReg property.
                                 *
                                 * @return
                                 *     possible object is
                                 *     {@link XMLGregorianCalendar }
                                 *
                                 */
                                public XMLGregorianCalendar getDateReg() {
                                    return dateReg;
                                }

                                /**
                                 * Sets the value of the dateReg property.
                                 *
                                 * @param value
                                 *     allowed object is
                                 *     {@link XMLGregorianCalendar }
                                 *
                                 */
                                public void setDateReg(XMLGregorianCalendar value) {
                                    this.dateReg = value;
                                }

                                /**
                                 * Gets the value of the kodPlat property.
                                 *
                                 * @return
                                 *     possible object is
                                 *     {@link Long }
                                 *
                                 */
                                public String getKodPlat() {
                                    return kodPlat;
                                }

                                /**
                                 * Sets the value of the kodPlat property.
                                 *
                                 * @param value
                                 *     allowed object is
                                 *     {@link Long }
                                 *
                                 */
                                public void setKodPlat(String value) {
                                    this.kodPlat = value;
                                }

                                /**
                                 * Gets the value of the dateFls property.
                                 *
                                 * @return
                                 *     possible object is
                                 *     {@link XMLGregorianCalendar }
                                 *
                                 */
                                public XMLGregorianCalendar getDateFls() {
                                    return dateFls;
                                }

                                /**
                                 * Sets the value of the dateFls property.
                                 *
                                 * @param value
                                 *     allowed object is
                                 *     {@link XMLGregorianCalendar }
                                 *
                                 */
                                public void setDateFls(XMLGregorianCalendar value) {
                                    this.dateFls = value;
                                }

                                /**
                                 * Gets the value of the typeReg property.
                                 *
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *
                                 */
                                public String getTypeReg() {
                                    return typeReg;
                                }

                                /**
                                 * Sets the value of the typeReg property.
                                 *
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *
                                 */
                                public void setTypeReg(String value) {
                                    this.typeReg = value;
                                }

                                /**
                                 * Gets the value of the sobstv property.
                                 *
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *
                                 */
                                public String getSobstv() {
                                    return sobstv;
                                }

                                /**
                                 * Sets the value of the sobstv property.
                                 *
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *
                                 */
                                public void setSobstv(String value) {
                                    this.sobstv = value;
                                }

                                /**
                                 * Gets the value of the osnovanie property.
                                 *
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *
                                 */
                                public String getOsnovanie() {
                                    return osnovanie;
                                }

                                /**
                                 * Sets the value of the osnovanie property.
                                 *
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *
                                 */
                                public void setOsnovanie(String value) {
                                    this.osnovanie = value;
                                }

                                /**
                                 * Gets the value of the sobstvennik property.
                                 *
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *
                                 */
                                public String getSobstvennik() {
                                    return sobstvennik;
                                }

                                /**
                                 * Sets the value of the sobstvennik property.
                                 *
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *
                                 */
                                public void setSobstvennik(String value) {
                                    this.sobstvennik = value;
                                }

                                /**
                                 * Gets the value of the lastUpdated property.
                                 *
                                 * @return
                                 *     possible object is
                                 *     {@link XMLGregorianCalendar }
                                 *
                                 */
                                public XMLGregorianCalendar getLastUpdated() {
                                    return lastUpdated;
                                }

                                /**
                                 * Sets the value of the lastUpdated property.
                                 *
                                 * @param value
                                 *     allowed object is
                                 *     {@link XMLGregorianCalendar }
                                 *
                                 */
                                public void setLastUpdated(XMLGregorianCalendar value) {
                                    this.lastUpdated = value;
                                }

                                /**
                                 * Gets the value of the nomKomn property.
                                 *
                                 * @return
                                 *     possible object is
                                 *     {@link Byte }
                                 *
                                 */
                                public Byte getNomKomn() {
                                    return nomKomn;
                                }

                                /**
                                 * Sets the value of the nomKomn property.
                                 *
                                 * @param value
                                 *     allowed object is
                                 *     {@link Byte }
                                 *
                                 */
                                public void setNomKomn(Byte value) {
                                    this.nomKomn = value;
                                }

                            }

                        }

                    }

                }

            }


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
             *         &lt;element name="start_tep" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="buildobjects">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;sequence>
             *                             &lt;element name="buildobject">
             *                               &lt;complexType>
             *                                 &lt;simpleContent>
             *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
             *                                     &lt;attribute name="buildobject_id" type="{http://www.w3.org/2001/XMLSchema}int" />
             *                                     &lt;attribute name="bld_address" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                                     &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                                     &lt;attribute name="s_zhilaya" type="{http://www.w3.org/2001/XMLSchema}float" />
             *                                     &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
             *                                     &lt;attribute name="ugd_guid" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                                   &lt;/extension>
             *                                 &lt;/simpleContent>
             *                               &lt;/complexType>
             *                             &lt;/element>
             *                           &lt;/sequence>
             *                         &lt;/restriction>
             *                       &lt;/complexContent>
             *                     &lt;/complexType>
             *                   &lt;/element>
             *                 &lt;/sequence>
             *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}short" />
             *                 &lt;attribute name="categorija" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                 &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="is_actual" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="is_invest" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="object_name" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="s_zhilaya" type="{http://www.w3.org/2001/XMLSchema}float" />
             *                 &lt;attribute name="region_code" type="{http://www.w3.org/2001/XMLSchema}byte" />
             *                 &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
             *                 &lt;attribute name="plan_year" type="{http://www.w3.org/2001/XMLSchema}short" />
             *                 &lt;attribute name="start_date" type="{http://www.w3.org/2001/XMLSchema}date" />
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
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
                "content"
            })
            public static class StartsTep {

                @XmlElementRef(name = "start_tep", type = JAXBElement.class, required = false)
                @XmlMixed
                protected List<Serializable> content;

                /**
                 * Gets the value of the content property.
                 *
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the content property.
                 *
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getContent().add(newItem);
                 * </pre>
                 *
                 *
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link JAXBElement }{@code <}{@link Root.Region.Kvartal.StartsTep.StartTep }{@code >}
                 * {@link String }
                 *
                 *
                 */
                public List<Serializable> getContent() {
                    if (content == null) {
                        content = new ArrayList<Serializable>();
                    }
                    return this.content;
                }


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
                 *         &lt;element name="buildobjects">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;sequence>
                 *                   &lt;element name="buildobject">
                 *                     &lt;complexType>
                 *                       &lt;simpleContent>
                 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
                 *                           &lt;attribute name="buildobject_id" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *                           &lt;attribute name="bld_address" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                           &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                           &lt;attribute name="s_zhilaya" type="{http://www.w3.org/2001/XMLSchema}float" />
                 *                           &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
                 *                           &lt;attribute name="ugd_guid" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                         &lt;/extension>
                 *                       &lt;/simpleContent>
                 *                     &lt;/complexType>
                 *                   &lt;/element>
                 *                 &lt;/sequence>
                 *               &lt;/restriction>
                 *             &lt;/complexContent>
                 *           &lt;/complexType>
                 *         &lt;/element>
                 *       &lt;/sequence>
                 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}short" />
                 *       &lt;attribute name="categorija" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *       &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="is_actual" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="is_invest" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="object_name" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="s_zhilaya" type="{http://www.w3.org/2001/XMLSchema}float" />
                 *       &lt;attribute name="region_code" type="{http://www.w3.org/2001/XMLSchema}byte" />
                 *       &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
                 *       &lt;attribute name="plan_year" type="{http://www.w3.org/2001/XMLSchema}short" />
                 *       &lt;attribute name="start_date" type="{http://www.w3.org/2001/XMLSchema}date" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 *
                 *
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "buildobjects"
                })
                public static class StartTep {

                    @XmlElement(required = true)
                    protected Root.Region.Kvartal.StartsTep.StartTep.Buildobjects buildobjects;
                    @XmlAttribute(name = "id")
                    protected Short id;
                    @XmlAttribute(name = "categorija")
                    protected Byte categorija;
                    @XmlAttribute(name = "geoloc")
                    protected String geoloc;
                    @XmlAttribute(name = "is_actual")
                    protected String isActual;
                    @XmlAttribute(name = "is_invest")
                    protected String isInvest;
                    @XmlAttribute(name = "object_name")
                    protected String objectName;
                    @XmlAttribute(name = "s_zhilaya")
                    protected Float sZhilaya;
                    @XmlAttribute(name = "region_code")
                    protected Byte regionCode;
                    @XmlAttribute(name = "last_updated")
                    @XmlSchemaType(name = "dateTime")
                    protected XMLGregorianCalendar lastUpdated;
                    @XmlAttribute(name = "plan_year")
                    protected Short planYear;
                    @XmlAttribute(name = "start_date")
                    @XmlSchemaType(name = "date")
                    protected XMLGregorianCalendar startDate;

                    /**
                     * Gets the value of the buildobjects property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Root.Region.Kvartal.StartsTep.StartTep.Buildobjects }
                     *
                     */
                    public Root.Region.Kvartal.StartsTep.StartTep.Buildobjects getBuildobjects() {
                        return buildobjects;
                    }

                    /**
                     * Sets the value of the buildobjects property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Root.Region.Kvartal.StartsTep.StartTep.Buildobjects }
                     *
                     */
                    public void setBuildobjects(Root.Region.Kvartal.StartsTep.StartTep.Buildobjects value) {
                        this.buildobjects = value;
                    }

                    /**
                     * Gets the value of the id property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Short }
                     *
                     */
                    public Short getId() {
                        return id;
                    }

                    /**
                     * Sets the value of the id property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Short }
                     *
                     */
                    public void setId(Short value) {
                        this.id = value;
                    }

                    /**
                     * Gets the value of the categorija property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Byte }
                     *
                     */
                    public Byte getCategorija() {
                        return categorija;
                    }

                    /**
                     * Sets the value of the categorija property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Byte }
                     *
                     */
                    public void setCategorija(Byte value) {
                        this.categorija = value;
                    }

                    /**
                     * Gets the value of the geoloc property.
                     *
                     * @return
                     *     possible object is
                     *     {@link String }
                     *
                     */
                    public String getGeoloc() {
                        return geoloc;
                    }

                    /**
                     * Sets the value of the geoloc property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *
                     */
                    public void setGeoloc(String value) {
                        this.geoloc = value;
                    }

                    /**
                     * Gets the value of the isActual property.
                     *
                     * @return
                     *     possible object is
                     *     {@link String }
                     *
                     */
                    public String getIsActual() {
                        return isActual;
                    }

                    /**
                     * Sets the value of the isActual property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *
                     */
                    public void setIsActual(String value) {
                        this.isActual = value;
                    }

                    /**
                     * Gets the value of the isInvest property.
                     *
                     * @return
                     *     possible object is
                     *     {@link String }
                     *
                     */
                    public String getIsInvest() {
                        return isInvest;
                    }

                    /**
                     * Sets the value of the isInvest property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *
                     */
                    public void setIsInvest(String value) {
                        this.isInvest = value;
                    }

                    /**
                     * Gets the value of the objectName property.
                     *
                     * @return
                     *     possible object is
                     *     {@link String }
                     *
                     */
                    public String getObjectName() {
                        return objectName;
                    }

                    /**
                     * Sets the value of the objectName property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *
                     */
                    public void setObjectName(String value) {
                        this.objectName = value;
                    }

                    /**
                     * Gets the value of the sZhilaya property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Float }
                     *
                     */
                    public Float getSZhilaya() {
                        return sZhilaya;
                    }

                    /**
                     * Sets the value of the sZhilaya property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Float }
                     *
                     */
                    public void setSZhilaya(Float value) {
                        this.sZhilaya = value;
                    }

                    /**
                     * Gets the value of the regionCode property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Byte }
                     *
                     */
                    public Byte getRegionCode() {
                        return regionCode;
                    }

                    /**
                     * Sets the value of the regionCode property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Byte }
                     *
                     */
                    public void setRegionCode(Byte value) {
                        this.regionCode = value;
                    }

                    /**
                     * Gets the value of the lastUpdated property.
                     *
                     * @return
                     *     possible object is
                     *     {@link XMLGregorianCalendar }
                     *
                     */
                    public XMLGregorianCalendar getLastUpdated() {
                        return lastUpdated;
                    }

                    /**
                     * Sets the value of the lastUpdated property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link XMLGregorianCalendar }
                     *
                     */
                    public void setLastUpdated(XMLGregorianCalendar value) {
                        this.lastUpdated = value;
                    }

                    /**
                     * Gets the value of the planYear property.
                     *
                     * @return
                     *     possible object is
                     *     {@link Short }
                     *
                     */
                    public Short getPlanYear() {
                        return planYear;
                    }

                    /**
                     * Sets the value of the planYear property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link Short }
                     *
                     */
                    public void setPlanYear(Short value) {
                        this.planYear = value;
                    }

                    /**
                     * Gets the value of the startDate property.
                     *
                     * @return
                     *     possible object is
                     *     {@link XMLGregorianCalendar }
                     *
                     */
                    public XMLGregorianCalendar getStartDate() {
                        return startDate;
                    }

                    /**
                     * Sets the value of the startDate property.
                     *
                     * @param value
                     *     allowed object is
                     *     {@link XMLGregorianCalendar }
                     *
                     */
                    public void setStartDate(XMLGregorianCalendar value) {
                        this.startDate = value;
                    }


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
                     *         &lt;element name="buildobject">
                     *           &lt;complexType>
                     *             &lt;simpleContent>
                     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
                     *                 &lt;attribute name="buildobject_id" type="{http://www.w3.org/2001/XMLSchema}int" />
                     *                 &lt;attribute name="bld_address" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *                 &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *                 &lt;attribute name="s_zhilaya" type="{http://www.w3.org/2001/XMLSchema}float" />
                     *                 &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
                     *                 &lt;attribute name="ugd_guid" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *               &lt;/extension>
                     *             &lt;/simpleContent>
                     *           &lt;/complexType>
                     *         &lt;/element>
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
                        "buildobject"
                    })
                    public static class Buildobjects {

                        @XmlElement(required = true)
                        protected Root.Region.Kvartal.StartsTep.StartTep.Buildobjects.Buildobject buildobject;

                        /**
                         * Gets the value of the buildobject property.
                         *
                         * @return
                         *     possible object is
                         *     {@link Root.Region.Kvartal.StartsTep.StartTep.Buildobjects.Buildobject }
                         *
                         */
                        public Root.Region.Kvartal.StartsTep.StartTep.Buildobjects.Buildobject getBuildobject() {
                            return buildobject;
                        }

                        /**
                         * Sets the value of the buildobject property.
                         *
                         * @param value
                         *     allowed object is
                         *     {@link Root.Region.Kvartal.StartsTep.StartTep.Buildobjects.Buildobject }
                         *
                         */
                        public void setBuildobject(Root.Region.Kvartal.StartsTep.StartTep.Buildobjects.Buildobject value) {
                            this.buildobject = value;
                        }


                        /**
                         * <p>Java class for anonymous complex type.
                         *
                         * <p>The following schema fragment specifies the expected content contained within this class.
                         *
                         * <pre>
                         * &lt;complexType>
                         *   &lt;simpleContent>
                         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
                         *       &lt;attribute name="buildobject_id" type="{http://www.w3.org/2001/XMLSchema}int" />
                         *       &lt;attribute name="bld_address" type="{http://www.w3.org/2001/XMLSchema}string" />
                         *       &lt;attribute name="geoloc" type="{http://www.w3.org/2001/XMLSchema}string" />
                         *       &lt;attribute name="s_zhilaya" type="{http://www.w3.org/2001/XMLSchema}float" />
                         *       &lt;attribute name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
                         *       &lt;attribute name="ugd_guid" type="{http://www.w3.org/2001/XMLSchema}string" />
                         *     &lt;/extension>
                         *   &lt;/simpleContent>
                         * &lt;/complexType>
                         * </pre>
                         *
                         *
                         */
                        @XmlAccessorType(XmlAccessType.FIELD)
                        @XmlType(name = "", propOrder = {
                            "value"
                        })
                        public static class Buildobject {

                            @XmlValue
                            protected String value;
                            @XmlAttribute(name = "buildobject_id")
                            protected Integer buildobjectId;
                            @XmlAttribute(name = "bld_address")
                            protected String bldAddress;
                            @XmlAttribute(name = "geoloc")
                            protected String geoloc;
                            @XmlAttribute(name = "s_zhilaya")
                            protected Float sZhilaya;
                            @XmlAttribute(name = "last_updated")
                            @XmlSchemaType(name = "dateTime")
                            protected XMLGregorianCalendar lastUpdated;
                            @XmlAttribute(name = "ugd_guid")
                            protected String ugdGuid;

                            /**
                             * Gets the value of the value property.
                             *
                             * @return
                             *     possible object is
                             *     {@link String }
                             *
                             */
                            public String getValue() {
                                return value;
                            }

                            /**
                             * Sets the value of the value property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *
                             */
                            public void setValue(String value) {
                                this.value = value;
                            }

                            /**
                             * Gets the value of the buildobjectId property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Integer }
                             *
                             */
                            public Integer getBuildobjectId() {
                                return buildobjectId;
                            }

                            /**
                             * Sets the value of the buildobjectId property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Integer }
                             *
                             */
                            public void setBuildobjectId(Integer value) {
                                this.buildobjectId = value;
                            }

                            /**
                             * Gets the value of the bldAddress property.
                             *
                             * @return
                             *     possible object is
                             *     {@link String }
                             *
                             */
                            public String getBldAddress() {
                                return bldAddress;
                            }

                            /**
                             * Sets the value of the bldAddress property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *
                             */
                            public void setBldAddress(String value) {
                                this.bldAddress = value;
                            }

                            /**
                             * Gets the value of the geoloc property.
                             *
                             * @return
                             *     possible object is
                             *     {@link String }
                             *
                             */
                            public String getGeoloc() {
                                return geoloc;
                            }

                            /**
                             * Sets the value of the geoloc property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *
                             */
                            public void setGeoloc(String value) {
                                this.geoloc = value;
                            }

                            /**
                             * Gets the value of the sZhilaya property.
                             *
                             * @return
                             *     possible object is
                             *     {@link Float }
                             *
                             */
                            public Float getSZhilaya() {
                                return sZhilaya;
                            }

                            /**
                             * Sets the value of the sZhilaya property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link Float }
                             *
                             */
                            public void setSZhilaya(Float value) {
                                this.sZhilaya = value;
                            }

                            /**
                             * Gets the value of the lastUpdated property.
                             *
                             * @return
                             *     possible object is
                             *     {@link XMLGregorianCalendar }
                             *
                             */
                            public XMLGregorianCalendar getLastUpdated() {
                                return lastUpdated;
                            }

                            /**
                             * Sets the value of the lastUpdated property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link XMLGregorianCalendar }
                             *
                             */
                            public void setLastUpdated(XMLGregorianCalendar value) {
                                this.lastUpdated = value;
                            }

                            /**
                             * Gets the value of the ugdGuid property.
                             *
                             * @return
                             *     possible object is
                             *     {@link String }
                             *
                             */
                            public String getUgdGuid() {
                                return ugdGuid;
                            }

                            /**
                             * Sets the value of the ugdGuid property.
                             *
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *
                             */
                            public void setUgdGuid(String value) {
                                this.ugdGuid = value;
                            }

                        }

                    }

                }

            }

        }

    }

}

