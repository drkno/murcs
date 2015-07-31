package sws.murcs.search.tokens;

/**
 * Represents a single piece of a search (e.g. "Foo")
 */
public class SearchToken extends Token {

    /**
     * The term to search for
     */
    private String searchTerm;

    /**
     * Creates a new search token with the specified query
     * @param theSearchTerm The term to search for
     */
    public SearchToken(final String theSearchTerm) {
        searchTerm = theSearchTerm;
    }

    @Override
    public boolean matches(final String input) {
        return input.contains(searchTerm);
    }
}
