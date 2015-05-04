package sws.murcs.model;

import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.TrackableValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;

/**
 * Model of a person.
 */
@XmlRootElement
@XmlType(propOrder = {"userId", "skills"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Person extends Model {
    @TrackableValue
    @XmlElement(name = "id")
    private String userId;
    @TrackableValue
    @XmlElementWrapper(name = "skills")
    @XmlElement(name = "skill")
    private ArrayList<Skill> skills = new ArrayList<>();

    /**
     * Gets a list of the skills the person has. Following the
     * pattern Java uses with JavaFX, changing the returned
     * list is the preferred way to add items to the list.
     * @return The person's skills.
     */
    public final ArrayList<Skill> getSkills() {
        return skills;
    }

    /**
     * Indicates if this person has the skill to take on
     * a particular role.
     * @param role The role
     * @return Whether the person can take on a role.
     */
    public final boolean canBeRole(final String role) {
        return getSkills()
                .stream()
                .filter(skill -> skill.getShortName().equals(role))
                .findFirst()
                .isPresent();
    }

    /**
     * Gets the user id.
     * @return The user id
     */
    public final String getUserId() {
        return userId;
    }

    /**
     * Sets the user id.
     * @param newUserID The new user id
     * @throws Exception User id is invalid
     */
    public final void setUserId(final String newUserID) throws Exception {
        validateUserId(newUserID);
        this.userId = newUserID.trim();
        commit("edit person");
    }

    /**
     * Indicates whether a value is a valid value for 'userId' to hold.
     * @param value The value.
     * @throws Exception if there is a duplicate object.
     */
    private void validateUserId(final String value) throws Exception {
        DuplicateObjectException.checkForDuplicates(this, value);
        InvalidParameterException.validate("User Id", value);
    }

    /**
     * Adds a skill to skills only if the person does not
     * already have that skill.
     * @param skill The skill to add
     * @throws DuplicateObjectException if
     * the person already has that skill
     */
    public final void addSkill(final Skill skill) throws DuplicateObjectException {
        if (!skills.contains(skill)) {
            this.skills.add(skill);
            commit("edit person");
        }
        else {
            throw new DuplicateObjectException("This is actually the same skill");
        }
    }

    /**
     * Adds a list of skills to the persons skills.
     * @param skillsToAdd Skills to be added to person
     * @throws DuplicateObjectException if the
     * person has any of the skills in the list
     */
    public final void addSkills(final ArrayList<Skill> skillsToAdd) throws DuplicateObjectException {
        for (Skill skill : skillsToAdd) {
            this.addSkill(skill);
        }
    }

    /**
     * Removes a skill from skills.
     * @param skill The skill to remove
     */
    public final void removeSkill(final Skill skill) {
        if (skills.contains(skill)) {
            this.skills.remove(skill);
            commit("edit person");
        }
    }

    @Override
    public final String toString() {
        return getShortName();
    }

    /**
     * Checks to see if to people are equal.
     * @param object Person to compare
     * @return boolean state
     */
    @Override
    public final boolean equals(final Object object) {
        if (!(object instanceof Person)) {
            return false;
        }
        Person person = (Person) object;
        String shortName1 = person.getShortName();
        String shortName2 = getShortName();
        if (shortName1 == null || shortName2 == null) {
            return shortName1 == shortName2;
        }
        return shortName1.equalsIgnoreCase(shortName2) || person.getUserId().equals(getUserId());
    }
}
