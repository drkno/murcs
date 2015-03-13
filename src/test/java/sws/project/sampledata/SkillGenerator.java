package sws.project.sampledata;

import sws.project.model.Skill;

/**
 * Generates random skills
 */
public class SkillGenerator implements Generator<Skill> {
    private String[] skills = {"Falling over", "Backflips", "C#", "Running away screaming"};
    private String[] descriptions = {"A very useful skill", "A not very useful skill"};

    public SkillGenerator(){

    }

    public SkillGenerator(String[] skillsList, String[] descriptionList){
        this.skills = skillsList;
        this.descriptions = descriptionList;
    }

    @Override
    public Skill generate() {
        Skill skill = new Skill();

        String name = NameGenerator.randomElement(skills);
        String description = NameGenerator.randomElement(descriptions);

        try {
            skill.setShortName(name);
        }
        catch (Exception e) {
            //Do nothing, don't have to deal with the exception if only generating test data.
        }

        skill.setLongName(name);
        skill.setDescription(description);

        return skill;
    }
}
