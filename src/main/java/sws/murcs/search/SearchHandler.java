package sws.murcs.search;

import edu.emory.mathcs.backport.java.util.Collections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sws.murcs.model.Model;
import sws.murcs.model.Organisation;

import java.util.Collection;
import java.util.Set;

public class SearchHandler {


    private <T extends Model> ObservableList<SearchResult> performSearch(final Collection<T>... collections) {
        ObservableList<SearchResult> searchResults = FXCollections.observableArrayList();
    }
}
