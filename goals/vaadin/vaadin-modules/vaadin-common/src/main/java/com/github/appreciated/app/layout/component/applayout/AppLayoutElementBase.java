package com.github.appreciated.app.layout.component.applayout;

import com.github.appreciated.app.layout.component.builder.interfaces.NavigationElementContainer;
import com.github.appreciated.app.layout.webcomponents.applayout.AppDrawer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

/**
 * The interface every {@link AppLayout} is required
 * to be implemented to allow any {@link com.github.appreciated.app.layout.component.builder.AppLayoutBuilder} to build it.
 */

public interface AppLayoutElementBase extends NavigationElementContainer {

    String getStyleName();

    Component getTitleComponent();

    void setTitleComponent(Component titleComponent);

    AppDrawer getDrawer();

    FlexLayout getTitleWrapper();

    void setIconComponent(Component appBarIconComponent);

    void setAppLayoutContent(HasElement content);

    boolean isUpNavigationEnabled();

    void setUpNavigationEnabled(boolean enable);

    void showUpNavigation(boolean visible);

    void setAppBar(Component component);

    void setAppMenu(Component component);

    default void init() {
    }

    HasElement getContentElement();

    void setPercentageHeight(boolean set);
}
