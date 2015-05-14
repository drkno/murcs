package sws.murcs.controller;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import sws.murcs.model.Model;

import java.util.Stack;

public class NavigationManager {

    private static NavigationManager manager;
    private ChoiceBox<ModelTypes> displayChoiceBox;
    private ListView displayList;
    private Stack<Model> backStack;
    private Stack<Model> forwardStack;

    public NavigationManager(ChoiceBox<ModelTypes> displayChoiceBox, ListView displayList, EditorPane editorPane, GridPane contentPane) {
        this.displayChoiceBox = displayChoiceBox;
        this.displayList = displayList;
        backStack = new Stack<>();
        forwardStack = new Stack<>();

        // Set action for the choice box
        displayChoiceBox
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observer, oldValue, newValue) -> updateList());

        // Other stuff
//        displayList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//
//            if (newValue == null) {
//                if (editorPane != null) {
//                    editorPane.dispose();
//                    editorPane = null;
//                    contentPane.getChildren().clear();
//                }
//                return;
//            }
//            if (editorPane == null) {
//                editorPane = new EditorPane((Model) newValue);
//                contentPane.getChildren().clear();
//                contentPane.getChildren().add(editorPane.getView());
//            } else {
//                if (editorPane.getModel().getClass() == newValue.getClass()) {
//                    editorPane.setModel((Model) newValue);
//                } else {
//                    editorPane.dispose();
//                    contentPane.getChildren().clear();
//                    editorPane = new EditorPane((Model) newValue);
//                    contentPane.getChildren().add(editorPane.getView());
//                }
//            }
//        });
    }

    public static NavigationManager instance() {
        return manager;
    }

    public static void construct(ChoiceBox<ModelTypes> displayChoiceBox, ListView displayList, EditorPane editorPane, GridPane contentPane) {
        manager = new NavigationManager(displayChoiceBox, displayList, editorPane, contentPane);
    }

    public void goTo(Model model) {

    }

    public void goBack() {

    }

    public void goForward() {

    }

    private void updateList() {

    }
}
