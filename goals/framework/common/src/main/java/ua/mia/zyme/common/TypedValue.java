
package ua.mia.zyme.common;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TypedValue {

    protected String enumValue;
    protected Boolean booleanValue;
    protected BigDecimal numberValue;
    protected LocalDateTime dateValue;
    protected String stringValue;
    public final static String P_ENUM_VALUE = "EnumValue";
    public final static String P_BOOLEAN_VALUE = "BooleanValue";
    public final static String P_NUMBER_VALUE = "NumberValue";
    public final static String P_DATE_VALUE = "DateValue";
    public final static String P_STRING_VALUE = "StringValue";

    /**
     * Default no-arg constructor
     * 
     */
    public TypedValue() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public TypedValue(final String enumValue, final Boolean booleanValue, final BigDecimal numberValue, final LocalDateTime dateValue, final String stringValue) {
        this.enumValue = enumValue;
        this.booleanValue = booleanValue;
        this.numberValue = numberValue;
        this.dateValue = dateValue;
        this.stringValue = stringValue;
    }

    /**
     * Gets the value of the enumValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnumValue() {
        return enumValue;
    }

    /**
     * Sets the value of the enumValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnumValue(String value) {
        this.enumValue = value;
    }

    /**
     * Gets the value of the booleanValue property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean getBooleanValue() {
        return booleanValue;
    }

    /**
     * Sets the value of the booleanValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBooleanValue(Boolean value) {
        this.booleanValue = value;
    }

    /**
     * Gets the value of the numberValue property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNumberValue() {
        return numberValue;
    }

    /**
     * Sets the value of the numberValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNumberValue(BigDecimal value) {
        this.numberValue = value;
    }

    /**
     * Gets the value of the dateValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getDateValue() {
        return dateValue;
    }

    /**
     * Sets the value of the dateValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateValue(LocalDateTime value) {
        this.dateValue = value;
    }

    /**
     * Gets the value of the stringValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * Sets the value of the stringValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStringValue(String value) {
        this.stringValue = value;
    }

    public TypedValue withEnumValue(String value) {
        setEnumValue(value);
        return this;
    }

    public TypedValue withBooleanValue(Boolean value) {
        setBooleanValue(value);
        return this;
    }

    public TypedValue withNumberValue(BigDecimal value) {
        setNumberValue(value);
        return this;
    }

    public TypedValue withDateValue(LocalDateTime value) {
        setDateValue(value);
        return this;
    }

    public TypedValue withStringValue(String value) {
        setStringValue(value);
        return this;
    }

    public static enum Type {
        ENUM_VALUE, BOOLEAN_VALUE, NUMBER_VALUE, DATE_VALUE, TRING_VALUE
//        public final static String P_ENUM_VALUE = "EnumValue";
//        public final static String P_BOOLEAN_VALUE = "BooleanValue";
//        public final static String P_NUMBER_VALUE = "NumberValue";
//        public final static String P_DATE_VALUE = "DateValue";
//        public final static String P_STRING_VALUE = "StringValue";
    }

}
