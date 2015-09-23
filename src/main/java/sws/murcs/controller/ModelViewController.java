package sws.murcs.controller;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import sws.murcs.controller.controls.cells.DisplayListCell;
import sws.murcs.controller.pipes.Tabbable;
import sws.murcs.controller.windowManagement.Window;
import sws.murcs.internationalization.InternationalizationHelper;
import sws.murcs.helpfulHints.HelpfulHintsView;
import sws.murcs.listeners.ViewUpdate;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;
import sws.murcs.model.Model;
import sws.murcs.model.ModelType;
import sws.murcs.model.Organisation;
import sws.murcs.model.Skill;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.model.observable.ModelObservableArrayList;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;
import sws.murcs.view.CreatorWindowView;

/**
 * Model View controller. Controls the main tabs.
 */
public class ModelViewController implements ViewUpdate<Model>, UndoRedoChangeListener,
        Tabbable {
    /**
     * The main display of the window which contains the display
     * list and the content pane.
     */
    @FXML
    private HBox hBoxMainDisplay;

    /**
     * The choice box for selecting the model type to be
     * displayed in the list.
     */
    @FXML
    private ChoiceBox<ModelType> displayChoiceBox;

    /**
     * The list which contains the models of the type selected
     * in the display list choice box.
     */
    @FXML
    private ListView displayList;

    /**
     * The content pane contains the information about the
     * currently selected model item.
     */
    @FXML
    private GridPane contentPane;

    /**
     * The Vbox containing the display list.
     */
    @FXML
    private VBox vBoxSideDisplay;

    /**
     * A creation window.
     */
    private CreatorWindowView creatorWindow;

    /**
     * The content pane to show the view for a model.
     */
    private EditorPane editorPane;

    /***
     * The toolbar associated with this controller.
     */
    private ToolBarController toolBarController;

    /**
     * The navigation manager for the controller.
     */
    private NavigationManager navigationManager;

    /**
     * The controller in charge of this one.
     */
    private MainController mainController;

    /**
     * The title property for the pane.
     */
    private SimpleStringProperty titleProperty = new SimpleStringProperty(InternationalizationHelper.tryGet("NewTab"));

    /**
     * The tab that view exists within.
     */
    private Tab containingTab;

    /**
     * Boolean if the hints are shown.
     */
    private boolean hintsAreShown;

    /**
     * The helpful hints view.
     */
    private HelpfulHintsView helpfulHints;

    /**
     * The active scene. We save this because JavaFX magically sets it to null (sometimes)
     * and we have no idea why. It also doesn't fire the change listener when it sets it
     * to null, so we're exploiting that :P
     */
    private Scene activeScene;

    /**
     * Initialises the GUI, setting up the the options in the choice box and populates the display list if necessary.
     * Put all initialisation of GUI in this function.
     */
    @FXML
    public final void initialize() {
        navigationManager = new NavigationManager();
        navigationManager.setModelViewController(this);

        hBoxMainDisplay.sceneProperty().addListener((observable, oldValue, newValue) -> {
            //We don't really want to store a null value.
            if (newValue != null) {
                activeScene = newValue;
            }
        });

        displayChoiceBox.setConverter(new StringConverter<ModelType>() {
            @Override
            public String toString(final ModelType object) {
                return InternationalizationHelper.tryGet(object.toString());
            }

            @Override
            public ModelType fromString(final String string) {
                return null;
            }
        });

        for (ModelType type : ModelType.values()) {
            displayChoiceBox.getItems().add(type);
        }
        displayChoiceBox.getStyleClass().add("no-shadow");
        displayChoiceBox
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observer, oldValue, newValue) -> {
                    updateList();
                    updateTitle();
                });

        displayChoiceBox.getSelectionModel().select(0);
        displayList.setCellFactory(param -> new DisplayListCell());

        //If the person control clicked, open in a new tab
        displayList.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.isControlDown() && DisplayListCell.getHoveringOver() != null) {
                navigateToNewTab(DisplayListCell.getHoveringOver());
                event.consume();
            }
        });

        displayList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                if (editorPane != null && newValue != null && editorPane.getController().isLoaded()) {
                    editorPane.getController().saveChanges();
                }
                updateDisplayListSelection(newValue, oldValue);
            }
            updateTitle();
        });

        UndoRedoManager.get().addChangeListener(this);
        showHelpfulHints();
        updateList();
    }

    /**
     * Updates the title property.
     */
    private void updateTitle() {
        Model selectedItem = (Model) displayList.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        titleProperty.set(selectedItem.getShortName());
    }

    /**
     * Updates the currently selected item in the display list.
     * @param newValue The new value
     * @param oldValue The old value
     */
    private void updateDisplayListSelection(final Object newValue, final Object oldValue) {
        if (newValue == null && editorPane != null) {
            editorPane.dispose();
            editorPane = null;
            contentPane.getChildren().clear();

            return;
        }

        navigationManager.navigateTo((Model) newValue);
        if (toolBarController != null) {
            toolBarController.updateBackForwardButtons();
        }

        if (oldValue == null) {
            displayList.scrollTo(newValue);
        }
    }

    /**
     * Shows the helpful hints and creates them if they have not been initialized.
     */
    private void showHelpfulHints() {
        if (helpfulHints == null) {
            helpfulHints = new HelpfulHintsView();
            helpfulHints.create();
        }
        if (!contentPane.getChildren().contains(helpfulHints.getView())) {
            contentPane.getChildren().add(helpfulHints.getView());
        }
        helpfulHints.showHints();
        hintsAreShown = true;
    }

    /**
     * Updates the display list on the left hand side of the screen.
     */
    private void updateList() {
        ModelType type = ModelType.getModelType(displayChoiceBox.getSelectionModel().getSelectedIndex());
        Organisation model = PersistenceManager.getCurrent().getCurrentModel();

        if (model == null) {
            return;
        }

        List<? extends Model> arrayList;
        switch (type) {
            case Project:
                arrayList = model.getProjects();
                break;
            case Person:
                arrayList = model.getPeople();
                break;
            case Team:
                arrayList = model.getTeams();
                break;
            case Skill:
                arrayList = model.getSkills();
                break;
            case Release:
                arrayList = model.getReleases();
                break;
            case Story:
                arrayList = model.getStories();
                break;
            case Backlog:
                arrayList = model.getBacklogs();
                break;
            case Sprint:
                arrayList = model.getSprints();
                break;
            default:
                throw new UnsupportedOperationException();
        }

        if (arrayList.getClass() == ModelObservableArrayList.class) {
            ModelObservableArrayList<? extends Model> arrList = (ModelObservableArrayList) arrayList;
            arrayList = new SortedList<>(arrList, (Comparator<? super Model>) arrList);
        }
        else {
            System.err.println("This list type does not yet have an ordering specified, "
                    + "please correct this so that the display list is shown correctly.");
        }

        boolean selectionCleared = false;
        if (displayList.getSelectionModel().getSelectedIndex() < 0) {
            selectionCleared = true;
        }

        if (editorPane == null || editorPane.getModel() == null || !arrayList.contains(editorPane.getModel())) {
            displayList.getSelectionModel().clearSelection();
            selectionCleared = true;
        }

        if (!Objects.equals(displayList.getItems(), arrayList)) {
            displayList.getSelectionModel().clearSelection();
            displayList.setItems((ObservableList) arrayList);
            selectionCleared = true;
        }

        if (selectionCleared && arrayList.size() > 0) {
            displayList.getSelectionModel().select(0);
            if (hintsAreShown && helpfulHints != null) {
                helpfulHints.hide();
                hintsAreShown = false;
            }
        } else if (arrayList.size() > 0) {
            displayList.getSelectionModel().select(editorPane.getModel());
            displayList.getSelectionModel().select(editorPane.getModel());
            if (hintsAreShown && helpfulHints != null) {
                helpfulHints.hide();
                hintsAreShown = false;
            }
        }
    }

    @Override
    public final void create() {
        create(displayChoiceBox.getValue());
    }

    @Override
    public final void create(final ModelType type) {
        MainController.showCreateWindow(type, this::selectItem);
    }

    /**
     * Function that is called when you want to delete a model.
     */
    @Override
    public final void remove() {
        Organisation model = PersistenceManager.getCurrent().getCurrentModel();
        if (model == null) {
            return;
        }

        final int selectedIndex = displayList.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            return;
        }

        Model selectedItem = (Model) displayList.getSelectionModel().getSelectedItem();

        // Ensures you can't delete Product Owner or Scrum Master
        if (ModelType.getModelType(displayChoiceBox.getSelectionModel().getSelectedIndex()) == ModelType.Skill) {
            if (selectedItem.getShortName().equals("PO") || selectedItem.getShortName().equals("SM")) {
                return;
            }
        }

        Collection<Model> usages = UsageHelper.findUsages(selectedItem);
        //Get the current window. Dion's stuff needs this.
        Window window = App.getWindowManager()
                .getAllWindows()
                .stream()
                .filter(w -> w.getStage() == activeScene.getWindow())
                .findFirst()
                .orElse(null);

        GenericPopup popup = new GenericPopup(window);
        String message = "{AreYouSureDelete}";
        if (usages.size() != 0) {
            message += "\n{This} ";
            ModelType type = ModelType.getModelType(selectedItem);
            message += type.toString().toLowerCase() + " {IsUsedIn} " + usages.size() + " {Places}:";
            for (Model usage : usages) {
                message += "\n" + usage.getShortName();
            }
        }
        popup.setTitleText("{Reallydelete}");
        popup.setMessageText(message);

        popup.addButton("{Yes}", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, () -> {
            popup.close();
            navigationManager.clearHistory();
            Model item = (Model) displayList.getSelectionModel().getSelectedItem();
            model.remove(item);
            toolBarController.updateBackForwardButtons();
        }, "danger-will-robinson");
        popup.addButton("{No}", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, popup::close, "everything-is-fine");
        popup.show();
    }

    @SuppressWarnings("checkstyle:finalparameters")
    @Override
    public final void selectItem(Model parameter) {
        ModelType type;
        ModelType selectedType = ModelType.getModelType(displayChoiceBox.getSelectionModel().getSelectedIndex());

        if (parameter == null) {
            displayList.getSelectionModel().select(0);
            displayList.scrollTo(0);
            parameter = (Model) displayList.getSelectionModel().getSelectedItem();
        }
        else {
            type = ModelType.getModelType(parameter);
            if (type != selectedType) {
                navigationManager.setIgnore(true);
                displayChoiceBox.getSelectionModel().select(ModelType.getSelectionType(type));
                navigationManager.setIgnore(false);
            }
            if (parameter != displayList.getSelectionModel().getSelectedItem()) {
                displayList.getSelectionModel().select(parameter);
                displayList.scrollTo(parameter);
            }

            if (displayList.getSelectionModel().getSelectedIndex() < 0) {
                displayList.scrollTo(parameter);
            }
        }

        if (toolBarController != null) {
            // The remove button should be greyed out
            // if no item is selected or built in skills (PO or SM) are selected
            toolBarController.removeButtonDisabled(parameter == null
                    || parameter instanceof Skill
                    && (((Skill) parameter).getShortName().equals("PO")
                    || ((Skill) parameter).getShortName().equals("SM")));
        }

        if (editorPane == null) {
            editorPane = new EditorPane(parameter, this);
            contentPane.getChildren().clear();
            contentPane.getChildren().add(editorPane.getView());
        }
        else {
            if (editorPane.getModel().getClass() == parameter.getClass()) {
                editorPane.setModel(parameter);
            }
            else {
                editorPane.dispose();
                contentPane.getChildren().clear();
                editorPane = new EditorPane(parameter, this);
                contentPane.getChildren().add(editorPane.getView());
            }
        }
    }

    @Override
    public SimpleStringProperty getTitle() {
        return titleProperty;
    }

    /**
     * The root of the current form.
     * @return The root node
     */
    @Override
    public final Parent getRoot() {
        return hBoxMainDisplay;
    }

    @Override
    public void toggleSideBar(final boolean sidebar) {
        if (!vBoxSideDisplay.managedProperty().isBound()) {
            vBoxSideDisplay.managedProperty().bind(vBoxSideDisplay.visibleProperty());
        }
        vBoxSideDisplay.setVisible(sidebar);
    }

    @Override
    public boolean sideBarVisible() {
        return vBoxSideDisplay.visibleProperty().getValue();
    }

    @Override
    public boolean canToggleSideBar() {
        return true;
    }

    /**
     * Navigates back.
     */
    @FXML
    public final void goBack() {
        if (!navigationManager.canGoBack()) {
            return;
        }
        displayList.getSelectionModel().clearSelection();
        navigationManager.goBack();
        toolBarController.updateBackForwardButtons();
        updateTitle();
    }

    @Override
    public boolean canGoForward() {
        return navigationManager.canGoForward();
    }

    @Override
    public boolean canGoBack() {
        return navigationManager.canGoBack();
    }

    @Override
    public void navigateTo(final Model model) {
        selectItem(model);
    }

    @Override
    public void navigateTo(final ModelType type) {
        displayChoiceBox.getSelectionModel().select(type);
    }

    @Override
    public void navigateToNewTab(final Model model) {
        navigationManager.navigateToNewTab(model);
    }

    @Override
    public ModelType getCurrentModelType() {
        return displayChoiceBox.getValue();
    }

    @Override
    public void update() {
        if (editorPane != null && PersistenceManager.getCurrent().getCurrentModel() != null) {
            editorPane.getController().loadObject();
        }
    }

    /**
     * Navigates forward.
     */
    @FXML
    public final void goForward() {
        if (!navigationManager.canGoForward()) {
            return;
        }
        displayList.getSelectionModel().clearSelection();
        navigationManager.goForward();
        toolBarController.updateBackForwardButtons();
        updateTitle();
    }

    @Override
    public void undoRedoNotification(final ChangeState param) {
        switch (param) {
            case Commit:
            case Forget:
            case Remake:
                updateList();
                break;
            case Revert:
                navigationManager.clearHistory();
                toolBarController.updateBackForwardButtons();
                updateList();
                break;
            default: break;
        }
    }

    @Override
    public void setToolBarController(final ToolBarController toolBarController) {
        this.toolBarController = toolBarController;
    }

    @Override
    public void setTab(final Tab tab) {
        this.containingTab = tab;
    }

    @Override
    public Tab getTab() {
        return this.containingTab;
    }

    @Override
    public void registerMainController(final MainController controller) {
        mainController = controller;
    }
}
