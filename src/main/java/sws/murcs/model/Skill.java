package sws.murcs.model;

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
        if (object == null || !(object instanceof Skill)) {
            return false;
        }
        String shortNameO = ((Skill) object).getShortName();
        String shortName = getShortName();
        if (shortName == null || shortNameO == null) {
            return shortName == shortNameO;
        }
        return shortName.toLowerCase().equals(shortNameO.toLowerCase());
    }

    @Override
    public final int hashCode() {
        int c = 0;
        if (getShortName() != null) {
            c = getShortName().hashCode();
        }
        return getHashCodePrime() + c;
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
