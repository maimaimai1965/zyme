package ua.mai.zyme.vaadin8.ui.filter;

import com.vaadin.ui.*;

public abstract class AbstractFilterField<TYPE_VALUE> extends HorizontalLayout {

    protected final FilterFieldProperties properties;
    protected Label captionComponent;
    protected AbstractComponent fieldComponent;
//    protected AbstractField valueField;


    public AbstractFilterField(FilterFieldProperties properties) {
        this.properties = properties;
        init();
    }

    public AbstractFilterField(String propertyId, String name, int fieldComponentWidth) {
        this(new FilterFieldProperties(propertyId, name).withFieldComponentWidth(fieldComponentWidth));
    }

    protected void init() {
        captionComponent = new Label(getPropertyName());
        addComponent(captionComponent);

        fieldComponent = createFieldComponent();
        fieldComponent.setWidth(properties.fieldComponentWidth, Unit.PIXELS);
        addComponent(fieldComponent);
    }

    public String getPropertyId() {
        return properties.getPropertyId();
    }

    public String getPropertyName() {
        return properties.getPropertyName();
    }

    public abstract void setValue(TYPE_VALUE value);

    public abstract TYPE_VALUE getValue();

    public abstract AbstractComponent createFieldComponent();

    public void clean() {
        setValue(null);
    }

    public boolean isEmpty() {
        return getValue() == null;
    }

}
