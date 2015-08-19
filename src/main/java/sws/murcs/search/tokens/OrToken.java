package sws.murcs.search.tokens;

import sws.murcs.search.SearchResult;

import java.util.Collection;
import java.util.LinkedList;

/**
 * An object representing the logical or operation
 * on a search.
 */
public class OrToken extends Token {

    /**
     * A collection of tokens to be "ored" together.
     */
    private Collection<Token> tokenCollection;

    /**
     * Creates a new Or token.
     */
    public OrToken() {
        tokenCollection = new LinkedList<>();
    }

    /**
     * Adds a token to be orred.
     * @param token The token to add.
     */
    public final void addToken(final Token token) {
        tokenCollection.add(token);
    }

    @Override
    public final SearchResult matches(final String query) {
        SearchResult searchResults = null;
        for (Token token : tokenCollection) {
            SearchResult searchResult = token.matches(query);
            if (searchResult != null) {
                if (searchResults == null) {
                    searchResults = searchResult;
                }
                else {
                    searchResults.addMatch(searchResult, query);
                }
            }
        }
        return searchResults;
    }
}
