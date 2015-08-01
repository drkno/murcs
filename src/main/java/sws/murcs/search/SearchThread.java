package sws.murcs.search;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sws.murcs.model.Model;
import sws.murcs.search.tokens.Token;

import java.util.Collection;

public class SearchThread<T extends Model> {
    private ObservableList<SearchResult<T>> searchResults;
    private Thread searchThread;
    private boolean shouldSearch;
    private Token searchValidator;
    private Collection<T>[] collections;

    public SearchThread() {
        searchResults = FXCollections.observableArrayList();
        shouldSearch = false;
        searchThread = new Thread(this::performSearch);
    }

    public ObservableList<SearchResult<T>> getSearchResults() {
        return searchResults;
    }

    public final void start(final Token theSearchValidator, final Collection<T>... searchableCollections) {
        if (searchThread != null && searchThread.isAlive()) {
            stop();
        }
        searchValidator = theSearchValidator;
        collections = searchableCollections;
        shouldSearch = true;
        searchThread.start();
    }

    public final void stop() {
        try {
            shouldSearch = false;
            searchThread.join();
            searchValidator = null;
            collections = null;
        } catch (InterruptedException e) {
            /*
                Thrown cause Java. Should not be reported cause if an exception is thrown
                what we wanted to happen was achieved anyway just under less than normal
                circumstances.
             */
        }
    }

    private void performSearch() {
        for (Collection<T> collection : collections) {
            for (T model : collection) {
                if (searchValidator.matches(model.getShortName())
                        || searchValidator.matches(model.getLongName())
                        || searchValidator.matches(model.getDescription())) {
                    searchResults.add(new SearchResult<>(model));
                }
            }
        }
    }
}
