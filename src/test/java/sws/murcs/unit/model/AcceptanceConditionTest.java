package sws.murcs.unit.model;

import org.junit.Test;
import sws.murcs.model.AcceptanceCondition;

import static org.junit.Assert.assertEquals;

public class AcceptanceConditionTest {

    @Test
    public void testModifyCondition() throws Exception{
        AcceptanceCondition condition = new AcceptanceCondition();

        condition.setCondition("foo");
        assertEquals("Condition should be foo!", "foo", condition.getCondition());

        condition.setCondition("bar");
        assertEquals("Condition should be bar!", "bar", condition.getCondition());
    }
}