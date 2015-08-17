package sws.murcs.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import sws.murcs.model.ModelType;
import sws.murcs.model.Organisation;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.search.tokens.Token;

import java.util.Collection;

/**
 * Object to handle the performing of searches.
 * This includes creating and handling search threads and
 * collating search results into a single useful collection.
 */
public  class SearchHandler {
    /**
     * Threads on which to perform searching.
     */
    private SearchThread[] searchThreads;

    /**
     * Results found from the search.
     */
    private ObservableList<SearchResult> results;

    /**
     * Creates a new search handler.
     */
    public SearchHandler() {
        Organisation organisation = PersistenceManager.getCurrent().getCurrentModel();
        results = FXCollections.observableArrayList();
        searchThreads = new SearchThread[] {
                new SearchThread<>(results, ModelType.Backlog, organisation.getBacklogs()),
                new SearchThread<>(results, ModelType.Person, organisation.getPeople()),
                new SearchThread<>(results, ModelType.Project, organisation.getProjects()),
                new SearchThread<>(results, ModelType.Release, organisation.getReleases()),
                new SearchThread<>(results, ModelType.Skill, organisation.getSkills()),
                new SearchThread<>(results, ModelType.Story, organisation.getStories()),
                new SearchThread<>(results, ModelType.Team, organisation.getTeams())
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
        Collection<ModelType> types = Token.getSearchTypes();
        for (SearchThread thread : searchThreads) {
            if (types.size() != 0 && !types.contains(thread.getSearchType())) {
                continue;
            }
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
        return new SortedList<>(results, SearchResult.getComparator());
    }
}
