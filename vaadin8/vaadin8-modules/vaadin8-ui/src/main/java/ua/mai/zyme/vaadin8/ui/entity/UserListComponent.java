package ua.mai.zyme.vaadin8.ui.entity;

import ua.mai.zyme.vaadin8.db.AdminService;
import ua.telesens.o320.trt.be.schema.tables.records.AaUserRecord;

import java.util.Collection;

public class UserListComponent extends EntityListComponent<AaUserRecord, UserFilterComponent> {

    private AdminService adminService;

    public UserListComponent(String caption, String imagePath, AdminService adminService) {
        super(caption, imagePath, new UserFilterComponent(), new Object[]{adminService});
    }

    @Override
    protected void initParams(Object ... params) {
        this.adminService = (AdminService) params[0];
    }

    @Override
    protected void initGrid() {
        grid.addColumn(AaUserRecord::getUserId).setCaption("User Id");
        grid.addColumn(AaUserRecord::getUserType).setCaption("User Type");
        grid.addColumn(AaUserRecord::getShortName).setCaption("Short Name");
        grid.addColumn(AaUserRecord::getFullName).setCaption("Full Name");
    }

    protected Collection<AaUserRecord> search() {
        return adminService.readUsers();
    }

}
