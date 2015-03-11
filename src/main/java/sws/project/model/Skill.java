package sws.project.model;
/**
 * Model of a skill.
 */

public class Skill extends Model{
    /**
     * Possible roles of a team.
     */
    public enum Role{
        ScrumMaster,
        Developer,
        PO,
    }

    private String description;

    /**
     * Gets a description of the skill
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description
     * @param description The new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Indicates if this skill is a type of Role
     * @param role The role
     * @return Whether this skill is of the type of the role
     */
    public boolean isRole(Role role){
        //This is a quick and dirty solution. Essentially what it does
        //is compare the two strings in the same case, without spaces.
        return getShortName().toLowerCase().replace(" ", "") == role.toString().toLowerCase().replace(" ", "");
    }

    @Override
    public boolean equals(Object object){
        return object instanceof Skill && ((Skill)object).getLongName() == getLongName();
    }
}
