package ua.mai.zyme.vaadin8.ui.filter;

public class FilterFieldProperties {

    public static final int DEFAULT_FIELD_COMPONENT_WIDTH = 100;
    private String propertyId;
    private String propertyName;
    protected Object filterFieldType;
    protected int fieldComponentWidth = DEFAULT_FIELD_COMPONENT_WIDTH;

    protected Object initialValue;

    public FilterFieldProperties() {
    }

    public FilterFieldProperties(String propertyId, String propertyName) {
        this.propertyId = propertyId;
        this.propertyName = propertyName;
    }

    public FilterFieldProperties(String propertyId, String propertyName, int fieldComponentWidth) {
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.fieldComponentWidth = fieldComponentWidth;
    }

    public FilterFieldProperties withPropertyId(String propertyId) {
        this.propertyId = propertyId;
        return this;
    }
    public FilterFieldProperties withPropertyName(String propertyName) {
        this.propertyName = propertyName;
        return this;
    }
    public FilterFieldProperties withFieldComponentWidth(int fieldComponentWidth) {
        this.fieldComponentWidth = fieldComponentWidth;
        return this;
    }
    public FilterFieldProperties withFieldComponentWidth(Object filterFieldType) {
        this.filterFieldType = filterFieldType;
        return this;
    }

    public FilterFieldProperties withInitialValue(Object initialValue) {
        this.initialValue = initialValue;
        return this;
    }

    public Object getFilterFieldType() {
        return filterFieldType;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getInitialValue() {
        return initialValue;
    }
}
