
package ru.croc.ugd.ssr.model.integration.etpmv;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ��������� ������� �� ���� ��������
 * 
 * <p>Java class for TaskDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TaskDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DocumentTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ParameterTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Parameter">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any processContents='lax'/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="IncludeXmlView" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="IncludeBinaryView" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskDataType", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", propOrder = {
    "documentTypeCode",
    "parameterTypeCode",
    "parameter",
    "includeXmlView",
    "includeBinaryView"
})
public class TaskDataType {

    @XmlElement(name = "DocumentTypeCode", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", required = true)
    protected String documentTypeCode;
    @XmlElement(name = "ParameterTypeCode", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", required = true)
    protected String parameterTypeCode;
    @XmlElement(name = "Parameter", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", required = true)
    protected TaskDataType.Parameter parameter;
    @XmlElement(name = "IncludeXmlView", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
    protected boolean includeXmlView;
    @XmlElement(name = "IncludeBinaryView", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
    protected boolean includeBinaryView;

    /**
     * Gets the value of the documentTypeCode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDocumentTypeCode() {
        return documentTypeCode;
    }

    /**
     * Sets the value of the documentTypeCode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDocumentTypeCode(String value) {
        this.documentTypeCode = value;
    }

    /**
     * Gets the value of the parameterTypeCode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getParameterTypeCode() {
        return parameterTypeCode;
    }

    /**
     * Sets the value of the parameterTypeCode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setParameterTypeCode(String value) {
        this.parameterTypeCode = value;
    }

    /**
     * Gets the value of the parameter property.
     *
     * @return
     *     possible object is
     *     {@link TaskDataType.Parameter }
     *
     */
    public TaskDataType.Parameter getParameter() {
        return parameter;
    }

    /**
     * Sets the value of the parameter property.
     *
     * @param value
     *     allowed object is
     *     {@link TaskDataType.Parameter }
     *
     */
    public void setParameter(TaskDataType.Parameter value) {
        this.parameter = value;
    }

    /**
     * Gets the value of the includeXmlView property.
     * 
     */
    public boolean isIncludeXmlView() {
        return includeXmlView;
    }

    /**
     * Sets the value of the includeXmlView property.
     * 
     */
    public void setIncludeXmlView(boolean value) {
        this.includeXmlView = value;
    }

    /**
     * Gets the value of the includeBinaryView property.
     * 
     */
    public boolean isIncludeBinaryView() {
        return includeBinaryView;
    }

    /**
     * Sets the value of the includeBinaryView property.
     * 
     */
    public void setIncludeBinaryView(boolean value) {
        this.includeBinaryView = value;
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
     *         &lt;any processContents='lax'/>
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
        "any"
    })
    public static class Parameter {

        @XmlAnyElement(lax = true)
        protected Object any;

        /**
         * Gets the value of the any property.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     {@link Element }
         *     
         */
        public Object getAny() {
            return any;
        }

        /**
         * Sets the value of the any property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     {@link Element }
         *     
         */
        public void setAny(Object value) {
            this.any = value;
        }

    }

}
