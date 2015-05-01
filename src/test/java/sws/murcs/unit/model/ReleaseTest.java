package sws.murcs.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.model.Release;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReleaseTest {

    private Release release1;
    private Release release2;

    @Before
    public void setUp() throws Exception {
        release1 = new Release();
        release2 = new Release();
        release1.setShortName("Project Release");
        release2.setShortName("A ridiculous release date");
    }

    @After
    public void tearDown() {
        release1 = null;
        release2 = null;
    }

    @Test (expected = Exception.class)
    public void setShortNameTest1() throws Exception {
        release1.setShortName(null);
    }

    @Test(expected = Exception.class)
    public void setShortNameTest2() throws Exception{
        release1.setShortName("");
    }

    @Test(expected = Exception.class)
    public void setShortNameTest3() throws Exception{
        release1.setShortName("   \n\r\t");
    }

    @Test
    public void equalsTest() throws Exception {
        release1.setShortName("A ridiculous release date");
        assertTrue(release2.equals(release1));
        release1.setShortName("Another release");
        assertFalse(release2.equals(release1));
    }
}
