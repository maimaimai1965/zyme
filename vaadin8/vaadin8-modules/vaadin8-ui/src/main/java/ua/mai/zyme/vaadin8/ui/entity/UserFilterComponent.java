package ua.mai.zyme.vaadin8.ui.entity;

import ua.mai.zyme.vaadin8.ui.filter.StringFilterField;

public class UserFilterComponent extends EntityFilterSimpleComponent {

    StringFilterField userIdField;
    StringFilterField shortNameField;
    StringFilterField fullNameField;

    @Override
    protected void init() {
        super.init();
        userIdField = new StringFilterField("userId", "User Id:", 80);
        addField(userIdField);
        shortNameField = new StringFilterField("shortName", "Short Name:", 100);
        addField(shortNameField);
        fullNameField = new StringFilterField("fullName", "Full Name:", 150);
        addField(fullNameField);
    }

}
