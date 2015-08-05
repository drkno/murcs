package sws.murcs.search;

import edu.emory.mathcs.backport.java.util.Collections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sws.murcs.model.Model;
import sws.murcs.model.Organisation;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.search.tokens.Token;

import java.util.Collection;
import java.util.Set;

public class SearchHandler {
    private SearchThread[] searchThreads;
    private ObservableList<SearchResult> results;

    public SearchHandler() {
        Organisation organisation = PersistenceManager.getCurrent().getCurrentModel();
        results = FXCollections.observableArrayList();
        searchThreads = new SearchThread[] {
                new SearchThread(results, organisation.getReleases()),
                new SearchThread(results, organisation.getStories()),
                new SearchThread(results, organisation.getProjects()),
                new SearchThread(results, organisation.getBacklogs()),
                new SearchThread(results, organisation.getSkills()),
                new SearchThread(results, organisation.getTeams()),
                new SearchThread(results, organisation.getPeople())
        };
    }

    public final void searchFor(final String text) {
        for (SearchThread thread : searchThreads) {
            thread.stop();
        }
        results.clear();
        if (text == null || text.trim().length() == 0) {
            return;
        }

        Token token = Token.parse(text);
        for (SearchThread thread : searchThreads) {
            thread.start(token);
        }
    }

    public ObservableList<SearchResult> getResults() {
        return results;
    }
}
