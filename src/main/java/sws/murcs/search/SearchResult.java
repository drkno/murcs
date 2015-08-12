package sws.murcs.search;

import sws.murcs.model.Model;

import java.util.*;

/**
 * Object that represents a SearchResult.
 */
public class SearchResult {

    /**
     * Model object that this search result points to.
     */
    private Model model;

    /**
     * Matches that were found.
     */
    private List<String> matches;

    /**
     * Collection of start indexes for a match.
     */
    private List<Integer> matchIndexes;

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
     * @param theMatch initial matched text.
     * @param startIndex the index that this match started at.
     * @param theContextBefore data that occurred immediately before the match.
     * @param theContextAfter data that occurred immediately after the match.
     */
    public SearchResult(final String theMatch, final int startIndex,
                        final String theContextBefore, final String theContextAfter) {
        matches = new ArrayList<>();
        matches.add(theMatch);
        matchIndexes = new ArrayList<>();
        matchIndexes.add(startIndex);
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
        return String.join(", ", matches);
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

    /**
     * Gets all the matches found and surrounded substrings.
     * Every second match is a string that is in-between a match.
     * @return the matches found and surrounded substrings.
     */
    public final List<String> getMatches() {
        return Collections.unmodifiableList(matches);
    }

    /**
     * Combines multiple search matches into one.
     * @param searchResult the search result to combine with this one.
     * @param query the query associated with this search result.
     */
    public final void addMatch(final SearchResult searchResult, final String query) {
        List<Map.Entry<Integer, String>> entries = new ArrayList<>();

        for (int i = 0; i < matches.size(); i += 2) {
            Map.Entry<Integer, String> kvp = new AbstractMap.SimpleEntry<>(matchIndexes.get(i), matches.get(i));
            entries.add(kvp);
        }

        for (int j = 0; j < searchResult.matches.size(); j += 2) {
            Map.Entry<Integer, String> kvp = new AbstractMap.SimpleEntry<>(
                    searchResult.matchIndexes.get(j), searchResult.matches.get(j));
            entries.add(kvp);
        }

        entries.sort((o1, o2) -> {
            int k = o1.getKey().compareTo(o2.getKey());
            if (k == 0) {
                return -Integer.compare(o1.getValue().length(), o2.getValue().length());
            }
            return k;
        });

        List<String> newMatches = new ArrayList<>();
        List<Integer> newIndexes = new ArrayList<>();

        int currIndex = 0;
        for (int i = 0; i < entries.size(); i++) {
            int index = entries.get(i).getKey();
            currIndex = index;
            while (currIndex <= query.length() && i < entries.size() && currIndex >= entries.get(i).getKey()) {
                Map.Entry<Integer, String> entry = entries.get(i);
                int change = entry.getKey() + entry.getValue().length();
                if (currIndex < change) {
                    currIndex = change;
                }
                i++;
            }
            i--;

            newMatches.add(query.substring(index, currIndex));
            newIndexes.add(index);

            if (i + 1 < entries.size()) {
                newMatches.add(query.substring(currIndex, entries.get(i + 1).getKey()));
                newIndexes.add(currIndex);
            }
        }

        matches = newMatches;
        matchIndexes = newIndexes;
    }
}
