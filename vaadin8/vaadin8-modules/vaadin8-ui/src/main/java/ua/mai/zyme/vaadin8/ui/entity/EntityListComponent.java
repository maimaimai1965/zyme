package ua.mai.zyme.vaadin8.ui.entity;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;

import java.util.Collection;

public class EntityListComponent<ENTITY, FILTER extends EntityFilterComponent> extends CustomComponent {

    protected VerticalLayout rootLayout;
    protected Label captionLabel;
    private String imagePath;
    protected FILTER filterComponent;
    protected Grid<ENTITY> grid;

    protected Button searchButton;
    protected Button resetButton;
    protected Button createButton;
    protected Button editButton;
    protected Button deleteButton;

    public EntityListComponent(Object...params) {
        this("", null, null, params);
    }

    public EntityListComponent(String caption, String imagePath, Object ... params) {
        this(caption, imagePath, null, params);
    }

    public EntityListComponent(String caption, String imagePath, FILTER filterComponent, Object...params) {
        this.filterComponent = filterComponent;
        this.imagePath = imagePath;
        if (filterComponent != null)
            filterComponent.setOwner(this);

        initParams(params);
        init();
        setCaption(caption);
    }

    protected void initParams(Object ... params) {}

    protected void init() {
        rootLayout = new VerticalLayout();
        rootLayout.setMargin(new MarginInfo(true, true, true, false));
        rootLayout.setSpacing(true);
        rootLayout.setSizeFull();

        createCaption();
        addFilter();
        createButtons();
        createGrid();
        refreshGrid();

        setCompositionRoot(rootLayout);
    }

    private void createCaption() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(false);
        horizontalLayout.setSpacing(true);
        if (imagePath != null) {
            com.vaadin.ui.Image image = new com.vaadin.ui.Image();
            image.setSource(new ThemeResource(imagePath));
            horizontalLayout.addComponent(image);
        }
        captionLabel = new Label();
        horizontalLayout.addComponent(captionLabel);
        rootLayout.addComponent(horizontalLayout);
    }

    private void addFilter() {
        if (filterComponent == null)
            return;
        filterComponent.setWidthFull();
        rootLayout.addComponent(filterComponent);
    }

    private void createButtons() {
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setMargin(false);
        buttonsLayout.setSpacing(true);
        buttonsLayout.setWidthFull();

        searchButton = new Button("Search", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                refreshGrid();
            }
        });
        buttonsLayout.addComponent(searchButton);

        resetButton = new Button("Reset", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                resetGrid();
            }
        });
        buttonsLayout.addComponent(resetButton);

        Label emptyLabel = new Label();
        buttonsLayout.addComponent(emptyLabel);
        emptyLabel.setWidthFull();
        buttonsLayout.setExpandRatio(emptyLabel, 1);

        createButton = new Button("Create");
        buttonsLayout.addComponent(createButton);

        editButton = new Button("Edit");
        buttonsLayout.addComponent(editButton);

        deleteButton = new Button("Delete");
        buttonsLayout.addComponent(deleteButton);

        rootLayout.addComponent(buttonsLayout);
    }
    private void createGrid() {
        grid = new Grid();
        initGrid();
        grid.setSizeFull();
        rootLayout.addComponent(grid);
        rootLayout.setExpandRatio(grid, 1);
    }

    protected void initGrid() {}

    protected void refreshGrid() {
        grid.setItems(search());
    }

    protected void resetGrid() {
        filterComponent.clean();
        refreshGrid();
    }

    protected Collection<ENTITY> search() {
        return null;
    }

    public void setCaption(String caption) {
        captionLabel.setValue(caption);
    }

}
