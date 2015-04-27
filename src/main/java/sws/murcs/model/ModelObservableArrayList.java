package sws.murcs.model;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.Observable;
import java.util.ArrayList;
import java.util.Collection;

public class ModelObservableArrayList<T extends Model> extends ObservableListWrapper<T> {
    public ModelObservableArrayList() {
        super(new ArrayList<>(), param -> new Observable[] {param.getToStringProperty()});
    }

    public ModelObservableArrayList(Collection c) {
        super(new ArrayList<>(c), param -> new Observable[] {param.getToStringProperty()});
    }
}
