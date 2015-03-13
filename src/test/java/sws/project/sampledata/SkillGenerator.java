package sws.project.sampledata;

import sws.project.model.Skill;

/**
 * Generates random skills
 */
public class SkillGenerator implements Generator<Skill> {
    private String[] skills = new String[]{"Falling over", "Backflips", "C#", "Running away screaming"};
    private String[] descriptions = new String[]{"A very useful skill", "A not very useful skill"};

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
            skill.setLongName(name);
            skill.setDescription(description);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return skill;
    }
}
