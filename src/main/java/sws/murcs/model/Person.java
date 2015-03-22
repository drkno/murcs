package sws.murcs.model;

import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.NameInvalidException;

import java.util.ArrayList;
import java.util.List;

/**
 * Model of a person.
 */
public class Person extends Model {
    private String userId;
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
    public boolean canBeRole(String role){
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
     * @throws sws.murcs.exceptions.NameInvalidException User id is invalid
     */
    public void setUserId(String userId) throws NameInvalidException {
        NameInvalidException.validate("User ID", userId);
        this.userId = userId;
    }

    /**
     * Adds a skill to skills only if the person does not already have that skill
     * @param skill The skill to add
     * @throws sws.murcs.exceptions.DuplicateObjectException if the person already has that skill
     */
    public void addSkill(Skill skill) throws DuplicateObjectException {
        if (!skills.contains(skill)) {
            this.skills.add(skill);
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
    public void addSkills(List<Skill> skills) throws DuplicateObjectException {
        for (Skill skill: skills) {
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
        }
    }

    /**
     * Returns the string of the short name
     * @return short name string
     */
    @Override
    public String toString() {
        return getShortName();
    }

    /**
     * Checks to see if to people are equal
     * @param object Person to compare
     * @return  boolean state
     */
    @Override
    public boolean equals(Object object){
        if (!(object instanceof Person)) return false;

        Person other = (Person)object;

        return other.getUserId() != null && other.getUserId().equals(getUserId());
    }
}
