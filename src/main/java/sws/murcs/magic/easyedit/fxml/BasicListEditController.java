package sws.murcs.magic.easyedit.fxml;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * Overrides the methods for creating and using an list view.
 */
public class BasicListEditController extends BasicEditController<Collection> implements Initializable {
    @FXML private Text titleText;
    @FXML private ListView listView;

    private ObservableList observableList;
    private Collection collection;

    @Override
    public void setTitle(String title) {
        if (this.titleText != null)
            this.titleText.setText(title);
    }

    @Override
    public final void setValue(final Collection value) {
        observableList.clear();
        observableList.addAll(value);

        this.collection = value;
    }

    @Override
    public final Class[] supportedTypes() {
        return new Class[]{Collection.class, ArrayList.class};
    }

    @Override
    public final void initialize(final URL location, final ResourceBundle resources) {
        observableList = FXCollections.observableArrayList();

        listView.setItems(observableList);
        listView.setCellFactory(new Callback<ListView<Object>, ListCell<Object>>() {
            @Override
            public ListCell call(final ListView<Object> param) {
                ListCell<Object> cell = new ListCell<Object>() {

                    @Override
                    protected void updateItem(final Object t, final boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.toString());
                        }
                    }
                };
                return cell;
            }
        });
        observableList.addListener((ListChangeListener.Change c) -> {
            if (collection  == null) return;
            collection.addAll(c.getAddedSubList());
            collection.removeAll(c.getRemoved());
        });
    }
}
