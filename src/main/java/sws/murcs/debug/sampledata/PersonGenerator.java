package sws.murcs.debug.sampledata;

import sws.murcs.exceptions.CustomException;
import sws.murcs.model.Person;
import sws.murcs.model.Skill;

import java.util.ArrayList;

/**
 * Generates random people with skills and roles
 */
public class PersonGenerator implements Generator<Person> {
    private final Generator<Skill> skillGenerator;

    /**
     * Instantiates a new person generator.
     */
    public PersonGenerator(){
        skillGenerator = new SkillGenerator();
    }

    /**
     * Instantiates a new person generator.
     * @param skillGenerator skill generator to use.
     */
    public PersonGenerator(Generator<Skill> skillGenerator){
        this.skillGenerator = skillGenerator;
    }

    /**
     * Generates a new random person.
     * @return a new random person.
     */
    @Override
    public Person generate() {
        Person p = new Person();

        String userId = NameGenerator.randomString(10, "0123456789");

        String shortName = NameGenerator.randomName();
        String longName = NameGenerator.randomTitle() + " " + shortName;

        int skillCount = NameGenerator.random(100);
        int roles = NameGenerator.random(100);
        ArrayList<Skill> skills = new ArrayList<>();

        for (int i = 0; i < skillCount; i++) {
            Skill newSkill = skillGenerator.generate();
            if (!skills.stream().filter(skill -> newSkill.equals(skill)).findAny().isPresent()) {
                skills.add(newSkill);
            }
        }

        try {
            Skill productOwner = new Skill();
            productOwner.setShortName("PO");
            productOwner.setLongName("Product Owner");
            productOwner.setDescription("has ability to insult design teams efforts");
            if (!skills.stream().filter(skill -> productOwner.equals(skill)).findAny().isPresent()) {
                skills.add(productOwner);
            }

            Skill scrumMaster = new Skill();
            scrumMaster.setShortName("SM");
            scrumMaster.setLongName("Scrum Master");
            scrumMaster.setDescription("is able to manage the efforts of a team and resolve difficulties");
            if (!skills.stream().filter(skill -> scrumMaster.equals(skill)).findAny().isPresent()) {
                skills.add(scrumMaster);
            }
        } catch (Exception e) {
            //will never ever happen. ever. an exception is only thrown if you try to set the shortname as null/empty
        }

        try {
            p.setUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        try {
            p.setShortName(shortName);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
            //Do nothing, don't have to deal with the exception if only generating test data.
        }

        p.setLongName(longName);

        try {
            p.addSkills(skills);
        }
        catch (CustomException e) {
            e.printStackTrace();
            return null;
            //Do nothing, don't have to deal with the exception if only generating test data.
        }

        return p;
    }
}
