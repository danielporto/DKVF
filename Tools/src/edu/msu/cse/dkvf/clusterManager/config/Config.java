//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.11.08 at 06:22:19 PM EST 
//


package edu.msu.cse.dkvf.clusterManager.config;

import java.util.ArrayList;
import java.util.List;
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
 *       &lt;all>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="client_port" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="server_port" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="control_port" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="storage" type="{}Storage" minOccurs="0"/>
 *         &lt;element name="connector_sleep_time" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="channel_capacity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="synch_communication" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="protocol_log_file" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="protocol_log_level" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="protocol_log_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="protocol_std_log_level" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="framework_log_file" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="framework_log_level" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="framework_log_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="framework_std_log_level" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="connect_to" type="{}ConnectTo" minOccurs="0"/>
 *         &lt;element name="protocol_properties" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="property" type="{}Property" maxOccurs="unbounded"/>
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
@XmlRootElement(name = "config")
public class Config {

    protected String id;
    @XmlElement(name = "client_port", defaultValue = "2002")
    protected String clientPort;
    @XmlElement(name = "server_port", defaultValue = "2000")
    protected String serverPort;
    @XmlElement(name = "control_port", defaultValue = "2001")
    protected String controlPort;
    protected Storage storage;
    @XmlElement(name = "connector_sleep_time", defaultValue = "10")
    protected String connectorSleepTime;
    @XmlElement(name = "channel_capacity")
    protected String channelCapacity;
    @XmlElement(name = "synch_communication", defaultValue = "false")
    protected Boolean synchCommunication;
    @XmlElement(name = "protocol_log_file", defaultValue = "protocol_log.txt")
    protected String protocolLogFile;
    @XmlElement(name = "protocol_log_level", defaultValue = "severe")
    protected String protocolLogLevel;
    @XmlElement(name = "protocol_log_type", defaultValue = "text")
    protected String protocolLogType;
    @XmlElement(name = "protocol_std_log_level", defaultValue = "off")
    protected String protocolStdLogLevel;
    @XmlElement(name = "framework_log_file", defaultValue = "framework_log.txt")
    protected String frameworkLogFile;
    @XmlElement(name = "framework_log_level", defaultValue = "severe")
    protected String frameworkLogLevel;
    @XmlElement(name = "framework_log_type", defaultValue = "text")
    protected String frameworkLogType;
    @XmlElement(name = "framework_std_log_level", defaultValue = "off")
    protected String frameworkStdLogLevel;
    @XmlElement(name = "connect_to")
    protected ConnectTo connectTo;
    @XmlElement(name = "protocol_properties")
    protected Config.ProtocolProperties protocolProperties;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the clientPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientPort() {
        return clientPort;
    }

    /**
     * Sets the value of the clientPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientPort(String value) {
        this.clientPort = value;
    }

    /**
     * Gets the value of the serverPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServerPort() {
        return serverPort;
    }

    /**
     * Sets the value of the serverPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServerPort(String value) {
        this.serverPort = value;
    }

    /**
     * Gets the value of the controlPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getControlPort() {
        return controlPort;
    }

    /**
     * Sets the value of the controlPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setControlPort(String value) {
        this.controlPort = value;
    }

    /**
     * Gets the value of the storage property.
     * 
     * @return
     *     possible object is
     *     {@link Storage }
     *     
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Sets the value of the storage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Storage }
     *     
     */
    public void setStorage(Storage value) {
        this.storage = value;
    }

    /**
     * Gets the value of the connectorSleepTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConnectorSleepTime() {
        return connectorSleepTime;
    }

    /**
     * Sets the value of the connectorSleepTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConnectorSleepTime(String value) {
        this.connectorSleepTime = value;
    }

    /**
     * Gets the value of the channelCapacity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChannelCapacity() {
        return channelCapacity;
    }

    /**
     * Sets the value of the channelCapacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChannelCapacity(String value) {
        this.channelCapacity = value;
    }

    /**
     * Gets the value of the synchCommunication property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSynchCommunication() {
        return synchCommunication;
    }

    /**
     * Sets the value of the synchCommunication property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSynchCommunication(Boolean value) {
        this.synchCommunication = value;
    }

    /**
     * Gets the value of the protocolLogFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtocolLogFile() {
        return protocolLogFile;
    }

    /**
     * Sets the value of the protocolLogFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtocolLogFile(String value) {
        this.protocolLogFile = value;
    }

    /**
     * Gets the value of the protocolLogLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtocolLogLevel() {
        return protocolLogLevel;
    }

    /**
     * Sets the value of the protocolLogLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtocolLogLevel(String value) {
        this.protocolLogLevel = value;
    }

    /**
     * Gets the value of the protocolLogType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtocolLogType() {
        return protocolLogType;
    }

    /**
     * Sets the value of the protocolLogType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtocolLogType(String value) {
        this.protocolLogType = value;
    }

    /**
     * Gets the value of the protocolStdLogLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtocolStdLogLevel() {
        return protocolStdLogLevel;
    }

    /**
     * Sets the value of the protocolStdLogLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtocolStdLogLevel(String value) {
        this.protocolStdLogLevel = value;
    }

    /**
     * Gets the value of the frameworkLogFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrameworkLogFile() {
        return frameworkLogFile;
    }

    /**
     * Sets the value of the frameworkLogFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrameworkLogFile(String value) {
        this.frameworkLogFile = value;
    }

    /**
     * Gets the value of the frameworkLogLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrameworkLogLevel() {
        return frameworkLogLevel;
    }

    /**
     * Sets the value of the frameworkLogLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrameworkLogLevel(String value) {
        this.frameworkLogLevel = value;
    }

    /**
     * Gets the value of the frameworkLogType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrameworkLogType() {
        return frameworkLogType;
    }

    /**
     * Sets the value of the frameworkLogType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrameworkLogType(String value) {
        this.frameworkLogType = value;
    }

    /**
     * Gets the value of the frameworkStdLogLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrameworkStdLogLevel() {
        return frameworkStdLogLevel;
    }

    /**
     * Sets the value of the frameworkStdLogLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrameworkStdLogLevel(String value) {
        this.frameworkStdLogLevel = value;
    }

    /**
     * Gets the value of the connectTo property.
     * 
     * @return
     *     possible object is
     *     {@link ConnectTo }
     *     
     */
    public ConnectTo getConnectTo() {
        return connectTo;
    }

    /**
     * Sets the value of the connectTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConnectTo }
     *     
     */
    public void setConnectTo(ConnectTo value) {
        this.connectTo = value;
    }

    /**
     * Gets the value of the protocolProperties property.
     * 
     * @return
     *     possible object is
     *     {@link Config.ProtocolProperties }
     *     
     */
    public Config.ProtocolProperties getProtocolProperties() {
        return protocolProperties;
    }

    /**
     * Sets the value of the protocolProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link Config.ProtocolProperties }
     *     
     */
    public void setProtocolProperties(Config.ProtocolProperties value) {
        this.protocolProperties = value;
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
     *         &lt;element name="property" type="{}Property" maxOccurs="unbounded"/>
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
        "property"
    })
    public static class ProtocolProperties {

        @XmlElement(required = true)
        protected List<Property> property;

        /**
         * Gets the value of the property property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the property property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getProperty().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Property }
         * 
         * 
         */
        public List<Property> getProperty() {
            if (property == null) {
                property = new ArrayList<Property>();
            }
            return this.property;
        }

    }

}
