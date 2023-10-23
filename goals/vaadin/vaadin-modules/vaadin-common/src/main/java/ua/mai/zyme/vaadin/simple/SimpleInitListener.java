package ua.mai.zyme.vaadin.simple;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
//import ua.telesens.o320.tif.common.SecurityUtil;

public class SimpleInitListener implements VaadinServiceInitListener {
    @Override
    public void serviceInit(ServiceInitEvent initEvent) {
//        final AccessControl accessControl = AccessControlFactory.getInstance()
//                .createAccessControl();
        RouteConfiguration configuration =
                RouteConfiguration.forApplicationScope();

        configuration.setRoute("", MainView2.class);
//        configuration.setRoute("login", LoginView.class);
//        configuration.setRoute("error", ErrorView.class);

//        initEvent.getSource().addUIInitListener(uiInitEvent -> {
//            uiInitEvent.getUI().addBeforeEnterListener(enterEvent -> {
//                if (!SecurityUtil.isAuthenticated() && !LoginView.class
//                        .equals(enterEvent.getNavigationTarget()))
//                    enterEvent.rerouteTo(LoginView.class);
//            });
//        });
    }
}
