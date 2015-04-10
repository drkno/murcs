package sws.murcs.debug.sampledata;

/**
 * Interface for generating sample data
 */
public interface Generator<T> {
    /**
     * Generates sample data of type T.
     * @return new sample data.
     */
    T generate();
}
