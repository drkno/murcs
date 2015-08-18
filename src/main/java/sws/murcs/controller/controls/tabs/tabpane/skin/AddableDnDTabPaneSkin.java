package sws.murcs.controller.controls.tabs.tabpane.skin;

import com.sun.javafx.scene.control.skin.TabPaneSkin;
import java.lang.reflect.Field;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.listeners.GenericCallback;
import sws.murcs.listeners.TabFactory;

/**
 * A tab pane skin with an add button.
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
        Button addButton = new Button("+");
        addButton.getStyleClass().add("tab-add-button");

        container.getChildren().add(addButton);

        try {
            Field fTabHeaderArea = TabPaneSkin.class.getDeclaredField("tabHeaderArea"); //$NON-NLS-1$
            fTabHeaderArea.setAccessible(true);

            Pane tabHeaderArea = (StackPane) fTabHeaderArea.get(this);
            Field fHeadersRegion = tabHeaderArea.getClass().getDeclaredField("headersRegion"); //$NON-NLS-1$
            fHeadersRegion.setAccessible(true);

            final Pane headersRegion = (StackPane) fHeadersRegion.get(tabHeaderArea);
            headersRegion.widthProperty().addListener((observable, oldValue, newValue) -> {
                addButton.setManaged(false);
                double height = addButton.getBoundsInLocal().getHeight();
                Bounds b = headersRegion.getBoundsInLocal();
                b = headersRegion.localToScene(b);
                b = AddableDnDTabPaneSkin.this.getSkinnable().sceneToLocal(b);
                addButton.relocate(b.getMaxX(), b.getMaxY() - height);
            });

            headersRegion.getChildren().addListener((ListChangeListener<Node>) observable -> {
                //observable.next();
            });
        } catch (Exception e){
            ErrorReporter.get().reportError(e, "Seems like Java's internal tab interface has changed :'(");
        }
    }

    /**
     * Sets the current tab factory
     * @param tabFactory the tab factory
     */
    public void setTabFactory(final TabFactory tabFactory) {
        this.tabFactory = tabFactory;
    }
}
