package ua.telesens.o320.tif.ui.widgets.placeholder.client;

import com.google.gwt.core.client.Scheduler;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.VTextField;
import com.vaadin.shared.ui.Connect;
import ua.telesens.o320.tif.ui.widgets.placeholder.PlaceHolder;
import ua.telesens.o320.tif.ui.widgets.placeholder.PlaceHolderState;

/**
 * @author Kulik Sergey
 */
@Connect(PlaceHolder.class)
public class PlaceHolderConnector extends AbstractExtensionConnector implements StateChangeEvent.StateChangeHandler {

    private static final long serialVersionUID = -3584902497101338136L;

    private VTextField textField;

    @Override
    protected void extend(ServerConnector target) {
        target.addStateChangeHandler(new StateChangeEvent.StateChangeHandler() {
            @Override
            public void onStateChanged(StateChangeEvent stateChangeEvent) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        updatePlaceHolder();
                    }
                });
            }
        });
        textField = (VTextField) ((ComponentConnector) target).getWidget();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        updatePlaceHolder();
    }

    @Override
    public PlaceHolderState getState() {
        return (PlaceHolderState) super.getState();
    }

    private void updatePlaceHolder() {
        textField.getElement().setAttribute("placeholder", getState().placeholder);
    }

}
