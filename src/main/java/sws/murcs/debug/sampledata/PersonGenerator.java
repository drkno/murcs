package sws.murcs.debug.sampledata;

import sws.murcs.exceptions.CustomException;
import sws.murcs.model.Person;
import sws.murcs.model.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates random people with skills and roles.
 */
public class PersonGenerator implements Generator<Person> {

    /**
     * Max number people generated for a low
     * threshold.
     */
    public static final int LOW_STRESS_MAX = 5;
    /**
     * Min number people generated for a low
     * threshold.
     */
    public static final int LOW_STRESS_MIN = 2;

    /**
     * Max number people generated for a medium
     * threshold.
     */
    public static final int MEDIUM_STRESS_MAX = 50;
    /**
     * Min number people generated for a medium
     * threshold.
     */
    public static final int MEDIUM_STRESS_MIN = 5;

    /**
     * Max number people generated for a high
     * threshold.
     */
    public static final int HIGH_STRESS_MAX = 500;
    /**
     * Min number of people generated for a high
     * threshold.
     */
    public static final int HIGH_STRESS_MIN = 50;

    /**
     * The generator that will be used for skills
     * that are added to the people generated.
     */
    private Generator<Skill> skillGenerator;
    /**
     * A pool of skills to choose from when adding
     * skills to people.
     */
    private List<Skill> skillPool;

    /**
     * Instantiates a new person generator.
     */
    public PersonGenerator() {
        skillGenerator = new SkillGenerator();
    }

    /**
     * Instantiates a new person generator.
     * @param generator skill generator to use.
     */
    public PersonGenerator(final Generator<Skill> generator) {
        this.skillGenerator = generator;
    }

    /**
     * Sets the skill generator for this generator.
     * @param generator The skill generator
     */
    public final void setSkillGenerator(final Generator<Skill> generator) {
        this.skillGenerator = generator;
    }

    /**
     * Sets the pool of skills to assign from. If null, skills will be generated.
     * @param skills The skill pool
     */
    public final void setSkillPool(final List<Skill> skills) {
        this.skillPool = skills;
    }

    /**
     * Generates skills for a person.
     * @param min The minimum number of skills
     * @param max The max number of skills
     * @return The skills
     */
    private List<Skill> generateSkills(final int min, final int max) {
        List<Skill> generated = new ArrayList<>();
        int skillCount = NameGenerator.random(min, max);

        //If we haven't been given a pool of skills, make some up
        if (skillPool == null) {
            for (int i = 0; i < skillCount; i++) {
                Skill newSkill = skillGenerator.generate();
                if (!generated.stream().filter(newSkill::equals).findAny().isPresent()) {
                    generated.add(newSkill);
                }
            }
        }
        else {
            //If there are more skills than we have just assign all of them
            if (skillCount > skillPool.size()) {
                skillCount = skillPool.size();
            }

            for (int i = 0; i < skillCount; i++) {
                //Remove the skill so we can't pick it again. We'll put it back when we're done
                Skill skill = skillPool.remove(NameGenerator.random(skillPool.size()));
                generated.add(skill);
            }

            //Put all the skills we took out back
            skillPool.addAll(generated.stream().collect(Collectors.toList()));
        }

        return generated;
    }

    @Override
    public final Person generate() {
        final int userIdLength = 10;
        final int maxSkills = 100;

        Person p = new Person();

        String userId = NameGenerator.randomString(userIdLength, "0123456789");

        String shortName = NameGenerator.randomName();
        String longName = NameGenerator.randomTitle() + " " + shortName;

        List<Skill> skills = generateSkills(0, maxSkills);

        try {
            Skill productOwner = new Skill();
            productOwner.setShortName("PO");
            productOwner.setLongName("Product Owner");
            productOwner.setDescription("has ability to insult design teams efforts");
            if (!skills.stream().filter(productOwner::equals).findAny().isPresent()) {
                skills.add(productOwner);
            }

            Skill scrumMaster = new Skill();
            scrumMaster.setShortName("SM");
            scrumMaster.setLongName("Scrum Master");
            scrumMaster.setDescription("is able to manage the efforts of a team and resolve difficulties");
            if (!skills.stream().filter(scrumMaster::equals).findAny().isPresent()) {
                skills.add(scrumMaster);
            }
        } catch (Exception e) {
            // Will never ever happen. ever. an exception is only
            // thrown if you try to set the short name as null/empty
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
            // Do nothing, don't have to deal with the exception
            // if only generating test data.
        }

        p.setLongName(longName);

        try {
            p.addSkills(skills);
        }
        catch (CustomException e) {
            e.printStackTrace();
            return null;
            // Do nothing, don't have to deal with the exception
            // if only generating test data.
        }

        return p;
    }
}
