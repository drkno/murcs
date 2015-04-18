package sws.murcs.model;

import sws.murcs.magic.tracking.TrackableValue;

/**
 * Model of a skill.
 */
public class Skill extends Model {

    /**
     * The short name for the product owner
     */
    public static final String PO_NAME = "PO";

    /**
     * The short name for a scrum master
     */
    public static final String SM_NAME = "SM";

    @TrackableValue
    private String description;

    /**
     * Enum for roles
     */
    public enum ROLES {
        PO,
        SM
    }

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
        commit("set skill description");
    }

    /**
     * Indicates whether the skill means you can be a scrum master
     * @return Whether you can be a scrum master
     */
    public boolean isScrumMasterSkill(){
        return SM_NAME.equals(getShortName());
    }

    /**
     * Indicates whether the skill means you can be a product owner
     * @return Whether you can be a product owner
     */
    public boolean isProductOwnerSkill(){
        return PO_NAME.equals(getShortName());
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Skill && ((Skill) object).getShortName().toLowerCase().equals(getShortName().toLowerCase());
    }

    /**
     * Returns the short name of the skill
     * @return Short name of the skill
     */
    @Override
    public String toString() {
        return getShortName();
    }
}
