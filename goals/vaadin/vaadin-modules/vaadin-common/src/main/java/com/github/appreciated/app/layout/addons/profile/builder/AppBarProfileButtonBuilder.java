package com.github.appreciated.app.layout.addons.profile.builder;

import com.github.appreciated.app.layout.addons.profile.ProfileButton;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.contextmenu.MenuItem;

public class AppBarProfileButtonBuilder {

    private final ProfileButton appBarProfileButton;

    private AppBarProfileButtonBuilder() {
        appBarProfileButton = new ProfileButton();
    }

    public static AppBarProfileButtonBuilder get() {
        return new AppBarProfileButtonBuilder();
    }

    public AppBarProfileButtonBuilder withItem(String text) {
        appBarProfileButton.addItem(text);
        return this;
    }

    public AppBarProfileButtonBuilder withItem(Component component) {
        appBarProfileButton.addItem(component);
        return this;
    }

    public AppBarProfileButtonBuilder withItem(String text, ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        appBarProfileButton.addItem(text, clickListener);
        return this;
    }

    public AppBarProfileButtonBuilder withItem(Component component, ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        appBarProfileButton.addItem(component, clickListener);
        return this;
    }

    public ProfileButton build() {
        return appBarProfileButton;
    }
}