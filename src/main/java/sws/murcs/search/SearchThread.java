package sws.murcs.search;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sws.murcs.model.Model;
import sws.murcs.search.tokens.Token;

import java.util.Collection;

public class SearchThread<T extends Model> {
    private Thread searchThread;
    private boolean shouldSearch;
    private Token searchValidator;

    private Collection<T>[] collections;
    private ObservableList<SearchResult> searchResults;

    public SearchThread(final ObservableList<SearchResult> list, final Collection<T>... searchableCollections) {
        searchResults = list;
        collections = searchableCollections;
        shouldSearch = false;
    }

    public final void start(final Token theSearchValidator) {
        if (searchThread != null && searchThread.isAlive()) {
            stop();
        }
        searchValidator = theSearchValidator;
        shouldSearch = true;
        try {
            searchThread = new Thread(this::performSearch);
            searchThread.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public final void stop() {
        try {
            if (searchThread != null) {
                shouldSearch = false;
                searchThread.join();
                searchValidator = null;
            }
        } catch (InterruptedException e) {
            /*
                Thrown cause Java. Should not be reported cause if an exception is thrown
                what we wanted to happen was achieved anyway just under less than normal
                circumstances.
             */
        }
    }

    private void performSearch() {
        for (Collection<? extends Model> collection : collections) {
            for (Model model : collection) {
                if (searchValidator.matches(model.getShortName())
                        || searchValidator.matches(model.getLongName())
                        || searchValidator.matches(model.getDescription())) {
                    Platform.runLater(() -> {
                        searchResults.add(new SearchResult<>(model, ""));
                    });

                }
            }
        }
    }
}
