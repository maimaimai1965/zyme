package ua.mai.zyme.vaadin8.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.v7.ui.Table;
import org.springframework.beans.factory.annotation.Autowired;
import ua.mai.zyme.vaadin8.db.AdminService;
import ua.mai.zyme.vaadin8.ui.entity.EntityListComponent;
import ua.mai.zyme.vaadin8.ui.entity.RoleListComponent;
import ua.mai.zyme.vaadin8.ui.entity.UserListComponent;

@SpringUI(path = "")
@Widgetset("Vaadin8WidgetSet")
@Theme("trt")
public class MainPage extends UI {

    @Autowired
    private AdminService adminService;

    private VerticalLayout entityLayout;
    @Override
    protected void init(VaadinRequest request) {
        // Create the content root layout for the UI
        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setMargin(false);
        contentLayout.setSpacing(false);
        contentLayout.setSizeFull();
        setContent(contentLayout);

        VerticalLayout entityButtonLayout = new VerticalLayout();
        entityButtonLayout.setWidthUndefined();
        contentLayout.addComponent(entityButtonLayout);

        entityButtonLayout.addComponent(new Button("Users",
                click -> showEntityComponent(
                               new UserListComponent("Users", "img/tif_aa/users_24.png", adminService))
        ));

        entityButtonLayout.addComponent(new Button("Roles",
                click -> showEntityComponent(
                               new RoleListComponent("Roles", "img/tif_aa/tif_roli_24.png", adminService))
        ));
        entityButtonLayout.addComponent(new Button("Table",
                click -> showComponent(
                               createSimpleTableComponent())
        ));

        entityLayout = new VerticalLayout();
        entityLayout.setMargin(false);
        entityLayout.setSpacing(false);
        entityLayout.setSizeFull();
        contentLayout.addComponent(entityLayout);
        contentLayout.setExpandRatio(entityLayout, 1);
    }

    private void showEntityComponent(EntityListComponent entityListComponent) {
        entityLayout.removeAllComponents();
        entityListComponent.setSizeFull();
        entityLayout.addComponent(entityListComponent);
        entityLayout.setExpandRatio(entityListComponent, 1);
    }

    private void showComponent(Component component) {
        entityLayout.removeAllComponents();
        component.setSizeFull();
        entityLayout.addComponent(component);
        entityLayout.setExpandRatio(component, 1);
    }

    private Component createSimpleTableComponent() {
        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setSizeFull();

        contentLayout.addComponent(new Label("Table Example"));
        Table table = new Table();
        contentLayout.addComponent(table);
        contentLayout.setExpandRatio(table, 1);

        table.addContainerProperty("Id", Integer.class, 0);
        table.addContainerProperty("Name", String.class, "default");

        return contentLayout;
    }

}