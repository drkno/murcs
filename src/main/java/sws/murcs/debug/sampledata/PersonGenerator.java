package sws.murcs.debug.sampledata;

import sws.murcs.exceptions.CustomException;
import sws.murcs.model.Person;
import sws.murcs.model.Skill;

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

        for (int i = 0; i < skillCount; i++) {
            Skill newSkill = skillGenerator.generate();
            if (!skills.stream().filter(skill -> newSkill.equals(skill)).findAny().isPresent()) {
                skills.add(newSkill);
            }
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
