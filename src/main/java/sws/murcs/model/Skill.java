package sws.murcs.model;

import sws.murcs.magic.tracking.TrackableValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Model of a skill.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Skill extends Model {

    /**
     * The short name for the product owner.
     */
    public static final String PO_NAME = "PO";

    /**
     * The short name for a scrum master.
     */
    public static final String SM_NAME = "SM";

    /**
     * The description of the skill.
     */
    @TrackableValue
    private String description;

    /**
     * Gets a description of the skill.
     * @return the description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     * @param newDescription The new description
     */
    public final void setDescription(final String newDescription) {
        this.description = newDescription;
        commit("edit skill");
    }

    /**
     * Indicates whether the skill means you can be a scrum master.
     * @return Whether you can be a scrum master
     */
    public final boolean isScrumMasterSkill() {
        return SM_NAME.equals(getShortName());
    }

    /**
     * Indicates whether the skill means you can be a product owner.
     * @return Whether you can be a product owner
     */
    public final boolean isProductOwnerSkill() {
        return PO_NAME.equals(getShortName());
    }

    @Override
    public final boolean equals(final Object object) {
        if (object == null || !(object instanceof Skill)) return false;
        String shortNameO = ((Skill) object).getShortName();
        String shortName = getShortName();
        if (shortName == null || shortNameO == null) return shortName == shortNameO;
        return shortName.toLowerCase().equals(shortNameO.toLowerCase());
    }

    /**
     * Returns the short name of the skill.
     * @return Short name of the skill
     */
    @Override
    public final String toString() {
        return getShortName();
    }

    /**
     * Enum for roles.
     */
    public enum ROLES {
        /**
         * Product Owner.
         */
        PO,
        /**
         * Scrum Master.
         */
        SM
    }
}
