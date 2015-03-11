package sws.project.model;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.project.exceptions.DuplicateObjectException;
import sws.project.sampledata.Generator;
import sws.project.sampledata.SkillGenerator;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * 3/11/2015
 * @author Dion
 */
public class PersonTest {

    private Person person;
    private Skill skillGenerated;
    private Skill skill;
    private static Generator<Skill> skillGenerator;


    @BeforeClass
    public static void oneTimeSetUp() {
        skillGenerator = new SkillGenerator();
    }

    @Before
    public void setUp() {
        skillGenerated = skillGenerator.generate();
        person = new Person();
        skill = new Skill();
        skill.setShortName("PO");
    }

    @After
    public void tearDown() {
        skillGenerated = null;
        person = null;
        skill = null;
    }

    @Test (expected = IllegalArgumentException.class)
    public void setShortNameTest() throws Exception{
        person.setShortName(null);
    }

    @Test
    public void canBeRoleTest() throws Exception {
        person.addSkill(skill);
        assertTrue(person.canBeRole(Skill.Role.PO));
        assertFalse(person.canBeRole(Skill.Role.ScrumMaster));
    }

    @Test
    public void addSkillTest() throws Exception {
        assertFalse(person.getSkills().contains(skillGenerated));

        person.addSkill(skillGenerated);
        assertTrue(person.getSkills().contains(skillGenerated));
    }

    @Test (expected = DuplicateObjectException.class)
    public void addSkillExceptionTest1() throws Exception {
        person.addSkill(skillGenerated);
        person.addSkill(skillGenerated);
    }

    @Test (expected = DuplicateObjectException.class)
    public void addSkillExceptionTest2() throws Exception {
        person.addSkill(skillGenerated);
        skill.setShortName(skillGenerated.getShortName());
        person.addSkill(skill);
    }

    @Test
    public void addSkillsTest() throws Exception {
        List<Skill> testSkills = new ArrayList<>();
        assertEquals(person.getSkills().size(), 0);
        testSkills.add(skillGenerated);

        person.addSkills(testSkills);
        assertTrue(person.getSkills().contains(skillGenerated));

        testSkills.add(skillGenerated);
        assertEquals(testSkills.size(), 2);
        assertEquals(person.getSkills().size(), 1);
    }

    @Test
    public void removeSkillTest() throws Exception {
        person.addSkill(skillGenerated);
        assertTrue(person.getSkills().contains(skillGenerated));

        person.removeSkill(skillGenerated);
        assertFalse(person.getSkills().contains(skillGenerated));

        person.removeSkill(skillGenerated);
        assertFalse(person.getSkills().contains(skillGenerated));
    }
}
