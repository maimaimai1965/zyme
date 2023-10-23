package ru.xpoft.vaadin;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SessionInitListener;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * @author xpoft
 */
public class SpringVaadinServlet extends VaadinServlet
{
    private static Logger logger = LoggerFactory.getLogger(SpringVaadinServlet.class);
    /**
     * Servlet parameter name for system message bean
     */
    private static final String SYSTEM_MESSAGES_BEAN_NAME_PARAMETER = "systemMessagesBeanName";
    private static final String CONTEXT_CONFIG_LOCATION_PARAMETER = "contextConfigLocation";
    /**
     * Spring Application Context
     */
    private transient ApplicationContext applicationContext;
    /**
     * system message bean name
     */
    private String systemMessagesBeanName = "";

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());

        if (servletConfig.getInitParameter(CONTEXT_CONFIG_LOCATION_PARAMETER) != null) {
            XmlWebApplicationContext context = new XmlWebApplicationContext();
            context.setParent(applicationContext);
            context.setConfigLocation(servletConfig.getInitParameter(CONTEXT_CONFIG_LOCATION_PARAMETER));
            context.setServletConfig(servletConfig);
            context.setServletContext(servletConfig.getServletContext());
            context.refresh();

            applicationContext = context;
        }

        if (servletConfig.getInitParameter(SYSTEM_MESSAGES_BEAN_NAME_PARAMETER) != null) {
            systemMessagesBeanName = servletConfig.getInitParameter(SYSTEM_MESSAGES_BEAN_NAME_PARAMETER);
            logger.debug("found SYSTEM_MESSAGES_BEAN_NAME_PARAMETER: {}", systemMessagesBeanName);
        }

        SpringApplicationContext.setApplicationContext(applicationContext);

        super.init(servletConfig);
    }

    protected void initializePlugin(final VaadinServletService service) {
        // Spring system messages provider
        if (systemMessagesBeanName != null && !systemMessagesBeanName.isEmpty()) {
            SpringVaadinSystemMessagesProvider messagesProvider = new SpringVaadinSystemMessagesProvider(applicationContext, systemMessagesBeanName);
            logger.debug("set SpringVaadinSystemMessagesProvider");
            service.setSystemMessagesProvider(messagesProvider);
        }

//        String uiProviderProperty = service.getDeploymentConfiguration().getApplicationOrSystemProperty(Constants.SERVLET_PARAMETER_UI_PROVIDER, null);
//
//        // Add SpringUIProvider if custom provider doesn't defined.
//        if (uiProviderProperty == null) {
//            service.addSessionInitListener(new SessionInitListener() {
//                @Override
//                public void sessionInit(SessionInitEvent event) throws ServiceException {
//                    event.getSession().addUIProvider(createUIProvider());
//                }
//            });
//        }
    }

//    private UIProvider createUIProvider() {
//        return new SpringUIProvider(this);
//    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
        final VaadinServletService service = super.createServletService(deploymentConfiguration);
        initializePlugin(service);
        return service;
    }
}
