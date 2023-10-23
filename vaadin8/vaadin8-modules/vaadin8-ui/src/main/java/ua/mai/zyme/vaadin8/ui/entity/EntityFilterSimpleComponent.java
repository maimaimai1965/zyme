package ua.mai.zyme.vaadin8.ui.entity;

import com.vaadin.ui.HorizontalLayout;
import ua.mai.zyme.vaadin8.ui.filter.AbstractFilterField;

public class EntityFilterSimpleComponent extends EntityFilterComponent {

    protected HorizontalLayout horizontalLayout;

    @Override
    protected void init() {
        horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(false);
        horizontalLayout.setSpacing(true);
        rootLayout.addComponent(horizontalLayout);
    }

    protected void addField(AbstractFilterField field) {
        horizontalLayout.addComponent(field);
        super.addField(field);
    }


}
