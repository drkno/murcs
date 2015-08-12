package sws.murcs.unit.controller.windowmanagement;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.controller.windowManagement.Shortcut;
import sws.murcs.controller.windowManagement.ShortcutManager;
import sws.murcs.controller.windowManagement.Window;
import sws.murcs.view.App;

/**
 *
 */
public class ShortcutManagerTest {
    private ShortcutManager shortcutManager;

    @Before
    public void setup() {
        shortcutManager = new ShortcutManager();
    }

    @Test
    public void testAddShortcut(){
        KeyCodeCombination shortcut = new KeyCodeCombination(KeyCode.D,KeyCodeCombination.SHORTCUT_DOWN);
        boolean[] tests = new boolean[1];
        tests[0] = false;

        shortcutManager.registerShortcut(shortcut, () -> {
            tests[0] = true;
        });

        Stage s = new Stage();
        Window window = new Window(s, null);

        shortcutManager.addAllShortcutsToWindow(window);
    }
}
