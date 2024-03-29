package ru.xpoft.vaadin;

import com.vaadin.flow.component.Component;

/**
 * @author xpoft
 */
public class SpringViewProvider// extends Navigator.ClassBasedViewProvider
{
    private final String beanName;
    private final boolean cached;
    private final ViewCacheContainer cacheContainer;

    /**
     * Create a new view provider which creates new view instances based on
     * a view class.
     *
     * @param viewName  name of the views to create (not null)
     * @param viewClass class to instantiate when a view is requested (not null)
     */
    public SpringViewProvider(String viewName, String beanName, Class<? extends Component> viewClass, boolean cached, ViewCacheContainer cacheContainer)
    {
//        super(viewName, viewClass);
        this.beanName = beanName;
        this.cached = cached;
        this.cacheContainer = cacheContainer;
    }

//    @Override
    public Component getView(String viewName)
    {
//        if (getViewName().equals(viewName))
//        {
            return cacheContainer.getView(viewName, beanName, cached);
//        }

//        return (Component)null;
    }
}
