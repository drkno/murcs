package sws.project.model;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.project.sampledata.Generator;
import sws.project.sampledata.SkillGenerator;

/**
 * 3/11/2015
 * @author Dion
 */
public class SkillTest {

    private Skill skill;
    private static Generator<Skill> skillGenerator;

    @BeforeClass
    public static void oneTimeSetUp() {
        skillGenerator = new SkillGenerator();
    }

    @Before
    public void setUp() {
        skill = new Skill();
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
        
    }

    @Test
    public void equalsTest() throws Exception {

    }
}
