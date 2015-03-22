package sws.murcs.magic.easyedit.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A basic controller for editing numbers. You can extend this or implement your own.
 * Any FXML associated with this controller should specify a 'titleText' Text object, a
 * 'numberText' TextField.
 */
public class BasicNumberEditController extends BasicEditController<Number> implements Initializable {
    @FXML private Text titleText;
    @FXML private TextField numberText;

    private Number value;
    private Number oldValue;

    @Override
    public void setTitle(String text) {
        titleText.setText(text);
    }

    @Override
    public void setValue(Number value) {
        numberText.setText(value.toString());
        oldValue = value;
    }

    @Override
    public Class[] supportedTypes() {
        return new Class[]{Number.class, Integer.class, int.class, Float.class, float.class, Double.class, double.class};
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        numberText.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!(oldValue && !newValue)) return;
            notifyChanged(null, this.oldValue, value);
            this.oldValue = value;
        });
    }

    /**
     * Trys to convert the text in 'numberText' to a number, in the following order
     * Integer, Float, Double and notifies any listeners that it has changed
     */
    private void convert(){
        Number value;
        try{
            value = Integer.parseInt(numberText.getText());
        }catch (Exception e1){
            try {
                value = Float.parseFloat(numberText.getText());
            }catch (Exception e2){
                try {
                    value = Double.parseDouble(numberText.getText());
                }catch (Exception e3){
                    showInvalid();
                    return;
                }
            }
        }

        notifyChanged(null, this.value, value);
        this.value = value;
    }
}
