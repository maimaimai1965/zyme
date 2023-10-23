package ua.mai.zyme.vaadin8.ui.entity;

import ua.mai.zyme.vaadin8.ui.filter.ComboboxFilterField;
import ua.mai.zyme.vaadin8.ui.filter.StringFilterField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ua.telesens.o320.trt.be.schema.Tables.AA_ROLE;

public class RoleFilterComponent extends EntityFilterSimpleComponent {

    StringFilterField roleIdField;
    StringFilterField roleCdField;
    StringFilterField roleNameField;
    ComboboxFilterField roleTypeField;

    @Override
    protected void init() {
        super.init();
        roleIdField = new StringFilterField(AA_ROLE.ROLE_ID.getName(), "Id:", 100);
        addField(roleIdField);
        roleCdField = new StringFilterField(AA_ROLE.ROLE_CD.getName(), "Code:", 200);
        addField(roleCdField);
        roleNameField = new StringFilterField(AA_ROLE.ROLE_NAME.getName(), "Name:", 300);
        addField(roleNameField);
        List<String> roleTypeOptions = new ArrayList();
//        roleTypeOptions.add("");
        roleTypeOptions.addAll(Arrays.asList(RoleType.VALUES));
        roleTypeField = new ComboboxFilterField(AA_ROLE.ROLE_TYPE.getName(), "Type:", 30, roleTypeOptions);
        addField(roleTypeField);
    }

}
