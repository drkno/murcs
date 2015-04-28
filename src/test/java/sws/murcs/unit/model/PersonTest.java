package sws.murcs.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Person;
import sws.murcs.model.Skill;
import sws.murcs.debug.sampledata.Generator;
import sws.murcs.debug.sampledata.SkillGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PersonTest {

    private Person person;
    private Skill skillGenerated;
    private Skill skill;
    private static Generator<Skill> skillGenerator;


    @BeforeClass
    public static void oneTimeSetUp() {
        skillGenerator = new SkillGenerator();
        UndoRedoManager.setDisabled(true);
    }

    @Before
    public void setUp() throws Exception {
        skillGenerated = skillGenerator.generate();
        person = new Person();
        skill = new Skill();
        skill.setShortName("LOL");
    }

    @After
    public void tearDown() {
        skillGenerated = null;
        person = null;
        skill = null;
    }

    @Test (expected = Exception.class)
    public void setShortNameTest1() throws Exception{
        person.setShortName(null);
    }

    @Test(expected = Exception.class)
    public void setShortNameTest2() throws Exception{
        person.setShortName("");
    }

    @Test(expected = Exception.class)
    public void setShortNameTest3() throws Exception{
        person.setShortName("   \n\r\t");
    }

    @Test
    public void canBeRoleTest() throws Exception {
        person.addSkill(skill);
        assertTrue(person.canBeRole("LOL"));
        assertFalse(person.canBeRole("ScrumMaster"));
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
