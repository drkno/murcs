package sws.murcs.search.tokens;

/**
 * A special token event handler to allow setting or
 * disabling of search features. Eg, this might be used
 * with !regex to enable regex.
 */
public interface SpecialTokenEvent {
    /**
     * Sets the value of the special token.
     * @param newValue enable or disable the feature of this token.
     */
    void setValue(boolean newValue);
}
