package sws.murcs.debug.sampledata;

/**
 * Interface for generating sample data.
 * @param <T> The model type of the generator.
 */
public interface Generator<T> {

    /**
     * Generates sample data of type T.
     * @return new sample data.
     */
    T generate();
}
