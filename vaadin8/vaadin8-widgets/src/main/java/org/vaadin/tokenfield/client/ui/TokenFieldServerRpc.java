package org.vaadin.tokenfield.client.ui;

import com.vaadin.shared.communication.ServerRpc;

public interface TokenFieldServerRpc extends ServerRpc {
    void deleteToken();
}
