package sws.murcs.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.tracking.UndoRedoManager;

public class PeerProgrammingGroupTest {

    @Before
    public void setUp() throws Exception {
        UndoRedoManager.get().setDisabled(true);
    }

    @After
    public void tearDown() throws Exception {
        UndoRedoManager.get().setDisabled(false);
    }

    @Test
    public void testGetProportionTime() throws Exception {
        PeerProgrammingGroup programmingGroup = new PeerProgrammingGroup("Test Person", 1.0f, 10.0f);
        Assert.assertEquals("Proportion calculated was incorrect.", 10.0f, programmingGroup.getProportionTime(), 0.005);
    }

    @Test
    public void testGetGroupMembers() throws Exception {
        PeerProgrammingGroup programmingGroup = new PeerProgrammingGroup("Test Person 1, Test Person 2", 1.0f, 10.0f);
        Assert.assertEquals("Test people were incorrect", "Test Person 1, Test Person 2", programmingGroup.getGroupMembers());
    }

    @Test
    public void testGetTimeSpent() throws Exception {
        PeerProgrammingGroup programmingGroup = new PeerProgrammingGroup("Test Person", 1.0f, 10.0f);
        Assert.assertEquals("Spent hours were incorrect.", 1.0f, programmingGroup.getTimeSpent(), 0.005);
    }

    @Test
    public void testGetTotalTime() throws Exception {
        PeerProgrammingGroup programmingGroup = new PeerProgrammingGroup("Test Person", 1.0f, 10.0f);
        Assert.assertEquals("Total hours were incorrect.", 10.0f, programmingGroup.getTotalTime(), 0.005);

    }
}