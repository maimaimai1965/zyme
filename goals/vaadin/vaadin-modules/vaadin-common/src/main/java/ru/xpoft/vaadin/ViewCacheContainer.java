package ru.xpoft.vaadin;

import com.vaadin.flow.component.Component;

/**
 * @author xpoft
 */
public interface ViewCacheContainer
{
    Component getView(String name, String beanName, boolean cached);
}
