package ru.xpoft.vaadin;

import com.vaadin.flow.server.*;
import com.vaadin.flow.component.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xpoft
 */
@Deprecated
public class SpringUIProvider //extends UIProvider
{
    private static Logger logger = LoggerFactory.getLogger(SpringUIProvider.class);

    /**
     * Servlet parameter name for UI bean
     */
    public static final String BEAN_NAME_PARAMETER = "beanName";

    public static final String THEME_NAME_PARAMETER = "themeName";

    private final SpringVaadinServlet servlet;

    public SpringUIProvider(SpringVaadinServlet servlet)
    {
        this.servlet = servlet;
    }

//    public String getTheme(UICreateEvent event) {
//        String vaadinThemeName = super.getTheme(event);
//        if(vaadinThemeName == null){
//            VaadinRequest request = event.getRequest();
//
//            vaadinThemeName = VaadinServlet.getDefaultTheme();
//
//            Object uiThemeName = request.getService().getDeploymentConfiguration().getApplicationOrSystemProperty(THEME_NAME_PARAMETER, null);
//            if (uiThemeName != null && uiThemeName instanceof String)
//            {
//                vaadinThemeName = uiThemeName.toString();
//            }
//        }
//
////        logger.debug("found THEME_NAME_PARAMETER: {}", vaadinThemeName);
//        return vaadinThemeName;
//    }

//    @Override
//    public UI createInstance(UICreateEvent event)
//    {
//        return (UI) servlet.getApplicationContext().getBean(getUIBeanName(event.getRequest()));
//    }

//    @Override
//    public Class<? extends UI> getUIClass(UIClassSelectionEvent event)
//    {
//        if (this.isSessionScopedUI(event.getRequest()))
//        {
//            logger.warn("You should use Prototype scope for UI only!");
//        }
//
//        return (Class<? extends UI>) servlet.getApplicationContext().getType(getUIBeanName(event.getRequest()));
//    }

    protected boolean isSessionScopedUI(VaadinRequest request)
    {
        return !servlet.getApplicationContext().isPrototype(getUIBeanName(request));
    }

    /**
     * Returns the bean name to be retrieved from the application bean context and
     * used as the UI. The default implementation uses the servlet init property
     * {@link #BEAN_NAME_PARAMETER} or "ui" if not defined.
     *
     * @param request the current Vaadin request
     * @return the UI bean name in the application context
     */
    protected String getUIBeanName(VaadinRequest request)
    {
        String vaadinBeanName = "ui";

        Object uiBeanName = request.getService().getDeploymentConfiguration().getApplicationOrSystemProperty(BEAN_NAME_PARAMETER, null, null);
        if (uiBeanName != null && uiBeanName instanceof String)
        {
            vaadinBeanName = uiBeanName.toString();
        }

        logger.debug("found BEAN_NAME_PARAMETER: {}", vaadinBeanName);
        return vaadinBeanName;
    }
}
