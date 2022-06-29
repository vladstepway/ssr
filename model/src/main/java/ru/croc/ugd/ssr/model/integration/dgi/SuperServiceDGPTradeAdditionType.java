
package ru.croc.ugd.ssr.model.integration.dgi;

import ru.croc.ugd.ssr.trade.TradeAdditionType;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Тип 17
 *
 * <p>Java class for SuperServiceDGPTradeAdditionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SuperServiceDGPTradeAdditionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TradeAdditionInfo" maxOccurs="unbounded"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="TradeAdditionTypeData" type="{http://www.ugd.croc.ru/ssr/trade}TradeAdditionType"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuperServiceDGPTradeAdditionType", propOrder = {
    "tradeAdditionInfo"
})
@XmlRootElement(name = "SuperServiceDGPTradeAddition")
public class SuperServiceDGPTradeAdditionType {

    @XmlElement(name = "TradeAdditionInfo", required = true)
    protected List<SuperServiceDGPTradeAdditionType.TradeAdditionInfo> tradeAdditionInfo;

    /**
     * Gets the value of the tradeAdditionInfo property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tradeAdditionInfo property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTradeAdditionInfo().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SuperServiceDGPTradeAdditionType.TradeAdditionInfo }
     *
     *
     */
    public List<SuperServiceDGPTradeAdditionType.TradeAdditionInfo> getTradeAdditionInfo() {
        if (tradeAdditionInfo == null) {
            tradeAdditionInfo = new ArrayList<SuperServiceDGPTradeAdditionType.TradeAdditionInfo>();
        }
        return this.tradeAdditionInfo;
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="TradeAdditionTypeData" type="{http://www.ugd.croc.ru/ssr/trade}TradeAdditionType"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "tradeAdditionTypeData"
    })
    public static class TradeAdditionInfo {

        @XmlElement(name = "TradeAdditionTypeData", required = true)
        protected TradeAdditionType tradeAdditionTypeData;

        /**
         * Gets the value of the tradeAdditionTypeData property.
         *
         * @return
         *     possible object is
         *     {@link TradeAdditionType }
         *
         */
        public TradeAdditionType getTradeAdditionTypeData() {
            return tradeAdditionTypeData;
        }

        /**
         * Sets the value of the tradeAdditionTypeData property.
         *
         * @param value
         *     allowed object is
         *     {@link TradeAdditionType }
         *
         */
        public void setTradeAdditionTypeData(TradeAdditionType value) {
            this.tradeAdditionTypeData = value;
        }

    }

}
