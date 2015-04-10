package sws.murcs.debug.sampledata;

/**
 * Interface for generating sample data
 */
public interface Generator<T> {
    T generate();
}
