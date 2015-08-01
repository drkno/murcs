package sws.murcs.search.tokens;

import java.util.Collection;
import java.util.LinkedList;

/**
 * An object representing the "&&" connection
 * in a search
 */
public class AndToken extends Token {
    /**
     * A collection of tokens to and together
     */
    private Collection<Token> tokenCollection;

    /**
     * Creates a new and token
     */
    public AndToken() {
        tokenCollection = new LinkedList<>();
    }

    /**
     * Adds a new token to be anded
     * @param token The token to and
     */
    public void addToken(Token token) {
        tokenCollection.add(token);
    }

    @Override
    public boolean matches(final String query) {
        return tokenCollection.stream().allMatch(token -> token.matches(query));
    }
}
