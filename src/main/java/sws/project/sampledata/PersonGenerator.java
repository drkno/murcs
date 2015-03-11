package sws.project.sampledata;

import sws.project.model.Person;
import sws.project.model.Skill;

import java.util.ArrayList;

/**
 *
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

        p.setUserId(userId);

        p.setShortName(shortName);
        p.setLongName(longName);

        p.getSkills().addAll(skills);

        return p;
    }
}
