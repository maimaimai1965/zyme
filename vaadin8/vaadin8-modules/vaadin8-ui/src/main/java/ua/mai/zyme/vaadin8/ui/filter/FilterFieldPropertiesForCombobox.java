package ua.mai.zyme.vaadin8.ui.filter;

import java.util.List;

public class FilterFieldPropertiesForCombobox extends FilterFieldProperties {

    private List<String> options;

    public FilterFieldPropertiesForCombobox(String propertyId, String propertyName, int fieldComponentWidth, List<String> options) {
        super(propertyId, propertyName);
        this.options = options;
    }

    public List<String> getOptions() {
        return options;
    }

}
