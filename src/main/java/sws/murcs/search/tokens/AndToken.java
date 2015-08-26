package sws.murcs.search.tokens;

import sws.murcs.search.SearchResult;

import java.util.Collection;
import java.util.LinkedList;

/**
 * An object representing the "&amp;&amp;" connection
 * in a search.
 */
public class AndToken extends Token {

    /**
     * A collection of tokens to and together.
     */
    private Collection<Token> tokenCollection;

    /**
     * Creates a new and token.
     */
    public AndToken() {
        tokenCollection = new LinkedList<>();
    }

    /**
     * Adds a new token to be and-ed.
     * @param token The token to and.
     */
    public final void addToken(final Token token) {
        tokenCollection.add(token);
    }

    @Override
    public final SearchResult matches(final String query) {
        SearchResult first = null;
        for (Token token : tokenCollection) {
            SearchResult searchResult = token.matches(query);
            if (searchResult == null) {
                return null;
            }

            if (first == null) {
                first = searchResult;
            }
            else {
                first.addMatch(searchResult, query);
            }
        }
        return first;
    }
}
