package sws.murcs.search;

import edu.emory.mathcs.backport.java.util.Collections;
import javafx.collections.ObservableList;
import sws.murcs.model.Model;
import java.util.Set;

public class SearchHandler {
    private Set<ObservableList<SearchResult<? extends Model>>> results;
    private boolean threadIsStarted;
    private Thread searchThread;

    public Set<ObservableList<SearchResult<? extends Model>>> getResults() {
        return (Set<ObservableList<SearchResult<? extends Model>>>) Collections.unmodifiableSet(results);
    }

    public SearchHandler(String searchCriteria) {

    }

    private void performSearch() {
        
    }

    public void startSearch() {
        if (threadIsStarted) {
            return;
        }
        searchThread = new Thread(this::performSearch);
        threadIsStarted = true;
        searchThread.start();
    }

    public void stopSearch() {
        if (!threadIsStarted) {
            return;
        }
        threadIsStarted = false;
        try {
            searchThread.join();
        }
        catch (InterruptedException e) {
            // Unlikely to happen and should not be reported as an error.
            // This is because the outcome is what we wanted, there was
            // just a short race that we join() ed on the wrong side of.
        }
    }
}
