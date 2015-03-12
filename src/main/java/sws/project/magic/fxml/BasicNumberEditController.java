package sws.project.magic.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.DoubleSummaryStatistics;
import java.util.ResourceBundle;

/**
 *
 */
public class BasicNumberEditController extends BasicEditController<Number> implements Initializable {
    @FXML private Text titleText;
    @FXML private TextField numberText;

    private Number value;

    @Override
    public void setTitle(String text) {
        titleText.setText(text);
    }

    @Override
    public void setValue(Number value) {
        numberText.setText(value.toString());
    }

    @Override
    public Class[] supportedTypes() {
        return new Class[]{Number.class, Integer.class, int.class, Float.class, float.class, Double.class, double.class};
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        numberText.textProperty().addListener((p, o, n) -> convert());
    }

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
