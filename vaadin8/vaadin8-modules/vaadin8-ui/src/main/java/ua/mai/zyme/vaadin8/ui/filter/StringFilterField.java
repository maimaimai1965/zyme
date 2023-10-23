package ua.mai.zyme.vaadin8.ui.filter;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;

public class StringFilterField extends AbstractFilterField<String> {

    public StringFilterField(String propertyId, String caption, int valueFieldWidth) {
        super(propertyId, caption, valueFieldWidth);
    }

    @Override
    public AbstractField createFieldComponent() {
        return new TextField();
    }

    @Override
    public void setValue(String value) {
        ((TextField) this.fieldComponent).setValue(value != null ? value.toString() : "");
    }

    public String getValue() {
        return ((TextField) fieldComponent).getValue();
    }

    @Override
    public boolean isEmpty() {
        return getValue() == null || getValue().isEmpty();
    }

}
