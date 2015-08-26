package sws.murcs.controller.controls.customGrid;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Base64;

/**
 * The main controller for the custom grid pane. This controls opening and setting up the grid pane and also closing it.
 */
public class GridController {

    /**
     * The root for the custom grid pane.
     */
    private CustomGridPane root;

    /**
     * The primary stage for the custom grid pane.
     */
    private static Stage primaryStage;

    /**
     * Title for the grid, has to be like this to avoid problems with serialisation in JavaFx.
     */
    public static final String TITLE_HASHCODE = "MjA0OA==";

    /**
     * The min width of the gird.
     */
    private static final int MIN_WIDTH = 200;

    /**
     * The min height of the grid.
     */
    private static final int MIN_HEIGHT = 400;

    /**
     * Shows the custom grid pane in a new window.
     */
    public final void show() {
        primaryStage = new Stage();
        root = new CustomGridPane();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/sws/murcs/controller/controls/customGrid/grid-styles.css");

        int margin = CustomGridPane.getMargin();

        primaryStage.setTitle(new String(Base64.getDecoder().decode(TITLE_HASHCODE)));
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(MIN_WIDTH / 2d);
        primaryStage.setMinHeight(MIN_HEIGHT / 2d);
        primaryStage.setWidth((MIN_HEIGHT + margin));
        primaryStage.setHeight((MIN_HEIGHT + MIN_WIDTH + margin));
        primaryStage.show();
    }

    /**
     * Closes the custom grid window.
     */
    public final void close() {
        primaryStage.close();
    }

}
