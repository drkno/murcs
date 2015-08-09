package sws.murcs.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sws.murcs.model.Organisation;
import sws.murcs.model.Release;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.search.tokens.Token;

public class SearchHandler {
    private SearchThread[] searchThreads;
    private ObservableList<SearchResult> results;

    public SearchHandler() {
        Organisation organisation = PersistenceManager.getCurrent().getCurrentModel();
        results = FXCollections.observableArrayList();
        searchThreads = new SearchThread[] {
                new SearchThread<Release>(results, organisation.getReleases()),
                new SearchThread(results, organisation.getStories()),
                new SearchThread(results, organisation.getProjects()),
                new SearchThread(results, organisation.getBacklogs()),
                new SearchThread(results, organisation.getSkills()),
                new SearchThread(results, organisation.getTeams()),
                new SearchThread(results, organisation.getPeople())
        };
    }

    /**
     * Cancels any existing searches an begins a search for the provided query.
     * This will result in any existing results being cleared from the provided
     * results list.
     * @param query the query to search for.
     */
    public final void searchFor(final String query) {
        // abort current search
        for (SearchThread thread : searchThreads) {
            thread.stop();
        }
        results.clear();

        // parse query
        Token token = Token.parse(query);

        if (token.isEmpty()) {
            // nothing to search for
            return;
        }

        // begin new search
        for (SearchThread thread : searchThreads) {
            thread.start(token);
        }
    }

    /**
     * Gets all results currently found from the search.
     * By default these results will be in order that they are found.
     * NOTE: This list is mutable so that it can be manipulated if
     * required for sort order or other purposes however will not
     * affect the ongoing search if it is changed.
     * @return results found from the current search.
     */
    public final ObservableList<SearchResult> getResults() {
        return results;
    }
}
