package sws.murcs.model;

import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.TrackableValue;

import javax.xml.bind.annotation.*;
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
    public ArrayList<Skill> getSkills() {
        return skills;
    }

    /**
     * Indicates if this person has the skill to take on
     * a particular role
     * @param role The role
     * @return Whether the person can take on a role.
     */
    public boolean canBeRole(String role) {
        return getSkills()
                .stream()
                .filter(skill -> skill.getShortName().equals(role))
                .findFirst()
                .isPresent();
    }

    /**
     * Gets the user id
     * @return The user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user id
     * @param userId The new user id
     * @throws java.lang.Exception User id is invalid
     */
    public void setUserId(String userId) throws Exception {
        validateUserId(userId);
        this.userId = userId.trim();
        commit("edit person");
    }

    /**
     * Indicates whether a value is a valid value for 'userId' to hold
     * @param value The value.
     * @throws sws.murcs.exceptions.DuplicateObjectException if there is a duplicate object.
     */
    private void validateUserId(String value) throws Exception {
        DuplicateObjectException.checkForDuplicates(this, value);
        InvalidParameterException.validate("User Id", value);
    }

    /**
     * Adds a skill to skills only if the person does not already have that skill
     * @param skill The skill to add
     * @throws sws.murcs.exceptions.DuplicateObjectException if the person already has that skill
     */
    public void addSkill(Skill skill) throws DuplicateObjectException {
        if (!skills.contains(skill)) {
            this.skills.add(skill);
            commit("edit person");
        }
        else {
            throw new DuplicateObjectException("This is actually the same skill");
        }
    }

    /**
     * Adds a list of skills to the persons skills
     * @param skills Skills to be added to person
     * @throws sws.murcs.exceptions.DuplicateObjectException if the person has any of the skills in the list
     */
    public void addSkills(ArrayList<Skill> skills) throws DuplicateObjectException {
        for (Skill skill : skills) {
            this.addSkill(skill);
        }
    }

    /**
     * Removes a skill from skills
     * @param skill The skill to remove
     */
    public void removeSkill(Skill skill) {
        if (skills.contains(skill)) {
            this.skills.remove(skill);
            commit("edit person");
        }
    }

    @Override
    public String toString() {
        return getShortName();
    }

    /**
     * Checks to see if to people are equal
     * @param object Person to compare
     * @return boolean state
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Person)) return false;
        Person person = (Person) object;
        String shortName1 = person.getShortName();
        String shortName2 = getShortName();
        if (shortName1 == null || shortName2 == null) return shortName1 == shortName2;
        return shortName1.toLowerCase().equals(shortName2.toLowerCase()) || person.getUserId().equals(getUserId());
    }
}
