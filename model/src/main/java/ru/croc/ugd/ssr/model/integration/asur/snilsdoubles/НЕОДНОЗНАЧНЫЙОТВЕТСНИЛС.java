
package ru.croc.ugd.ssr.model.integration.asur.snilsdoubles;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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
 *         &lt;element name="ФИО">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Фамилия">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *                         &lt;maxLength value="40"/>
 *                         &lt;minLength value="1"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="Имя">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *                         &lt;maxLength value="40"/>
 *                         &lt;minLength value="1"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="Отчество" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *                         &lt;maxLength value="40"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Пол">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="МУЖСКОЙ"/>
 *               &lt;enumeration value="ЖЕНСКИЙ"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;choice>
 *           &lt;element name="ДатаРождения">
 *             &lt;simpleType>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                 &lt;pattern value="\d{2}\.\d{2}\.\d{4}"/>
 *               &lt;/restriction>
 *             &lt;/simpleType>
 *           &lt;/element>
 *           &lt;element name="ДатаРожденияОсобая">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="День">
 *                       &lt;simpleType>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *                           &lt;minInclusive value="0"/>
 *                           &lt;maxInclusive value="31"/>
 *                         &lt;/restriction>
 *                       &lt;/simpleType>
 *                     &lt;/element>
 *                     &lt;element name="Месяц">
 *                       &lt;simpleType>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *                           &lt;minInclusive value="0"/>
 *                           &lt;maxInclusive value="12"/>
 *                         &lt;/restriction>
 *                       &lt;/simpleType>
 *                     &lt;/element>
 *                     &lt;element name="Год">
 *                       &lt;simpleType>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}gYear">
 *                         &lt;/restriction>
 *                       &lt;/simpleType>
 *                     &lt;/element>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/choice>
 *         &lt;element name="СведенияОдвойнике" maxOccurs="99" minOccurs="2">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="СтраховойНомер">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;pattern value="\d{3}-\d{3}-\d{3} \d{2}"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="МестоРождения">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="ТипМестаРождения">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;enumeration value="ОСОБОЕ"/>
 *                                   &lt;enumeration value="СТАНДАРТНОЕ"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="ГородРождения" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *                                   &lt;maxLength value="200"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="РайонРождения" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *                                   &lt;maxLength value="200"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="РегионРождения" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *                                   &lt;maxLength value="200"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="СтранаРождения" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *                                   &lt;maxLength value="200"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="УдостоверяющийДокумент">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="ТипУдостоверяющего">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;maxLength value="14"/>
 *                                   &lt;enumeration value="ПАСПОРТ"/>
 *                                   &lt;enumeration value="ЗГПАСПОРТ"/>
 *                                   &lt;enumeration value="СВИД О РОЖД"/>
 *                                   &lt;enumeration value="УДОСТ ОФИЦЕРА"/>
 *                                   &lt;enumeration value="СПРАВКА ОБ ОСВ"/>
 *                                   &lt;enumeration value="ПАСПОРТ МОРФЛТ"/>
 *                                   &lt;enumeration value="ВОЕННЫЙ БИЛЕТ"/>
 *                                   &lt;enumeration value="ДИППАСПОРТ РФ"/>
 *                                   &lt;enumeration value="ИНПАСПОРТ"/>
 *                                   &lt;enumeration value="СВИД БЕЖЕНЦА"/>
 *                                   &lt;enumeration value="ВИД НА ЖИТЕЛЬ"/>
 *                                   &lt;enumeration value="УДОСТ БЕЖЕНЦА"/>
 *                                   &lt;enumeration value="ВРЕМ УДОСТ"/>
 *                                   &lt;enumeration value="ПАСПОРТ РОССИИ"/>
 *                                   &lt;enumeration value="ЗГПАСПОРТ РФ"/>
 *                                   &lt;enumeration value="ПАСПОРТ МОРЯКА"/>
 *                                   &lt;enumeration value="ВОЕН БИЛЕТ ОЗ"/>
 *                                   &lt;enumeration value="ПРОЧЕЕ"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="Документ">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="НаименованиеУдостоверяющего">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *                                             &lt;maxLength value="80"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="СерияРимскиеЦифры" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *                                             &lt;maxLength value="8"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="СерияРусскиеБуквы" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *                                             &lt;maxLength value="8"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="НомерУдостоверяющего" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedLong">
 *                                             &lt;minInclusive value="0"/>
 *                                             &lt;maxInclusive value="99999999"/>
 *                                             &lt;totalDigits value="8"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="ДатаВыдачи">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;pattern value="\d{2}\.\d{2}\.\d{4}"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
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
 *         &lt;element name="КоличествоДвойников">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger">
 *               &lt;minInclusive value="2"/>
 *               &lt;maxInclusive value="99"/>
 *               &lt;totalDigits value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ДатаФормирования">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{2}\.\d{2}\.\d{4}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
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
    "\u0444\u0438\u043e",
    "\u043f\u043e\u043b",
    "\u0434\u0430\u0442\u0430\u0420\u043e\u0436\u0434\u0435\u043d\u0438\u044f",
    "\u0434\u0430\u0442\u0430\u0420\u043e\u0436\u0434\u0435\u043d\u0438\u044f\u041e\u0441\u043e\u0431\u0430\u044f",
    "\u0441\u0432\u0435\u0434\u0435\u043d\u0438\u044f\u041e\u0434\u0432\u043e\u0439\u043d\u0438\u043a\u0435",
    "\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e\u0414\u0432\u043e\u0439\u043d\u0438\u043a\u043e\u0432",
    "\u0434\u0430\u0442\u0430\u0424\u043e\u0440\u043c\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u044f"
})
@XmlRootElement(name = "\u041d\u0415\u041e\u0414\u041d\u041e\u0417\u041d\u0410\u0427\u041d\u042b\u0419_\u041e\u0422\u0412\u0415\u0422_\u0421\u041d\u0418\u041b\u0421")
public class НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС {

    @XmlElement(name = "\u0424\u0418\u041e", required = true)
    protected НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.ФИО фио;
    @XmlElement(name = "\u041f\u043e\u043b", required = true)
    protected String пол;
    @XmlElement(name = "\u0414\u0430\u0442\u0430\u0420\u043e\u0436\u0434\u0435\u043d\u0438\u044f")
    protected String датаРождения;
    @XmlElement(name = "\u0414\u0430\u0442\u0430\u0420\u043e\u0436\u0434\u0435\u043d\u0438\u044f\u041e\u0441\u043e\u0431\u0430\u044f")
    protected НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.ДатаРожденияОсобая датаРожденияОсобая;
    @XmlElement(name = "\u0421\u0432\u0435\u0434\u0435\u043d\u0438\u044f\u041e\u0434\u0432\u043e\u0439\u043d\u0438\u043a\u0435", required = true)
    protected List<НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике> сведенияОдвойнике;
    @XmlElement(name = "\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e\u0414\u0432\u043e\u0439\u043d\u0438\u043a\u043e\u0432")
    protected int количествоДвойников;
    @XmlElement(name = "\u0414\u0430\u0442\u0430\u0424\u043e\u0440\u043c\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u044f", required = true)
    protected String датаФормирования;

    /**
     * Gets the value of the фио property.
     * 
     * @return
     *     possible object is
     *     {@link НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.ФИО }
     *     
     */
    public НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.ФИО getФИО() {
        return фио;
    }

    /**
     * Sets the value of the фио property.
     * 
     * @param value
     *     allowed object is
     *     {@link НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.ФИО }
     *     
     */
    public void setФИО(НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.ФИО value) {
        this.фио = value;
    }

    /**
     * Gets the value of the пол property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getПол() {
        return пол;
    }

    /**
     * Sets the value of the пол property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setПол(String value) {
        this.пол = value;
    }

    /**
     * Gets the value of the датаРождения property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getДатаРождения() {
        return датаРождения;
    }

    /**
     * Sets the value of the датаРождения property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setДатаРождения(String value) {
        this.датаРождения = value;
    }

    /**
     * Gets the value of the датаРожденияОсобая property.
     * 
     * @return
     *     possible object is
     *     {@link НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.ДатаРожденияОсобая }
     *     
     */
    public НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.ДатаРожденияОсобая getДатаРожденияОсобая() {
        return датаРожденияОсобая;
    }

    /**
     * Sets the value of the датаРожденияОсобая property.
     * 
     * @param value
     *     allowed object is
     *     {@link НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.ДатаРожденияОсобая }
     *     
     */
    public void setДатаРожденияОсобая(НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.ДатаРожденияОсобая value) {
        this.датаРожденияОсобая = value;
    }

    /**
     * Gets the value of the сведенияОдвойнике property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the сведенияОдвойнике property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getСведенияОдвойнике().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике }
     * 
     * 
     */
    public List<НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике> getСведенияОдвойнике() {
        if (сведенияОдвойнике == null) {
            сведенияОдвойнике = new ArrayList<НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике>();
        }
        return this.сведенияОдвойнике;
    }

    /**
     * Gets the value of the количествоДвойников property.
     * 
     */
    public int getКоличествоДвойников() {
        return количествоДвойников;
    }

    /**
     * Sets the value of the количествоДвойников property.
     * 
     */
    public void setКоличествоДвойников(int value) {
        this.количествоДвойников = value;
    }

    /**
     * Gets the value of the датаФормирования property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getДатаФормирования() {
        return датаФормирования;
    }

    /**
     * Sets the value of the датаФормирования property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setДатаФормирования(String value) {
        this.датаФормирования = value;
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
     *         &lt;element name="День">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
     *               &lt;minInclusive value="0"/>
     *               &lt;maxInclusive value="31"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="Месяц">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
     *               &lt;minInclusive value="0"/>
     *               &lt;maxInclusive value="12"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="Год">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}gYear">
     *             &lt;/restriction>
     *           &lt;/simpleType>
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
        "\u0434\u0435\u043d\u044c",
        "\u043c\u0435\u0441\u044f\u0446",
        "\u0433\u043e\u0434"
    })
    public static class ДатаРожденияОсобая {

        @XmlElement(name = "\u0414\u0435\u043d\u044c")
        protected int день;
        @XmlElement(name = "\u041c\u0435\u0441\u044f\u0446")
        protected int месяц;
        @XmlElement(name = "\u0413\u043e\u0434", required = true)
        protected XMLGregorianCalendar год;

        /**
         * Gets the value of the день property.
         * 
         */
        public int getДень() {
            return день;
        }

        /**
         * Sets the value of the день property.
         * 
         */
        public void setДень(int value) {
            this.день = value;
        }

        /**
         * Gets the value of the месяц property.
         * 
         */
        public int getМесяц() {
            return месяц;
        }

        /**
         * Sets the value of the месяц property.
         * 
         */
        public void setМесяц(int value) {
            this.месяц = value;
        }

        /**
         * Gets the value of the год property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getГод() {
            return год;
        }

        /**
         * Sets the value of the год property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setГод(XMLGregorianCalendar value) {
            this.год = value;
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
     *         &lt;element name="СтраховойНомер">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;pattern value="\d{3}-\d{3}-\d{3} \d{2}"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="МестоРождения">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="ТипМестаРождения">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;enumeration value="ОСОБОЕ"/>
     *                         &lt;enumeration value="СТАНДАРТНОЕ"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="ГородРождения" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
     *                         &lt;maxLength value="200"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="РайонРождения" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
     *                         &lt;maxLength value="200"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="РегионРождения" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
     *                         &lt;maxLength value="200"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="СтранаРождения" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
     *                         &lt;maxLength value="200"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="УдостоверяющийДокумент">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="ТипУдостоверяющего">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;maxLength value="14"/>
     *                         &lt;enumeration value="ПАСПОРТ"/>
     *                         &lt;enumeration value="ЗГПАСПОРТ"/>
     *                         &lt;enumeration value="СВИД О РОЖД"/>
     *                         &lt;enumeration value="УДОСТ ОФИЦЕРА"/>
     *                         &lt;enumeration value="СПРАВКА ОБ ОСВ"/>
     *                         &lt;enumeration value="ПАСПОРТ МОРФЛТ"/>
     *                         &lt;enumeration value="ВОЕННЫЙ БИЛЕТ"/>
     *                         &lt;enumeration value="ДИППАСПОРТ РФ"/>
     *                         &lt;enumeration value="ИНПАСПОРТ"/>
     *                         &lt;enumeration value="СВИД БЕЖЕНЦА"/>
     *                         &lt;enumeration value="ВИД НА ЖИТЕЛЬ"/>
     *                         &lt;enumeration value="УДОСТ БЕЖЕНЦА"/>
     *                         &lt;enumeration value="ВРЕМ УДОСТ"/>
     *                         &lt;enumeration value="ПАСПОРТ РОССИИ"/>
     *                         &lt;enumeration value="ЗГПАСПОРТ РФ"/>
     *                         &lt;enumeration value="ПАСПОРТ МОРЯКА"/>
     *                         &lt;enumeration value="ВОЕН БИЛЕТ ОЗ"/>
     *                         &lt;enumeration value="ПРОЧЕЕ"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="Документ">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="НаименованиеУдостоверяющего">
     *                               &lt;simpleType>
     *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
     *                                   &lt;maxLength value="80"/>
     *                                 &lt;/restriction>
     *                               &lt;/simpleType>
     *                             &lt;/element>
     *                             &lt;element name="СерияРимскиеЦифры" minOccurs="0">
     *                               &lt;simpleType>
     *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
     *                                   &lt;maxLength value="8"/>
     *                                 &lt;/restriction>
     *                               &lt;/simpleType>
     *                             &lt;/element>
     *                             &lt;element name="СерияРусскиеБуквы" minOccurs="0">
     *                               &lt;simpleType>
     *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
     *                                   &lt;maxLength value="8"/>
     *                                 &lt;/restriction>
     *                               &lt;/simpleType>
     *                             &lt;/element>
     *                             &lt;element name="НомерУдостоверяющего" minOccurs="0">
     *                               &lt;simpleType>
     *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedLong">
     *                                   &lt;minInclusive value="0"/>
     *                                   &lt;maxInclusive value="99999999"/>
     *                                   &lt;totalDigits value="8"/>
     *                                 &lt;/restriction>
     *                               &lt;/simpleType>
     *                             &lt;/element>
     *                             &lt;element name="ДатаВыдачи">
     *                               &lt;simpleType>
     *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                                   &lt;pattern value="\d{2}\.\d{2}\.\d{4}"/>
     *                                 &lt;/restriction>
     *                               &lt;/simpleType>
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
        "\u0441\u0442\u0440\u0430\u0445\u043e\u0432\u043e\u0439\u041d\u043e\u043c\u0435\u0440",
        "\u043c\u0435\u0441\u0442\u043e\u0420\u043e\u0436\u0434\u0435\u043d\u0438\u044f",
        "\u0443\u0434\u043e\u0441\u0442\u043e\u0432\u0435\u0440\u044f\u044e\u0449\u0438\u0439\u0414\u043e\u043a\u0443\u043c\u0435\u043d\u0442"
    })
    public static class СведенияОдвойнике {

        @XmlElement(name = "\u0421\u0442\u0440\u0430\u0445\u043e\u0432\u043e\u0439\u041d\u043e\u043c\u0435\u0440", required = true)
        protected String страховойНомер;
        @XmlElement(name = "\u041c\u0435\u0441\u0442\u043e\u0420\u043e\u0436\u0434\u0435\u043d\u0438\u044f", required = true)
        protected НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.МестоРождения местоРождения;
        @XmlElement(name = "\u0423\u0434\u043e\u0441\u0442\u043e\u0432\u0435\u0440\u044f\u044e\u0449\u0438\u0439\u0414\u043e\u043a\u0443\u043c\u0435\u043d\u0442", required = true)
        protected НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.УдостоверяющийДокумент удостоверяющийДокумент;

        /**
         * Gets the value of the страховойНомер property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getСтраховойНомер() {
            return страховойНомер;
        }

        /**
         * Sets the value of the страховойНомер property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setСтраховойНомер(String value) {
            this.страховойНомер = value;
        }

        /**
         * Gets the value of the местоРождения property.
         * 
         * @return
         *     possible object is
         *     {@link НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.МестоРождения }
         *     
         */
        public НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.МестоРождения getМестоРождения() {
            return местоРождения;
        }

        /**
         * Sets the value of the местоРождения property.
         * 
         * @param value
         *     allowed object is
         *     {@link НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.МестоРождения }
         *     
         */
        public void setМестоРождения(НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.МестоРождения value) {
            this.местоРождения = value;
        }

        /**
         * Gets the value of the удостоверяющийДокумент property.
         * 
         * @return
         *     possible object is
         *     {@link НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.УдостоверяющийДокумент }
         *     
         */
        public НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.УдостоверяющийДокумент getУдостоверяющийДокумент() {
            return удостоверяющийДокумент;
        }

        /**
         * Sets the value of the удостоверяющийДокумент property.
         * 
         * @param value
         *     allowed object is
         *     {@link НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.УдостоверяющийДокумент }
         *     
         */
        public void setУдостоверяющийДокумент(НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.УдостоверяющийДокумент value) {
            this.удостоверяющийДокумент = value;
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
         *         &lt;element name="ТипМестаРождения">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;enumeration value="ОСОБОЕ"/>
         *               &lt;enumeration value="СТАНДАРТНОЕ"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="ГородРождения" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
         *               &lt;maxLength value="200"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="РайонРождения" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
         *               &lt;maxLength value="200"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="РегионРождения" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
         *               &lt;maxLength value="200"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="СтранаРождения" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
         *               &lt;maxLength value="200"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
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
            "\u0442\u0438\u043f\u041c\u0435\u0441\u0442\u0430\u0420\u043e\u0436\u0434\u0435\u043d\u0438\u044f",
            "\u0433\u043e\u0440\u043e\u0434\u0420\u043e\u0436\u0434\u0435\u043d\u0438\u044f",
            "\u0440\u0430\u0439\u043e\u043d\u0420\u043e\u0436\u0434\u0435\u043d\u0438\u044f",
            "\u0440\u0435\u0433\u0438\u043e\u043d\u0420\u043e\u0436\u0434\u0435\u043d\u0438\u044f",
            "\u0441\u0442\u0440\u0430\u043d\u0430\u0420\u043e\u0436\u0434\u0435\u043d\u0438\u044f"
        })
        public static class МестоРождения {

            @XmlElement(name = "\u0422\u0438\u043f\u041c\u0435\u0441\u0442\u0430\u0420\u043e\u0436\u0434\u0435\u043d\u0438\u044f", required = true)
            protected String типМестаРождения;
            @XmlElement(name = "\u0413\u043e\u0440\u043e\u0434\u0420\u043e\u0436\u0434\u0435\u043d\u0438\u044f")
            @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
            protected String городРождения;
            @XmlElement(name = "\u0420\u0430\u0439\u043e\u043d\u0420\u043e\u0436\u0434\u0435\u043d\u0438\u044f")
            @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
            protected String районРождения;
            @XmlElement(name = "\u0420\u0435\u0433\u0438\u043e\u043d\u0420\u043e\u0436\u0434\u0435\u043d\u0438\u044f")
            @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
            protected String регионРождения;
            @XmlElement(name = "\u0421\u0442\u0440\u0430\u043d\u0430\u0420\u043e\u0436\u0434\u0435\u043d\u0438\u044f")
            @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
            protected String странаРождения;

            /**
             * Gets the value of the типМестаРождения property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getТипМестаРождения() {
                return типМестаРождения;
            }

            /**
             * Sets the value of the типМестаРождения property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setТипМестаРождения(String value) {
                this.типМестаРождения = value;
            }

            /**
             * Gets the value of the городРождения property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getГородРождения() {
                return городРождения;
            }

            /**
             * Sets the value of the городРождения property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setГородРождения(String value) {
                this.городРождения = value;
            }

            /**
             * Gets the value of the районРождения property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getРайонРождения() {
                return районРождения;
            }

            /**
             * Sets the value of the районРождения property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setРайонРождения(String value) {
                this.районРождения = value;
            }

            /**
             * Gets the value of the регионРождения property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getРегионРождения() {
                return регионРождения;
            }

            /**
             * Sets the value of the регионРождения property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setРегионРождения(String value) {
                this.регионРождения = value;
            }

            /**
             * Gets the value of the странаРождения property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getСтранаРождения() {
                return странаРождения;
            }

            /**
             * Sets the value of the странаРождения property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setСтранаРождения(String value) {
                this.странаРождения = value;
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
         *         &lt;element name="ТипУдостоверяющего">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;maxLength value="14"/>
         *               &lt;enumeration value="ПАСПОРТ"/>
         *               &lt;enumeration value="ЗГПАСПОРТ"/>
         *               &lt;enumeration value="СВИД О РОЖД"/>
         *               &lt;enumeration value="УДОСТ ОФИЦЕРА"/>
         *               &lt;enumeration value="СПРАВКА ОБ ОСВ"/>
         *               &lt;enumeration value="ПАСПОРТ МОРФЛТ"/>
         *               &lt;enumeration value="ВОЕННЫЙ БИЛЕТ"/>
         *               &lt;enumeration value="ДИППАСПОРТ РФ"/>
         *               &lt;enumeration value="ИНПАСПОРТ"/>
         *               &lt;enumeration value="СВИД БЕЖЕНЦА"/>
         *               &lt;enumeration value="ВИД НА ЖИТЕЛЬ"/>
         *               &lt;enumeration value="УДОСТ БЕЖЕНЦА"/>
         *               &lt;enumeration value="ВРЕМ УДОСТ"/>
         *               &lt;enumeration value="ПАСПОРТ РОССИИ"/>
         *               &lt;enumeration value="ЗГПАСПОРТ РФ"/>
         *               &lt;enumeration value="ПАСПОРТ МОРЯКА"/>
         *               &lt;enumeration value="ВОЕН БИЛЕТ ОЗ"/>
         *               &lt;enumeration value="ПРОЧЕЕ"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="Документ">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="НаименованиеУдостоверяющего">
         *                     &lt;simpleType>
         *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
         *                         &lt;maxLength value="80"/>
         *                       &lt;/restriction>
         *                     &lt;/simpleType>
         *                   &lt;/element>
         *                   &lt;element name="СерияРимскиеЦифры" minOccurs="0">
         *                     &lt;simpleType>
         *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
         *                         &lt;maxLength value="8"/>
         *                       &lt;/restriction>
         *                     &lt;/simpleType>
         *                   &lt;/element>
         *                   &lt;element name="СерияРусскиеБуквы" minOccurs="0">
         *                     &lt;simpleType>
         *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
         *                         &lt;maxLength value="8"/>
         *                       &lt;/restriction>
         *                     &lt;/simpleType>
         *                   &lt;/element>
         *                   &lt;element name="НомерУдостоверяющего" minOccurs="0">
         *                     &lt;simpleType>
         *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedLong">
         *                         &lt;minInclusive value="0"/>
         *                         &lt;maxInclusive value="99999999"/>
         *                         &lt;totalDigits value="8"/>
         *                       &lt;/restriction>
         *                     &lt;/simpleType>
         *                   &lt;/element>
         *                   &lt;element name="ДатаВыдачи">
         *                     &lt;simpleType>
         *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                         &lt;pattern value="\d{2}\.\d{2}\.\d{4}"/>
         *                       &lt;/restriction>
         *                     &lt;/simpleType>
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
            "\u0442\u0438\u043f\u0423\u0434\u043e\u0441\u0442\u043e\u0432\u0435\u0440\u044f\u044e\u0449\u0435\u0433\u043e",
            "\u0434\u043e\u043a\u0443\u043c\u0435\u043d\u0442"
        })
        public static class УдостоверяющийДокумент {

            @XmlElement(name = "\u0422\u0438\u043f\u0423\u0434\u043e\u0441\u0442\u043e\u0432\u0435\u0440\u044f\u044e\u0449\u0435\u0433\u043e", required = true)
            protected String типУдостоверяющего;
            @XmlElement(name = "\u0414\u043e\u043a\u0443\u043c\u0435\u043d\u0442", required = true)
            protected НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.УдостоверяющийДокумент.Документ документ;

            /**
             * Gets the value of the типУдостоверяющего property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getТипУдостоверяющего() {
                return типУдостоверяющего;
            }

            /**
             * Sets the value of the типУдостоверяющего property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setТипУдостоверяющего(String value) {
                this.типУдостоверяющего = value;
            }

            /**
             * Gets the value of the документ property.
             * 
             * @return
             *     possible object is
             *     {@link НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.УдостоверяющийДокумент.Документ }
             *     
             */
            public НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.УдостоверяющийДокумент.Документ getДокумент() {
                return документ;
            }

            /**
             * Sets the value of the документ property.
             * 
             * @param value
             *     allowed object is
             *     {@link НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.УдостоверяющийДокумент.Документ }
             *     
             */
            public void setДокумент(НЕОДНОЗНАЧНЫЙОТВЕТСНИЛС.СведенияОдвойнике.УдостоверяющийДокумент.Документ value) {
                this.документ = value;
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
             *         &lt;element name="НаименованиеУдостоверяющего">
             *           &lt;simpleType>
             *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
             *               &lt;maxLength value="80"/>
             *             &lt;/restriction>
             *           &lt;/simpleType>
             *         &lt;/element>
             *         &lt;element name="СерияРимскиеЦифры" minOccurs="0">
             *           &lt;simpleType>
             *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
             *               &lt;maxLength value="8"/>
             *             &lt;/restriction>
             *           &lt;/simpleType>
             *         &lt;/element>
             *         &lt;element name="СерияРусскиеБуквы" minOccurs="0">
             *           &lt;simpleType>
             *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
             *               &lt;maxLength value="8"/>
             *             &lt;/restriction>
             *           &lt;/simpleType>
             *         &lt;/element>
             *         &lt;element name="НомерУдостоверяющего" minOccurs="0">
             *           &lt;simpleType>
             *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedLong">
             *               &lt;minInclusive value="0"/>
             *               &lt;maxInclusive value="99999999"/>
             *               &lt;totalDigits value="8"/>
             *             &lt;/restriction>
             *           &lt;/simpleType>
             *         &lt;/element>
             *         &lt;element name="ДатаВыдачи">
             *           &lt;simpleType>
             *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
             *               &lt;pattern value="\d{2}\.\d{2}\.\d{4}"/>
             *             &lt;/restriction>
             *           &lt;/simpleType>
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
                "\u043d\u0430\u0438\u043c\u0435\u043d\u043e\u0432\u0430\u043d\u0438\u0435\u0423\u0434\u043e\u0441\u0442\u043e\u0432\u0435\u0440\u044f\u044e\u0449\u0435\u0433\u043e",
                "\u0441\u0435\u0440\u0438\u044f\u0420\u0438\u043c\u0441\u043a\u0438\u0435\u0426\u0438\u0444\u0440\u044b",
                "\u0441\u0435\u0440\u0438\u044f\u0420\u0443\u0441\u0441\u043a\u0438\u0435\u0411\u0443\u043a\u0432\u044b",
                "\u043d\u043e\u043c\u0435\u0440\u0423\u0434\u043e\u0441\u0442\u043e\u0432\u0435\u0440\u044f\u044e\u0449\u0435\u0433\u043e",
                "\u0434\u0430\u0442\u0430\u0412\u044b\u0434\u0430\u0447\u0438"
            })
            public static class Документ {

                @XmlElement(name = "\u041d\u0430\u0438\u043c\u0435\u043d\u043e\u0432\u0430\u043d\u0438\u0435\u0423\u0434\u043e\u0441\u0442\u043e\u0432\u0435\u0440\u044f\u044e\u0449\u0435\u0433\u043e", required = true)
                @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
                protected String наименованиеУдостоверяющего;
                @XmlElement(name = "\u0421\u0435\u0440\u0438\u044f\u0420\u0438\u043c\u0441\u043a\u0438\u0435\u0426\u0438\u0444\u0440\u044b")
                @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
                protected String серияРимскиеЦифры;
                @XmlElement(name = "\u0421\u0435\u0440\u0438\u044f\u0420\u0443\u0441\u0441\u043a\u0438\u0435\u0411\u0443\u043a\u0432\u044b")
                @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
                protected String серияРусскиеБуквы;
                @XmlElement(name = "\u041d\u043e\u043c\u0435\u0440\u0423\u0434\u043e\u0441\u0442\u043e\u0432\u0435\u0440\u044f\u044e\u0449\u0435\u0433\u043e")
                protected Integer номерУдостоверяющего;
                @XmlElement(name = "\u0414\u0430\u0442\u0430\u0412\u044b\u0434\u0430\u0447\u0438", required = true)
                protected String датаВыдачи;

                /**
                 * Gets the value of the наименованиеУдостоверяющего property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getНаименованиеУдостоверяющего() {
                    return наименованиеУдостоверяющего;
                }

                /**
                 * Sets the value of the наименованиеУдостоверяющего property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setНаименованиеУдостоверяющего(String value) {
                    this.наименованиеУдостоверяющего = value;
                }

                /**
                 * Gets the value of the серияРимскиеЦифры property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getСерияРимскиеЦифры() {
                    return серияРимскиеЦифры;
                }

                /**
                 * Sets the value of the серияРимскиеЦифры property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setСерияРимскиеЦифры(String value) {
                    this.серияРимскиеЦифры = value;
                }

                /**
                 * Gets the value of the серияРусскиеБуквы property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getСерияРусскиеБуквы() {
                    return серияРусскиеБуквы;
                }

                /**
                 * Sets the value of the серияРусскиеБуквы property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setСерияРусскиеБуквы(String value) {
                    this.серияРусскиеБуквы = value;
                }

                /**
                 * Gets the value of the номерУдостоверяющего property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Integer }
                 *     
                 */
                public Integer getНомерУдостоверяющего() {
                    return номерУдостоверяющего;
                }

                /**
                 * Sets the value of the номерУдостоверяющего property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Integer }
                 *     
                 */
                public void setНомерУдостоверяющего(Integer value) {
                    this.номерУдостоверяющего = value;
                }

                /**
                 * Gets the value of the датаВыдачи property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getДатаВыдачи() {
                    return датаВыдачи;
                }

                /**
                 * Sets the value of the датаВыдачи property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setДатаВыдачи(String value) {
                    this.датаВыдачи = value;
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
     *         &lt;element name="Фамилия">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
     *               &lt;maxLength value="40"/>
     *               &lt;minLength value="1"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="Имя">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
     *               &lt;maxLength value="40"/>
     *               &lt;minLength value="1"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="Отчество" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
     *               &lt;maxLength value="40"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
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
        "\u0444\u0430\u043c\u0438\u043b\u0438\u044f",
        "\u0438\u043c\u044f",
        "\u043e\u0442\u0447\u0435\u0441\u0442\u0432\u043e"
    })
    public static class ФИО {

        @XmlElement(name = "\u0424\u0430\u043c\u0438\u043b\u0438\u044f", required = true)
        @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
        protected String фамилия;
        @XmlElement(name = "\u0418\u043c\u044f", required = true)
        @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
        protected String имя;
        @XmlElement(name = "\u041e\u0442\u0447\u0435\u0441\u0442\u0432\u043e")
        @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
        protected String отчество;

        /**
         * Gets the value of the фамилия property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getФамилия() {
            return фамилия;
        }

        /**
         * Sets the value of the фамилия property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setФамилия(String value) {
            this.фамилия = value;
        }

        /**
         * Gets the value of the имя property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getИмя() {
            return имя;
        }

        /**
         * Sets the value of the имя property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setИмя(String value) {
            this.имя = value;
        }

        /**
         * Gets the value of the отчество property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getОтчество() {
            return отчество;
        }

        /**
         * Sets the value of the отчество property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setОтчество(String value) {
            this.отчество = value;
        }

    }

}
