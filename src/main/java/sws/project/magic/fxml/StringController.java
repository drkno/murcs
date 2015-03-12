package sws.project.magic.fxml;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 *
 */
public class StringController implements EditFormController<String>, Initializable {
    private ArrayList<Predicate<String>> validators = new ArrayList<>();
    private ArrayList<ChangeListener<String>> changeListeners = new ArrayList<>();

    @FXML
    private Text title;

    @FXML
    private TextArea textBox;

    @Override
    public void setTitle(String title){
        this.title.setText(title);
    }

    @Override
    public void addValidator(Predicate<String> predicate) {
        validators.add(predicate);
    }

    @Override
    public void addChangeListener(ChangeListener<String> listener) {
        changeListeners.add(listener);
    }

    private void onChange(ObservableValue<? extends String> property, String oldValue, String newValue){
        for (Predicate<String> predicate : validators){
            if (!predicate.test(newValue)) {
                showInvalid();
                return;
            }
        }

        for (ChangeListener<String> listener : changeListeners){
            listener.changed(property, oldValue, newValue);
        }
    }

    private void showInvalid(){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textBox.textProperty().addListener((p, o, n) -> onChange(p, o, n));
    }
}
