package org.vaadin.tokenfield;

import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.v7.ui.ComboBox;
import org.vaadin.tokenfield.client.ui.TokenFieldServerRpc;

public abstract class TokenComboBox extends ComboBox {

    private static final long serialVersionUID = 8382983756053298383L;

    protected TokenField.InsertPosition insertPosition;

    public TokenComboBox(TokenField.InsertPosition insertPosition) {
        this.insertPosition = insertPosition;
        TokenFieldServerRpc rpc = this::onDelete;
        registerRpc(rpc);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addVariable(this, "del", false);
        if (insertPosition == TokenField.InsertPosition.AFTER) {
            target.addAttribute("after", true);
        }
    }

    public void setTokenInsertPosition(TokenField.InsertPosition insertPosition) {
        this.insertPosition = insertPosition;
        markAsDirty();
    }

    abstract protected void onDelete();

}
