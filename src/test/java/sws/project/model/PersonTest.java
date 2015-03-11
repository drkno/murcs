package sws.project.model;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.project.sampledata.Generator;
import sws.project.sampledata.PersonGenerator;
import sws.project.sampledata.SkillGenerator;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * 3/11/2015
 * @author Dion
 */
public class PersonTest {

    private Person personGenerated;
    private Person person;
    private Skill skillGenerated;
    private Skill skill;
    private static Generator<Person> personGenerator;
    private static Generator<Skill> skillGenerator;


    @BeforeClass
    public static void oneTimeSetUp() {
        personGenerator = new PersonGenerator();
        skillGenerator = new SkillGenerator();
    }

    @Before
    public void setUp() {
        personGenerated = personGenerator.generate();
        skillGenerated = skillGenerator.generate();
        person = new Person();
        skill = new Skill();
        skill.setShortName("PO");
    }

    @After
    public void tearDown() {
        personGenerated = null;
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
    public void addSkillTest() throws Exception{
        int filteredSize;
        assertFalse(person.getSkills().contains(skillGenerated));

        person.addSkill(skillGenerated);
        assertTrue(person.getSkills().contains(skillGenerated));

        person.addSkill(skillGenerated);
        assertEquals(person.getSkills().size(), 1);

        skill.setShortName(skillGenerated.getShortName());
        person.addSkill(skill);
        assertEquals(person.getSkills().size(), 1);
    }

    @Test
    public void addSkills() throws Exception {
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
        personGenerated.addSkill(skillGenerated);
        assertTrue(personGenerated.getSkills().contains(skillGenerated));

        personGenerated.removeSkill(skillGenerated);
        assertFalse(personGenerated.getSkills().contains(skillGenerated));

        personGenerated.removeSkill(skillGenerated);
        assertFalse(personGenerated.getSkills().contains(skillGenerated));
    }
}
