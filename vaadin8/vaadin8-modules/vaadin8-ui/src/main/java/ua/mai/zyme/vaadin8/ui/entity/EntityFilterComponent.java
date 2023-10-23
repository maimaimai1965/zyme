package ua.mai.zyme.vaadin8.ui.entity;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import ua.mai.zyme.vaadin8.ui.filter.AbstractFilterField;

import java.util.LinkedHashMap;
import java.util.Map;

public class EntityFilterComponent<ENTITY> extends CustomComponent {

    protected EntityListComponent owner;
    private LinkedHashMap<String, AbstractFilterField> fields = new LinkedHashMap<>();

    protected VerticalLayout rootLayout;

    public EntityFilterComponent(Object...params) {
        initParams(params);
        createRootLayout();
        init();
    }

    protected void initParams(Object...params) {}

    protected void createRootLayout() {
        rootLayout = new VerticalLayout();
        rootLayout.setMargin(new MarginInfo(false, true, false, false));
        rootLayout.setSpacing(true);
        rootLayout.setSizeFull();
        setCompositionRoot(rootLayout);
    }

    protected void init() {}

    public EntityListComponent getOwner() {
        return owner;
    }

    public void setOwner(EntityListComponent owner) {
        this.owner = owner;
    }

    protected void addField(AbstractFilterField field) {
        registryField(field);
    }

    private void registryField(AbstractFilterField field) {
        fields.put(field.getPropertyId(), field);
    }

    public void clean() {
        fields.forEach((key, field) -> field.clean());
    }

    public Map<String, Object> getFilterValues() {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        for (AbstractFilterField field: fields.values()) {
            if (!field.isEmpty())
                resultMap.put(field.getPropertyId(), field.getValue());
        }
        return resultMap;
    }

}
