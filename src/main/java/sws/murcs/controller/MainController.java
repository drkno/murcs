package sws.murcs.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.view.App;

/**
 * A controller for the base pane.
 */
public class MainController {
    /**
     * The Menu bar for the application.
     */
    @FXML
    private MenuBar menuBar;

    /**
     * The Menu items for the main window.
     */
    @FXML
    private MenuItem fileQuit, undoMenuItem, redoMenuItem, open, save, saveAs, generateReport, addProject, newModel,
            addTeam, addPerson, addSkill, addRelease, addStory, addBacklog, showHide, revert, highlightToggle;

    /**
     * The menu that contains the check menu items for toggling sections of the toolbar.
     */
    @FXML
    private Menu toolBarMenu;


    /**
     * The side display which contains the display list. Also the top toolbar and menu container.
     */
    @FXML
    private VBox vBoxSideDisplay, titleVBox;

    @FXML
    public final void initialize() {
        addToolBar();
        App.addListener(e -> {
            e.consume();
            fileQuitPress(null);
        });
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            menuBar.useSystemMenuBarProperty().set(true);
        }
    }

    /**
     * Called when the Quit button is pressed in the
     * file menu and quit the current application.
     * @param event The even that triggers the function
     */
    @FXML
    private void fileQuitPress(final ActionEvent event) {
        if (UndoRedoManager.canRevert()) {
            GenericPopup popup = new GenericPopup();
            popup.setWindowTitle("Unsaved Changes");
            popup.setTitleText("Do you wish to save changes?");
            popup.setMessageText("You have unsaved changes to your project.");
            popup.addButton("Discard", GenericPopup.Position.LEFT, GenericPopup.Action.NONE, m -> {
                popup.close();
                Platform.exit();
            });
            popup.addButton("Save", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, m -> {
                // Let the user save the project
                if (save()) {
                    popup.close();
                    Platform.exit();
                }
            });
            popup.addButton("Cancel", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, m -> popup.close());
            popup.show();
        }
        else {
            Platform.exit();
        }
    }

    /**
     * Toggles the view of the display list box at the side.
     * @param event The event that triggers the function
     */
    @FXML
    private void toggleItemListView(final ActionEvent event) {
        if (!vBoxSideDisplay.managedProperty().isBound()) {
            vBoxSideDisplay.managedProperty().bind(vBoxSideDisplay.visibleProperty());
        }
        vBoxSideDisplay.setVisible(!vBoxSideDisplay.isVisible());
    }

    /**
     * Adds the toolbar to the application window.
     */
    private void addToolBar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/murcs/ToolBar.fxml"));
            Parent view = loader.load();
            titleVBox.getChildren().add(view);
            ToolBarController controller = loader.getController();
            controller.setLinkedController(this);
            controller.setToolBarMenu(toolBarMenu);
            toolBarController = controller;
        }
        catch (Exception e) {
            ErrorReporter.get().reportErrorSecretly(e, "Unable to create editor");
        }
    }

}
