package ua.mai.zyme.vaadin8.ui.entity;

import ua.mai.zyme.vaadin8.db.AdminService;
import ua.telesens.o320.trt.be.schema.tables.records.AaRoleRecord;

import java.util.Collection;

public class RoleListComponent extends EntityListComponent<AaRoleRecord, RoleFilterComponent> {

    private AdminService adminService;

    public RoleListComponent(String caption, String imagePath, AdminService adminService) {
        super(caption, imagePath, new RoleFilterComponent(), new Object[]{adminService});
    }

    @Override
    protected void initParams(Object ... params) {
        this.adminService = (AdminService) params[0];
    }

    @Override
    protected void initGrid() {
        grid.addColumn(AaRoleRecord::getRoleId).setCaption("Id");
        grid.addColumn(AaRoleRecord::getRoleCd).setCaption("Code");
        grid.addColumn(AaRoleRecord::getRoleName).setCaption("Name");
        grid.addColumn(AaRoleRecord::getRoleType).setCaption("Type");
        grid.addColumn(AaRoleRecord::getNote).setCaption("Note");
    }

    protected Collection<AaRoleRecord> search() {
        return adminService.readRoles(filterComponent.getFilterValues());
    }


}
