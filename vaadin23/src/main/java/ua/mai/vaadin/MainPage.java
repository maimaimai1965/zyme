package ua.mai.vaadin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;

//@SpringUI(path = "")
//@Theme("trt")
@Route("")
public class MainPage extends HorizontalLayout {

//    @Autowired
//    private AdminService adminService;

    private VerticalLayout entityLayout;

    public MainPage() {
        init();
    }

    protected void init() {
        // Create the content root layout for the UI
        setMargin(false);
        setSpacing(false);
        setSizeFull();
//        setContent(contentLayout);
        add(new Button("Test"));

        VerticalLayout entityButtonLayout = new VerticalLayout();
//        entityButtonLayout.setWidthUndefined();
        add(entityButtonLayout);

//        entityButtonLayout.add(new Button("Users",
//                click -> showEntityComponent(
//                               new UserListComponent("Users", "img/tif_aa/users_24.png", adminService))
//        ));
//
//        entityButtonLayout.add(new Button("Roles",
//                click -> showEntityComponent(
//                               new RoleListComponent("Roles", "img/tif_aa/tif_roli_24.png", adminService))
//        ));
//        entityButtonLayout.add(new Button("Table",
//                click -> showComponent(
//                               createSimpleTableComponent())
//        ));

        entityLayout = new VerticalLayout();
        entityLayout.setMargin(false);
        entityLayout.setSpacing(false);
        entityLayout.setSizeFull();
        add(entityLayout);
//        contentLayout.setExpandRatio(entityLayout, 1);
    }

//    private void showEntityComponent(EntityListComponent entityListComponent) {
//        entityLayout.removeAllComponents();
//        entityListComponent.setSizeFull();
//        entityLayout.add(entityListComponent);
//        entityLayout.setExpandRatio(entityListComponent, 1);
//    }
//
//    private void showComponent(Component component) {
//        entityLayout.removeAllComponents();
//        component.setSizeFull();
//        entityLayout.add(component);
//        entityLayout.setExpandRatio(component, 1);
//    }
//
//    private Component createSimpleTableComponent() {
//        VerticalLayout contentLayout = new VerticalLayout();
//        contentLayout.setSizeFull();
//
//        contentLayout.add(new Label("Table Example"));
//        Table table = new Table();
//        contentLayout.add(table);
//        contentLayout.setExpandRatio(table, 1);
//
//        table.addContainerProperty("Id", Integer.class, 0);
//        table.addContainerProperty("Name", String.class, "default");
//
//        return contentLayout;
//    }

}