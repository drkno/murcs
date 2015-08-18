package sws.murcs.controller.controls.tabs.tabpane.skin;

import com.sun.javafx.scene.control.skin.TabPaneSkin;
import java.lang.reflect.Field;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.listeners.TabFactory;

/**
 * A tab pane skin with an add button. This class is essentially a giant Hack.
 */
public class AddableDnDTabPaneSkin  extends DnDTabPaneSkin{
    /**
     * Button for adding tabs.
     */
    private Button addTabButton;

    /**
     * The tabpane this skin belongs to.
     */
    private TabPane tabPane;

    /**
     * The container of the tab pane.
     */
    private Pane container;

    /**
     * Creates a new tab factory to use
     * when creating new tabs.
     */
    private TabFactory tabFactory;

    /**
     * The header pane.
     */
    private StackPane headerPane;

    /**
     * Trust me. You don't want to know.
     * (you do? Oh. Well JavaFx doesn't play nice with sizing
     * unless you've started..).
     */
    private boolean started;

    /**
     * Create a new skin.
     * @param container the container of the tab pane
     * @param tabPane the tab pane
     */
    public AddableDnDTabPaneSkin(final Pane container, final TabPane tabPane) {
        super(tabPane);

        this.container = container;
        this.tabPane = tabPane;
        setupAddButton();
    }

    /**
     * Sets up the add button for the tab pane.
     */
    private void setupAddButton() {
        addTabButton = new Button("+");
        addTabButton.getStyleClass().add("tab-add-button");
        addTabButton.setOnAction(e -> {
            if (tabFactory != null){
                tabPane.getTabs().add(tabFactory.createTab(tabPane));
            }
        });

        container.getChildren().add(addTabButton);

        try {
            Field fTabHeaderArea = TabPaneSkin.class.getDeclaredField("tabHeaderArea"); //$NON-NLS-1$
            fTabHeaderArea.setAccessible(true);

            Pane tabHeaderArea = (StackPane) fTabHeaderArea.get(this);
            Field fHeadersRegion = tabHeaderArea.getClass().getDeclaredField("headersRegion"); //$NON-NLS-1$
            fHeadersRegion.setAccessible(true);

            headerPane = (StackPane) fHeadersRegion.get(tabHeaderArea);
            headerPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                //Stop the layout from updating the buttons position
                addTabButton.setManaged(false);
                recalculateAddPosition();
                started = true;
            });
        } catch (Exception e){
            ErrorReporter.get().reportError(e, "Seems like Java's internal tab interface has changed :'(");
        }
    }

    /**
     * Recalculates and updates the add button position.
     */
    private void recalculateAddPosition() {
        double height = addTabButton.getBoundsInLocal().getHeight();
        Bounds b = headerPane.getBoundsInLocal();
        b = headerPane.localToScene(b);
        b = AddableDnDTabPaneSkin.this.getSkinnable().sceneToLocal(b);
        addTabButton.relocate(b.getMaxX() + 5 * addTabButton.getScaleX() + (started ? 0 : 5), 5 * addTabButton.getScaleY());
    }

    /**
     * Sets the current tab factory
     * @param tabFactory the tab factory
     */
    public void setTabFactory(final TabFactory tabFactory) {
        this.tabFactory = tabFactory;
    }
}
