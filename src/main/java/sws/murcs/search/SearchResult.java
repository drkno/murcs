package sws.murcs.search;

import sws.murcs.model.Model;

/**
 * Object that represents a SearchResult.
 */
public class SearchResult {

    /**
     * Model object that this search result points to.
     */
    private Model model;

    /**
     * Match that was found.
     */
    private String match;

    /**
     * Creates a new object that represents a search result.
     * @param theMatch matched text.
     */
    public SearchResult(final String theMatch) {
        match = theMatch;
    }

    /**
     * Sets the model that this search result is associated with.
     * @param theModel the model to associate this result with.
     */
    public final void setModel(final Model theModel) {
        model = theModel;
    }

    /**
     * Gets the model that this result was found on.
     * @return the model.
     */
    public final Model getModel() {
        return model;
    }

    @Override
    public final String toString() {
        return match;
    }
}
