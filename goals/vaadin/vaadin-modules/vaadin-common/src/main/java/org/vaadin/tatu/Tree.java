package org.vaadin.tatu;

import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.function.ValueProvider;

/**
 * Vaadin Tree component https://github.com/amahdy/vaadin-tree
 */
public class Tree<T> extends TreeGrid<T> {

    public Tree(ValueProvider<T, ?> valueProvider) {
        addHierarchyColumn(valueProvider);
    }
}