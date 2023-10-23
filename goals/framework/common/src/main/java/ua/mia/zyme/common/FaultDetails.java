
package ua.mia.zyme.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 */
public class FaultDetails {

    protected String code;
    protected String message;
    protected List<TypedValue> values;
    public final static String P_CODE = "Code";
    public final static String P_MESSAGE = "Message";
    public final static String P_VALUES = "Values";

    /**
     * Default no-arg constructor
     * 
     */
    public FaultDetails() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public FaultDetails(final String code, final String message, final List<TypedValue> values) {
        this.code = code;
        this.message = message;
        this.values = values;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the values property.
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypedValue }
     * 
     * 
     */
    public List<TypedValue> getValues() {
        if (values == null) {
            values = new ArrayList<TypedValue>();
        }
        return this.values;
    }

    public FaultDetails withCode(String value) {
        setCode(value);
        return this;
    }

    public FaultDetails withMessage(String value) {
        setMessage(value);
        return this;
    }

    public FaultDetails withValues(TypedValue... values) {
        if (values!= null) {
            for (TypedValue value: values) {
                getValues().add(value);
            }
        }
        return this;
    }

    public FaultDetails withValues(Collection<TypedValue> values) {
        if (values!= null) {
            getValues().addAll(values);
        }
        return this;
    }

}
