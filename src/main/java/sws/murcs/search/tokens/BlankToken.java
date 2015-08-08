package sws.murcs.search.tokens;

import sws.murcs.search.SearchResult;

/**
 * A token that represents an empty query.
 */
public class BlankToken extends Token {
    @Override
    public final SearchResult matches(final String query) {
        return null;
    }

    @Override
    public final boolean isEmpty() {
        return true;
    }
}
