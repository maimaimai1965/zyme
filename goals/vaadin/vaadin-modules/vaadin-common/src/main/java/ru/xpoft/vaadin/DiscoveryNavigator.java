package ru.xpoft.vaadin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * @author xpoft
 */
public class DiscoveryNavigator implements ViewCacheContainer//extends Navigator
{
    protected class ViewCache implements Serializable
    {
        private final String name;
        private final String beanName;
        private final Class<? extends Component> clazz;
        private final boolean cached;

        ViewCache(String name, String beanName, Class<? extends Component> clazz, boolean cached)
        {
            this.name = name;
            this.beanName = beanName;
            this.clazz = clazz;
            this.cached = cached;
        }

        public String getName()
        {
            return name;
        }

        public String getBeanName()
        {
            return beanName;
        }

        public Class<? extends Component> getClazz()
        {
            return clazz;
        }

        public boolean isCached()
        {
            return cached;
        }
    }

    private static Logger logger = LoggerFactory.getLogger(DiscoveryNavigator.class);
    protected static final List<ViewCache> views = Collections.synchronizedList(new ArrayList<ViewCache>());
    private final Map<String, Component> viewScoped = Collections.synchronizedMap(new HashMap<String, Component>());

//    public DiscoveryNavigator(UI ui, ComponentContainer container)
//    {
//        super(ui, container);
//        initViews();
//    }

//    public DiscoveryNavigator(UI ui, SingleComponentContainer container)
//    {
//        super(ui, container);
//        initViews();
//    }

//    public DiscoveryNavigator(UI ui, ViewDisplay display)
//    {
//        super(ui, display);
//        initViews();
//    }

//    public DiscoveryNavigator(UI ui, NavigationStateManager stateManager, ViewDisplay display)
//    {
//        super(ui, stateManager, display);
//        initViews();
//    }

    protected void initViews()
    {
        if (views.isEmpty())
        {
            logger.debug("discovery views from spring context");

            long start = Calendar.getInstance().getTimeInMillis();
            String[] beansName = SpringApplicationContext.getApplicationContext().getBeanDefinitionNames();

            // Also looking for parent's beans definition
            if (SpringApplicationContext.getApplicationContext().getParent() != null)
            {
                String[] parentBeansName = SpringApplicationContext.getApplicationContext().getParent().getBeanDefinitionNames();
                String[] newBeansName = new String[beansName.length + parentBeansName.length];

                System.arraycopy(beansName, 0, newBeansName, 0, beansName.length);
                System.arraycopy(parentBeansName, 0, newBeansName, beansName.length, parentBeansName.length);

                beansName = newBeansName;
            }
            for (String beanName : beansName)
            {
                Class beanClass = SpringApplicationContext.getApplicationContext().getType(beanName);

                // Check for a valid bean class because "abstract" beans may not have a bean class defined.
                if (beanClass != null && beanClass.isAnnotationPresent(VaadinView.class) && Component.class.isAssignableFrom(beanClass))
                {
                    VaadinView vaadinView = (VaadinView) beanClass.getAnnotation(VaadinView.class);
                    String viewName = vaadinView.value();
                    boolean viewCached = vaadinView.cached();

                    ViewCache viewCache = new ViewCache(viewName, beanName, beanClass, viewCached);
                    views.add(viewCache);
                    logger.debug("view name: \"{}\", class: {}, viewCached: {}", viewName, beanClass, viewCached);
                }
            }

            long end = Calendar.getInstance().getTimeInMillis();
            logger.debug("time: {}ms", (end - start));
        }
        else
        {
            logger.debug("discovery views from cache");
        }

        addCachedBeans();
    }

    public void addBeanView(String viewName, Class<? extends Component> viewClass)
    {
        addBeanView(viewName, viewClass, false);
    }

    /**
     * Add bean manually
     *
     * @param viewName
     * @param viewClass
     * @param cached
     */
    public void addBeanView(String viewName, Class<? extends Component> viewClass, boolean cached)
    {
        // Check parameters
        if (viewName == null || viewClass == null)
        {
            throw new IllegalArgumentException("view and viewClass must be non-null");
        }

        String[] beanNames = SpringApplicationContext.getApplicationContext().getBeanNamesForType(viewClass);
        if (beanNames.length != 1)
        {
            throw new IllegalArgumentException("cant't resolve bean name for class :" + viewClass.getName());
        }

//        removeView(viewName);
        addBeanView(viewName, beanNames[0], viewClass, cached);
    }

    protected void addCachedBeans()
    {
        for (ViewCache view : views)
        {
            addBeanView(view.name, view.beanName, view.clazz, view.cached);
        }
    }

    /**
     * It's prefer to use BeanName instead of Class
     * Because bean can be wrapper. Like SpringSecurity
     * Caused by: org.springframework.beans.factory.BeanNotOfRequiredTypeException: Bean named 'testView' must be of type [...testView], but was actually of type [$Proxy26]
     *
     */
    protected void addBeanView(String viewName, String beanName, Class<? extends Component> viewClass, boolean cached)
    {
//        addProvider(new SpringViewProvider(viewName, beanName, viewClass, cached, this));
    }

//    @Override
    public void navigateTo(String navigationState)
    {
        // We can't bind NULL
        if (navigationState == null)
        {
            navigationState = "";
        }

        // fix Vaadin
        if (navigationState.startsWith("!"))
        {
//            super.navigateTo(navigationState.substring(1));
        }
        else
        {
//            super.navigateTo(navigationState);
        }
    }

    /**
     * Better way
     *
     * @param name
     * @param beanName
     * @param cached
     * @return
     */
    @Override
    public Component getView(String name, String beanName, boolean cached)
    {
        if (cached)
        {
            if (viewScoped.containsKey(name))
            {
                return viewScoped.get(name);
            }

            Component view = (Component) SpringApplicationContext.getApplicationContext().getBean(beanName);
            viewScoped.put(name, view);

            return view;
        }

        return (Component) SpringApplicationContext.getApplicationContext().getBean(beanName);
    }
}
