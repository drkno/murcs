package sws.project.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * 3/11/2015
 * @author Dion
 */
public class SkillTest {

    private Skill skill;
    private Skill skill2;
    private Skill skill3;


    @Before
    public void setUp() {
        skill = new Skill();
        skill2 = new Skill();
        skill3 = new Skill();
        skill.setShortName("PO");
        skill2.setShortName("A very good skill to have");
        skill3.setShortName("Something worth while");
    }

    @After
    public void tearDown() {
        skill = null;
    }

    @Test (expected = IllegalArgumentException.class)
    public void setShortNameTest() throws Exception{
        skill.setShortName(null);
    }

    @Test
    public void isRoleTest() throws Exception {
        assertTrue(skill.isRole(Skill.Role.PO));

        skill.setShortName("pO");
        assertTrue(skill.isRole(Skill.Role.PO));

        skill.setShortName("developer");
        assertFalse(skill.isRole(Skill.Role.PO));
    }

    @Test
    public void equalsTest() throws Exception {
        skill2.setShortName("PO");
        assertTrue(skill.equals(skill2));
        assertFalse(skill.equals(skill3));
    }
}
