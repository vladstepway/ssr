package ru.croc.ugd.ssr.model.integration.etpmv;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
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
 *       &lt;all>
 *         &lt;element name="applicant_type" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;enumeration value="3"/>
 *               &lt;enumeration value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="applicant_type_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="application_type" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="cadastral_number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="comb_okrug" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="comb_okrug_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractor_account" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractor_address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractor_bank_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractor_bic" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="contractor_corr_account" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractor_first_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractor_inn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractor_kpp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractor_last_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractor_ogrn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractor_okpo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractor_patronymic" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractor_phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="end_date" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="firstname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fullname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="goals" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="goals_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="goals_obj" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="goals_obj_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="goals_ogr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="goals_ogr_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="kadnumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="kadnumber2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="legal_address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="legal_reason" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="legal_reason_applicant" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="list_coordinates" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="middlename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="new_request_type_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notice_date" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="notice_number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="object_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ogrn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="order_date" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="order_number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="project_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="refinement_adress_work" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reissue_goal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reissue_goal_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="request_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="request_type_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shortname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="start_date" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="trustee_legal_reason" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="type" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="work_end_date" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="work_start_date" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="work_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="yavl" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="yavl_dov" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="yavl_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="zayv_pod" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="zayv_pod_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="zayv_yavl" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="project_worklist" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="project_work" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;all>
 *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="ppr_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="ppr_work_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="ppr_work_name_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="ppr_work_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="ppr_work_type_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="road_address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/all>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="adress_worklist" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="adress_work" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;all>
 *                             &lt;element name="addressid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="block" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="build" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="hold" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="house" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="okrug" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="rayon" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="street" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="streetid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="target" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/all>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="oati_sved_joblist" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="oati_sved_job" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;all>
 *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="signwork_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="signwork_name_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="work_amount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                             &lt;element name="work_unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="work_unit_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/all>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="oati_sved_job2list" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="oati_sved_job2" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;all>
 *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="work_amount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                             &lt;element name="work_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="work_name_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="work_unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="work_unit_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/all>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="oati_sved_job3list" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="oati_sved_job3" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;all>
 *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="work_amount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                             &lt;element name="work_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="work_name_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="work_unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="work_unit_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/all>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "ServiceProperties")
public class ServiceProperties {

    @XmlElement(name = "applicant_type")
    protected Integer applicantType;
    @XmlElement(name = "applicant_type_name")
    protected String applicantTypeName;
    @XmlElement(name = "application_type")
    protected Boolean applicationType;
    @XmlElement(name = "cadastral_number")
    protected String cadastralNumber;
    @XmlElement(name = "comb_okrug")
    protected String combOkrug;
    @XmlElement(name = "comb_okrug_name")
    protected String combOkrugName;
    @XmlElement(name = "contractor_account")
    protected String contractorAccount;
    @XmlElement(name = "contractor_address")
    protected String contractorAddress;
    @XmlElement(name = "contractor_bank_name")
    protected String contractorBankName;
    @XmlElement(name = "contractor_bic")
    protected Long contractorBic;
    @XmlElement(name = "contractor_corr_account")
    protected String contractorCorrAccount;
    @XmlElement(name = "contractor_first_name")
    protected String contractorFirstName;
    @XmlElement(name = "contractor_inn")
    protected String contractorInn;
    @XmlElement(name = "contractor_kpp")
    protected String contractorKpp;
    @XmlElement(name = "contractor_last_name")
    protected String contractorLastName;
    @XmlElement(name = "contractor_ogrn")
    protected String contractorOgrn;
    @XmlElement(name = "contractor_okpo")
    protected String contractorOkpo;
    @XmlElement(name = "contractor_patronymic")
    protected String contractorPatronymic;
    @XmlElement(name = "contractor_phone")
    protected String contractorPhone;
    protected String email;
    @XmlElement(name = "end_date")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endDate;
    protected String firstname;
    protected String fullname;
    protected String goals;
    @XmlElement(name = "goals_name")
    protected String goalsName;
    @XmlElement(name = "goals_obj")
    protected String goalsObj;
    @XmlElement(name = "goals_obj_name")
    protected String goalsObjName;
    @XmlElement(name = "goals_ogr")
    protected String goalsOgr;
    @XmlElement(name = "goals_ogr_name")
    protected String goalsOgrName;
    protected String inn;
    protected String kadnumber;
    protected String kadnumber2;
    protected String lastname;
    @XmlElement(name = "legal_address")
    protected String legalAddress;
    @XmlElement(name = "legal_reason")
    protected Integer legalReason;
    @XmlElement(name = "legal_reason_applicant")
    protected Integer legalReasonApplicant;
    @XmlElement(name = "list_coordinates")
    protected String listCoordinates;
    protected String middlename;
    protected String name;
    @XmlElement(name = "new_request_type_name")
    protected String newRequestTypeName;
    @XmlElement(name = "notice_date")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar noticeDate;
    @XmlElement(name = "notice_number")
    protected String noticeNumber;
    @XmlElement(name = "object_type")
    protected String objectType;
    protected String ogrn;
    @XmlElement(name = "order_date")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar orderDate;
    @XmlElement(name = "order_number")
    protected String orderNumber;
    protected String phone;
    @XmlElement(name = "project_name")
    protected String projectName;
    @XmlElement(name = "refinement_adress_work")
    protected String refinementAdressWork;
    @XmlElement(name = "reissue_goal")
    protected String reissueGoal;
    @XmlElement(name = "reissue_goal_name")
    protected String reissueGoalName;
    @XmlElement(name = "request_type")
    protected String requestType;
    @XmlElement(name = "request_type_name")
    protected String requestTypeName;
    protected String shortname;
    @XmlElement(name = "start_date")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startDate;
    @XmlElement(name = "trustee_legal_reason")
    protected Integer trusteeLegalReason;
    protected Integer type;
    @XmlElement(name = "work_end_date")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar workEndDate;
    @XmlElement(name = "work_start_date")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar workStartDate;
    @XmlElement(name = "work_type")
    protected String workType;
    protected Integer yavl;
    @XmlElement(name = "yavl_dov")
    protected Integer yavlDov;
    @XmlElement(name = "yavl_name")
    protected String yavlName;
    @XmlElement(name = "zayv_pod")
    protected Integer zayvPod;
    @XmlElement(name = "zayv_pod_name")
    protected String zayvPodName;
    @XmlElement(name = "zayv_yavl")
    protected Integer zayvYavl;
    @XmlElement(name = "project_worklist")
    protected ServiceProperties.ProjectWorklist projectWorklist;
    @XmlElement(name = "adress_worklist")
    protected ServiceProperties.AdressWorklist adressWorklist;
    @XmlElement(name = "oati_sved_joblist")
    protected ServiceProperties.OatiSvedJoblist oatiSvedJoblist;
    @XmlElement(name = "oati_sved_job2list")
    protected ServiceProperties.OatiSvedJob2List oatiSvedJob2List;
    @XmlElement(name = "oati_sved_job3list")
    protected ServiceProperties.OatiSvedJob3List oatiSvedJob3List;
    @XmlElement(name = "sms_data")
    protected String smsData;
    @XmlElement(name = "push_data")
    protected String pushData;
    @XmlElement(name = "push_title")
    protected String pushTitle;
    @XmlElement(name = "simple_text")
    protected String simpleText;
    @XmlElement(name = "unsubscribe_link")
    protected String unsubscribeLink;
    @XmlElement(name = "event_link")
    protected String eventLink;
    @XmlElement(name = "method_informing")
    protected ServiceProperties.MethodInforming methodInforming;

    /**
     * getSmsData.
     * @return String
     */
    public String getSmsData() {
        return smsData;
    }

    /**
     * setSmsData.
     */
    public void setSmsData(String smsData) {
        this.smsData = smsData;
    }

    /**
     * getPushData.
     * @return String
     */
    public String getPushData() {
        return pushData;
    }

    /**
     * setPushData.
     */
    public void setPushData(String pushData) {
        this.pushData = pushData;
    }

    /**
     * getPushTitle.
     * @return String
     */
    public String getPushTitle() {
        return pushTitle;
    }

    /**
     * setPushTitle.
     */
    public void setPushTitle(String pushTitle) {
        this.pushTitle = pushTitle;
    }

    /**
     * Gets the value of the applicantType property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getApplicantType() {
        return applicantType;
    }

    /**
     * Sets the value of the applicantType property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setApplicantType(Integer value) {
        this.applicantType = value;
    }

    /**
     * Gets the value of the applicantTypeName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getApplicantTypeName() {
        return applicantTypeName;
    }

    /**
     * Sets the value of the applicantTypeName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setApplicantTypeName(String value) {
        this.applicantTypeName = value;
    }

    /**
     * Gets the value of the applicationType property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isApplicationType() {
        return applicationType;
    }

    /**
     * Sets the value of the applicationType property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setApplicationType(Boolean value) {
        this.applicationType = value;
    }

    /**
     * Gets the value of the cadastralNumber property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCadastralNumber() {
        return cadastralNumber;
    }

    /**
     * Sets the value of the cadastralNumber property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCadastralNumber(String value) {
        this.cadastralNumber = value;
    }

    /**
     * Gets the value of the combOkrug property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCombOkrug() {
        return combOkrug;
    }

    /**
     * Sets the value of the combOkrug property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCombOkrug(String value) {
        this.combOkrug = value;
    }

    /**
     * Gets the value of the combOkrugName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCombOkrugName() {
        return combOkrugName;
    }

    /**
     * Sets the value of the combOkrugName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCombOkrugName(String value) {
        this.combOkrugName = value;
    }

    /**
     * Gets the value of the contractorAccount property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContractorAccount() {
        return contractorAccount;
    }

    /**
     * Sets the value of the contractorAccount property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContractorAccount(String value) {
        this.contractorAccount = value;
    }

    /**
     * Gets the value of the contractorAddress property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContractorAddress() {
        return contractorAddress;
    }

    /**
     * Sets the value of the contractorAddress property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContractorAddress(String value) {
        this.contractorAddress = value;
    }

    /**
     * Gets the value of the contractorBankName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContractorBankName() {
        return contractorBankName;
    }

    /**
     * Sets the value of the contractorBankName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContractorBankName(String value) {
        this.contractorBankName = value;
    }

    /**
     * Gets the value of the contractorBic property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Long getContractorBic() {
        return contractorBic;
    }

    /**
     * Sets the value of the contractorBic property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setContractorBic(Long value) {
        this.contractorBic = value;
    }

    /**
     * Gets the value of the contractorCorrAccount property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContractorCorrAccount() {
        return contractorCorrAccount;
    }

    /**
     * Sets the value of the contractorCorrAccount property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContractorCorrAccount(String value) {
        this.contractorCorrAccount = value;
    }

    /**
     * Gets the value of the contractorFirstName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContractorFirstName() {
        return contractorFirstName;
    }

    /**
     * Sets the value of the contractorFirstName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContractorFirstName(String value) {
        this.contractorFirstName = value;
    }

    /**
     * Gets the value of the contractorInn property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContractorInn() {
        return contractorInn;
    }

    /**
     * Sets the value of the contractorInn property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContractorInn(String value) {
        this.contractorInn = value;
    }

    /**
     * Gets the value of the contractorKpp property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContractorKpp() {
        return contractorKpp;
    }

    /**
     * Sets the value of the contractorKpp property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContractorKpp(String value) {
        this.contractorKpp = value;
    }

    /**
     * Gets the value of the contractorLastName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContractorLastName() {
        return contractorLastName;
    }

    /**
     * Sets the value of the contractorLastName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContractorLastName(String value) {
        this.contractorLastName = value;
    }

    /**
     * Gets the value of the contractorOgrn property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContractorOgrn() {
        return contractorOgrn;
    }

    /**
     * Sets the value of the contractorOgrn property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContractorOgrn(String value) {
        this.contractorOgrn = value;
    }

    /**
     * Gets the value of the contractorOkpo property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContractorOkpo() {
        return contractorOkpo;
    }

    /**
     * Sets the value of the contractorOkpo property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContractorOkpo(String value) {
        this.contractorOkpo = value;
    }

    /**
     * Gets the value of the contractorPatronymic property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContractorPatronymic() {
        return contractorPatronymic;
    }

    /**
     * Sets the value of the contractorPatronymic property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContractorPatronymic(String value) {
        this.contractorPatronymic = value;
    }

    /**
     * Gets the value of the contractorPhone property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContractorPhone() {
        return contractorPhone;
    }

    /**
     * Sets the value of the contractorPhone property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContractorPhone(String value) {
        this.contractorPhone = value;
    }

    /**
     * Gets the value of the email property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the endDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the firstname property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Sets the value of the firstname property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFirstname(String value) {
        this.firstname = value;
    }

    /**
     * Gets the value of the fullname property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * Sets the value of the fullname property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFullname(String value) {
        this.fullname = value;
    }

    /**
     * Gets the value of the goals property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGoals() {
        return goals;
    }

    /**
     * Sets the value of the goals property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGoals(String value) {
        this.goals = value;
    }

    /**
     * Gets the value of the goalsName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGoalsName() {
        return goalsName;
    }

    /**
     * Sets the value of the goalsName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGoalsName(String value) {
        this.goalsName = value;
    }

    /**
     * Gets the value of the goalsObj property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGoalsObj() {
        return goalsObj;
    }

    /**
     * Sets the value of the goalsObj property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGoalsObj(String value) {
        this.goalsObj = value;
    }

    /**
     * Gets the value of the goalsObjName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGoalsObjName() {
        return goalsObjName;
    }

    /**
     * Sets the value of the goalsObjName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGoalsObjName(String value) {
        this.goalsObjName = value;
    }

    /**
     * Gets the value of the goalsOgr property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGoalsOgr() {
        return goalsOgr;
    }

    /**
     * Sets the value of the goalsOgr property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGoalsOgr(String value) {
        this.goalsOgr = value;
    }

    /**
     * Gets the value of the goalsOgrName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGoalsOgrName() {
        return goalsOgrName;
    }

    /**
     * Sets the value of the goalsOgrName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGoalsOgrName(String value) {
        this.goalsOgrName = value;
    }

    /**
     * Gets the value of the inn property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getInn() {
        return inn;
    }

    /**
     * Sets the value of the inn property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInn(String value) {
        this.inn = value;
    }

    /**
     * Gets the value of the kadnumber property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKadnumber() {
        return kadnumber;
    }

    /**
     * Sets the value of the kadnumber property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKadnumber(String value) {
        this.kadnumber = value;
    }

    /**
     * Gets the value of the kadnumber2 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKadnumber2() {
        return kadnumber2;
    }

    /**
     * Sets the value of the kadnumber2 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKadnumber2(String value) {
        this.kadnumber2 = value;
    }

    /**
     * Gets the value of the lastname property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Sets the value of the lastname property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLastname(String value) {
        this.lastname = value;
    }

    /**
     * Gets the value of the legalAddress property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLegalAddress() {
        return legalAddress;
    }

    /**
     * Sets the value of the legalAddress property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLegalAddress(String value) {
        this.legalAddress = value;
    }

    /**
     * Gets the value of the legalReason property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getLegalReason() {
        return legalReason;
    }

    /**
     * Sets the value of the legalReason property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setLegalReason(Integer value) {
        this.legalReason = value;
    }

    /**
     * Gets the value of the legalReasonApplicant property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getLegalReasonApplicant() {
        return legalReasonApplicant;
    }

    /**
     * Sets the value of the legalReasonApplicant property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setLegalReasonApplicant(Integer value) {
        this.legalReasonApplicant = value;
    }

    /**
     * Gets the value of the listCoordinates property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getListCoordinates() {
        return listCoordinates;
    }

    /**
     * Sets the value of the listCoordinates property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setListCoordinates(String value) {
        this.listCoordinates = value;
    }

    /**
     * Gets the value of the middlename property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMiddlename() {
        return middlename;
    }

    /**
     * Sets the value of the middlename property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMiddlename(String value) {
        this.middlename = value;
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
     * Gets the value of the newRequestTypeName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNewRequestTypeName() {
        return newRequestTypeName;
    }

    /**
     * Sets the value of the newRequestTypeName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNewRequestTypeName(String value) {
        this.newRequestTypeName = value;
    }

    /**
     * Gets the value of the noticeDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getNoticeDate() {
        return noticeDate;
    }

    /**
     * Sets the value of the noticeDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setNoticeDate(XMLGregorianCalendar value) {
        this.noticeDate = value;
    }

    /**
     * Gets the value of the noticeNumber property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNoticeNumber() {
        return noticeNumber;
    }

    /**
     * Sets the value of the noticeNumber property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNoticeNumber(String value) {
        this.noticeNumber = value;
    }

    /**
     * Gets the value of the objectType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * Sets the value of the objectType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setObjectType(String value) {
        this.objectType = value;
    }

    /**
     * Gets the value of the ogrn property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOgrn() {
        return ogrn;
    }

    /**
     * Sets the value of the ogrn property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOgrn(String value) {
        this.ogrn = value;
    }

    /**
     * Gets the value of the orderDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getOrderDate() {
        return orderDate;
    }

    /**
     * Sets the value of the orderDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setOrderDate(XMLGregorianCalendar value) {
        this.orderDate = value;
    }

    /**
     * Gets the value of the orderNumber property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the value of the orderNumber property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrderNumber(String value) {
        this.orderNumber = value;
    }

    /**
     * Gets the value of the phone property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPhone(String value) {
        this.phone = value;
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
     * Gets the value of the refinementAdressWork property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRefinementAdressWork() {
        return refinementAdressWork;
    }

    /**
     * Sets the value of the refinementAdressWork property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRefinementAdressWork(String value) {
        this.refinementAdressWork = value;
    }

    /**
     * Gets the value of the reissueGoal property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getReissueGoal() {
        return reissueGoal;
    }

    /**
     * Sets the value of the reissueGoal property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReissueGoal(String value) {
        this.reissueGoal = value;
    }

    /**
     * Gets the value of the reissueGoalName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getReissueGoalName() {
        return reissueGoalName;
    }

    /**
     * Sets the value of the reissueGoalName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReissueGoalName(String value) {
        this.reissueGoalName = value;
    }

    /**
     * Gets the value of the requestType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRequestType() {
        return requestType;
    }

    /**
     * Sets the value of the requestType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRequestType(String value) {
        this.requestType = value;
    }

    /**
     * Gets the value of the requestTypeName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRequestTypeName() {
        return requestTypeName;
    }

    /**
     * Sets the value of the requestTypeName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRequestTypeName(String value) {
        this.requestTypeName = value;
    }

    /**
     * Gets the value of the shortname property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getShortname() {
        return shortname;
    }

    /**
     * Sets the value of the shortname property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setShortname(String value) {
        this.shortname = value;
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
     * Gets the value of the trusteeLegalReason property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getTrusteeLegalReason() {
        return trusteeLegalReason;
    }

    /**
     * Sets the value of the trusteeLegalReason property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setTrusteeLegalReason(Integer value) {
        this.trusteeLegalReason = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setType(Integer value) {
        this.type = value;
    }

    /**
     * Gets the value of the workEndDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getWorkEndDate() {
        return workEndDate;
    }

    /**
     * Sets the value of the workEndDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setWorkEndDate(XMLGregorianCalendar value) {
        this.workEndDate = value;
    }

    /**
     * Gets the value of the workStartDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getWorkStartDate() {
        return workStartDate;
    }

    /**
     * Sets the value of the workStartDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setWorkStartDate(XMLGregorianCalendar value) {
        this.workStartDate = value;
    }

    /**
     * Gets the value of the workType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getWorkType() {
        return workType;
    }

    /**
     * Sets the value of the workType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setWorkType(String value) {
        this.workType = value;
    }

    /**
     * Gets the value of the yavl property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getYavl() {
        return yavl;
    }

    /**
     * Sets the value of the yavl property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setYavl(Integer value) {
        this.yavl = value;
    }

    /**
     * Gets the value of the yavlDov property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getYavlDov() {
        return yavlDov;
    }

    /**
     * Sets the value of the yavlDov property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setYavlDov(Integer value) {
        this.yavlDov = value;
    }

    /**
     * Gets the value of the yavlName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getYavlName() {
        return yavlName;
    }

    /**
     * Sets the value of the yavlName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setYavlName(String value) {
        this.yavlName = value;
    }

    /**
     * Gets the value of the zayvPod property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getZayvPod() {
        return zayvPod;
    }

    /**
     * Sets the value of the zayvPod property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setZayvPod(Integer value) {
        this.zayvPod = value;
    }

    /**
     * Gets the value of the zayvPodName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getZayvPodName() {
        return zayvPodName;
    }

    /**
     * Sets the value of the zayvPodName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setZayvPodName(String value) {
        this.zayvPodName = value;
    }

    /**
     * Gets the value of the zayvYavl property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getZayvYavl() {
        return zayvYavl;
    }

    /**
     * Sets the value of the zayvYavl property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setZayvYavl(Integer value) {
        this.zayvYavl = value;
    }

    /**
     * Gets the value of the projectWorklist property.
     *
     * @return
     *     possible object is
     *     {@link ServiceProperties.ProjectWorklist }
     *
     */
    public ServiceProperties.ProjectWorklist getProjectWorklist() {
        return projectWorklist;
    }

    /**
     * Sets the value of the projectWorklist property.
     *
     * @param value
     *     allowed object is
     *     {@link ServiceProperties.ProjectWorklist }
     *
     */
    public void setProjectWorklist(ServiceProperties.ProjectWorklist value) {
        this.projectWorklist = value;
    }

    /**
     * Gets the value of the adressWorklist property.
     *
     * @return
     *     possible object is
     *     {@link ServiceProperties.AdressWorklist }
     *
     */
    public ServiceProperties.AdressWorklist getAdressWorklist() {
        return adressWorklist;
    }

    /**
     * Sets the value of the adressWorklist property.
     *
     * @param value
     *     allowed object is
     *     {@link ServiceProperties.AdressWorklist }
     *
     */
    public void setAdressWorklist(ServiceProperties.AdressWorklist value) {
        this.adressWorklist = value;
    }

    /**
     * Gets the value of the oatiSvedJoblist property.
     *
     * @return
     *     possible object is
     *     {@link ServiceProperties.OatiSvedJoblist }
     *
     */
    public ServiceProperties.OatiSvedJoblist getOatiSvedJoblist() {
        return oatiSvedJoblist;
    }

    /**
     * Sets the value of the oatiSvedJoblist property.
     *
     * @param value
     *     allowed object is
     *     {@link ServiceProperties.OatiSvedJoblist }
     *
     */
    public void setOatiSvedJoblist(ServiceProperties.OatiSvedJoblist value) {
        this.oatiSvedJoblist = value;
    }

    /**
     * Gets the value of the oatiSvedJob2List property.
     *
     * @return
     *     possible object is
     *     {@link ServiceProperties.OatiSvedJob2List }
     *
     */
    public ServiceProperties.OatiSvedJob2List getOatiSvedJob2List() {
        return oatiSvedJob2List;
    }

    /**
     * Sets the value of the oatiSvedJob2List property.
     *
     * @param value
     *     allowed object is
     *     {@link ServiceProperties.OatiSvedJob2List }
     *
     */
    public void setOatiSvedJob2List(ServiceProperties.OatiSvedJob2List value) {
        this.oatiSvedJob2List = value;
    }

    /**
     * Gets the value of the oatiSvedJob3List property.
     *
     * @return
     *     possible object is
     *     {@link ServiceProperties.OatiSvedJob3List }
     *
     */
    public ServiceProperties.OatiSvedJob3List getOatiSvedJob3List() {
        return oatiSvedJob3List;
    }

    /**
     * Sets the value of the oatiSvedJob3List property.
     *
     * @param value
     *     allowed object is
     *     {@link ServiceProperties.OatiSvedJob3List }
     *
     */
    public void setOatiSvedJob3List(ServiceProperties.OatiSvedJob3List value) {
        this.oatiSvedJob3List = value;
    }

    public String getSimpleText() {
        return simpleText;
    }

    public void setSimpleText(String simpleText) {
        this.simpleText = simpleText;
    }

    public String getUnsubscribeLink() {
        return unsubscribeLink;
    }

    public void setUnsubscribeLink(String unsubscribeLink) {
        this.unsubscribeLink = unsubscribeLink;
    }

    public String getEventLink() {
        return eventLink;
    }

    public void setEventLink(String eventLink) {
        this.eventLink = eventLink;
    }

    public ServiceProperties.MethodInforming getMethodInforming() {
        return methodInforming;
    }

    public void setMethodInforming(ServiceProperties.MethodInforming methodInforming) {
        this.methodInforming = methodInforming;
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
     *         &lt;element name="adress_work" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;all>
     *                   &lt;element name="addressid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="block" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="build" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="hold" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="house" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="okrug" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="rayon" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="street" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="streetid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="target" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/all>
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
        "adressWork"
    })
    public static class AdressWorklist {

        @XmlElement(name = "adress_work", required = true)
        protected List<AdressWork> adressWork;

        /**
         * Gets the value of the adressWork property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the adressWork property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAdressWork().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ServiceProperties.AdressWorklist.AdressWork }
         *
         *
         */
        public List<AdressWork> getAdressWork() {
            if (adressWork == null) {
                adressWork = new ArrayList<AdressWork>();
            }
            return this.adressWork;
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
         *       &lt;all>
         *         &lt;element name="addressid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="block" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="build" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="hold" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="house" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="okrug" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="rayon" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="street" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="streetid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="target" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/all>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {

        })
        public static class AdressWork {

            protected String addressid;
            protected String block;
            protected String build;
            protected String hold;
            protected String house;
            protected String name;
            protected String okrug;
            protected String rayon;
            protected String street;
            protected String streetid;
            protected String target;

            /**
             * Gets the value of the addressid property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getAddressid() {
                return addressid;
            }

            /**
             * Sets the value of the addressid property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setAddressid(String value) {
                this.addressid = value;
            }

            /**
             * Gets the value of the block property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getBlock() {
                return block;
            }

            /**
             * Sets the value of the block property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setBlock(String value) {
                this.block = value;
            }

            /**
             * Gets the value of the build property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getBuild() {
                return build;
            }

            /**
             * Sets the value of the build property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setBuild(String value) {
                this.build = value;
            }

            /**
             * Gets the value of the hold property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getHold() {
                return hold;
            }

            /**
             * Sets the value of the hold property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setHold(String value) {
                this.hold = value;
            }

            /**
             * Gets the value of the house property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getHouse() {
                return house;
            }

            /**
             * Sets the value of the house property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setHouse(String value) {
                this.house = value;
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
             * Gets the value of the rayon property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getRayon() {
                return rayon;
            }

            /**
             * Sets the value of the rayon property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setRayon(String value) {
                this.rayon = value;
            }

            /**
             * Gets the value of the street property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getStreet() {
                return street;
            }

            /**
             * Sets the value of the street property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setStreet(String value) {
                this.street = value;
            }

            /**
             * Gets the value of the streetid property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getStreetid() {
                return streetid;
            }

            /**
             * Sets the value of the streetid property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setStreetid(String value) {
                this.streetid = value;
            }

            /**
             * Gets the value of the target property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getTarget() {
                return target;
            }

            /**
             * Sets the value of the target property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setTarget(String value) {
                this.target = value;
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
     *         &lt;element name="oati_sved_job2" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;all>
     *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="work_amount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
     *                   &lt;element name="work_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="work_name_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="work_unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="work_unit_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/all>
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
        "oatiSvedJob2"
    })
    public static class OatiSvedJob2List {

        @XmlElement(name = "oati_sved_job2", required = true)
        protected List<OatiSvedJob2> oatiSvedJob2;

        /**
         * Gets the value of the oatiSvedJob2 property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the oatiSvedJob2 property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOatiSvedJob2().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ServiceProperties.OatiSvedJob2List.OatiSvedJob2 }
         *
         *
         */
        public List<OatiSvedJob2> getOatiSvedJob2() {
            if (oatiSvedJob2 == null) {
                oatiSvedJob2 = new ArrayList<OatiSvedJob2>();
            }
            return this.oatiSvedJob2;
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
         *       &lt;all>
         *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="work_amount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
         *         &lt;element name="work_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="work_name_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="work_unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="work_unit_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/all>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {

        })
        public static class OatiSvedJob2 {

            protected String name;
            @XmlElement(name = "work_amount")
            protected Integer workAmount;
            @XmlElement(name = "work_name")
            protected String workName;
            @XmlElement(name = "work_name_code")
            protected String workNameCode;
            @XmlElement(name = "work_unit")
            protected String workUnit;
            @XmlElement(name = "work_unit_code")
            protected String workUnitCode;

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
             * Gets the value of the workAmount property.
             *
             * @return
             *     possible object is
             *     {@link Integer }
             *
             */
            public Integer getWorkAmount() {
                return workAmount;
            }

            /**
             * Sets the value of the workAmount property.
             *
             * @param value
             *     allowed object is
             *     {@link Integer }
             *
             */
            public void setWorkAmount(Integer value) {
                this.workAmount = value;
            }

            /**
             * Gets the value of the workName property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getWorkName() {
                return workName;
            }

            /**
             * Sets the value of the workName property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setWorkName(String value) {
                this.workName = value;
            }

            /**
             * Gets the value of the workNameCode property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getWorkNameCode() {
                return workNameCode;
            }

            /**
             * Sets the value of the workNameCode property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setWorkNameCode(String value) {
                this.workNameCode = value;
            }

            /**
             * Gets the value of the workUnit property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getWorkUnit() {
                return workUnit;
            }

            /**
             * Sets the value of the workUnit property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setWorkUnit(String value) {
                this.workUnit = value;
            }

            /**
             * Gets the value of the workUnitCode property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getWorkUnitCode() {
                return workUnitCode;
            }

            /**
             * Sets the value of the workUnitCode property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setWorkUnitCode(String value) {
                this.workUnitCode = value;
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
     *         &lt;element name="oati_sved_job3" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;all>
     *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="work_amount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
     *                   &lt;element name="work_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="work_name_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="work_unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="work_unit_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/all>
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
        "oatiSvedJob3"
    })
    public static class OatiSvedJob3List {

        @XmlElement(name = "oati_sved_job3", required = true)
        protected List<OatiSvedJob3> oatiSvedJob3;

        /**
         * Gets the value of the oatiSvedJob3 property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the oatiSvedJob3 property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOatiSvedJob3().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ServiceProperties.OatiSvedJob3List.OatiSvedJob3 }
         *
         *
         */
        public List<OatiSvedJob3> getOatiSvedJob3() {
            if (oatiSvedJob3 == null) {
                oatiSvedJob3 = new ArrayList<OatiSvedJob3>();
            }
            return this.oatiSvedJob3;
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
         *       &lt;all>
         *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="work_amount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
         *         &lt;element name="work_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="work_name_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="work_unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="work_unit_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/all>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {

        })
        public static class OatiSvedJob3 {

            protected String name;
            @XmlElement(name = "work_amount")
            protected Integer workAmount;
            @XmlElement(name = "work_name")
            protected String workName;
            @XmlElement(name = "work_name_code")
            protected String workNameCode;
            @XmlElement(name = "work_unit")
            protected String workUnit;
            @XmlElement(name = "work_unit_code")
            protected String workUnitCode;

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
             * Gets the value of the workAmount property.
             *
             * @return
             *     possible object is
             *     {@link Integer }
             *
             */
            public Integer getWorkAmount() {
                return workAmount;
            }

            /**
             * Sets the value of the workAmount property.
             *
             * @param value
             *     allowed object is
             *     {@link Integer }
             *
             */
            public void setWorkAmount(Integer value) {
                this.workAmount = value;
            }

            /**
             * Gets the value of the workName property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getWorkName() {
                return workName;
            }

            /**
             * Sets the value of the workName property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setWorkName(String value) {
                this.workName = value;
            }

            /**
             * Gets the value of the workNameCode property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getWorkNameCode() {
                return workNameCode;
            }

            /**
             * Sets the value of the workNameCode property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setWorkNameCode(String value) {
                this.workNameCode = value;
            }

            /**
             * Gets the value of the workUnit property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getWorkUnit() {
                return workUnit;
            }

            /**
             * Sets the value of the workUnit property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setWorkUnit(String value) {
                this.workUnit = value;
            }

            /**
             * Gets the value of the workUnitCode property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getWorkUnitCode() {
                return workUnitCode;
            }

            /**
             * Sets the value of the workUnitCode property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setWorkUnitCode(String value) {
                this.workUnitCode = value;
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
     *         &lt;element name="oati_sved_job" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;all>
     *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="signwork_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="signwork_name_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="work_amount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
     *                   &lt;element name="work_unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="work_unit_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/all>
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
        "oatiSvedJob"
    })
    public static class OatiSvedJoblist {

        @XmlElement(name = "oati_sved_job", required = true)
        protected List<OatiSvedJob> oatiSvedJob;

        /**
         * Gets the value of the oatiSvedJob property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the oatiSvedJob property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOatiSvedJob().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ServiceProperties.OatiSvedJoblist.OatiSvedJob }
         *
         *
         */
        public List<OatiSvedJob> getOatiSvedJob() {
            if (oatiSvedJob == null) {
                oatiSvedJob = new ArrayList<OatiSvedJob>();
            }
            return this.oatiSvedJob;
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
         *       &lt;all>
         *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="signwork_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="signwork_name_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="work_amount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
         *         &lt;element name="work_unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="work_unit_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/all>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {

        })
        public static class OatiSvedJob {

            protected String name;
            @XmlElement(name = "signwork_name")
            protected String signworkName;
            @XmlElement(name = "signwork_name_code")
            protected String signworkNameCode;
            @XmlElement(name = "work_amount")
            protected Integer workAmount;
            @XmlElement(name = "work_unit")
            protected String workUnit;
            @XmlElement(name = "work_unit_code")
            protected String workUnitCode;

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
             * Gets the value of the signworkName property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getSignworkName() {
                return signworkName;
            }

            /**
             * Sets the value of the signworkName property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setSignworkName(String value) {
                this.signworkName = value;
            }

            /**
             * Gets the value of the signworkNameCode property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getSignworkNameCode() {
                return signworkNameCode;
            }

            /**
             * Sets the value of the signworkNameCode property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setSignworkNameCode(String value) {
                this.signworkNameCode = value;
            }

            /**
             * Gets the value of the workAmount property.
             *
             * @return
             *     possible object is
             *     {@link Integer }
             *
             */
            public Integer getWorkAmount() {
                return workAmount;
            }

            /**
             * Sets the value of the workAmount property.
             *
             * @param value
             *     allowed object is
             *     {@link Integer }
             *
             */
            public void setWorkAmount(Integer value) {
                this.workAmount = value;
            }

            /**
             * Gets the value of the workUnit property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getWorkUnit() {
                return workUnit;
            }

            /**
             * Sets the value of the workUnit property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setWorkUnit(String value) {
                this.workUnit = value;
            }

            /**
             * Gets the value of the workUnitCode property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getWorkUnitCode() {
                return workUnitCode;
            }

            /**
             * Sets the value of the workUnitCode property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setWorkUnitCode(String value) {
                this.workUnitCode = value;
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
     *         &lt;element name="project_work" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;all>
     *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="ppr_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="ppr_work_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="ppr_work_name_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="ppr_work_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="ppr_work_type_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="road_address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/all>
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
        "projectWork"
    })
    public static class ProjectWorklist {

        @XmlElement(name = "project_work", required = true)
        protected List<ProjectWork> projectWork;

        /**
         * Gets the value of the projectWork property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the projectWork property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getProjectWork().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ServiceProperties.ProjectWorklist.ProjectWork }
         *
         *
         */
        public List<ProjectWork> getProjectWork() {
            if (projectWork == null) {
                projectWork = new ArrayList<ProjectWork>();
            }
            return this.projectWork;
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
         *       &lt;all>
         *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="ppr_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="ppr_work_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="ppr_work_name_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="ppr_work_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="ppr_work_type_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="road_address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/all>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {

        })
        public static class ProjectWork {

            protected String name;
            @XmlElement(name = "ppr_name")
            protected String pprName;
            @XmlElement(name = "ppr_work_name")
            protected String pprWorkName;
            @XmlElement(name = "ppr_work_name_code")
            protected String pprWorkNameCode;
            @XmlElement(name = "ppr_work_type")
            protected String pprWorkType;
            @XmlElement(name = "ppr_work_type_name")
            protected String pprWorkTypeName;
            @XmlElement(name = "road_address")
            protected String roadAddress;

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
             * Gets the value of the pprName property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPprName() {
                return pprName;
            }

            /**
             * Sets the value of the pprName property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPprName(String value) {
                this.pprName = value;
            }

            /**
             * Gets the value of the pprWorkName property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPprWorkName() {
                return pprWorkName;
            }

            /**
             * Sets the value of the pprWorkName property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPprWorkName(String value) {
                this.pprWorkName = value;
            }

            /**
             * Gets the value of the pprWorkNameCode property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPprWorkNameCode() {
                return pprWorkNameCode;
            }

            /**
             * Sets the value of the pprWorkNameCode property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPprWorkNameCode(String value) {
                this.pprWorkNameCode = value;
            }

            /**
             * Gets the value of the pprWorkType property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPprWorkType() {
                return pprWorkType;
            }

            /**
             * Sets the value of the pprWorkType property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPprWorkType(String value) {
                this.pprWorkType = value;
            }

            /**
             * Gets the value of the pprWorkTypeName property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPprWorkTypeName() {
                return pprWorkTypeName;
            }

            /**
             * Sets the value of the pprWorkTypeName property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPprWorkTypeName(String value) {
                this.pprWorkTypeName = value;
            }

            /**
             * Gets the value of the roadAddress property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRoadAddress() {
                return roadAddress;
            }

            /**
             * Sets the value of the roadAddress property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRoadAddress(String value) {
                this.roadAddress = value;
            }

        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class MethodInforming {
        @XmlElement(name = "value")
        protected List<Integer> value;

        public List<Integer> getValue() {
            if (value == null) {
                value = new ArrayList<Integer>();
            }
            return this.value;
        }
    }

}
