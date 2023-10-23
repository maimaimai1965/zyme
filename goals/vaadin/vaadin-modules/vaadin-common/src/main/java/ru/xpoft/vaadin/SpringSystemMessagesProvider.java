package ru.xpoft.vaadin;

import com.vaadin.flow.server.SystemMessages;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author xpoft
 */
public interface SpringSystemMessagesProvider extends Serializable
{
    SystemMessages getSystemMessages(Locale locale);
}
