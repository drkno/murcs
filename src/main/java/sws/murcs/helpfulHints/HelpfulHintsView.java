package sws.murcs.helpfulHints;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.debug.sampledata.GenerationHelper;
import sws.murcs.model.persistence.PersistenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The helpful hints view to be loaded into the main app view.
 */
public class HelpfulHintsView {

    /**
     * The controller for the helpful hints.
     */
    private HelpfulHintsController controller;

    /**
     * The helpfulHints view view.
     */
    private Parent view;

    /**
     * List of hints loaded into view.
     */
    private List<String> hints;

    /**
     * The last hint that was displayed.
     */
    private String oldHint;

    /**
     * The path to the hints file to load from.
     */
    private final String path = "/sws/murcs/helpfulHints/helpfulHints.nsv";

    /**
     * Sets up the helpful hints view.
     * And loads all hints from the helpful hints file.
     */
    public HelpfulHintsView() {
        hints = new ArrayList<>();
        loadHintsFromFile(path);
    }

    /**
     * Creates an instance of the controller and initializes controller variables.
     */
    public void create() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/murcs/HelpfulHints/HelpfulHints.fxml"));
        try {
            view = loader.load();
            controller = loader.getController();
            controller.setModel(this);
            controller.setup();
        } catch (IOException e) {
            ErrorReporter.get().reportError(e, "Failed to load helpful hints view");
        }
    }

    /**
     * Loads hints from a file.
     * In debug mode it will also add hints annotated with "@" at the start of the hint.
     * @param path The path to load the file from
     */
    private void loadHintsFromFile(final String path) {
        try {
            InputStream input = getClass().getResourceAsStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            boolean isInDebugMode = PersistenceManager.getCurrent().getCurrentModel().isUsingGeneratedData();
            String line = br.readLine();
            while (line != null) {
                if (line.startsWith("@")) {
                    if (isInDebugMode) {
                        hints.add(line.substring(1));
                    }
                }
                else {
                    hints.add(line);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            //This will never happen
            ErrorReporter.get().reportError(e, "No such file as " + path);
        }
    }

    /**
     * Gets the view creating from loading the fxml.
     * @return The view.
     */
    public Parent getView() {
        return view;
    }

    /**
     * Generates a random hint from the array of hints.
     * @return A hint.
     */
    private String generateHint() {
        return hints.get(GenerationHelper.random(0, hints.size() - 1));
    }

    /**
     * Gets a random hint from the list.
     * If the last hint is the same as the new hint generated then continue to request a hint until a new one is found.
     * @return A hint, hopefully helpful ;)
     */
    protected String getHint() {
        String newHint = generateHint();
        if (oldHint != null && Objects.equals(newHint, oldHint)) {
            return getHint();
        }
        oldHint = newHint;
        return newHint;
    }

    /**
     * Shows the hints.
     */
    public void showHints() {
        controller.play();
    }

    /**
     * Hides hints.
     */
    public void hide() {
        controller.hide();
    }
}
