package sws.murcs.search;

import sws.murcs.model.Model;

public class SearchResult<T extends Model> {
    public enum MatchStrength {
        Strong,
        Weak
    }

    private T model;
    private String match;
    private MatchStrength strength;

    public SearchResult(T matchedModel, String match) {

    }
}
