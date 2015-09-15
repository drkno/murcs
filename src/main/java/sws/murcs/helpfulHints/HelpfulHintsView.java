package sws.murcs.helpfulHints;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.debug.sampledata.GenerationHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by wooll on 15/09/2015.
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
    private String oldHint;


    public HelpfulHintsView() {
        hints = new ArrayList<>();
        loadHintsFromFile();
    }

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

    private void loadHintsFromFile() {
        String path = "/sws/murcs/helpfulHints/helpfulHints.nsv";
        try {
            InputStream input = getClass().getResourceAsStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(input));

            String line = br.readLine();
            while (line != null) {
                hints.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            //This will never happen
            ErrorReporter.get().reportError(e, "No such file as " + path);
        }
    }

    public Parent getView() {
        return view;
    }

    private String generateHint() {
        return hints.get(GenerationHelper.random(0, hints.size() - 1));
    }

    /**
     * Gets a random hint from the list.
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

    public void showHints() {
        controller.play();
    }

    public void hide() {
        controller.hide();
    }
}
