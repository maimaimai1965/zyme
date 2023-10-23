package ua.mai.zyme.vaadin.simple;

import com.github.appreciated.app.layout.component.appbar.AppBarBuilder;
import com.github.appreciated.app.layout.component.applayout.LeftLayouts;
import com.github.appreciated.app.layout.component.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.router.AppLayoutRouterLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
//import ua.telesens.o320.tif.common.SecurityUtil;
//import ua.telesens.o320.tif.ui.common.base.component.AbstractI18NComponent;
//import ua.telesens.o320.tif.ui.common.base.i18n.I18NListenerComponent;
//import ua.telesens.o320.tif.ui.common.i18n.Messages;
//import ua.telesens.o320.tif.ui.common.module.ModuleRegistry;
//import ua.telesens.o320.tif.ui.common.module.menu.DialogComponent;
//import ua.telesens.o320.tif.ui.common.module.menu.MenuComponent;
//import ua.telesens.o320.tif.ui.security.SecurityService;

import java.util.Locale;

@Push//(transport = Transport.WEBSOCKET_XHR)
@PWA(name = "PGW", shortName = "PGW", iconPath = "themes/custom-theme/favicon.ico")
@NpmPackage(value = "line-awesome", version = "1.3.0")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@CssImport(value = "./com/github/appreciated/app-layout/app-layout-styles-lumo.css")
@CssImport(value = "./com/github/appreciated/app-layout/left/left-hybrid.css")
public class MainView extends AppLayoutRouterLayout<LeftLayouts.LeftHybrid>
        implements  BeanFactoryAware, InitializingBean, AppShellConfigurator, HasDynamicTitle {//AppLayout, RouterLayout,

    private H1 viewTitle;

//    private SideMenu sideMenu;
    private Label appVersion;
//    private Nav nav;

    protected BeanFactory beanFactory;

//    private LoginView loginView;
//
//    @Autowired
//    ModuleRegistry moduleRegistry;
//
//    @Autowired
//    LoginModule loginModule;
//
//    @Autowired
//    SecurityService securityService;

    private String logoResource;
    private String loginResource;

    private Float logoWidth;
    private Float logoHeight;

    private Float loginWidth;
    private Float loginHeight;

    private String applicationVersion;

    private LeftLayouts.LeftHybrid layout;

//    @Autowired
//    private ObjectProvider<ViewWithHeaderDisplay> viewDisplayProvider;

//    public String getDisplayName() {
//        return getTranslation(Messages.MainDisplayName);
//    }
//
//    public String getViewDescription() {
//        return getTranslation(Messages.MainDescription);
//    }

    @Override
    public String getPageTitle() {
        return "Simple";
//        return getDisplayName();
    }

    public MainView() {
//        setLoginView(loginView);
//        setLoginModule(loginModule);
//        setPrimarySection(Section.DRAWER);
//        RouterLink loginLink = new RouterLink("Login", LoginView.class);
//        addToNavbar(true, createHeaderContent());
//        initLoginView();
//        UI.getCurrent().getPage().addStyleSheet(
//                "./com/github/appreciated/app-layout/app-layout-styles-lumo.css");
        createHeaderContent();
        AppLayoutBuilder<LeftLayouts.LeftHybrid>  appLayoutBuilder = AppLayoutBuilder.get(LeftLayouts.LeftHybrid.class);
        appLayoutBuilder
//                .withTitle(viewTitle)
                .withAppBar(AppBarBuilder.get()

                .build())
        ;
//        LeftAppMenuBuilder appMenuBuilder = LeftAppMenuBuilder.get();
//        appMenuBuilder.add();
        layout = appLayoutBuilder.build();
        init(layout);

    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        addToDrawer(createDrawerContent());
//        layout.setAppBar(createViewDisplay());//todo
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("text-secondary");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("m-0", "text-l");

        Header header = new Header();//, viewTitle/toggle
        header.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border", "flex", "h-xl", "items-center",
                "w-full");
//        initLoginView();
//        createViewDisplay(createContentRoot());
        return header;
    }

    private Component createDrawerContent() {
        appVersion = new Label();
//        appVersion.addClassName("");

        H2 appName = new H2("Simple");
//        H2 appName = new H2(getDisplayName());
//        appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");

        Section section = new Section(appName,
                 createFooter());//createNavigation(),
//        section.setMinWidth(250, Unit.PIXELS);
//        section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
        return section;
    }

//    private Nav createNavigation() {
//        nav = new Nav();
//        nav.setHeightFull();
//        nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
//        nav.getElement().setAttribute("aria-labelledby", "views");

//        H3 views = new H3("Views");
//        views.addClassNames("flex", "h-m", "items-center", "mx-m", "my-0", "text-s", "text-tertiary");
//        views.setId("views");
//        if (SecurityUtil.isAuthenticated()){
//            sideMenu = beanFactory.getBean(SideMenu.class);
//            sideMenu.setModuleRegistry(moduleRegistry);
//            sideMenu.addMenuEventListener(this);
//            sideMenu.init();
//            nav.add(sideMenu);
//        }

//        return nav;
//    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("flex", "items-center", "my-s", "px-m", "py-xs");

        return layout;
    }

//    @Override
//    protected void afterNavigation() {
//        super.afterNavigation();
//        viewTitle.setText(getViewDescription());
//    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    private Component createContentRoot() {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(false);
        content.setSpacing(false);
        return content;
    }

    private Component createViewDisplay(FlexLayout content) {
//        ViewWithHeaderDisplay viewDisplay = viewDisplayProvider.getObject(content);
//        viewDisplay.setLoginModule(loginModule);
//        viewDisplay.setLogoResource(new Image(logoResource, ""));
//        viewDisplay.setLogoHeight(logoHeight);
//        viewDisplay.setLogoWidth(logoWidth);
//        viewDisplay.setLoginEventListener(this);
//        return viewDisplay.init();
        return new Label("Cool!");
    }

//    private Component createAppTitle() {
//        AppTitle appTitle = beanFactory.getBean(AppTitle.class);
//        appTitle.setLogoResource(new Image(logoResource, ""));
//        appTitle.setLogoHeight(logoHeight);
//        appTitle.setLogoWidth(logoWidth);
//
//        return appTitle.init();
//    }

//    public void initLoginView() {
////        loginView = loginViewProvider.getObject();
//        loginView.setApplicationVersion(applicationVersion);
//        loginView.setLoginModule(getLoginModule());
//        loginView.setImageResource(loginResource);
//        loginView.setImageWidth(loginWidth);
//        loginView.setImageHeight(loginHeight);
//        loginView.setLoginEventListener(this);
//        loginView.postInitView();
//    }

//    private LoginModule getLoginModule() {
//        if (loginModule == null) {
//            throw new RuntimeException("Cannot find Login module!");
//        }
//        return loginModule;
//    }

//    @Override
//    public void menuItemSelected(MenuComponent.MenuEvent event) {
//        try {
////            View.MAIN.navigateToSubView(event.getItemId());
////            UI.getCurrent().setUriFragment("!" + View.MAIN.viewName() + "/"
////                    + event.getItemId(), false);
////            setNavigationViewComponent(event.getView().getParent().get());
////            getUI().ifPresent(ui -> ui.navigate( event.getViewClass()));
//            UI.getCurrent().navigate(event.getViewClass());
////            if (splitMode != null) {
////                setupSplitModeWithParamsFactory(event.getViewFactory());
////                splitMode.fireViewChanged();
////            }
////            updateWindowCaption();
//        } catch (Exception err) {
////            if (splitMode != null && splitMode.getFrame() != null) splitMode.getFrame().cleanUp();
//            throw err;
//        }
//    }

//    @Override
//    public void updateInternationalizedData(Locale locale) {
//        getUI().ifPresent(ui -> ui.getPage().setTitle(getDisplayName()));
//        viewTitle.setText(getViewDescription());
//        if(sideMenu !=null) sideMenu.updateInternationalizedData(locale);
//        if(layout !=null && layout.getTitleComponent() !=null)((AbstractI18NComponent)layout.getTitleComponent()).updateInternationalizedData(locale);
////        appVersion.setText(getTranslation(Messages.LoginAppVersion, locale, applicationVersion));
////        UI.getCurrent().getPage().setTitle(getDisplayName());
//    }

//    @Override
//    public void onComponentEvent(ComponentEvent event) {
//        if(event instanceof MenuComponent.MenuEvent){
//            try {
//                UI.getCurrent().navigate(((MenuComponent.MenuEvent)event).getViewClass());
//
//            } catch (Exception err) {
//                throw err;
//            }
//        }
//    }

    @Override
    public void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

////        if (!RouteConfiguration.forSessionScope().isRouteRegistered(MainView.class)) {
////            RouteConfiguration.forSessionScope().setRoute("", MainView.class, MainLayout.class);
////        }
//        if (SecurityUtil.isAuthenticated()){
//            sideMenu = beanFactory.getBean(SideMenu.class);
//            sideMenu.setModuleRegistry(moduleRegistry);
//            sideMenu.addMenuEventListener(this);
//            sideMenu.init();
//            layout.setAppMenu(sideMenu.getAppMenu());
//            layout.setMenuVisible(true);
////            init();
//
////            AppHeader appHeader = beanFactory.getBean(AppHeader.class);
////            appHeader.init();
//            layout.setAppBar(createViewDisplay(new FlexLayout()));
//            layout.setTitleComponent(createAppTitle());
//        }
    }

//    @Override
//    public void onDetach(DetachEvent detachEvent) {
//        I18NListenerComponent.super.onDetach(detachEvent);
//    }

//    @Override
//    public void dialogItemSelected(DialogComponent.DialogEvent event) {
//
//    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public Image getLogoResource() {
        return new Image(logoResource, "");
    }

    public void setLogoResource(String logoResource) {
        this.logoResource = logoResource;
    }

    public void setLoginResource(String loginResource) {
        this.loginResource = loginResource;
    }

//    public void setLoginModule(LoginModule loginModule) {
//        this.loginModule = loginModule;
//    }

    public void setLogoWidth(Float logoWidth) {
        this.logoWidth = logoWidth;
    }

    public void setLogoHeight(Float logoHeight) {
        this.logoHeight = logoHeight;
    }

    public void setLoginWidth(Float loginWidth) {
        this.loginWidth = loginWidth;
    }

    public void setLoginHeight(Float loginHeight) {
        this.loginHeight = loginHeight;
    }

//    public ModuleRegistry getModuleRegistry() {
//        return moduleRegistry;
//    }

//    public void setModuleRegistry(ModuleRegistry moduleRegistry) {
//        this.moduleRegistry = moduleRegistry;
//    }

    public void setApplicationVersion(String applicationVersion) {
        Assert.hasLength(applicationVersion, "applicationVersion must be not empty");
        this.applicationVersion = applicationVersion;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

//    @Override
//    public void userLogin(Long userId, String uname) {
////        getUI().get().navigate(MainView.class);
//        UI.getCurrent().navigate(MainView.class);
//    }

//    @Override
//    public void userLogout(Long userId, String uname) {
////        SecurityContextHolder.clearContext();
////        UI.getCurrent().getSession().close();
////        UI.getCurrent().getPage().reload();
//        securityService.logout();
//    }

//    public void setLoginView(LoginView loginView) {
//        this.loginView = loginView;
//    }

    @Override
    public void configurePage(AppShellSettings settings)  {
//        settings.addInlineFromFile(InitialPageSettings.Position.PREPEND,
//                "inline.js", InitialPageSettings.WrapMode.JAVASCRIPT);
//
//        settings.addMetaTag("og:title", "The Rock");
//        settings.addMetaTag("og:type", "video.movie");
//        settings.addMetaTag("og:url",
//                "http://www.imdb.com/title/tt0117500/");
//        settings.addMetaTag("og:image",
//                "http://ia.media-imdb.com/images/rock.jpg");

        settings.addLink("shortcut icon", "src/main/resources/META-INF/resources/themes/custom-theme/favicon.ico");
//        settings.addFavIcon("icon", "icons/icon-192.png", "192x192");
    }
}
