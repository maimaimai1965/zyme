package ua.mai.zyme.vaadin8.ui.filter;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ComboBox;
import ua.mai.zyme.vaadin8.ui.filter.AbstractFilterField;

import java.util.List;

public class ComboboxFilterField extends AbstractFilterField<String> {

    private ComboBox combobox;

    public ComboboxFilterField(String propertyId, String caption, int valueFieldWidth, List<String> options) {
        super(new FilterFieldPropertiesForCombobox(propertyId, caption, valueFieldWidth, options));
    }

    @Override
    public AbstractComponent createFieldComponent() {
        combobox = new ComboBox<String>();
        combobox.setDataProvider(new ListDataProvider<>(getOptions()));
        return combobox;
    }

    public List<String> getOptions() {
        return ((FilterFieldPropertiesForCombobox) properties).getOptions();
    }

    public void setValue(String value) {
        combobox.setSelectedItem(value);
//        valueField.setValue(value != null ? value.toString() : "");
    }

    @Override
    public String getValue() {
        return combobox.getValue() != null ? combobox.getValue().toString() : null;
    }

    @Override
    public boolean isEmpty() {
        return getValue() == null || getValue().isEmpty();
    }

}
