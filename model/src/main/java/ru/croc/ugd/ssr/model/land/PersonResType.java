package ru.croc.ugd.ssr.model.land;

import ru.reinform.cdp.utils.mapper.xml.LocalDateTimeXmlAdapter;
import ru.reinform.cdp.utils.mapper.xml.LocalDateXmlAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Формат JSON ответа для лэндинга
 * 
 * <p>Java class for personResType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="personResType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="snils" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="waiter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="marital" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contract" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="families" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="family" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="affairId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="roomType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="encumbrances" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="invalid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="regDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="statusLiving" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="spouse" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="residents" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="resident" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *                                                 &lt;element name="statusLiving" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="flat">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="snosUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="snosAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="snosFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="snosRooms" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="snosRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="snosLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="snosTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="snosFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="offer" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="letterDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="letterId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="letterStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="option" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="views" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="viewFlat" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                 &lt;element name="view" maxOccurs="unbounded">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;sequence>
 *                                                           &lt;element name="viewDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *                                                         &lt;/sequence>
 *                                                       &lt;/restriction>
 *                                                     &lt;/complexContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="decision" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="decisionResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                 &lt;element name="decisionDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *                                                 &lt;element name="fullPacket" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                 &lt;element name="contract" minOccurs="0">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;sequence>
 *                                                           &lt;element name="removalType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                           &lt;element name="contractProj" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                           &lt;element name="contractStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                           &lt;element name="contractSignDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *                                                           &lt;element name="contractSignPlace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                           &lt;element name="keyPass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                           &lt;element name="keyIssue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                           &lt;element name="newFlat" maxOccurs="unbounded" minOccurs="0">
 *                                                             &lt;complexType>
 *                                                               &lt;complexContent>
 *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                   &lt;sequence>
 *                                                                     &lt;element name="newUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                                     &lt;element name="newAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                     &lt;element name="newFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                                     &lt;element name="newRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                     &lt;element name="newLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                     &lt;element name="newTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                     &lt;element name="newFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                     &lt;element name="imperfections" minOccurs="0">
 *                                                                       &lt;complexType>
 *                                                                         &lt;complexContent>
 *                                                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                             &lt;sequence>
 *                                                                               &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                                               &lt;element name="imperfection" maxOccurs="unbounded">
 *                                                                                 &lt;complexType>
 *                                                                                   &lt;complexContent>
 *                                                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                                       &lt;sequence>
 *                                                                                         &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                                                         &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
 *                                                                                           &lt;complexType>
 *                                                                                             &lt;complexContent>
 *                                                                                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                                                 &lt;sequence>
 *                                                                                                   &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                                                                   &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                                                                 &lt;/sequence>
 *                                                                                               &lt;/restriction>
 *                                                                                             &lt;/complexContent>
 *                                                                                           &lt;/complexType>
 *                                                                                         &lt;/element>
 *                                                                                         &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *                                                                                         &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *                                                                                         &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *                                                                                       &lt;/sequence>
 *                                                                                     &lt;/restriction>
 *                                                                                   &lt;/complexContent>
 *                                                                                 &lt;/complexType>
 *                                                                               &lt;/element>
 *                                                                             &lt;/sequence>
 *                                                                           &lt;/restriction>
 *                                                                         &lt;/complexContent>
 *                                                                       &lt;/complexType>
 *                                                                     &lt;/element>
 *                                                                     &lt;element name="shipping" minOccurs="0">
 *                                                                       &lt;complexType>
 *                                                                         &lt;complexContent>
 *                                                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                             &lt;sequence>
 *                                                                               &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                               &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *                                                                               &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                               &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *                                                                               &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                               &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                             &lt;/sequence>
 *                                                                           &lt;/restriction>
 *                                                                         &lt;/complexContent>
 *                                                                       &lt;/complexType>
 *                                                                     &lt;/element>
 *                                                                   &lt;/sequence>
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
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="additions" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="addition" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="applicationId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                 &lt;element name="applicationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *                                                 &lt;element name="tradeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                 &lt;element name="moneyType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                 &lt;element name="decision" minOccurs="0">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;sequence>
 *                                                           &lt;element name="commissionDecisionResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                           &lt;element name="decisionDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *                                                           &lt;element name="auction" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                           &lt;element name="contract" minOccurs="0">
 *                                                             &lt;complexType>
 *                                                               &lt;complexContent>
 *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                   &lt;sequence>
 *                                                                     &lt;element name="contractProj" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                                     &lt;element name="contractStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                     &lt;element name="contractSignDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *                                                                     &lt;element name="contractSignPlace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                     &lt;element name="keyPass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                     &lt;element name="keyIssue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                     &lt;element name="newFlat" maxOccurs="unbounded" minOccurs="0">
 *                                                                       &lt;complexType>
 *                                                                         &lt;complexContent>
 *                                                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                             &lt;sequence>
 *                                                                               &lt;element name="newUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                                               &lt;element name="newAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                               &lt;element name="newFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                                               &lt;element name="newRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                               &lt;element name="newLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                               &lt;element name="newTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                               &lt;element name="newFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                               &lt;element name="imperfections" minOccurs="0">
 *                                                                                 &lt;complexType>
 *                                                                                   &lt;complexContent>
 *                                                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                                       &lt;sequence>
 *                                                                                         &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                                                         &lt;element name="imperfection" maxOccurs="unbounded">
 *                                                                                           &lt;complexType>
 *                                                                                             &lt;complexContent>
 *                                                                                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                                                 &lt;sequence>
 *                                                                                                   &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                                                                   &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
 *                                                                                                     &lt;complexType>
 *                                                                                                       &lt;complexContent>
 *                                                                                                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                                                           &lt;sequence>
 *                                                                                                             &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                                                                             &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                                                                           &lt;/sequence>
 *                                                                                                         &lt;/restriction>
 *                                                                                                       &lt;/complexContent>
 *                                                                                                     &lt;/complexType>
 *                                                                                                   &lt;/element>
 *                                                                                                   &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *                                                                                                   &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *                                                                                                   &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *                                                                                                 &lt;/sequence>
 *                                                                                               &lt;/restriction>
 *                                                                                             &lt;/complexContent>
 *                                                                                           &lt;/complexType>
 *                                                                                         &lt;/element>
 *                                                                                       &lt;/sequence>
 *                                                                                     &lt;/restriction>
 *                                                                                   &lt;/complexContent>
 *                                                                                 &lt;/complexType>
 *                                                                               &lt;/element>
 *                                                                               &lt;element name="shipping" minOccurs="0">
 *                                                                                 &lt;complexType>
 *                                                                                   &lt;complexContent>
 *                                                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                                       &lt;sequence>
 *                                                                                         &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                                         &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *                                                                                         &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                                         &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *                                                                                         &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                                         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                                                       &lt;/sequence>
 *                                                                                     &lt;/restriction>
 *                                                                                   &lt;/complexContent>
 *                                                                                 &lt;/complexType>
 *                                                                               &lt;/element>
 *                                                                             &lt;/sequence>
 *                                                                           &lt;/restriction>
 *                                                                         &lt;/complexContent>
 *                                                                       &lt;/complexType>
 *                                                                     &lt;/element>
 *                                                                   &lt;/sequence>
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
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="cipData" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="cipAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="cipPhone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="cipTime" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                     &lt;/sequence>
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
@XmlType(name = "personResType", propOrder = {
    "snils",
    "waiter",
    "marital",
    "contract",
    "families"
})
public class PersonResType {

    @XmlElement(required = true)
    protected String snils;
    protected String waiter;
    protected String marital;
    protected String contract;
    protected PersonResType.Families families;

    /**
     * Gets the value of the snils property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSnils() {
        return snils;
    }

    /**
     * Sets the value of the snils property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSnils(String value) {
        this.snils = value;
    }

    /**
     * Gets the value of the waiter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWaiter() {
        return waiter;
    }

    /**
     * Sets the value of the waiter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaiter(String value) {
        this.waiter = value;
    }

    /**
     * Gets the value of the marital property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMarital() {
        return marital;
    }

    /**
     * Sets the value of the marital property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarital(String value) {
        this.marital = value;
    }

    /**
     * Gets the value of the contract property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContract() {
        return contract;
    }

    /**
     * Sets the value of the contract property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContract(String value) {
        this.contract = value;
    }

    /**
     * Gets the value of the families property.
     * 
     * @return
     *     possible object is
     *     {@link PersonResType.Families }
     *     
     */
    public PersonResType.Families getFamilies() {
        return families;
    }

    /**
     * Sets the value of the families property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonResType.Families }
     *     
     */
    public void setFamilies(PersonResType.Families value) {
        this.families = value;
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
     *         &lt;element name="family" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="affairId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="roomType" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="encumbrances" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="invalid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="regDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="statusLiving" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="spouse" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="residents" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="resident" maxOccurs="unbounded">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
     *                                       &lt;element name="statusLiving" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="flat">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="snosUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="snosAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="snosFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="snosRooms" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="snosRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="snosLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="snosTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="snosFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="offer" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="letterDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="letterId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="letterStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="option" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="views" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="viewFlat" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                       &lt;element name="view" maxOccurs="unbounded">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;sequence>
     *                                                 &lt;element name="viewDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
     *                                               &lt;/sequence>
     *                                             &lt;/restriction>
     *                                           &lt;/complexContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="decision" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="decisionResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                       &lt;element name="decisionDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
     *                                       &lt;element name="fullPacket" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                       &lt;element name="contract" minOccurs="0">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;sequence>
     *                                                 &lt;element name="removalType" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                                 &lt;element name="contractProj" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                 &lt;element name="contractStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                 &lt;element name="contractSignDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
     *                                                 &lt;element name="contractSignPlace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                 &lt;element name="keyPass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                 &lt;element name="keyIssue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                 &lt;element name="newFlat" maxOccurs="unbounded" minOccurs="0">
     *                                                   &lt;complexType>
     *                                                     &lt;complexContent>
     *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                         &lt;sequence>
     *                                                           &lt;element name="newUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                                           &lt;element name="newAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                           &lt;element name="newFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                                           &lt;element name="newRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                           &lt;element name="newLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                           &lt;element name="newTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                           &lt;element name="newFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                           &lt;element name="imperfections" minOccurs="0">
     *                                                             &lt;complexType>
     *                                                               &lt;complexContent>
     *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                                   &lt;sequence>
     *                                                                     &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                                                     &lt;element name="imperfection" maxOccurs="unbounded">
     *                                                                       &lt;complexType>
     *                                                                         &lt;complexContent>
     *                                                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                                             &lt;sequence>
     *                                                                               &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                                                               &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
     *                                                                                 &lt;complexType>
     *                                                                                   &lt;complexContent>
     *                                                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                                                       &lt;sequence>
     *                                                                                         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                                                                         &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                                                                       &lt;/sequence>
     *                                                                                     &lt;/restriction>
     *                                                                                   &lt;/complexContent>
     *                                                                                 &lt;/complexType>
     *                                                                               &lt;/element>
     *                                                                               &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
     *                                                                               &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
     *                                                                               &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
     *                                                                             &lt;/sequence>
     *                                                                           &lt;/restriction>
     *                                                                         &lt;/complexContent>
     *                                                                       &lt;/complexType>
     *                                                                     &lt;/element>
     *                                                                   &lt;/sequence>
     *                                                                 &lt;/restriction>
     *                                                               &lt;/complexContent>
     *                                                             &lt;/complexType>
     *                                                           &lt;/element>
     *                                                           &lt;element name="shipping" minOccurs="0">
     *                                                             &lt;complexType>
     *                                                               &lt;complexContent>
     *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                                   &lt;sequence>
     *                                                                     &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                                     &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
     *                                                                     &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                                     &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
     *                                                                     &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                                     &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                                   &lt;/sequence>
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
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="additions" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="addition" maxOccurs="unbounded">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="applicationId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                       &lt;element name="applicationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
     *                                       &lt;element name="tradeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                       &lt;element name="moneyType" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                       &lt;element name="decision" minOccurs="0">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;sequence>
     *                                                 &lt;element name="commissionDecisionResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                                 &lt;element name="decisionDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
     *                                                 &lt;element name="auction" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                                 &lt;element name="contract" minOccurs="0">
     *                                                   &lt;complexType>
     *                                                     &lt;complexContent>
     *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                         &lt;sequence>
     *                                                           &lt;element name="contractProj" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                                           &lt;element name="contractStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                           &lt;element name="contractSignDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
     *                                                           &lt;element name="contractSignPlace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                           &lt;element name="keyPass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                           &lt;element name="keyIssue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                           &lt;element name="newFlat" maxOccurs="unbounded" minOccurs="0">
     *                                                             &lt;complexType>
     *                                                               &lt;complexContent>
     *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                                   &lt;sequence>
     *                                                                     &lt;element name="newUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                                                     &lt;element name="newAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                                     &lt;element name="newFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                                                     &lt;element name="newRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                                     &lt;element name="newLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                                     &lt;element name="newTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                                     &lt;element name="newFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                                     &lt;element name="imperfections" minOccurs="0">
     *                                                                       &lt;complexType>
     *                                                                         &lt;complexContent>
     *                                                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                                             &lt;sequence>
     *                                                                               &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                                                               &lt;element name="imperfection" maxOccurs="unbounded">
     *                                                                                 &lt;complexType>
     *                                                                                   &lt;complexContent>
     *                                                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                                                       &lt;sequence>
     *                                                                                         &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                                                                         &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
     *                                                                                           &lt;complexType>
     *                                                                                             &lt;complexContent>
     *                                                                                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                                                                 &lt;sequence>
     *                                                                                                   &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                                                                                   &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                                                                                 &lt;/sequence>
     *                                                                                               &lt;/restriction>
     *                                                                                             &lt;/complexContent>
     *                                                                                           &lt;/complexType>
     *                                                                                         &lt;/element>
     *                                                                                         &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
     *                                                                                         &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
     *                                                                                         &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
     *                                                                                       &lt;/sequence>
     *                                                                                     &lt;/restriction>
     *                                                                                   &lt;/complexContent>
     *                                                                                 &lt;/complexType>
     *                                                                               &lt;/element>
     *                                                                             &lt;/sequence>
     *                                                                           &lt;/restriction>
     *                                                                         &lt;/complexContent>
     *                                                                       &lt;/complexType>
     *                                                                     &lt;/element>
     *                                                                     &lt;element name="shipping" minOccurs="0">
     *                                                                       &lt;complexType>
     *                                                                         &lt;complexContent>
     *                                                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                                             &lt;sequence>
     *                                                                               &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                                               &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
     *                                                                               &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                                               &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
     *                                                                               &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                                               &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                                                             &lt;/sequence>
     *                                                                           &lt;/restriction>
     *                                                                         &lt;/complexContent>
     *                                                                       &lt;/complexType>
     *                                                                     &lt;/element>
     *                                                                   &lt;/sequence>
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
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="cipData" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="cipAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="cipPhone" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="cipTime" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                           &lt;/sequence>
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
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "family"
    })
    public static class Families {

        @XmlElement(required = true)
        protected List<PersonResType.Families.Family> family;

        /**
         * Gets the value of the family property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the family property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFamily().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link PersonResType.Families.Family }
         * 
         * 
         */
        public List<PersonResType.Families.Family> getFamily() {
            if (family == null) {
                family = new ArrayList<PersonResType.Families.Family>();
            }
            return this.family;
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
         *         &lt;element name="affairId" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="roomType" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="encumbrances" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="invalid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="regDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="statusLiving" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="spouse" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="residents" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="resident" maxOccurs="unbounded">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
         *                             &lt;element name="statusLiving" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="flat">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="snosUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="snosAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="snosFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="snosRooms" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="snosRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="snosLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="snosTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="snosFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="offer" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="letterDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="letterId" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="letterStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="option" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="views" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="viewFlat" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                             &lt;element name="view" maxOccurs="unbounded">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;sequence>
         *                                       &lt;element name="viewDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
         *                                     &lt;/sequence>
         *                                   &lt;/restriction>
         *                                 &lt;/complexContent>
         *                               &lt;/complexType>
         *                             &lt;/element>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="decision" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="decisionResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                             &lt;element name="decisionDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
         *                             &lt;element name="fullPacket" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                             &lt;element name="contract" minOccurs="0">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;sequence>
         *                                       &lt;element name="removalType" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                                       &lt;element name="contractProj" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                       &lt;element name="contractStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                       &lt;element name="contractSignDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
         *                                       &lt;element name="contractSignPlace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                       &lt;element name="keyPass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                       &lt;element name="keyIssue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                       &lt;element name="newFlat" maxOccurs="unbounded" minOccurs="0">
         *                                         &lt;complexType>
         *                                           &lt;complexContent>
         *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                               &lt;sequence>
         *                                                 &lt;element name="newUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                                                 &lt;element name="newAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                 &lt;element name="newFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                                                 &lt;element name="newRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                 &lt;element name="newLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                 &lt;element name="newTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                 &lt;element name="newFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                 &lt;element name="imperfections" minOccurs="0">
         *                                                   &lt;complexType>
         *                                                     &lt;complexContent>
         *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                                         &lt;sequence>
         *                                                           &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                                                           &lt;element name="imperfection" maxOccurs="unbounded">
         *                                                             &lt;complexType>
         *                                                               &lt;complexContent>
         *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                                                   &lt;sequence>
         *                                                                     &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                                                                     &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
         *                                                                       &lt;complexType>
         *                                                                         &lt;complexContent>
         *                                                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                                                             &lt;sequence>
         *                                                                               &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                                                                               &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                                                                             &lt;/sequence>
         *                                                                           &lt;/restriction>
         *                                                                         &lt;/complexContent>
         *                                                                       &lt;/complexType>
         *                                                                     &lt;/element>
         *                                                                     &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
         *                                                                     &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
         *                                                                     &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
         *                                                                   &lt;/sequence>
         *                                                                 &lt;/restriction>
         *                                                               &lt;/complexContent>
         *                                                             &lt;/complexType>
         *                                                           &lt;/element>
         *                                                         &lt;/sequence>
         *                                                       &lt;/restriction>
         *                                                     &lt;/complexContent>
         *                                                   &lt;/complexType>
         *                                                 &lt;/element>
         *                                                 &lt;element name="shipping" minOccurs="0">
         *                                                   &lt;complexType>
         *                                                     &lt;complexContent>
         *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                                         &lt;sequence>
         *                                                           &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                           &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
         *                                                           &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                           &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
         *                                                           &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                           &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                         &lt;/sequence>
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
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="additions" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="addition" maxOccurs="unbounded">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="applicationId" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                             &lt;element name="applicationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
         *                             &lt;element name="tradeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                             &lt;element name="moneyType" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                             &lt;element name="decision" minOccurs="0">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;sequence>
         *                                       &lt;element name="commissionDecisionResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                                       &lt;element name="decisionDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
         *                                       &lt;element name="auction" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                                       &lt;element name="contract" minOccurs="0">
         *                                         &lt;complexType>
         *                                           &lt;complexContent>
         *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                               &lt;sequence>
         *                                                 &lt;element name="contractProj" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                                                 &lt;element name="contractStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                 &lt;element name="contractSignDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
         *                                                 &lt;element name="contractSignPlace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                 &lt;element name="keyPass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                 &lt;element name="keyIssue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                 &lt;element name="newFlat" maxOccurs="unbounded" minOccurs="0">
         *                                                   &lt;complexType>
         *                                                     &lt;complexContent>
         *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                                         &lt;sequence>
         *                                                           &lt;element name="newUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                                                           &lt;element name="newAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                           &lt;element name="newFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                                                           &lt;element name="newRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                           &lt;element name="newLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                           &lt;element name="newTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                           &lt;element name="newFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                           &lt;element name="imperfections" minOccurs="0">
         *                                                             &lt;complexType>
         *                                                               &lt;complexContent>
         *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                                                   &lt;sequence>
         *                                                                     &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                                                                     &lt;element name="imperfection" maxOccurs="unbounded">
         *                                                                       &lt;complexType>
         *                                                                         &lt;complexContent>
         *                                                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                                                             &lt;sequence>
         *                                                                               &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                                                                               &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
         *                                                                                 &lt;complexType>
         *                                                                                   &lt;complexContent>
         *                                                                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                                                                       &lt;sequence>
         *                                                                                         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                                                                                         &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                                                                                       &lt;/sequence>
         *                                                                                     &lt;/restriction>
         *                                                                                   &lt;/complexContent>
         *                                                                                 &lt;/complexType>
         *                                                                               &lt;/element>
         *                                                                               &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
         *                                                                               &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
         *                                                                               &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
         *                                                                             &lt;/sequence>
         *                                                                           &lt;/restriction>
         *                                                                         &lt;/complexContent>
         *                                                                       &lt;/complexType>
         *                                                                     &lt;/element>
         *                                                                   &lt;/sequence>
         *                                                                 &lt;/restriction>
         *                                                               &lt;/complexContent>
         *                                                             &lt;/complexType>
         *                                                           &lt;/element>
         *                                                           &lt;element name="shipping" minOccurs="0">
         *                                                             &lt;complexType>
         *                                                               &lt;complexContent>
         *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                                                   &lt;sequence>
         *                                                                     &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                                     &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
         *                                                                     &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                                     &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
         *                                                                     &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                                     &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                                                                   &lt;/sequence>
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
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="cipData" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="cipAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="cipPhone" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="cipTime" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                 &lt;/sequence>
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
            "affairId",
            "roomType",
            "encumbrances",
            "invalid",
            "regDate",
            "statusLiving",
            "spouse",
            "residents",
            "flat",
            "offer",
            "additions",
            "cipData"
        })
        public static class Family {

            @XmlElement(required = true)
            protected String affairId;
            @XmlElement(required = true)
            protected String roomType;
            protected String encumbrances;
            protected String invalid;
            protected String regDate;
            @XmlElement(required = true)
            protected String statusLiving;
            protected String spouse;
            protected PersonResType.Families.Family.Residents residents;
            @XmlElement(required = true)
            protected PersonResType.Families.Family.Flat flat;
            protected PersonResType.Families.Family.Offer offer;
            protected PersonResType.Families.Family.Additions additions;
            protected PersonResType.Families.Family.CipData cipData;

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
             * Gets the value of the roomType property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRoomType() {
                return roomType;
            }

            /**
             * Sets the value of the roomType property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRoomType(String value) {
                this.roomType = value;
            }

            /**
             * Gets the value of the encumbrances property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getEncumbrances() {
                return encumbrances;
            }

            /**
             * Sets the value of the encumbrances property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setEncumbrances(String value) {
                this.encumbrances = value;
            }

            /**
             * Gets the value of the invalid property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getInvalid() {
                return invalid;
            }

            /**
             * Sets the value of the invalid property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setInvalid(String value) {
                this.invalid = value;
            }

            /**
             * Gets the value of the regDate property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRegDate() {
                return regDate;
            }

            /**
             * Sets the value of the regDate property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRegDate(String value) {
                this.regDate = value;
            }

            /**
             * Gets the value of the statusLiving property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getStatusLiving() {
                return statusLiving;
            }

            /**
             * Sets the value of the statusLiving property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setStatusLiving(String value) {
                this.statusLiving = value;
            }

            /**
             * Gets the value of the spouse property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSpouse() {
                return spouse;
            }

            /**
             * Sets the value of the spouse property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSpouse(String value) {
                this.spouse = value;
            }

            /**
             * Gets the value of the residents property.
             * 
             * @return
             *     possible object is
             *     {@link PersonResType.Families.Family.Residents }
             *     
             */
            public PersonResType.Families.Family.Residents getResidents() {
                return residents;
            }

            /**
             * Sets the value of the residents property.
             * 
             * @param value
             *     allowed object is
             *     {@link PersonResType.Families.Family.Residents }
             *     
             */
            public void setResidents(PersonResType.Families.Family.Residents value) {
                this.residents = value;
            }

            /**
             * Gets the value of the flat property.
             * 
             * @return
             *     possible object is
             *     {@link PersonResType.Families.Family.Flat }
             *     
             */
            public PersonResType.Families.Family.Flat getFlat() {
                return flat;
            }

            /**
             * Sets the value of the flat property.
             * 
             * @param value
             *     allowed object is
             *     {@link PersonResType.Families.Family.Flat }
             *     
             */
            public void setFlat(PersonResType.Families.Family.Flat value) {
                this.flat = value;
            }

            /**
             * Gets the value of the offer property.
             * 
             * @return
             *     possible object is
             *     {@link PersonResType.Families.Family.Offer }
             *     
             */
            public PersonResType.Families.Family.Offer getOffer() {
                return offer;
            }

            /**
             * Sets the value of the offer property.
             * 
             * @param value
             *     allowed object is
             *     {@link PersonResType.Families.Family.Offer }
             *     
             */
            public void setOffer(PersonResType.Families.Family.Offer value) {
                this.offer = value;
            }

            /**
             * Gets the value of the additions property.
             * 
             * @return
             *     possible object is
             *     {@link PersonResType.Families.Family.Additions }
             *     
             */
            public PersonResType.Families.Family.Additions getAdditions() {
                return additions;
            }

            /**
             * Sets the value of the additions property.
             * 
             * @param value
             *     allowed object is
             *     {@link PersonResType.Families.Family.Additions }
             *     
             */
            public void setAdditions(PersonResType.Families.Family.Additions value) {
                this.additions = value;
            }

            /**
             * Gets the value of the cipData property.
             * 
             * @return
             *     possible object is
             *     {@link PersonResType.Families.Family.CipData }
             *     
             */
            public PersonResType.Families.Family.CipData getCipData() {
                return cipData;
            }

            /**
             * Sets the value of the cipData property.
             * 
             * @param value
             *     allowed object is
             *     {@link PersonResType.Families.Family.CipData }
             *     
             */
            public void setCipData(PersonResType.Families.Family.CipData value) {
                this.cipData = value;
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
             *         &lt;element name="addition" maxOccurs="unbounded">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="applicationId" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                   &lt;element name="applicationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
             *                   &lt;element name="tradeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                   &lt;element name="moneyType" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                   &lt;element name="decision" minOccurs="0">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;sequence>
             *                             &lt;element name="commissionDecisionResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                             &lt;element name="decisionDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
             *                             &lt;element name="auction" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                             &lt;element name="contract" minOccurs="0">
             *                               &lt;complexType>
             *                                 &lt;complexContent>
             *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                     &lt;sequence>
             *                                       &lt;element name="contractProj" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                                       &lt;element name="contractStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                       &lt;element name="contractSignDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
             *                                       &lt;element name="contractSignPlace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                       &lt;element name="keyPass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                       &lt;element name="keyIssue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                       &lt;element name="newFlat" maxOccurs="unbounded" minOccurs="0">
             *                                         &lt;complexType>
             *                                           &lt;complexContent>
             *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                               &lt;sequence>
             *                                                 &lt;element name="newUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                                                 &lt;element name="newAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                                 &lt;element name="newFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                                                 &lt;element name="newRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                                 &lt;element name="newLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                                 &lt;element name="newTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                                 &lt;element name="newFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                                 &lt;element name="imperfections" minOccurs="0">
             *                                                   &lt;complexType>
             *                                                     &lt;complexContent>
             *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                                         &lt;sequence>
             *                                                           &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                                                           &lt;element name="imperfection" maxOccurs="unbounded">
             *                                                             &lt;complexType>
             *                                                               &lt;complexContent>
             *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                                                   &lt;sequence>
             *                                                                     &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                                                                     &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
             *                                                                       &lt;complexType>
             *                                                                         &lt;complexContent>
             *                                                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                                                             &lt;sequence>
             *                                                                               &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                                                                               &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                                                                             &lt;/sequence>
             *                                                                           &lt;/restriction>
             *                                                                         &lt;/complexContent>
             *                                                                       &lt;/complexType>
             *                                                                     &lt;/element>
             *                                                                     &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
             *                                                                     &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
             *                                                                     &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
             *                                                                   &lt;/sequence>
             *                                                                 &lt;/restriction>
             *                                                               &lt;/complexContent>
             *                                                             &lt;/complexType>
             *                                                           &lt;/element>
             *                                                         &lt;/sequence>
             *                                                       &lt;/restriction>
             *                                                     &lt;/complexContent>
             *                                                   &lt;/complexType>
             *                                                 &lt;/element>
             *                                                 &lt;element name="shipping" minOccurs="0">
             *                                                   &lt;complexType>
             *                                                     &lt;complexContent>
             *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                                         &lt;sequence>
             *                                                           &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                                           &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
             *                                                           &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                                           &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
             *                                                           &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                                           &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                                         &lt;/sequence>
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
                "addition"
            })
            public static class Additions {

                @XmlElement(required = true)
                protected List<PersonResType.Families.Family.Additions.Addition> addition;

                /**
                 * Gets the value of the addition property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the addition property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getAddition().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link PersonResType.Families.Family.Additions.Addition }
                 * 
                 * 
                 */
                public List<PersonResType.Families.Family.Additions.Addition> getAddition() {
                    if (addition == null) {
                        addition = new ArrayList<PersonResType.Families.Family.Additions.Addition>();
                    }
                    return this.addition;
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
                 *         &lt;element name="applicationId" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *         &lt;element name="applicationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                 *         &lt;element name="tradeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *         &lt;element name="moneyType" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *         &lt;element name="decision" minOccurs="0">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;sequence>
                 *                   &lt;element name="commissionDecisionResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *                   &lt;element name="decisionDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                 *                   &lt;element name="auction" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *                   &lt;element name="contract" minOccurs="0">
                 *                     &lt;complexType>
                 *                       &lt;complexContent>
                 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                           &lt;sequence>
                 *                             &lt;element name="contractProj" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *                             &lt;element name="contractStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                             &lt;element name="contractSignDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                 *                             &lt;element name="contractSignPlace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                             &lt;element name="keyPass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                             &lt;element name="keyIssue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                             &lt;element name="newFlat" maxOccurs="unbounded" minOccurs="0">
                 *                               &lt;complexType>
                 *                                 &lt;complexContent>
                 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                                     &lt;sequence>
                 *                                       &lt;element name="newUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *                                       &lt;element name="newAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                                       &lt;element name="newFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *                                       &lt;element name="newRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                                       &lt;element name="newLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                                       &lt;element name="newTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                                       &lt;element name="newFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                                       &lt;element name="imperfections" minOccurs="0">
                 *                                         &lt;complexType>
                 *                                           &lt;complexContent>
                 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                                               &lt;sequence>
                 *                                                 &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *                                                 &lt;element name="imperfection" maxOccurs="unbounded">
                 *                                                   &lt;complexType>
                 *                                                     &lt;complexContent>
                 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                                                         &lt;sequence>
                 *                                                           &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *                                                           &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
                 *                                                             &lt;complexType>
                 *                                                               &lt;complexContent>
                 *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                                                                   &lt;sequence>
                 *                                                                     &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *                                                                     &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *                                                                   &lt;/sequence>
                 *                                                                 &lt;/restriction>
                 *                                                               &lt;/complexContent>
                 *                                                             &lt;/complexType>
                 *                                                           &lt;/element>
                 *                                                           &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                 *                                                           &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                 *                                                           &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
                 *                                                         &lt;/sequence>
                 *                                                       &lt;/restriction>
                 *                                                     &lt;/complexContent>
                 *                                                   &lt;/complexType>
                 *                                                 &lt;/element>
                 *                                               &lt;/sequence>
                 *                                             &lt;/restriction>
                 *                                           &lt;/complexContent>
                 *                                         &lt;/complexType>
                 *                                       &lt;/element>
                 *                                       &lt;element name="shipping" minOccurs="0">
                 *                                         &lt;complexType>
                 *                                           &lt;complexContent>
                 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                                               &lt;sequence>
                 *                                                 &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                                                 &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                 *                                                 &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                                                 &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                 *                                                 &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                                                 &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                                               &lt;/sequence>
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
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "applicationId",
                    "applicationDate",
                    "tradeType",
                    "moneyType",
                    "decision"
                })
                public static class Addition {

                    @XmlElement(required = true)
                    protected String applicationId;
                    @XmlElement(required = true, type = String.class)
                    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
                    @XmlSchemaType(name = "date")
                    protected LocalDate applicationDate;
                    @XmlElement(required = true)
                    protected String tradeType;
                    @XmlElement(required = true)
                    protected String moneyType;
                    protected PersonResType.Families.Family.Additions.Addition.Decision decision;

                    /**
                     * Gets the value of the applicationId property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getApplicationId() {
                        return applicationId;
                    }

                    /**
                     * Sets the value of the applicationId property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setApplicationId(String value) {
                        this.applicationId = value;
                    }

                    /**
                     * Gets the value of the applicationDate property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link LocalDate }
                     *     
                     */
                    public LocalDate getApplicationDate() {
                        return applicationDate;
                    }

                    /**
                     * Sets the value of the applicationDate property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link LocalDate }
                     *     
                     */
                    public void setApplicationDate(LocalDate value) {
                        this.applicationDate = value;
                    }

                    /**
                     * Gets the value of the tradeType property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getTradeType() {
                        return tradeType;
                    }

                    /**
                     * Sets the value of the tradeType property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setTradeType(String value) {
                        this.tradeType = value;
                    }

                    /**
                     * Gets the value of the moneyType property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getMoneyType() {
                        return moneyType;
                    }

                    /**
                     * Sets the value of the moneyType property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setMoneyType(String value) {
                        this.moneyType = value;
                    }

                    /**
                     * Gets the value of the decision property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link PersonResType.Families.Family.Additions.Addition.Decision }
                     *     
                     */
                    public PersonResType.Families.Family.Additions.Addition.Decision getDecision() {
                        return decision;
                    }

                    /**
                     * Sets the value of the decision property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link PersonResType.Families.Family.Additions.Addition.Decision }
                     *     
                     */
                    public void setDecision(PersonResType.Families.Family.Additions.Addition.Decision value) {
                        this.decision = value;
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
                     *         &lt;element name="commissionDecisionResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
                     *         &lt;element name="decisionDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                     *         &lt;element name="auction" type="{http://www.w3.org/2001/XMLSchema}string"/>
                     *         &lt;element name="contract" minOccurs="0">
                     *           &lt;complexType>
                     *             &lt;complexContent>
                     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                 &lt;sequence>
                     *                   &lt;element name="contractProj" type="{http://www.w3.org/2001/XMLSchema}string"/>
                     *                   &lt;element name="contractStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                   &lt;element name="contractSignDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                     *                   &lt;element name="contractSignPlace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                   &lt;element name="keyPass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                   &lt;element name="keyIssue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                   &lt;element name="newFlat" maxOccurs="unbounded" minOccurs="0">
                     *                     &lt;complexType>
                     *                       &lt;complexContent>
                     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                           &lt;sequence>
                     *                             &lt;element name="newUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
                     *                             &lt;element name="newAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                             &lt;element name="newFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
                     *                             &lt;element name="newRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                             &lt;element name="newLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                             &lt;element name="newTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                             &lt;element name="newFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                             &lt;element name="imperfections" minOccurs="0">
                     *                               &lt;complexType>
                     *                                 &lt;complexContent>
                     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                                     &lt;sequence>
                     *                                       &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
                     *                                       &lt;element name="imperfection" maxOccurs="unbounded">
                     *                                         &lt;complexType>
                     *                                           &lt;complexContent>
                     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                                               &lt;sequence>
                     *                                                 &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
                     *                                                 &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
                     *                                                   &lt;complexType>
                     *                                                     &lt;complexContent>
                     *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                                                         &lt;sequence>
                     *                                                           &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
                     *                                                           &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
                     *                                                         &lt;/sequence>
                     *                                                       &lt;/restriction>
                     *                                                     &lt;/complexContent>
                     *                                                   &lt;/complexType>
                     *                                                 &lt;/element>
                     *                                                 &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                     *                                                 &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                     *                                                 &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
                     *                                               &lt;/sequence>
                     *                                             &lt;/restriction>
                     *                                           &lt;/complexContent>
                     *                                         &lt;/complexType>
                     *                                       &lt;/element>
                     *                                     &lt;/sequence>
                     *                                   &lt;/restriction>
                     *                                 &lt;/complexContent>
                     *                               &lt;/complexType>
                     *                             &lt;/element>
                     *                             &lt;element name="shipping" minOccurs="0">
                     *                               &lt;complexType>
                     *                                 &lt;complexContent>
                     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                                     &lt;sequence>
                     *                                       &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                                       &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                     *                                       &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                                       &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                     *                                       &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                                       &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                                     &lt;/sequence>
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
                        "commissionDecisionResult",
                        "decisionDate",
                        "auction",
                        "contract"
                    })
                    public static class Decision {

                        @XmlElement(required = true)
                        protected String commissionDecisionResult;
                        @XmlElement(required = true, type = String.class)
                        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
                        @XmlSchemaType(name = "date")
                        protected LocalDate decisionDate;
                        @XmlElement(required = true)
                        protected String auction;
                        protected PersonResType.Families.Family.Additions.Addition.Decision.Contract contract;

                        /**
                         * Gets the value of the commissionDecisionResult property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getCommissionDecisionResult() {
                            return commissionDecisionResult;
                        }

                        /**
                         * Sets the value of the commissionDecisionResult property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setCommissionDecisionResult(String value) {
                            this.commissionDecisionResult = value;
                        }

                        /**
                         * Gets the value of the decisionDate property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link LocalDate }
                         *     
                         */
                        public LocalDate getDecisionDate() {
                            return decisionDate;
                        }

                        /**
                         * Sets the value of the decisionDate property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link LocalDate }
                         *     
                         */
                        public void setDecisionDate(LocalDate value) {
                            this.decisionDate = value;
                        }

                        /**
                         * Gets the value of the auction property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getAuction() {
                            return auction;
                        }

                        /**
                         * Sets the value of the auction property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setAuction(String value) {
                            this.auction = value;
                        }

                        /**
                         * Gets the value of the contract property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link PersonResType.Families.Family.Additions.Addition.Decision.Contract }
                         *     
                         */
                        public PersonResType.Families.Family.Additions.Addition.Decision.Contract getContract() {
                            return contract;
                        }

                        /**
                         * Sets the value of the contract property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link PersonResType.Families.Family.Additions.Addition.Decision.Contract }
                         *     
                         */
                        public void setContract(PersonResType.Families.Family.Additions.Addition.Decision.Contract value) {
                            this.contract = value;
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
                         *         &lt;element name="contractProj" type="{http://www.w3.org/2001/XMLSchema}string"/>
                         *         &lt;element name="contractStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *         &lt;element name="contractSignDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                         *         &lt;element name="contractSignPlace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *         &lt;element name="keyPass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *         &lt;element name="keyIssue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *         &lt;element name="newFlat" maxOccurs="unbounded" minOccurs="0">
                         *           &lt;complexType>
                         *             &lt;complexContent>
                         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                         *                 &lt;sequence>
                         *                   &lt;element name="newUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
                         *                   &lt;element name="newAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *                   &lt;element name="newFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
                         *                   &lt;element name="newRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *                   &lt;element name="newLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *                   &lt;element name="newTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *                   &lt;element name="newFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *                   &lt;element name="imperfections" minOccurs="0">
                         *                     &lt;complexType>
                         *                       &lt;complexContent>
                         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                         *                           &lt;sequence>
                         *                             &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
                         *                             &lt;element name="imperfection" maxOccurs="unbounded">
                         *                               &lt;complexType>
                         *                                 &lt;complexContent>
                         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                         *                                     &lt;sequence>
                         *                                       &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
                         *                                       &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
                         *                                         &lt;complexType>
                         *                                           &lt;complexContent>
                         *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                         *                                               &lt;sequence>
                         *                                                 &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
                         *                                                 &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
                         *                                               &lt;/sequence>
                         *                                             &lt;/restriction>
                         *                                           &lt;/complexContent>
                         *                                         &lt;/complexType>
                         *                                       &lt;/element>
                         *                                       &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                         *                                       &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                         *                                       &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
                         *                                     &lt;/sequence>
                         *                                   &lt;/restriction>
                         *                                 &lt;/complexContent>
                         *                               &lt;/complexType>
                         *                             &lt;/element>
                         *                           &lt;/sequence>
                         *                         &lt;/restriction>
                         *                       &lt;/complexContent>
                         *                     &lt;/complexType>
                         *                   &lt;/element>
                         *                   &lt;element name="shipping" minOccurs="0">
                         *                     &lt;complexType>
                         *                       &lt;complexContent>
                         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                         *                           &lt;sequence>
                         *                             &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *                             &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                         *                             &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *                             &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                         *                             &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *                             &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *                           &lt;/sequence>
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
                         *     &lt;/restriction>
                         *   &lt;/complexContent>
                         * &lt;/complexType>
                         * </pre>
                         * 
                         * 
                         */
                        @XmlAccessorType(XmlAccessType.FIELD)
                        @XmlType(name = "", propOrder = {
                            "contractProj",
                            "contractStatus",
                            "contractSignDate",
                            "contractSignPlace",
                            "keyPass",
                            "keyIssue",
                            "newFlat"
                        })
                        public static class Contract {

                            @XmlElement(required = true)
                            protected String contractProj;
                            protected String contractStatus;
                            @XmlElement(type = String.class)
                            @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
                            @XmlSchemaType(name = "date")
                            protected LocalDate contractSignDate;
                            protected String contractSignPlace;
                            protected String keyPass;
                            protected String keyIssue;
                            protected List<PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat> newFlat;

                            /**
                             * Gets the value of the contractProj property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getContractProj() {
                                return contractProj;
                            }

                            /**
                             * Sets the value of the contractProj property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setContractProj(String value) {
                                this.contractProj = value;
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
                             * Gets the value of the contractSignDate property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link LocalDate }
                             *     
                             */
                            public LocalDate getContractSignDate() {
                                return contractSignDate;
                            }

                            /**
                             * Sets the value of the contractSignDate property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link LocalDate }
                             *     
                             */
                            public void setContractSignDate(LocalDate value) {
                                this.contractSignDate = value;
                            }

                            /**
                             * Gets the value of the contractSignPlace property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getContractSignPlace() {
                                return contractSignPlace;
                            }

                            /**
                             * Sets the value of the contractSignPlace property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setContractSignPlace(String value) {
                                this.contractSignPlace = value;
                            }

                            /**
                             * Gets the value of the keyPass property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getKeyPass() {
                                return keyPass;
                            }

                            /**
                             * Sets the value of the keyPass property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setKeyPass(String value) {
                                this.keyPass = value;
                            }

                            /**
                             * Gets the value of the keyIssue property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getKeyIssue() {
                                return keyIssue;
                            }

                            /**
                             * Sets the value of the keyIssue property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setKeyIssue(String value) {
                                this.keyIssue = value;
                            }

                            /**
                             * Gets the value of the newFlat property.
                             * 
                             * <p>
                             * This accessor method returns a reference to the live list,
                             * not a snapshot. Therefore any modification you make to the
                             * returned list will be present inside the JAXB object.
                             * This is why there is not a <CODE>set</CODE> method for the newFlat property.
                             * 
                             * <p>
                             * For example, to add a new item, do as follows:
                             * <pre>
                             *    getNewFlat().add(newItem);
                             * </pre>
                             * 
                             * 
                             * <p>
                             * Objects of the following type(s) are allowed in the list
                             * {@link PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat }
                             * 
                             * 
                             */
                            public List<PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat> getNewFlat() {
                                if (newFlat == null) {
                                    newFlat = new ArrayList<PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat>();
                                }
                                return this.newFlat;
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
                             *         &lt;element name="newUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
                             *         &lt;element name="newAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                             *         &lt;element name="newFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
                             *         &lt;element name="newRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                             *         &lt;element name="newLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                             *         &lt;element name="newTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                             *         &lt;element name="newFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                             *         &lt;element name="imperfections" minOccurs="0">
                             *           &lt;complexType>
                             *             &lt;complexContent>
                             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                             *                 &lt;sequence>
                             *                   &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
                             *                   &lt;element name="imperfection" maxOccurs="unbounded">
                             *                     &lt;complexType>
                             *                       &lt;complexContent>
                             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                             *                           &lt;sequence>
                             *                             &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
                             *                             &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
                             *                               &lt;complexType>
                             *                                 &lt;complexContent>
                             *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                             *                                     &lt;sequence>
                             *                                       &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
                             *                                       &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
                             *                                     &lt;/sequence>
                             *                                   &lt;/restriction>
                             *                                 &lt;/complexContent>
                             *                               &lt;/complexType>
                             *                             &lt;/element>
                             *                             &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                             *                             &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                             *                             &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
                             *                           &lt;/sequence>
                             *                         &lt;/restriction>
                             *                       &lt;/complexContent>
                             *                     &lt;/complexType>
                             *                   &lt;/element>
                             *                 &lt;/sequence>
                             *               &lt;/restriction>
                             *             &lt;/complexContent>
                             *           &lt;/complexType>
                             *         &lt;/element>
                             *         &lt;element name="shipping" minOccurs="0">
                             *           &lt;complexType>
                             *             &lt;complexContent>
                             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                             *                 &lt;sequence>
                             *                   &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                             *                   &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                             *                   &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                             *                   &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                             *                   &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                             *                   &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                             *                 &lt;/sequence>
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
                                "newUNOM",
                                "newAddress",
                                "newFlatNum",
                                "newRoomCount",
                                "newLivSquare",
                                "newTotSquare",
                                "newFloor",
                                "imperfections",
                                "shipping"
                            })
                            public static class NewFlat {

                                @XmlElement(required = true)
                                protected String newUNOM;
                                protected String newAddress;
                                @XmlElement(required = true)
                                protected String newFlatNum;
                                protected String newRoomCount;
                                protected String newLivSquare;
                                protected String newTotSquare;
                                protected String newFloor;
                                protected PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Imperfections imperfections;
                                protected PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Shipping shipping;

                                /**
                                 * Gets the value of the newUNOM property.
                                 * 
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *     
                                 */
                                public String getNewUNOM() {
                                    return newUNOM;
                                }

                                /**
                                 * Sets the value of the newUNOM property.
                                 * 
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *     
                                 */
                                public void setNewUNOM(String value) {
                                    this.newUNOM = value;
                                }

                                /**
                                 * Gets the value of the newAddress property.
                                 * 
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *     
                                 */
                                public String getNewAddress() {
                                    return newAddress;
                                }

                                /**
                                 * Sets the value of the newAddress property.
                                 * 
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *     
                                 */
                                public void setNewAddress(String value) {
                                    this.newAddress = value;
                                }

                                /**
                                 * Gets the value of the newFlatNum property.
                                 * 
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *     
                                 */
                                public String getNewFlatNum() {
                                    return newFlatNum;
                                }

                                /**
                                 * Sets the value of the newFlatNum property.
                                 * 
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *     
                                 */
                                public void setNewFlatNum(String value) {
                                    this.newFlatNum = value;
                                }

                                /**
                                 * Gets the value of the newRoomCount property.
                                 * 
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *     
                                 */
                                public String getNewRoomCount() {
                                    return newRoomCount;
                                }

                                /**
                                 * Sets the value of the newRoomCount property.
                                 * 
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *     
                                 */
                                public void setNewRoomCount(String value) {
                                    this.newRoomCount = value;
                                }

                                /**
                                 * Gets the value of the newLivSquare property.
                                 * 
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *     
                                 */
                                public String getNewLivSquare() {
                                    return newLivSquare;
                                }

                                /**
                                 * Sets the value of the newLivSquare property.
                                 * 
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *     
                                 */
                                public void setNewLivSquare(String value) {
                                    this.newLivSquare = value;
                                }

                                /**
                                 * Gets the value of the newTotSquare property.
                                 * 
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *     
                                 */
                                public String getNewTotSquare() {
                                    return newTotSquare;
                                }

                                /**
                                 * Sets the value of the newTotSquare property.
                                 * 
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *     
                                 */
                                public void setNewTotSquare(String value) {
                                    this.newTotSquare = value;
                                }

                                /**
                                 * Gets the value of the newFloor property.
                                 * 
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *     
                                 */
                                public String getNewFloor() {
                                    return newFloor;
                                }

                                /**
                                 * Sets the value of the newFloor property.
                                 * 
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *     
                                 */
                                public void setNewFloor(String value) {
                                    this.newFloor = value;
                                }

                                /**
                                 * Gets the value of the imperfections property.
                                 * 
                                 * @return
                                 *     possible object is
                                 *     {@link PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Imperfections }
                                 *     
                                 */
                                public PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Imperfections getImperfections() {
                                    return imperfections;
                                }

                                /**
                                 * Sets the value of the imperfections property.
                                 * 
                                 * @param value
                                 *     allowed object is
                                 *     {@link PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Imperfections }
                                 *     
                                 */
                                public void setImperfections(PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Imperfections value) {
                                    this.imperfections = value;
                                }

                                /**
                                 * Gets the value of the shipping property.
                                 * 
                                 * @return
                                 *     possible object is
                                 *     {@link PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Shipping }
                                 *     
                                 */
                                public PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Shipping getShipping() {
                                    return shipping;
                                }

                                /**
                                 * Sets the value of the shipping property.
                                 * 
                                 * @param value
                                 *     allowed object is
                                 *     {@link PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Shipping }
                                 *     
                                 */
                                public void setShipping(PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Shipping value) {
                                    this.shipping = value;
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
                                 *         &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
                                 *         &lt;element name="imperfection" maxOccurs="unbounded">
                                 *           &lt;complexType>
                                 *             &lt;complexContent>
                                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                                 *                 &lt;sequence>
                                 *                   &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
                                 *                   &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
                                 *                     &lt;complexType>
                                 *                       &lt;complexContent>
                                 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                                 *                           &lt;sequence>
                                 *                             &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
                                 *                             &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
                                 *                           &lt;/sequence>
                                 *                         &lt;/restriction>
                                 *                       &lt;/complexContent>
                                 *                     &lt;/complexType>
                                 *                   &lt;/element>
                                 *                   &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                                 *                   &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                                 *                   &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
                                 *                 &lt;/sequence>
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
                                    "defect",
                                    "imperfection"
                                })
                                public static class Imperfections {

                                    @XmlElement(required = true)
                                    protected String defect;
                                    @XmlElement(required = true)
                                    protected List<PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Imperfections.Imperfection> imperfection;

                                    /**
                                     * Gets the value of the defect property.
                                     * 
                                     * @return
                                     *     possible object is
                                     *     {@link String }
                                     *     
                                     */
                                    public String getDefect() {
                                        return defect;
                                    }

                                    /**
                                     * Sets the value of the defect property.
                                     * 
                                     * @param value
                                     *     allowed object is
                                     *     {@link String }
                                     *     
                                     */
                                    public void setDefect(String value) {
                                        this.defect = value;
                                    }

                                    /**
                                     * Gets the value of the imperfection property.
                                     * 
                                     * <p>
                                     * This accessor method returns a reference to the live list,
                                     * not a snapshot. Therefore any modification you make to the
                                     * returned list will be present inside the JAXB object.
                                     * This is why there is not a <CODE>set</CODE> method for the imperfection property.
                                     * 
                                     * <p>
                                     * For example, to add a new item, do as follows:
                                     * <pre>
                                     *    getImperfection().add(newItem);
                                     * </pre>
                                     * 
                                     * 
                                     * <p>
                                     * Objects of the following type(s) are allowed in the list
                                     * {@link PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Imperfections.Imperfection }
                                     * 
                                     * 
                                     */
                                    public List<PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Imperfections.Imperfection> getImperfection() {
                                        if (imperfection == null) {
                                            imperfection = new ArrayList<PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Imperfections.Imperfection>();
                                        }
                                        return this.imperfection;
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
                                     *         &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
                                     *         &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
                                     *           &lt;complexType>
                                     *             &lt;complexContent>
                                     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                                     *                 &lt;sequence>
                                     *                   &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
                                     *                   &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
                                     *                 &lt;/sequence>
                                     *               &lt;/restriction>
                                     *             &lt;/complexContent>
                                     *           &lt;/complexType>
                                     *         &lt;/element>
                                     *         &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                                     *         &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                                     *         &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
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
                                        "actId",
                                        "defectItem",
                                        "defectsEliminatedNotificationDate",
                                        "acceptedDefectsDate",
                                        "filingDate"
                                    })
                                    public static class Imperfection {

                                        @XmlElement(required = true)
                                        protected String actId;
                                        protected List<PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Imperfections.Imperfection.DefectItem> defectItem;
                                        @XmlElement(required = true, type = String.class)
                                        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
                                        @XmlSchemaType(name = "date")
                                        protected LocalDate defectsEliminatedNotificationDate;
                                        @XmlElement(required = true, type = String.class)
                                        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
                                        @XmlSchemaType(name = "date")
                                        protected LocalDate acceptedDefectsDate;
                                        @XmlElement(required = true, type = String.class)
                                        @XmlJavaTypeAdapter(LocalDateTimeXmlAdapter.class)
                                        @XmlSchemaType(name = "dateTime")
                                        protected LocalDateTime filingDate;

                                        /**
                                         * Gets the value of the actId property.
                                         * 
                                         * @return
                                         *     possible object is
                                         *     {@link String }
                                         *     
                                         */
                                        public String getActId() {
                                            return actId;
                                        }

                                        /**
                                         * Sets the value of the actId property.
                                         * 
                                         * @param value
                                         *     allowed object is
                                         *     {@link String }
                                         *     
                                         */
                                        public void setActId(String value) {
                                            this.actId = value;
                                        }

                                        /**
                                         * Gets the value of the defectItem property.
                                         * 
                                         * <p>
                                         * This accessor method returns a reference to the live list,
                                         * not a snapshot. Therefore any modification you make to the
                                         * returned list will be present inside the JAXB object.
                                         * This is why there is not a <CODE>set</CODE> method for the defectItem property.
                                         * 
                                         * <p>
                                         * For example, to add a new item, do as follows:
                                         * <pre>
                                         *    getDefectItem().add(newItem);
                                         * </pre>
                                         * 
                                         * 
                                         * <p>
                                         * Objects of the following type(s) are allowed in the list
                                         * {@link PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Imperfections.Imperfection.DefectItem }
                                         * 
                                         * 
                                         */
                                        public List<PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Imperfections.Imperfection.DefectItem> getDefectItem() {
                                            if (defectItem == null) {
                                                defectItem = new ArrayList<PersonResType.Families.Family.Additions.Addition.Decision.Contract.NewFlat.Imperfections.Imperfection.DefectItem>();
                                            }
                                            return this.defectItem;
                                        }

                                        /**
                                         * Gets the value of the defectsEliminatedNotificationDate property.
                                         * 
                                         * @return
                                         *     possible object is
                                         *     {@link LocalDate }
                                         *     
                                         */
                                        public LocalDate getDefectsEliminatedNotificationDate() {
                                            return defectsEliminatedNotificationDate;
                                        }

                                        /**
                                         * Sets the value of the defectsEliminatedNotificationDate property.
                                         * 
                                         * @param value
                                         *     allowed object is
                                         *     {@link LocalDate }
                                         *     
                                         */
                                        public void setDefectsEliminatedNotificationDate(LocalDate value) {
                                            this.defectsEliminatedNotificationDate = value;
                                        }

                                        /**
                                         * Gets the value of the acceptedDefectsDate property.
                                         * 
                                         * @return
                                         *     possible object is
                                         *     {@link LocalDate }
                                         *     
                                         */
                                        public LocalDate getAcceptedDefectsDate() {
                                            return acceptedDefectsDate;
                                        }

                                        /**
                                         * Sets the value of the acceptedDefectsDate property.
                                         * 
                                         * @param value
                                         *     allowed object is
                                         *     {@link LocalDate }
                                         *     
                                         */
                                        public void setAcceptedDefectsDate(LocalDate value) {
                                            this.acceptedDefectsDate = value;
                                        }

                                        /**
                                         * Gets the value of the filingDate property.
                                         * 
                                         * @return
                                         *     possible object is
                                         *     {@link LocalDateTime }
                                         *     
                                         */
                                        public LocalDateTime getFilingDate() {
                                            return filingDate;
                                        }

                                        /**
                                         * Sets the value of the filingDate property.
                                         * 
                                         * @param value
                                         *     allowed object is
                                         *     {@link LocalDateTime }
                                         *     
                                         */
                                        public void setFilingDate(LocalDateTime value) {
                                            this.filingDate = value;
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
                                         *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
                                         *         &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
                                            "description",
                                            "flatElement"
                                        })
                                        public static class DefectItem {

                                            @XmlElement(required = true)
                                            protected String description;
                                            @XmlElement(required = true)
                                            protected String flatElement;

                                            /**
                                             * Gets the value of the description property.
                                             * 
                                             * @return
                                             *     possible object is
                                             *     {@link String }
                                             *     
                                             */
                                            public String getDescription() {
                                                return description;
                                            }

                                            /**
                                             * Sets the value of the description property.
                                             * 
                                             * @param value
                                             *     allowed object is
                                             *     {@link String }
                                             *     
                                             */
                                            public void setDescription(String value) {
                                                this.description = value;
                                            }

                                            /**
                                             * Gets the value of the flatElement property.
                                             * 
                                             * @return
                                             *     possible object is
                                             *     {@link String }
                                             *     
                                             */
                                            public String getFlatElement() {
                                                return flatElement;
                                            }

                                            /**
                                             * Sets the value of the flatElement property.
                                             * 
                                             * @param value
                                             *     allowed object is
                                             *     {@link String }
                                             *     
                                             */
                                            public void setFlatElement(String value) {
                                                this.flatElement = value;
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
                                 *         &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                                 *         &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                                 *         &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                                 *         &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                                 *         &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                                 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
                                    "eno",
                                    "shippingDateEnd",
                                    "processInstanceId",
                                    "shippingDateStart",
                                    "shippingDateTimeInfo",
                                    "status"
                                })
                                public static class Shipping {

                                    protected String eno;
                                    @XmlElement(type = String.class)
                                    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
                                    @XmlSchemaType(name = "date")
                                    protected LocalDate shippingDateEnd;
                                    protected String processInstanceId;
                                    @XmlElement(type = String.class)
                                    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
                                    @XmlSchemaType(name = "date")
                                    protected LocalDate shippingDateStart;
                                    protected String shippingDateTimeInfo;
                                    protected String status;

                                    /**
                                     * Gets the value of the eno property.
                                     * 
                                     * @return
                                     *     possible object is
                                     *     {@link String }
                                     *     
                                     */
                                    public String getEno() {
                                        return eno;
                                    }

                                    /**
                                     * Sets the value of the eno property.
                                     * 
                                     * @param value
                                     *     allowed object is
                                     *     {@link String }
                                     *     
                                     */
                                    public void setEno(String value) {
                                        this.eno = value;
                                    }

                                    /**
                                     * Gets the value of the shippingDateEnd property.
                                     * 
                                     * @return
                                     *     possible object is
                                     *     {@link LocalDate }
                                     *     
                                     */
                                    public LocalDate getShippingDateEnd() {
                                        return shippingDateEnd;
                                    }

                                    /**
                                     * Sets the value of the shippingDateEnd property.
                                     * 
                                     * @param value
                                     *     allowed object is
                                     *     {@link LocalDate }
                                     *     
                                     */
                                    public void setShippingDateEnd(LocalDate value) {
                                        this.shippingDateEnd = value;
                                    }

                                    /**
                                     * Gets the value of the processInstanceId property.
                                     * 
                                     * @return
                                     *     possible object is
                                     *     {@link String }
                                     *     
                                     */
                                    public String getProcessInstanceId() {
                                        return processInstanceId;
                                    }

                                    /**
                                     * Sets the value of the processInstanceId property.
                                     * 
                                     * @param value
                                     *     allowed object is
                                     *     {@link String }
                                     *     
                                     */
                                    public void setProcessInstanceId(String value) {
                                        this.processInstanceId = value;
                                    }

                                    /**
                                     * Gets the value of the shippingDateStart property.
                                     * 
                                     * @return
                                     *     possible object is
                                     *     {@link LocalDate }
                                     *     
                                     */
                                    public LocalDate getShippingDateStart() {
                                        return shippingDateStart;
                                    }

                                    /**
                                     * Sets the value of the shippingDateStart property.
                                     * 
                                     * @param value
                                     *     allowed object is
                                     *     {@link LocalDate }
                                     *     
                                     */
                                    public void setShippingDateStart(LocalDate value) {
                                        this.shippingDateStart = value;
                                    }

                                    /**
                                     * Gets the value of the shippingDateTimeInfo property.
                                     * 
                                     * @return
                                     *     possible object is
                                     *     {@link String }
                                     *     
                                     */
                                    public String getShippingDateTimeInfo() {
                                        return shippingDateTimeInfo;
                                    }

                                    /**
                                     * Sets the value of the shippingDateTimeInfo property.
                                     * 
                                     * @param value
                                     *     allowed object is
                                     *     {@link String }
                                     *     
                                     */
                                    public void setShippingDateTimeInfo(String value) {
                                        this.shippingDateTimeInfo = value;
                                    }

                                    /**
                                     * Gets the value of the status property.
                                     * 
                                     * @return
                                     *     possible object is
                                     *     {@link String }
                                     *     
                                     */
                                    public String getStatus() {
                                        return status;
                                    }

                                    /**
                                     * Sets the value of the status property.
                                     * 
                                     * @param value
                                     *     allowed object is
                                     *     {@link String }
                                     *     
                                     */
                                    public void setStatus(String value) {
                                        this.status = value;
                                    }

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
             *         &lt;element name="cipAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="cipPhone" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="cipTime" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
                "cipAddress",
                "cipPhone",
                "cipTime"
            })
            public static class CipData {

                @XmlElement(required = true)
                protected String cipAddress;
                @XmlElement(required = true)
                protected String cipPhone;
                @XmlElement(required = true)
                protected String cipTime;

                /**
                 * Gets the value of the cipAddress property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getCipAddress() {
                    return cipAddress;
                }

                /**
                 * Sets the value of the cipAddress property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setCipAddress(String value) {
                    this.cipAddress = value;
                }

                /**
                 * Gets the value of the cipPhone property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getCipPhone() {
                    return cipPhone;
                }

                /**
                 * Sets the value of the cipPhone property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setCipPhone(String value) {
                    this.cipPhone = value;
                }

                /**
                 * Gets the value of the cipTime property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getCipTime() {
                    return cipTime;
                }

                /**
                 * Sets the value of the cipTime property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setCipTime(String value) {
                    this.cipTime = value;
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
             *         &lt;element name="snosUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="snosAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="snosFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="snosRooms" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="snosRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="snosLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="snosTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="snosFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
                "snosUNOM",
                "snosAddress",
                "snosFlatNum",
                "snosRooms",
                "snosRoomCount",
                "snosLivSquare",
                "snosTotSquare",
                "snosFloor"
            })
            public static class Flat {

                @XmlElement(required = true)
                protected String snosUNOM;
                protected String snosAddress;
                @XmlElement(required = true)
                protected String snosFlatNum;
                protected String snosRooms;
                protected String snosRoomCount;
                protected String snosLivSquare;
                protected String snosTotSquare;
                protected String snosFloor;

                /**
                 * Gets the value of the snosUNOM property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getSnosUNOM() {
                    return snosUNOM;
                }

                /**
                 * Sets the value of the snosUNOM property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setSnosUNOM(String value) {
                    this.snosUNOM = value;
                }

                /**
                 * Gets the value of the snosAddress property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getSnosAddress() {
                    return snosAddress;
                }

                /**
                 * Sets the value of the snosAddress property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setSnosAddress(String value) {
                    this.snosAddress = value;
                }

                /**
                 * Gets the value of the snosFlatNum property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getSnosFlatNum() {
                    return snosFlatNum;
                }

                /**
                 * Sets the value of the snosFlatNum property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setSnosFlatNum(String value) {
                    this.snosFlatNum = value;
                }

                /**
                 * Gets the value of the snosRooms property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getSnosRooms() {
                    return snosRooms;
                }

                /**
                 * Sets the value of the snosRooms property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setSnosRooms(String value) {
                    this.snosRooms = value;
                }

                /**
                 * Gets the value of the snosRoomCount property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getSnosRoomCount() {
                    return snosRoomCount;
                }

                /**
                 * Sets the value of the snosRoomCount property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setSnosRoomCount(String value) {
                    this.snosRoomCount = value;
                }

                /**
                 * Gets the value of the snosLivSquare property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getSnosLivSquare() {
                    return snosLivSquare;
                }

                /**
                 * Sets the value of the snosLivSquare property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setSnosLivSquare(String value) {
                    this.snosLivSquare = value;
                }

                /**
                 * Gets the value of the snosTotSquare property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getSnosTotSquare() {
                    return snosTotSquare;
                }

                /**
                 * Sets the value of the snosTotSquare property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setSnosTotSquare(String value) {
                    this.snosTotSquare = value;
                }

                /**
                 * Gets the value of the snosFloor property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getSnosFloor() {
                    return snosFloor;
                }

                /**
                 * Sets the value of the snosFloor property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setSnosFloor(String value) {
                    this.snosFloor = value;
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
             *         &lt;element name="letterDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="letterId" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="letterStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="option" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="views" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="viewFlat" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                   &lt;element name="view" maxOccurs="unbounded">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;sequence>
             *                             &lt;element name="viewDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
             *                           &lt;/sequence>
             *                         &lt;/restriction>
             *                       &lt;/complexContent>
             *                     &lt;/complexType>
             *                   &lt;/element>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="decision" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="decisionResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                   &lt;element name="decisionDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
             *                   &lt;element name="fullPacket" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                   &lt;element name="contract" minOccurs="0">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;sequence>
             *                             &lt;element name="removalType" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                             &lt;element name="contractProj" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                             &lt;element name="contractStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                             &lt;element name="contractSignDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
             *                             &lt;element name="contractSignPlace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                             &lt;element name="keyPass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                             &lt;element name="keyIssue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                             &lt;element name="newFlat" maxOccurs="unbounded" minOccurs="0">
             *                               &lt;complexType>
             *                                 &lt;complexContent>
             *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                     &lt;sequence>
             *                                       &lt;element name="newUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                                       &lt;element name="newAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                       &lt;element name="newFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                                       &lt;element name="newRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                       &lt;element name="newLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                       &lt;element name="newTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                       &lt;element name="newFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                       &lt;element name="imperfections" minOccurs="0">
             *                                         &lt;complexType>
             *                                           &lt;complexContent>
             *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                               &lt;sequence>
             *                                                 &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                                                 &lt;element name="imperfection" maxOccurs="unbounded">
             *                                                   &lt;complexType>
             *                                                     &lt;complexContent>
             *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                                         &lt;sequence>
             *                                                           &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                                                           &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
             *                                                             &lt;complexType>
             *                                                               &lt;complexContent>
             *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                                                   &lt;sequence>
             *                                                                     &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                                                                     &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                                                                   &lt;/sequence>
             *                                                                 &lt;/restriction>
             *                                                               &lt;/complexContent>
             *                                                             &lt;/complexType>
             *                                                           &lt;/element>
             *                                                           &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
             *                                                           &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
             *                                                           &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
             *                                                         &lt;/sequence>
             *                                                       &lt;/restriction>
             *                                                     &lt;/complexContent>
             *                                                   &lt;/complexType>
             *                                                 &lt;/element>
             *                                               &lt;/sequence>
             *                                             &lt;/restriction>
             *                                           &lt;/complexContent>
             *                                         &lt;/complexType>
             *                                       &lt;/element>
             *                                       &lt;element name="shipping" minOccurs="0">
             *                                         &lt;complexType>
             *                                           &lt;complexContent>
             *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                               &lt;sequence>
             *                                                 &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                                 &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
             *                                                 &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                                 &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
             *                                                 &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                                 &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                                               &lt;/sequence>
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
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "letterDate",
                "letterId",
                "letterStatus",
                "option",
                "views",
                "decision"
            })
            public static class Offer {

                @XmlElement(required = true)
                protected String letterDate;
                @XmlElement(required = true)
                protected String letterId;
                @XmlElement(required = true)
                protected String letterStatus;
                @XmlElement(required = true)
                protected String option;
                protected PersonResType.Families.Family.Offer.Views views;
                protected PersonResType.Families.Family.Offer.Decision decision;

                /**
                 * Gets the value of the letterDate property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getLetterDate() {
                    return letterDate;
                }

                /**
                 * Sets the value of the letterDate property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setLetterDate(String value) {
                    this.letterDate = value;
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

                /**
                 * Gets the value of the letterStatus property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getLetterStatus() {
                    return letterStatus;
                }

                /**
                 * Sets the value of the letterStatus property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setLetterStatus(String value) {
                    this.letterStatus = value;
                }

                /**
                 * Gets the value of the option property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getOption() {
                    return option;
                }

                /**
                 * Sets the value of the option property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setOption(String value) {
                    this.option = value;
                }

                /**
                 * Gets the value of the views property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link PersonResType.Families.Family.Offer.Views }
                 *     
                 */
                public PersonResType.Families.Family.Offer.Views getViews() {
                    return views;
                }

                /**
                 * Sets the value of the views property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link PersonResType.Families.Family.Offer.Views }
                 *     
                 */
                public void setViews(PersonResType.Families.Family.Offer.Views value) {
                    this.views = value;
                }

                /**
                 * Gets the value of the decision property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link PersonResType.Families.Family.Offer.Decision }
                 *     
                 */
                public PersonResType.Families.Family.Offer.Decision getDecision() {
                    return decision;
                }

                /**
                 * Sets the value of the decision property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link PersonResType.Families.Family.Offer.Decision }
                 *     
                 */
                public void setDecision(PersonResType.Families.Family.Offer.Decision value) {
                    this.decision = value;
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
                 *         &lt;element name="decisionResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *         &lt;element name="decisionDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                 *         &lt;element name="fullPacket" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *         &lt;element name="contract" minOccurs="0">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;sequence>
                 *                   &lt;element name="removalType" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *                   &lt;element name="contractProj" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                   &lt;element name="contractStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                   &lt;element name="contractSignDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                 *                   &lt;element name="contractSignPlace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                   &lt;element name="keyPass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                   &lt;element name="keyIssue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                   &lt;element name="newFlat" maxOccurs="unbounded" minOccurs="0">
                 *                     &lt;complexType>
                 *                       &lt;complexContent>
                 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                           &lt;sequence>
                 *                             &lt;element name="newUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *                             &lt;element name="newAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                             &lt;element name="newFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *                             &lt;element name="newRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                             &lt;element name="newLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                             &lt;element name="newTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                             &lt;element name="newFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                             &lt;element name="imperfections" minOccurs="0">
                 *                               &lt;complexType>
                 *                                 &lt;complexContent>
                 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                                     &lt;sequence>
                 *                                       &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *                                       &lt;element name="imperfection" maxOccurs="unbounded">
                 *                                         &lt;complexType>
                 *                                           &lt;complexContent>
                 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                                               &lt;sequence>
                 *                                                 &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *                                                 &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
                 *                                                   &lt;complexType>
                 *                                                     &lt;complexContent>
                 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                                                         &lt;sequence>
                 *                                                           &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *                                                           &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *                                                         &lt;/sequence>
                 *                                                       &lt;/restriction>
                 *                                                     &lt;/complexContent>
                 *                                                   &lt;/complexType>
                 *                                                 &lt;/element>
                 *                                                 &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                 *                                                 &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                 *                                                 &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
                 *                                               &lt;/sequence>
                 *                                             &lt;/restriction>
                 *                                           &lt;/complexContent>
                 *                                         &lt;/complexType>
                 *                                       &lt;/element>
                 *                                     &lt;/sequence>
                 *                                   &lt;/restriction>
                 *                                 &lt;/complexContent>
                 *                               &lt;/complexType>
                 *                             &lt;/element>
                 *                             &lt;element name="shipping" minOccurs="0">
                 *                               &lt;complexType>
                 *                                 &lt;complexContent>
                 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                                     &lt;sequence>
                 *                                       &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                                       &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                 *                                       &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                                       &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                 *                                       &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                                       &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *                                     &lt;/sequence>
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
                    "decisionResult",
                    "decisionDate",
                    "fullPacket",
                    "contract"
                })
                public static class Decision {

                    @XmlElement(required = true)
                    protected String decisionResult;
                    @XmlElement(required = true, type = String.class)
                    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
                    @XmlSchemaType(name = "date")
                    protected LocalDate decisionDate;
                    @XmlElement(required = true)
                    protected String fullPacket;
                    protected PersonResType.Families.Family.Offer.Decision.Contract contract;

                    /**
                     * Gets the value of the decisionResult property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getDecisionResult() {
                        return decisionResult;
                    }

                    /**
                     * Sets the value of the decisionResult property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setDecisionResult(String value) {
                        this.decisionResult = value;
                    }

                    /**
                     * Gets the value of the decisionDate property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link LocalDate }
                     *     
                     */
                    public LocalDate getDecisionDate() {
                        return decisionDate;
                    }

                    /**
                     * Sets the value of the decisionDate property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link LocalDate }
                     *     
                     */
                    public void setDecisionDate(LocalDate value) {
                        this.decisionDate = value;
                    }

                    /**
                     * Gets the value of the fullPacket property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getFullPacket() {
                        return fullPacket;
                    }

                    /**
                     * Sets the value of the fullPacket property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setFullPacket(String value) {
                        this.fullPacket = value;
                    }

                    /**
                     * Gets the value of the contract property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link PersonResType.Families.Family.Offer.Decision.Contract }
                     *     
                     */
                    public PersonResType.Families.Family.Offer.Decision.Contract getContract() {
                        return contract;
                    }

                    /**
                     * Sets the value of the contract property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link PersonResType.Families.Family.Offer.Decision.Contract }
                     *     
                     */
                    public void setContract(PersonResType.Families.Family.Offer.Decision.Contract value) {
                        this.contract = value;
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
                     *         &lt;element name="removalType" type="{http://www.w3.org/2001/XMLSchema}string"/>
                     *         &lt;element name="contractProj" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *         &lt;element name="contractStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *         &lt;element name="contractSignDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                     *         &lt;element name="contractSignPlace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *         &lt;element name="keyPass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *         &lt;element name="keyIssue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *         &lt;element name="newFlat" maxOccurs="unbounded" minOccurs="0">
                     *           &lt;complexType>
                     *             &lt;complexContent>
                     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                 &lt;sequence>
                     *                   &lt;element name="newUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
                     *                   &lt;element name="newAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                   &lt;element name="newFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
                     *                   &lt;element name="newRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                   &lt;element name="newLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                   &lt;element name="newTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                   &lt;element name="newFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                   &lt;element name="imperfections" minOccurs="0">
                     *                     &lt;complexType>
                     *                       &lt;complexContent>
                     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                           &lt;sequence>
                     *                             &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
                     *                             &lt;element name="imperfection" maxOccurs="unbounded">
                     *                               &lt;complexType>
                     *                                 &lt;complexContent>
                     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                                     &lt;sequence>
                     *                                       &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
                     *                                       &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
                     *                                         &lt;complexType>
                     *                                           &lt;complexContent>
                     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                                               &lt;sequence>
                     *                                                 &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
                     *                                                 &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
                     *                                               &lt;/sequence>
                     *                                             &lt;/restriction>
                     *                                           &lt;/complexContent>
                     *                                         &lt;/complexType>
                     *                                       &lt;/element>
                     *                                       &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                     *                                       &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                     *                                       &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
                     *                                     &lt;/sequence>
                     *                                   &lt;/restriction>
                     *                                 &lt;/complexContent>
                     *                               &lt;/complexType>
                     *                             &lt;/element>
                     *                           &lt;/sequence>
                     *                         &lt;/restriction>
                     *                       &lt;/complexContent>
                     *                     &lt;/complexType>
                     *                   &lt;/element>
                     *                   &lt;element name="shipping" minOccurs="0">
                     *                     &lt;complexType>
                     *                       &lt;complexContent>
                     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                           &lt;sequence>
                     *                             &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                             &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                     *                             &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                             &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                     *                             &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                             &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                     *                           &lt;/sequence>
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
                     *     &lt;/restriction>
                     *   &lt;/complexContent>
                     * &lt;/complexType>
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "removalType",
                        "contractProj",
                        "contractStatus",
                        "contractSignDate",
                        "contractSignPlace",
                        "keyPass",
                        "keyIssue",
                        "newFlat"
                    })
                    public static class Contract {

                        @XmlElement(required = true)
                        protected String removalType;
                        protected String contractProj;
                        protected String contractStatus;
                        @XmlElement(type = String.class)
                        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
                        @XmlSchemaType(name = "date")
                        protected LocalDate contractSignDate;
                        protected String contractSignPlace;
                        protected String keyPass;
                        protected String keyIssue;
                        protected List<PersonResType.Families.Family.Offer.Decision.Contract.NewFlat> newFlat;

                        /**
                         * Gets the value of the removalType property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getRemovalType() {
                            return removalType;
                        }

                        /**
                         * Sets the value of the removalType property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setRemovalType(String value) {
                            this.removalType = value;
                        }

                        /**
                         * Gets the value of the contractProj property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getContractProj() {
                            return contractProj;
                        }

                        /**
                         * Sets the value of the contractProj property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setContractProj(String value) {
                            this.contractProj = value;
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
                         * Gets the value of the contractSignDate property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link LocalDate }
                         *     
                         */
                        public LocalDate getContractSignDate() {
                            return contractSignDate;
                        }

                        /**
                         * Sets the value of the contractSignDate property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link LocalDate }
                         *     
                         */
                        public void setContractSignDate(LocalDate value) {
                            this.contractSignDate = value;
                        }

                        /**
                         * Gets the value of the contractSignPlace property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getContractSignPlace() {
                            return contractSignPlace;
                        }

                        /**
                         * Sets the value of the contractSignPlace property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setContractSignPlace(String value) {
                            this.contractSignPlace = value;
                        }

                        /**
                         * Gets the value of the keyPass property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getKeyPass() {
                            return keyPass;
                        }

                        /**
                         * Sets the value of the keyPass property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setKeyPass(String value) {
                            this.keyPass = value;
                        }

                        /**
                         * Gets the value of the keyIssue property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getKeyIssue() {
                            return keyIssue;
                        }

                        /**
                         * Sets the value of the keyIssue property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setKeyIssue(String value) {
                            this.keyIssue = value;
                        }

                        /**
                         * Gets the value of the newFlat property.
                         * 
                         * <p>
                         * This accessor method returns a reference to the live list,
                         * not a snapshot. Therefore any modification you make to the
                         * returned list will be present inside the JAXB object.
                         * This is why there is not a <CODE>set</CODE> method for the newFlat property.
                         * 
                         * <p>
                         * For example, to add a new item, do as follows:
                         * <pre>
                         *    getNewFlat().add(newItem);
                         * </pre>
                         * 
                         * 
                         * <p>
                         * Objects of the following type(s) are allowed in the list
                         * {@link PersonResType.Families.Family.Offer.Decision.Contract.NewFlat }
                         * 
                         * 
                         */
                        public List<PersonResType.Families.Family.Offer.Decision.Contract.NewFlat> getNewFlat() {
                            if (newFlat == null) {
                                newFlat = new ArrayList<PersonResType.Families.Family.Offer.Decision.Contract.NewFlat>();
                            }
                            return this.newFlat;
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
                         *         &lt;element name="newUNOM" type="{http://www.w3.org/2001/XMLSchema}string"/>
                         *         &lt;element name="newAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *         &lt;element name="newFlatNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
                         *         &lt;element name="newRoomCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *         &lt;element name="newLivSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *         &lt;element name="newTotSquare" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *         &lt;element name="newFloor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *         &lt;element name="imperfections" minOccurs="0">
                         *           &lt;complexType>
                         *             &lt;complexContent>
                         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                         *                 &lt;sequence>
                         *                   &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
                         *                   &lt;element name="imperfection" maxOccurs="unbounded">
                         *                     &lt;complexType>
                         *                       &lt;complexContent>
                         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                         *                           &lt;sequence>
                         *                             &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
                         *                             &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
                         *                               &lt;complexType>
                         *                                 &lt;complexContent>
                         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                         *                                     &lt;sequence>
                         *                                       &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
                         *                                       &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
                         *                                     &lt;/sequence>
                         *                                   &lt;/restriction>
                         *                                 &lt;/complexContent>
                         *                               &lt;/complexType>
                         *                             &lt;/element>
                         *                             &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                         *                             &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                         *                             &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
                         *                           &lt;/sequence>
                         *                         &lt;/restriction>
                         *                       &lt;/complexContent>
                         *                     &lt;/complexType>
                         *                   &lt;/element>
                         *                 &lt;/sequence>
                         *               &lt;/restriction>
                         *             &lt;/complexContent>
                         *           &lt;/complexType>
                         *         &lt;/element>
                         *         &lt;element name="shipping" minOccurs="0">
                         *           &lt;complexType>
                         *             &lt;complexContent>
                         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                         *                 &lt;sequence>
                         *                   &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *                   &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                         *                   &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *                   &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                         *                   &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *                   &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                         *                 &lt;/sequence>
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
                            "newUNOM",
                            "newAddress",
                            "newFlatNum",
                            "newRoomCount",
                            "newLivSquare",
                            "newTotSquare",
                            "newFloor",
                            "imperfections",
                            "shipping"
                        })
                        public static class NewFlat {

                            @XmlElement(required = true)
                            protected String newUNOM;
                            protected String newAddress;
                            @XmlElement(required = true)
                            protected String newFlatNum;
                            protected String newRoomCount;
                            protected String newLivSquare;
                            protected String newTotSquare;
                            protected String newFloor;
                            protected PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections imperfections;
                            protected PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Shipping shipping;

                            /**
                             * Gets the value of the newUNOM property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getNewUNOM() {
                                return newUNOM;
                            }

                            /**
                             * Sets the value of the newUNOM property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setNewUNOM(String value) {
                                this.newUNOM = value;
                            }

                            /**
                             * Gets the value of the newAddress property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getNewAddress() {
                                return newAddress;
                            }

                            /**
                             * Sets the value of the newAddress property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setNewAddress(String value) {
                                this.newAddress = value;
                            }

                            /**
                             * Gets the value of the newFlatNum property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getNewFlatNum() {
                                return newFlatNum;
                            }

                            /**
                             * Sets the value of the newFlatNum property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setNewFlatNum(String value) {
                                this.newFlatNum = value;
                            }

                            /**
                             * Gets the value of the newRoomCount property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getNewRoomCount() {
                                return newRoomCount;
                            }

                            /**
                             * Sets the value of the newRoomCount property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setNewRoomCount(String value) {
                                this.newRoomCount = value;
                            }

                            /**
                             * Gets the value of the newLivSquare property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getNewLivSquare() {
                                return newLivSquare;
                            }

                            /**
                             * Sets the value of the newLivSquare property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setNewLivSquare(String value) {
                                this.newLivSquare = value;
                            }

                            /**
                             * Gets the value of the newTotSquare property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getNewTotSquare() {
                                return newTotSquare;
                            }

                            /**
                             * Sets the value of the newTotSquare property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setNewTotSquare(String value) {
                                this.newTotSquare = value;
                            }

                            /**
                             * Gets the value of the newFloor property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getNewFloor() {
                                return newFloor;
                            }

                            /**
                             * Sets the value of the newFloor property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setNewFloor(String value) {
                                this.newFloor = value;
                            }

                            /**
                             * Gets the value of the imperfections property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections }
                             *     
                             */
                            public PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections getImperfections() {
                                return imperfections;
                            }

                            /**
                             * Sets the value of the imperfections property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections }
                             *     
                             */
                            public void setImperfections(PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections value) {
                                this.imperfections = value;
                            }

                            /**
                             * Gets the value of the shipping property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Shipping }
                             *     
                             */
                            public PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Shipping getShipping() {
                                return shipping;
                            }

                            /**
                             * Sets the value of the shipping property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Shipping }
                             *     
                             */
                            public void setShipping(PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Shipping value) {
                                this.shipping = value;
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
                             *         &lt;element name="defect" type="{http://www.w3.org/2001/XMLSchema}string"/>
                             *         &lt;element name="imperfection" maxOccurs="unbounded">
                             *           &lt;complexType>
                             *             &lt;complexContent>
                             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                             *                 &lt;sequence>
                             *                   &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
                             *                   &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
                             *                     &lt;complexType>
                             *                       &lt;complexContent>
                             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                             *                           &lt;sequence>
                             *                             &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
                             *                             &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
                             *                           &lt;/sequence>
                             *                         &lt;/restriction>
                             *                       &lt;/complexContent>
                             *                     &lt;/complexType>
                             *                   &lt;/element>
                             *                   &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                             *                   &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                             *                   &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
                             *                 &lt;/sequence>
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
                                "defect",
                                "imperfection"
                            })
                            public static class Imperfections {

                                @XmlElement(required = true)
                                protected String defect;
                                @XmlElement(required = true)
                                protected List<PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections.Imperfection> imperfection;

                                /**
                                 * Gets the value of the defect property.
                                 * 
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *     
                                 */
                                public String getDefect() {
                                    return defect;
                                }

                                /**
                                 * Sets the value of the defect property.
                                 * 
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *     
                                 */
                                public void setDefect(String value) {
                                    this.defect = value;
                                }

                                /**
                                 * Gets the value of the imperfection property.
                                 * 
                                 * <p>
                                 * This accessor method returns a reference to the live list,
                                 * not a snapshot. Therefore any modification you make to the
                                 * returned list will be present inside the JAXB object.
                                 * This is why there is not a <CODE>set</CODE> method for the imperfection property.
                                 * 
                                 * <p>
                                 * For example, to add a new item, do as follows:
                                 * <pre>
                                 *    getImperfection().add(newItem);
                                 * </pre>
                                 * 
                                 * 
                                 * <p>
                                 * Objects of the following type(s) are allowed in the list
                                 * {@link PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections.Imperfection }
                                 * 
                                 * 
                                 */
                                public List<PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections.Imperfection> getImperfection() {
                                    if (imperfection == null) {
                                        imperfection = new ArrayList<PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections.Imperfection>();
                                    }
                                    return this.imperfection;
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
                                 *         &lt;element name="actId" type="{http://www.w3.org/2001/XMLSchema}string"/>
                                 *         &lt;element name="defectItem" maxOccurs="unbounded" minOccurs="0">
                                 *           &lt;complexType>
                                 *             &lt;complexContent>
                                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                                 *                 &lt;sequence>
                                 *                   &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
                                 *                   &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
                                 *                 &lt;/sequence>
                                 *               &lt;/restriction>
                                 *             &lt;/complexContent>
                                 *           &lt;/complexType>
                                 *         &lt;/element>
                                 *         &lt;element name="defectsEliminatedNotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                                 *         &lt;element name="acceptedDefectsDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                                 *         &lt;element name="filingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
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
                                    "actId",
                                    "defectItem",
                                    "defectsEliminatedNotificationDate",
                                    "acceptedDefectsDate",
                                    "filingDate"
                                })
                                public static class Imperfection {

                                    @XmlElement(required = true)
                                    protected String actId;
                                    protected List<PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections.Imperfection.DefectItem> defectItem;
                                    @XmlElement(required = true, type = String.class)
                                    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
                                    @XmlSchemaType(name = "date")
                                    protected LocalDate defectsEliminatedNotificationDate;
                                    @XmlElement(required = true, type = String.class)
                                    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
                                    @XmlSchemaType(name = "date")
                                    protected LocalDate acceptedDefectsDate;
                                    @XmlElement(required = true, type = String.class)
                                    @XmlJavaTypeAdapter(LocalDateTimeXmlAdapter.class)
                                    @XmlSchemaType(name = "dateTime")
                                    protected LocalDateTime filingDate;

                                    /**
                                     * Gets the value of the actId property.
                                     * 
                                     * @return
                                     *     possible object is
                                     *     {@link String }
                                     *     
                                     */
                                    public String getActId() {
                                        return actId;
                                    }

                                    /**
                                     * Sets the value of the actId property.
                                     * 
                                     * @param value
                                     *     allowed object is
                                     *     {@link String }
                                     *     
                                     */
                                    public void setActId(String value) {
                                        this.actId = value;
                                    }

                                    /**
                                     * Gets the value of the defectItem property.
                                     * 
                                     * <p>
                                     * This accessor method returns a reference to the live list,
                                     * not a snapshot. Therefore any modification you make to the
                                     * returned list will be present inside the JAXB object.
                                     * This is why there is not a <CODE>set</CODE> method for the defectItem property.
                                     * 
                                     * <p>
                                     * For example, to add a new item, do as follows:
                                     * <pre>
                                     *    getDefectItem().add(newItem);
                                     * </pre>
                                     * 
                                     * 
                                     * <p>
                                     * Objects of the following type(s) are allowed in the list
                                     * {@link PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections.Imperfection.DefectItem }
                                     * 
                                     * 
                                     */
                                    public List<PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections.Imperfection.DefectItem> getDefectItem() {
                                        if (defectItem == null) {
                                            defectItem = new ArrayList<PersonResType.Families.Family.Offer.Decision.Contract.NewFlat.Imperfections.Imperfection.DefectItem>();
                                        }
                                        return this.defectItem;
                                    }

                                    /**
                                     * Gets the value of the defectsEliminatedNotificationDate property.
                                     * 
                                     * @return
                                     *     possible object is
                                     *     {@link LocalDate }
                                     *     
                                     */
                                    public LocalDate getDefectsEliminatedNotificationDate() {
                                        return defectsEliminatedNotificationDate;
                                    }

                                    /**
                                     * Sets the value of the defectsEliminatedNotificationDate property.
                                     * 
                                     * @param value
                                     *     allowed object is
                                     *     {@link LocalDate }
                                     *     
                                     */
                                    public void setDefectsEliminatedNotificationDate(LocalDate value) {
                                        this.defectsEliminatedNotificationDate = value;
                                    }

                                    /**
                                     * Gets the value of the acceptedDefectsDate property.
                                     * 
                                     * @return
                                     *     possible object is
                                     *     {@link LocalDate }
                                     *     
                                     */
                                    public LocalDate getAcceptedDefectsDate() {
                                        return acceptedDefectsDate;
                                    }

                                    /**
                                     * Sets the value of the acceptedDefectsDate property.
                                     * 
                                     * @param value
                                     *     allowed object is
                                     *     {@link LocalDate }
                                     *     
                                     */
                                    public void setAcceptedDefectsDate(LocalDate value) {
                                        this.acceptedDefectsDate = value;
                                    }

                                    /**
                                     * Gets the value of the filingDate property.
                                     * 
                                     * @return
                                     *     possible object is
                                     *     {@link LocalDateTime }
                                     *     
                                     */
                                    public LocalDateTime getFilingDate() {
                                        return filingDate;
                                    }

                                    /**
                                     * Sets the value of the filingDate property.
                                     * 
                                     * @param value
                                     *     allowed object is
                                     *     {@link LocalDateTime }
                                     *     
                                     */
                                    public void setFilingDate(LocalDateTime value) {
                                        this.filingDate = value;
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
                                     *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
                                     *         &lt;element name="flatElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
                                        "description",
                                        "flatElement"
                                    })
                                    public static class DefectItem {

                                        @XmlElement(required = true)
                                        protected String description;
                                        @XmlElement(required = true)
                                        protected String flatElement;

                                        /**
                                         * Gets the value of the description property.
                                         * 
                                         * @return
                                         *     possible object is
                                         *     {@link String }
                                         *     
                                         */
                                        public String getDescription() {
                                            return description;
                                        }

                                        /**
                                         * Sets the value of the description property.
                                         * 
                                         * @param value
                                         *     allowed object is
                                         *     {@link String }
                                         *     
                                         */
                                        public void setDescription(String value) {
                                            this.description = value;
                                        }

                                        /**
                                         * Gets the value of the flatElement property.
                                         * 
                                         * @return
                                         *     possible object is
                                         *     {@link String }
                                         *     
                                         */
                                        public String getFlatElement() {
                                            return flatElement;
                                        }

                                        /**
                                         * Sets the value of the flatElement property.
                                         * 
                                         * @param value
                                         *     allowed object is
                                         *     {@link String }
                                         *     
                                         */
                                        public void setFlatElement(String value) {
                                            this.flatElement = value;
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
                             *         &lt;element name="eno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                             *         &lt;element name="shippingDateEnd" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                             *         &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                             *         &lt;element name="shippingDateStart" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
                             *         &lt;element name="shippingDateTimeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                             *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
                                "eno",
                                "shippingDateEnd",
                                "processInstanceId",
                                "shippingDateStart",
                                "shippingDateTimeInfo",
                                "status"
                            })
                            public static class Shipping {

                                protected String eno;
                                @XmlElement(type = String.class)
                                @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
                                @XmlSchemaType(name = "date")
                                protected LocalDate shippingDateEnd;
                                protected String processInstanceId;
                                @XmlElement(type = String.class)
                                @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
                                @XmlSchemaType(name = "date")
                                protected LocalDate shippingDateStart;
                                protected String shippingDateTimeInfo;
                                protected String status;

                                /**
                                 * Gets the value of the eno property.
                                 * 
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *     
                                 */
                                public String getEno() {
                                    return eno;
                                }

                                /**
                                 * Sets the value of the eno property.
                                 * 
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *     
                                 */
                                public void setEno(String value) {
                                    this.eno = value;
                                }

                                /**
                                 * Gets the value of the shippingDateEnd property.
                                 * 
                                 * @return
                                 *     possible object is
                                 *     {@link LocalDate }
                                 *     
                                 */
                                public LocalDate getShippingDateEnd() {
                                    return shippingDateEnd;
                                }

                                /**
                                 * Sets the value of the shippingDateEnd property.
                                 * 
                                 * @param value
                                 *     allowed object is
                                 *     {@link LocalDate }
                                 *     
                                 */
                                public void setShippingDateEnd(LocalDate value) {
                                    this.shippingDateEnd = value;
                                }

                                /**
                                 * Gets the value of the processInstanceId property.
                                 * 
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *     
                                 */
                                public String getProcessInstanceId() {
                                    return processInstanceId;
                                }

                                /**
                                 * Sets the value of the processInstanceId property.
                                 * 
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *     
                                 */
                                public void setProcessInstanceId(String value) {
                                    this.processInstanceId = value;
                                }

                                /**
                                 * Gets the value of the shippingDateStart property.
                                 * 
                                 * @return
                                 *     possible object is
                                 *     {@link LocalDate }
                                 *     
                                 */
                                public LocalDate getShippingDateStart() {
                                    return shippingDateStart;
                                }

                                /**
                                 * Sets the value of the shippingDateStart property.
                                 * 
                                 * @param value
                                 *     allowed object is
                                 *     {@link LocalDate }
                                 *     
                                 */
                                public void setShippingDateStart(LocalDate value) {
                                    this.shippingDateStart = value;
                                }

                                /**
                                 * Gets the value of the shippingDateTimeInfo property.
                                 * 
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *     
                                 */
                                public String getShippingDateTimeInfo() {
                                    return shippingDateTimeInfo;
                                }

                                /**
                                 * Sets the value of the shippingDateTimeInfo property.
                                 * 
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *     
                                 */
                                public void setShippingDateTimeInfo(String value) {
                                    this.shippingDateTimeInfo = value;
                                }

                                /**
                                 * Gets the value of the status property.
                                 * 
                                 * @return
                                 *     possible object is
                                 *     {@link String }
                                 *     
                                 */
                                public String getStatus() {
                                    return status;
                                }

                                /**
                                 * Sets the value of the status property.
                                 * 
                                 * @param value
                                 *     allowed object is
                                 *     {@link String }
                                 *     
                                 */
                                public void setStatus(String value) {
                                    this.status = value;
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
                 *         &lt;element name="viewFlat" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *         &lt;element name="view" maxOccurs="unbounded">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;sequence>
                 *                   &lt;element name="viewDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                 *                 &lt;/sequence>
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
                    "viewFlat",
                    "view"
                })
                public static class Views {

                    @XmlElement(required = true)
                    protected String viewFlat;
                    @XmlElement(required = true)
                    protected List<PersonResType.Families.Family.Offer.Views.View> view;

                    /**
                     * Gets the value of the viewFlat property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getViewFlat() {
                        return viewFlat;
                    }

                    /**
                     * Sets the value of the viewFlat property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setViewFlat(String value) {
                        this.viewFlat = value;
                    }

                    /**
                     * Gets the value of the view property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the JAXB object.
                     * This is why there is not a <CODE>set</CODE> method for the view property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getView().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link PersonResType.Families.Family.Offer.Views.View }
                     * 
                     * 
                     */
                    public List<PersonResType.Families.Family.Offer.Views.View> getView() {
                        if (view == null) {
                            view = new ArrayList<PersonResType.Families.Family.Offer.Views.View>();
                        }
                        return this.view;
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
                     *         &lt;element name="viewDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
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
                        "viewDate"
                    })
                    public static class View {

                        @XmlElement(required = true, type = String.class)
                        @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
                        @XmlSchemaType(name = "date")
                        protected LocalDate viewDate;

                        /**
                         * Gets the value of the viewDate property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link LocalDate }
                         *     
                         */
                        public LocalDate getViewDate() {
                            return viewDate;
                        }

                        /**
                         * Sets the value of the viewDate property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link LocalDate }
                         *     
                         */
                        public void setViewDate(LocalDate value) {
                            this.viewDate = value;
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
             *         &lt;element name="resident" maxOccurs="unbounded">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
             *                   &lt;element name="statusLiving" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                 &lt;/sequence>
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
                "resident"
            })
            public static class Residents {

                @XmlElement(required = true)
                protected List<PersonResType.Families.Family.Residents.Resident> resident;

                /**
                 * Gets the value of the resident property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the resident property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getResident().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link PersonResType.Families.Family.Residents.Resident }
                 * 
                 * 
                 */
                public List<PersonResType.Families.Family.Residents.Resident> getResident() {
                    if (resident == null) {
                        resident = new ArrayList<PersonResType.Families.Family.Residents.Resident>();
                    }
                    return this.resident;
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
                 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
                 *         &lt;element name="statusLiving" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
                    "birthDate",
                    "statusLiving"
                })
                public static class Resident {

                    @XmlElement(required = true, type = String.class)
                    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
                    @XmlSchemaType(name = "date")
                    protected LocalDate birthDate;
                    @XmlElement(required = true)
                    protected String statusLiving;

                    /**
                     * Gets the value of the birthDate property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link LocalDate }
                     *     
                     */
                    public LocalDate getBirthDate() {
                        return birthDate;
                    }

                    /**
                     * Sets the value of the birthDate property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link LocalDate }
                     *     
                     */
                    public void setBirthDate(LocalDate value) {
                        this.birthDate = value;
                    }

                    /**
                     * Gets the value of the statusLiving property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getStatusLiving() {
                        return statusLiving;
                    }

                    /**
                     * Sets the value of the statusLiving property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setStatusLiving(String value) {
                        this.statusLiving = value;
                    }

                }

            }

        }

    }

}
