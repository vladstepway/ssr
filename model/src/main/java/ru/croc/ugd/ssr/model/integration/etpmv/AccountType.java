package ru.croc.ugd.ssr.model.integration.etpmv;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AccountType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AccountType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="UL"/>
 *     &lt;enumeration value="IP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AccountType", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/")
@XmlEnum
public enum AccountType {


    /**
     * Legal Entity
     * 
     */
    UL,

    /**
     * Individual Entrepreneur
     * 
     */
    IP;

    public String value() {
        return name();
    }

    public static AccountType fromValue(String v) {
        return valueOf(v);
    }

}
