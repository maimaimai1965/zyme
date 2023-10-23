package ua.telesens.o320.tif.ui.widgets.placeholder;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.ClientConnector;
import com.vaadin.ui.AbstractTextField;

/**
 * @author Kulik Sergey
 */
public class PlaceHolder extends AbstractExtension {

    public static PlaceHolder extend(AbstractTextField field) {
        PlaceHolder me = new PlaceHolder();
        me.extend((AbstractClientConnector) field);
        return me;
    }

    protected Class<? extends ClientConnector> getSupportedParentType() {
        return AbstractTextField.class;
    }

    public void setPlaceHolder(String placeHolder) {
        getState().placeholder = placeHolder;
    }

    @Override
    public PlaceHolderState getState() {
        return (PlaceHolderState) super.getState();
    }

    private PlaceHolder() {
    }
}
