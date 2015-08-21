package sws.murcs.controller;

import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import sws.murcs.controller.pipes.ModelManagable;
import sws.murcs.controller.pipes.Navigable;
import sws.murcs.controller.pipes.ToolBarCommands;
import sws.murcs.model.ModelType;

/**
 * Controller for the toolbar.
 */
public class ToolBarController {

    /**
     * All the buttons on the toolbar.
     */
    @FXML
    private Button backButton, forwardButton, undoButton, redoButton, revertButton, removeButton,
            openButton, saveButton, saveAsButton, sendFeedbackButton, generateReportButton, searchButton;

    /**
     * The toolbar sections for the toolbar.
     */
    @FXML
    private HBox navigationToolBar, historyToolBar, editToolBar, reportingToolBar, searchToolBar;

    /**
     * The overall container for the toolbar.
     */
    @FXML
    private ToolBar toolBar;

    /**
     * The context menu that is used for toggling visibility on sections of the toolbar.
     */
    @FXML
    private ContextMenu visibilityContextMenu;

    /**
     * The toolbar on any parent controller that controls visibility for sections of the toolbar.
     */
    private Menu toolBarMenu;

    /**
     * The shortcut key to used based on the OS.
     */
    private String shortCutKey;

    /**
     * The controller that is linked to the toolbar that manages all of the commands coming from the toolbar.
     * This would usually be the controller for the fxml that you are injecting the toolbar into.
     */
    private ToolBarCommands linkedController;

    /**
     * The navigation controller that navigation commands are routed through.
     */
    private Navigable navigable;

    /**
     * The controller responsible for managing model objects.
     */
    private ModelManagable modelManagable;

    /**
     * Creates a new toolbar controller.
     */
    public ToolBarController() {
    }

    /**
     * Initialises the toolbar by setting up appropriate tooltips.
     */
    @FXML
    @SuppressWarnings("checkstyle:avoidinlineconditionals")
    private void initialize() {
        shortCutKey  = System.getProperty("os.name").toLowerCase().contains("mac") ? "Command" : "Ctrl";
        setUpToolTips();
    }

    /**
     * Sets up the tooltips for the toolbar controls that will have different shortcuts on different OS's.
     */
    private void setUpToolTips() {
        revertButton.getTooltip().setText("Revert changes (" + shortCutKey + "+R)");
        openButton.getTooltip().setText("Open project (" + shortCutKey + "+O)");
        saveAsButton.getTooltip().setText("Save As (" + shortCutKey + "+Shift+S)");
        saveButton.getTooltip().setText("Save (" + shortCutKey + "+S)");
        sendFeedbackButton.getTooltip().setText("Send feedback to the developers (" + shortCutKey + "+B)");
        generateReportButton.getTooltip().setText("Generate report (" + shortCutKey + "+G)");
        searchButton.getTooltip().setText("Search (" + shortCutKey + "+F Or " + shortCutKey + "+Space)");
    }

    /**
     * Sets the linked controller for the toolbar.
     * @param controller The controller that is linked to the toolbar.
     */
    public final void setLinkedController(final ToolBarCommands controller) {
        if (controller != null) {
            linkedController = controller;
        }
    }

    /**
     * Sets up the tool bar menu that is linked to the toolbar (in app controller this is in the view menu).
     * @param menu The menu that has links to the toolbar for showing and hiding sections.
     */
    public final void setToolBarMenu(final Menu menu) {
        toolBarMenu = menu;
    }

    /**
     * The function called when you click the back button. It redirects it through the linkedController.
     * @param event Clicking the back button.
     */
    @FXML
    private void backButtonClick(final ActionEvent event) {
        navigable.goBack();
        updateBackForwardButtons();
    }

    /**
     * The function called when you click the forward button. It redirects it through the linkedController.
     * @param event Clicking the forward button.
     */
    @FXML
    private void forwardButtonClick(final ActionEvent event) {
        navigable.goForward();
        updateBackForwardButtons();
    }

    /**
     * The function called when you click the undo button. It redirects it through the linkedController.
     * @param event Clicking the undo button.
     */
    @FXML
    private void undoButtonClick(final ActionEvent event) {
        linkedController.undo(event);
    }

    /**
     * The function called when you click the redo button. It redirects it through the linkedController.
     * @param event Clicking the redo button.
     */
    @FXML
    private void redoButtonClick(final ActionEvent event) {
        linkedController.redo(event);
    }

    /**
     * The function called when you click the revert button. It redirects it through the linkedController.
     * @param event Clicking the revert button.
     */
    @FXML
    private void revertButtonClick(final ActionEvent event) {
        linkedController.revert(event);
    }

    /**
     * The function called when you click the add button. It redirects it through the linkedController.
     * @param event Clicking the add button.
     */
    @FXML
    private void addButtonClick(final ActionEvent event) {
        ModelType type = null;
        if (event != null && event.getSource() instanceof MenuItem) {
            //If pressing a menu item to add a person, team or skill
            String id = ((MenuItem) event.getSource()).getId();
            switch (id) {
                case "addProject":
                    type = ModelType.Project;
                    break;
                case "addPerson":
                    type = ModelType.Person;
                    break;
                case "addTeam":
                    type = ModelType.Team;
                    break;
                case "addSkill":
                    type = ModelType.Skill;
                    break;
                case "addRelease":
                    type = ModelType.Release;
                    break;
                case "addBacklog":
                    type = ModelType.Backlog;
                    break;
                case "addStory":
                    type = ModelType.Story;
                    break;
                default:
                    throw new UnsupportedOperationException("Adding has not been implemented.");
            }
        }

        if (type == null) {
            modelManagable.create();
        }
        else{
            modelManagable.create(type);
        }
    }

    /**
     * The function called when you click the save as button. It redirects it through the linkedController.
     * @param event Clicking the save button.
     */
    @FXML
    private void saveButtonClick(final ActionEvent event) {
        linkedController.save(event);
    }

    /**
     * The function called when you click the save as button. It redirects it through the linkedController.
     * @param event Clicking the save as button.
     */
    @FXML
    private void saveAsButtonClick(final ActionEvent event) {
        linkedController.saveAs(event);
    }

    /**
     * The function called when you click the open button. It redirects it through the linkedController.
     * @param event Clicking the open button.
     */
    @FXML
    private void openButtonClick(final ActionEvent event) {
        linkedController.open(event);
    }

    /**
     * The function called when you click the generate report button. It redirects it through the linkedController.
     * @param event Clicking the generate report button.
     */
    @FXML
    private void generateReportButtonClick(final ActionEvent event) {
        linkedController.generateReport(event);
    }

    /**
     * The function called when you click the send feedback button. It redirects it through the linkedController.
     * @param event Clicking the send feedback button.
     */
    @FXML
    private void sendFeedbackButtonClick(final ActionEvent event) {
        linkedController.reportBug();
    }

    /**
     * The function called when you click the remove button. It redirects it through the linkedController.
     * @param event Clicking the remove button in the toolbar.
     */
    @FXML
    private void removeButtonClick(final ActionEvent event) {
        modelManagable.remove();
    }

    /**
     * The function called when you click the remove button. It redirects it through the linkedController.
     * @param event Clicking the search button in the toolbar.
     */
    @FXML
    private void searchButtonClick(final ActionEvent event) {
        linkedController.search(event);
    }

    /**
     * Toggles the state of the back and forward buttons if they disabled or enabled.
     */
    public final void updateBackForwardButtons() {
        backButton.setDisable(!navigable.canGoBack());
        forwardButton.setDisable(!navigable.canGoForward());
    }

    /**
     * Updates the undo button on the toolbar. Changing whether or not it is disabled and also changing the tooltip.
     * @param disabled Whether or not the button should be disabled.
     * @param tooltip The tooltip that should be displayed on the button.
     */
    public final void updateUndoButton(final boolean disabled, final String tooltip) {
        undoButton.setDisable(disabled);
        undoButton.getTooltip().setText(tooltip + " (" + shortCutKey + "+Z)");
    }

    /**
     * Updates the redo button on the toolbar. Changing whether or not it is disabled and also changing the tooltip.
     * @param disabled Wether or not the button should be disabled.
     * @param tooltip The tooltip that should be displayed on the button.
     */
    public final void updateRedoButton(final boolean disabled, final String tooltip) {
        redoButton.setDisable(disabled);
        redoButton.getTooltip().setText(tooltip + " (" + shortCutKey + "+Y)");
    }

    /**
     * Updates the revert button, depending on whether or not it should be enabled or disabled.
     * @param disabled Whether or not the button should be enabled or disabled.
     */
    public final void updateRevertButton(final boolean disabled) {
        revertButton.setDisable(disabled);
    }

    /**
     * Sets whether or not the remove button is disabled or not.
     * @param disabled Whether or not it is disabled.
     */
    public final void removeButtonDisabled(final boolean disabled) {
        removeButton.setDisable(disabled);
    }

    /**
     * Toggles the toolbar section based on the check menu item that you have clicked on. This hides or shows that
     * section on the toolbar. The code is slightly messy but it does work well.
     * @param event Clicking on a check menu item in the context menu for the toolbar.
     */
    @FXML
    protected final void toolBarToggle(final ActionEvent event) {
        CheckMenuItem menuItem = (CheckMenuItem) event.getSource();
        boolean isFromAppController = menuItem.getParentMenu() != null;
        boolean isChecked = menuItem.isSelected();
        HBox associatedToolBar;
        switch (menuItem.getId()) {
            case "navigation":
                associatedToolBar = navigationToolBar;
                updateCheckMenu("navigation", isFromAppController, isChecked);
                break;
            case "history":
                associatedToolBar = historyToolBar;
                updateCheckMenu("history", isFromAppController, isChecked);
                break;
            case "edit":
                associatedToolBar = editToolBar;
                updateCheckMenu("edit", isFromAppController, isChecked);
                break;
            case "reporting":
                associatedToolBar = reportingToolBar;
                updateCheckMenu("reporting", isFromAppController, isChecked);
                break;
            case "search":
                associatedToolBar = searchToolBar;
                updateCheckMenu("search", isFromAppController, isChecked);
                break;
            default: throw new UnsupportedOperationException("EXPLOSION!!!!!!!!!(unsupported toolbar)");
        }

        boolean showing = !associatedToolBar.visibleProperty().getValue();
        associatedToolBar.setVisible(showing);
        if (showing) {
            associatedToolBar.setPrefWidth(Control.USE_COMPUTED_SIZE);
        }
        else {
            associatedToolBar.setPrefWidth(0);
        }

        updateVisibleSeparators();
    }

    /**
     * Updates the check menu item that didn't fire off the event to toggle sections of the toolbar, so that they
     * are both in sync with each other.
     * @param id The id of the checkMenuItem that needs to be udpated.
     * @param fromAppController Whether or not the event came from the app controller.
     * @param checked Whether or not the menu item that fired the event is checked.
     */
    private void updateCheckMenu(final String id, final boolean fromAppController, final boolean checked) {
        if (!fromAppController) {
            //This is just in case you add a toolbar to a window that doesn't have a view menu that allows you
            //to toggle sections of the toolbar on and off. In all other cases toolBarMenu should not be null.
            if (toolBarMenu == null) {
                return;
            }
            toolBarMenu.getItems()
                    .stream()
                    .filter(menuItem -> menuItem.getId().equals(id))
                    .findFirst()
                    .ifPresent(menuItem1 -> ((CheckMenuItem) menuItem1).setSelected(checked));
        }
        else {
            visibilityContextMenu.getItems()
                    .stream()
                    .filter(menuItem -> menuItem.getId().equals(id))
                    .findFirst()
                    .ifPresent(menuItem1 -> ((CheckMenuItem) menuItem1).setSelected(checked));
        }
    }

    /**
     * Updates the separators that are visible depending on the tool bar sections that are visible.
     */
    private void updateVisibleSeparators() {
        List<Node> toolBarItems = toolBar.getItems();
        int firstVisibleIndex = -1;
        int nextVisibleIndex = -1;

        //If you wish to keep your brain intact ignore the following code and just accept that it works. If you don't
        //care about your sanity or the state of your brain read on for a full look into the stupidity of James.
        //The basic idea is that we go through the items in the toolbar, turning off all the separators (visibly)
        //and recording the indexes of visible HBoxs in the firstVisibleIndex and nextVisibleIndex. Once we've found two
        //visible HBoxs we turn on a separator between them and make the firstVisibleIndex the nextVisibleIndex, the
        //nextVisibleIndex -1 and carry on until we reach the end of the toolbar. Hope you had fun reading and
        //understanding that.
        //Firstly note the amusingly uncommon for loop that uses the <= instead of <. This is because otherwise we don't
        //make the last separator visible after invisibling it.
        for (int i = 0; i <= toolBarItems.size(); ++i) {
            //If we've found two HBoxs that are visible then we'll go in here.
            if (firstVisibleIndex != -1 && nextVisibleIndex != -1) {
                //Index of the separator to make visible again.
                int visibleSeparatorIndex = firstVisibleIndex + 1;
                Separator visibleSeparator = (Separator) toolBarItems.get(visibleSeparatorIndex);
                visibleSeparator.setVisible(true);
                visibleSeparator.setPrefWidth(Control.USE_COMPUTED_SIZE);
                firstVisibleIndex = nextVisibleIndex;
                nextVisibleIndex = -1;
                //"I am necessary evil"
                if (i == toolBarItems.size()) {
                    break;
                }
                //Turn off the separator who's index you're currently on.
                Separator invisibleSeparator = (Separator) toolBarItems.get(i);
                invisibleSeparator.setVisible(false);
                invisibleSeparator.setPrefWidth(0.0);
            }
            else {
                //"I am necessary evil"
                if (i == toolBarItems.size()) {
                    break;
                }
                //Find indexes of visible HBoxes
                if (toolBarItems.get(i) instanceof HBox) {
                    if (toolBarItems.get(i).isVisible()) {
                        if (firstVisibleIndex == -1) {
                            firstVisibleIndex = i;
                        } else {
                            nextVisibleIndex = i;
                        }
                    }
                }
                else {
                    //Turn off the separators
                    Separator invisibleSeparator = (Separator) toolBarItems.get(i);
                    invisibleSeparator.setVisible(false);
                    invisibleSeparator.setPrefWidth(0.0);
                }
            }
        }
    }

    /**
     * Gets the tool bar node.
     * @return The toolBar.
     */
    public final ToolBar getToolBar() {
        return toolBar;
    }

    /**
     * The navigable that the toolbar controls.
     * @return The navigable
     */
    public Navigable getNavigable() {
        return navigable;
    }

    /**
     * Sets the navigable that the toolbar controls.
     * @param navigable The new navigable
     */
    public void setNavigable(final Navigable navigable) {
        this.navigable = navigable;
    }

    /**
     * Gets the controller that is responsible for
     * managing model commands.
     * @return The manager
     */
    public ModelManagable getModelManagable() {
        return modelManagable;
    }

    /**
     * Sets the controller that will recieve create/remove commands
     * from the tool bar.
     * @param modelManagable The controller responsible
     */
    public void setModelManagable(final ModelManagable modelManagable) {
        this.modelManagable = modelManagable;
    }
}
