package sws.murcs.debug.sampledata;

/**
 * Interface for generating sample data
 */
public interface Generator<T> {
    public enum Stress{
        High,
        Medium,
        Low,
    }

    /**
     * Generates sample data of type T.
     * @param stress The stress of the generated Model
     * @return new sample data.
     */
    T generate(Stress stress);

    /**
     * Generates sample data of type T.
     * @return new sample data
     */
    //T generate();
}
