package sws.murcs.unit.model;

import org.junit.Test;
import sws.murcs.model.EstimateType;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by james_000 on 29/06/2015.
 */
public class EstimateTypeTest {

    @Test
    public void testGetEstimates() throws Exception {
        List<String> estimates = EstimateType.Fibonacci.getEstimates();

        assertEquals("1", estimates.get(0));
        assertEquals(6, estimates.size());

        estimates = EstimateType.MovieClassification.getEstimates();
        assertEquals("M", estimates.get(2));
        assertEquals(6, estimates.size());

        estimates = EstimateType.ShirtSize.getEstimates();
        assertEquals("American", estimates.get(5));
    }

    @Test
    public void testConvert() throws Exception {
        assertEquals("1", EstimateType.ShirtSize.convert(EstimateType.Fibonacci, "XS"));
        assertEquals("American", EstimateType.MovieClassification.convert(EstimateType.ShirtSize, "Banned"));

        assertEquals("M", EstimateType.ShirtSize.convert(EstimateType.ShirtSize, "M"));
        assertEquals("Not Estimated", EstimateType.ShirtSize.convert(EstimateType.Fibonacci, "Not Estimated"));
        assertEquals("1", EstimateType.ShirtSize.convert(EstimateType.Fibonacci, "XS"));
    }
}