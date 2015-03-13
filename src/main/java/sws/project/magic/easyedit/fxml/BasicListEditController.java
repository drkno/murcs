package sws.project.magic.easyedit.fxml;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

/**
 *
 */
public class BasicListEditController extends BasicEditController<Collection> implements Initializable {
    @FXML private Text titleText;
    @FXML private ListView listView;

    private ObservableList collection;

    @Override
    public void setTitle(String title) {
        if (this.titleText != null)
            this.titleText.setText(title);
    }

    @Override
    public void setValue(Collection value) {
        collection.clear();
        collection.addAll(value);
    }

    @Override
    public Class[] supportedTypes() {
        return new Class[]{Collection.class};
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        collection = FXCollections.observableArrayList();

        listView.setItems(collection);
        listView.setCellFactory(new Callback<ListView<Object>, ListCell<Object>>() {
            @Override
            public ListCell call(ListView<Object> param) {
                ListCell<Object> cell = new ListCell<Object>() {

                    @Override
                    protected void updateItem(Object t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.toString());
                        }
                    }
                };
                return cell;
            }
        });
    }
}
