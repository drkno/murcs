package sws.murcs.debug.sampledata;

import sws.murcs.exceptions.CustomException;
import sws.murcs.model.Person;
import sws.murcs.model.Skill;

import java.util.ArrayList;

/**
 * Generates random people with skills and roles
 */
public class PersonGenerator implements Generator<Person> {
    public static final int LOW_STRESS_MAX = 5;
    public static final int LOW_STRESS_MIN = 2;

    public static final int MEDIUM_STRESS_MAX = 50;
    public static final int MEDIUM_STRESS_MIN = 5;

    public static final int HIGH_STRESS_MAX = 500;
    public static final int HIGH_STRESS_MIN = 50;

    private Generator<Skill> skillGenerator;
    private ArrayList<Skill> skillPool;

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
     * Sets the skill generator for this generator
     * @param skillGenerator The skill generator
     */
    public void setSkillGenerator(Generator<Skill> skillGenerator){
        this.skillGenerator = skillGenerator;
    }

    /**
     * Sets the pool of skills to assign from. If null, skills will be generated
     * @param skillPool The skill pool
     */
    public void setSkillPool(ArrayList<Skill> skillPool){
        this.skillPool = skillPool;
    }

    /**
     * Generates skills for a person
     * @param min The minimum number of skills
     * @param max The max number of skills
     * @return The skills
     */
    private ArrayList<Skill> generateSkills(int min, int max){
        ArrayList<Skill> generated = new ArrayList<>();
        int skillCount = NameGenerator.random(min, max);

        //If we haven't been given a pool of skills, make some up
        if (skillPool == null){
            for (int i = 0; i < skillCount; i++){
                Skill newSkill = skillGenerator.generate();
                if (!generated.stream().filter(skill -> newSkill.equals(skill)).findAny().isPresent()) {
                    generated.add(newSkill);
                }
            }
        }
        else{
            //If there are more skills than we have just assign all of them
            if (skillCount > skillPool.size()) skillCount = skillPool.size();

            for (int i = 0; i < skillCount; i++){
                //Remove the skill so we can't pick it again. We'll put it back when we're done
                Skill skill = skillPool.remove(NameGenerator.random(skillPool.size()));
                generated.add(skill);
            }

            //Put all the skills we took out back
            for (Skill skill : generated)
                skillPool.add(skill);
        }

        return generated;
    }

    @Override
    public Person generate() {
        Person p = new Person();

        String userId = NameGenerator.randomString(10, "0123456789");

        String shortName = NameGenerator.randomName();
        String longName = NameGenerator.randomTitle() + " " + shortName;

        ArrayList<Skill> skills = generateSkills(0, 100);

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
