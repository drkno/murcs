package sws.project.sampledata;

import sws.project.exceptions.DuplicateObjectException;
import sws.project.model.Person;
import sws.project.model.Skill;

import java.util.ArrayList;

/**
 * Generates random people with skills and roles
 */
public class PersonGenerator implements Generator<Person> {
    private Generator<Skill> skillGenerator;

    public PersonGenerator(){
        skillGenerator = new SkillGenerator();
    }

    public PersonGenerator(Generator<Skill> skillGenerator){
        this.skillGenerator = skillGenerator;
    }

    @Override
    public Person generate() {
        Person p = new Person();

        String userId = NameGenerator.randomString(10, "0123456789");

        String shortName = NameGenerator.randomName();
        String longName = NameGenerator.randomTitle() + " " + shortName;

        int skillCount = NameGenerator.random(100);
        ArrayList<Skill> skills = new ArrayList<>();

        for (int i = 0; i < skillCount; ++i)
            skills.add(skillGenerator.generate());

        try {
            p.setUserId(userId);
        } catch (Exception e) {
            return null;
        }
        try {
            p.setShortName(shortName);
        }
        catch (Exception e) {
            //Do nothing, don't have to deal with the exception if only generating test data.
        }

        p.setLongName(longName);

        try {
            p.addSkills(skills);
        }
        catch (DuplicateObjectException e) {
            //Do nothing, don't have to deal with the exception if only generating test data.
        }

        return p;
    }
}
