package sws.murcs.debug.sampledata;

import sws.murcs.model.Skill;

/**
 * Generates random skills
 */
public class SkillGenerator implements Generator<Skill> {
    private String[] skills = {"Falling over", "Backflips", "C#", "Running away screaming", "PO", "SM"};
    private String[] descriptions = {"A very useful skill", "A not very useful skill"};

    /**
     * Instantiates a new random skill generator.
     */
    public SkillGenerator() {}

    /**
     * Instantiates a new random skill generator.
     * @param skillsList skills to generate from.
     * @param descriptionList descriptions to generate from.
     */
    public SkillGenerator(String[] skillsList, String[] descriptionList){
        this.skills = skillsList;
        this.descriptions = descriptionList;
    }

    /**
     * Generates a new random skill.
     * @return a new random skill.
     */
    @Override
    public Skill generate() {
        Skill skill = new Skill();

        String name = NameGenerator.randomElement(skills);
        String description = NameGenerator.randomElement(descriptions);

        try {
            skill.setShortName(name);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
            //Do nothing, don't have to deal with the exception if only generating test data.
        }

        skill.setLongName(name);
        skill.setDescription(description);

        return skill;
    }
}
