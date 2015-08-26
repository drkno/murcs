package sws.murcs.unit.model.organisation;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.murcs.debug.sampledata.GenerationHelper;
import sws.murcs.debug.sampledata.OrganisationGenerator;
import sws.murcs.debug.sampledata.PersonGenerator;
import sws.murcs.debug.sampledata.SkillGenerator;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Model;
import sws.murcs.model.Organisation;
import sws.murcs.model.Person;
import sws.murcs.model.Skill;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.model.persistence.loaders.FilePersistenceLoader;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OrganisationSkillTest {
    private static OrganisationGenerator generator;
    private static Organisation model;

    @BeforeClass
    public static void classSetup() {
        generator = new OrganisationGenerator(OrganisationGenerator.Stress.High);
        UndoRedoManager.setDisabled(true);
        if (PersistenceManager.getCurrent() == null) {
            PersistenceManager.setCurrent(new PersistenceManager(new FilePersistenceLoader()));
        }
        model = getNeworganisation();
    }

    @AfterClass
    public static void classTearDown() {
        UndoRedoManager.setDisabled(false);
        PersistenceManager.getCurrent().setCurrentModel(null);
        model = null;
    }

    /**
     * Generates a organisation, and sets it to the currently in use
     * model in the current persistence manager instance.
     * @throws NullPointerException if no persistence manager exists.
     * @return a new organisation.
     */
    private static Organisation getNeworganisation() {
        PersistenceManager.getCurrent().setCurrentModel(null);
        Organisation model = generator.generate();
        PersistenceManager.getCurrent().setCurrentModel(model);
        return model;
    }

    @Test
    public void testGetSkillsNotNullOrEmpty() throws Exception {
        List<Skill> skills = model.getSkills();
        Assert.assertNotNull("getSkills() should return skills but is null.", skills);
        Assert.assertNotEquals("getSkills() should return skills but is empty.", 0, skills.size());
    }

    @Test
    public void testGetSkillsSkillRemoved() throws Exception {
        List<Skill> skills = model.getSkills();
        int size = skills.size();
        Skill removedSkill = skills.get(0);
        model.remove(removedSkill);
        skills = model.getSkills();

        Assert.assertFalse("Skills should not contain the removed skill.", skills.contains(removedSkill));
        Assert.assertNotEquals("Skills should not be the same size as before removing a skill.", size, skills.size());
    }

    @Test
    public void testGetSkillsAdded() throws Exception {
        List<Skill> skills = model.getSkills();
        Skill skillToAdd = skills.get(0);
        model.remove(skillToAdd);
        skills = model.getSkills();
        int size = skills.size();

        model.add(skillToAdd);

        Assert.assertTrue("Skills should contain the added skill.", skills.contains(skillToAdd));
        Assert.assertNotEquals("Skills should not be the same size as before adding a skill.", size, skills.size());
    }

    @Test(expected = InvalidParameterException.class)
    public void testSkillsNoShortNameAdded() throws Exception {
        Skill skill = new Skill();
        model.add(skill);
    }

    @Test
    public void testSkillsNoShortNameNotAddedRemoved() throws Exception {
        List<Skill> skills = model.getSkills();
        int size = skills.size();
        Skill skill = new Skill();
        model.remove(skill);
        Assert.assertEquals("Removing skill that isn't in the model should not change the skills collection.", size, skills.size());
    }

    @Test(expected = DuplicateObjectException.class)
    public void testSkillsDuplicateAdded() throws Exception {
        List<Skill> skills = model.getSkills();
        model.add(skills.get(0));
    }

    @Test
    public void testGetSkillsNoDuplicates() throws Exception {
        List<Skill> skills = model.getSkills();

        List<Skill> skillDuplicates = new ArrayList<>();
        skills.stream().filter(skill -> !skillDuplicates.add(skill)).forEach(skill -> {
            Assert.fail("There cannot be duplicate skills returned by getSkills().");
        });
    }

    @Test
    public void testSkillExists() throws Exception {
        List<Skill> skills = model.getSkills();
        Assert.assertTrue("Skill exists but was not found.", UsageHelper.exists(skills.get(0)));
    }

    @Test
    public void testSkillDoesNotExist() throws Exception {
        Skill skill = new Skill();
        skill.setShortName("testing1234");
        Assert.assertFalse("Skill exists when it should not.", UsageHelper.exists(skill));
    }

    @Test
    public void testSkillFindUsagesDoesNotExist() throws Exception {
        Skill skill = new Skill();
        skill.setShortName("testing1234");
        List<Model> usages = UsageHelper.findUsages(skill);

        Assert.assertNotNull("The returned usages was null.", usages);
        Assert.assertEquals("Usages were found for skill not in model.", 0, usages.size());
    }

    @Test
    public void testSkillFindUsages() throws Exception {
        List<Skill> skills = model.getSkills();
        List<Person> people = model.getPeople();
        try {
            people.get(0).addSkill(skills.get(0));
        }
        catch (DuplicateObjectException e) {
            // ignore, we just want to ensure skill is attached to a person
        }
        List<Model> usages = UsageHelper.findUsages(skills.get(0));

        Assert.assertNotNull("The returned usages was null.", usages);
        Assert.assertNotEquals("Usages were not found for skill.", 0, usages.size());
        Assert.assertTrue("Item should be in use.", UsageHelper.inUse(skills.get(0)));
    }

    @Test
    public void testDeletionsCascadeSkill() throws Exception {
        ArrayList<Skill> skillsBackup = new ArrayList<>(model.getSkills());
        ArrayList<Person> peopleBackup = new ArrayList<>(model.getPeople());
        SkillGenerator skillGenerator = new SkillGenerator();
        PersonGenerator personGenerator = new PersonGenerator(skillGenerator);
        //Make sure we're working from a clean slate
        model.getSkills().clear();
        model.getPeople().clear();

        //Generate some random people
        for (int i = 0; i < 10; ++i) {
            Person person = personGenerator.generate();
            person.setUserId(person.getUserId() + i);
            person.setShortName(person.getShortName() + i);
            person.clearSkills();
            model.add(person);
        }

        //Add a few skills to the model and to a random person
        for (int i = 0; i < 10; ++i) {
            Skill skill = skillGenerator.generate();

            //Avoid duplicates
            skill.setShortName(skill.getShortName() + i);

            model.add(skill);
            model.getPeople().get(GenerationHelper.random(model.getPeople().size())).addSkill(skill);
        }

        //Remove all the skills from the model. This should cascade to the people with the skills being removed
        for (int i = 0; i < model.getSkills().size(); ++i){
            model.remove(model.getSkills().get(i));
            i--;
        }

        //Check that all the skills have been removed from all the people
        for (Person p : model.getPeople()) {
            assertEquals("The person should now have no skills", 0, p.getSkills().size());
        }
        model.addCollection(skillsBackup);
        model.addCollection(peopleBackup);
    }
}
