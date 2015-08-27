package sws.murcs.unit.model;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.EstimateInfo;

import static org.junit.Assert.assertEquals;

public class EstimateInfoTest {
    EstimateInfo estimate;

    @Before
    public void setUp() {
        UndoRedoManager.setDisabled(true);

        estimate = new EstimateInfo();
        estimate.setEstimateForDay(1, LocalDate.of(2001, 1, 1));
    }

    @Test
    public void testAddToEstimate() throws Exception {
        estimate.addToEstimateForDay(1, LocalDate.of(2001, 1, 1));
        assertEquals(2, estimate.getEstimateForDay(LocalDate.of(2001, 1, 1)), 0.1);

        estimate.addToEstimateForDay(1, LocalDate.of(2000, 1, 1));
        assertEquals(1, estimate.getEstimateForDay(LocalDate.of(2000, 1, 1)), 0.1);
        assertEquals(3, estimate.getEstimateForDay(LocalDate.of(2001, 1, 1)), 0.1);
    }

    @Test
    public void setEstimateTest() throws Exception {
        estimate.setCurrentEstimate(5);
        assertEquals(5, estimate.getCurrentEstimate(), 0.1);
    }

    @Test
    public void setPreviousEstimate() {
        LocalDate date = LocalDate.of(2000, 1, 1);

        //The current estimate should be 1 (as the estimate in 2001 is 1 and today is after 2001)
        assertEquals(1, estimate.getEstimateForDay(LocalDate.of(2001, 1, 1)), 0.1);
        assertEquals(1, estimate.getCurrentEstimate(), 0.1);

        //Should add ten to all estimates Jan 1, 2000
        estimate.setEstimateForDay(10, date);

        assertEquals(10f, estimate.getEstimateForDay(date), 0.1);
        assertEquals(11f, estimate.getEstimateForDay(LocalDate.of(2001, 1, 1)), 0.1);
        assertEquals(11f, estimate.getCurrentEstimate(), 0.1);

        //Shouldn't change anything apart from our estimate for today
        estimate.setCurrentEstimate(1);
        assertEquals(10, estimate.getEstimateForDay(date), 0.1);
        assertEquals(11, estimate.getEstimateForDay(LocalDate.of(2001, 1, 1)), 0.1);
        assertEquals(1, estimate.getCurrentEstimate(), 0.1);

        estimate.setEstimateForDay(6, LocalDate.of(2003, 1, 1));
        assertEquals(6, estimate.getEstimateForDay(LocalDate.of(2003, 1, 1)), 0.1);

        //Set this to six, meaning we should subtract (6 - 11) from all estimates after this one
        estimate.setEstimateForDay(6, LocalDate.of(2002, 1, 1));

        assertEquals(6, estimate.getEstimateForDay(LocalDate.of(2002, 1, 1)), 0.1);
        assertEquals(1, estimate.getEstimateForDay(LocalDate.of(2003, 1, 1)), 0.1);

        //This should be 0, as we should never have negative estimates
        assertEquals(0, estimate.getCurrentEstimate(), 0.1);
    }

    @Test
    public void testMerge() throws Exception {
        int year = 2000;
        int month = 1;

        LocalDate[] dates = new LocalDate[6];
        dates[0] = LocalDate.of(year, month, 1);
        dates[1] = LocalDate.of(year, month, 2);
        dates[2] = LocalDate.of(year, month, 3);
        dates[3] = LocalDate.of(year, month, 4);
        dates[4] = LocalDate.of(year, month, 5);
        dates[5] = LocalDate.of(year, month, 6);

        EstimateInfo first = new EstimateInfo();
        first.setEstimateForDay(5, dates[0]);
        first.setEstimateForDay(7, dates[1]);
        first.setEstimateForDay(9, dates[4]);

        EstimateInfo second = new EstimateInfo();
        second.setEstimateForDay(7, dates[2]);
        second.setEstimateForDay(6, dates[3]);
        second.setEstimateForDay(8, dates[4]);
        second.setEstimateForDay(3, dates[5]);

        EstimateInfo firstMerged = new EstimateInfo();
        firstMerged.mergeIn(first, second);

        assertEquals(5, firstMerged.getEstimateForDay(dates[0]), 0.1);
        assertEquals(7, firstMerged.getEstimateForDay(dates[1]), 0.1);
        assertEquals(14, firstMerged.getEstimateForDay(dates[2]), 0.1);
        assertEquals(13, firstMerged.getEstimateForDay(dates[3]), 0.1);
        assertEquals(17, firstMerged.getEstimateForDay(dates[4]), 0.1);
        assertEquals(12, firstMerged.getEstimateForDay(dates[5]), 0.1);

        //Test merging statically works
        EstimateInfo secondMerge = EstimateInfo.merge(first, second);
        assertEquals(firstMerged, secondMerge);

        //Test merging into an existing TimeEstimate works
        second.mergeIn(first);
        assertEquals(firstMerged, second);
    }
}