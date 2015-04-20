package sws.murcs.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.model.Skill;

import static org.junit.Assert.*;

public class SkillTest {

    private Skill skill;
    private Skill skill2;
    private Skill skill3;


    @Before
    public void setUp() throws Exception {
        skill = new Skill();
        skill2 = new Skill();
        skill3 = new Skill();
        skill.setShortName("OMG");
        skill2.setShortName("A very good skill to have");
        skill3.setShortName("Something worth while");
    }

    @After
    public void tearDown() {
        skill = null;
        skill2 = null;
        skill3 = null;
    }

    @Test (expected = Exception.class)
    public void setShortNameTest1() throws Exception{
        skill.setShortName(null);
    }

    @Test(expected = Exception.class)
    public void setShortNameTest2() throws Exception{
        skill.setShortName("");
    }

    @Test(expected = Exception.class)
    public void setShortNameTest3() throws Exception{
        skill.setShortName("   \n\r\t");
    }

    @Test
    public void equalsTest() throws Exception {
        skill2.setShortName("OMG");
        assertTrue(skill.equals(skill2));
        assertFalse(skill.equals(skill3));
    }
}
