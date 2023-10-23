package org.vaadin.tokenfield;

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;

/**
 * Created by SKulik on 20.09.2017.
 */
public class ExtendedButton extends Button {
    public ExtendedButton() {
    }

    public ExtendedButton(String caption) {
        super(caption);
    }

    public ExtendedButton(Resource icon) {
        super(icon);
    }

    public ExtendedButton(String caption, Resource icon) {
        super(caption, icon);
    }

    public ExtendedButton(String caption, ClickListener listener) {
        super(caption, listener);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
    }
}
