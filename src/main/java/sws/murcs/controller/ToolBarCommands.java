package sws.murcs.controller;

import javafx.event.ActionEvent;

public interface ToolBarCommands {

    void back(ActionEvent event);

    void forward(ActionEvent event);

    void undo(ActionEvent event);

    void redo(ActionEvent event);

    void revert(ActionEvent event);

    void add(ActionEvent event);

    void generateReport(ActionEvent event);

    void reportBug();

    boolean save(ActionEvent event);

    boolean saveAs(ActionEvent event);

    void open(ActionEvent event);

}
