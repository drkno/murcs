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
     * The text before a match.
     */
    private String contextBefore;

    /**
     * The text after the match.
     */
    private String contextAfter;

    /**
     * The type of model this match occurred on.
     */
    private String modelType;

    /**
     * The name of the field that the match occurred on.
     */
    private String fieldName;

    /**
     * Creates a new object that represents a search result.
     * @param theMatch matched text.
     * @param theContextBefore data that occurred immediately before the match.
     * @param theContextAfter data that occurred immediately after the match.
     */
    public SearchResult(final String theMatch, final String theContextBefore, final String theContextAfter) {
        match = theMatch;
        contextAfter = theContextAfter;
        contextBefore = theContextBefore;
    }

    /**
     * Sets the model that this search result is associated with.
     * @param theModel the model to associate this result with.
     * @param theFieldName the name of the field that the match occurred on.
     */
    public final void setModel(final Model theModel, final String theFieldName) {
        model = theModel;
        modelType = model.getClass().getSimpleName();
        fieldName = theFieldName;
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

    /**
     * Gets the matched text.
     * @return the matched text.
     */
    public final String matched() {
        return match;
    }

    /**
     * Gets the context of the selection before the match.
     * Can be a 0 length string if the match is long.
     * @return the text before the match.
     */
    public final String selectionBefore() {
        return contextBefore;
    }

    /**
     * Gets the context of the selection after the match.
     * Can be a 0 length string if the match is long.
     * @return the text after the match.
     */
    public final String selectionAfter() {
        return contextAfter;
    }

    /**
     * Gets the type of model the match occurred on.
     * @return the model type.
     */
    public final String getModelType() {
        return modelType;
    }

    /**
     * Gets the name of the field that the match occurred on.
     * @return the name of the field.
     */
    public final String getFieldName() {
        return fieldName;
    }
}
