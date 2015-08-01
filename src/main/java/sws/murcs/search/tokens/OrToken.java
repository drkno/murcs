package sws.murcs.search.tokens;

import java.util.Collection;
import java.util.LinkedList;

/**
 * An object representing the logical or operation
 * on a search
 */
public class OrToken extends Token {

    /**
     * A collection of tokens to be "ored" together
     */
    private Collection<Token> tokenCollection;

    /**
     * Creates a new Or token
     */
    public OrToken() {
        tokenCollection = new LinkedList<>();
    }

    /**
     * Adds a token to be orred
     * @param token The token to add
     */
    public void addToken(Token token) {
        tokenCollection.add(token);
    }

    @Override
    public boolean matches(final String query) {
        return tokenCollection.stream().anyMatch(token -> token.matches(query));
    }
}
