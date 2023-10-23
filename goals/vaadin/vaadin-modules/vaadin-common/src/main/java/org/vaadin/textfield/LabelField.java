package org.vaadin.textfield;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;

public class LabelField extends CustomField<String> {

    private Label label;
    private TextField editor;

    public LabelField() {
        super();
        editor = new TextField();
        label = new Label();
    }

    public LabelField(Label label, TextField editor) {
        this.label = label;
        this.editor = editor;
    }

    public LabelField(String defaultValue, Label label, TextField editor) {
        super(defaultValue);
        this.label = label;
        this.editor = editor;
    }

    @Override
    protected String generateModelValue() {
        return editor.getValue();
    }

    @Override
    protected void setPresentationValue(String o) {
        editor.setValue(o);
    }
}
