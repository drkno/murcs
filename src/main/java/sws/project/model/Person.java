package sws.project.model;

import sws.project.exceptions.DuplicateObjectException;

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
    public boolean canBeRole(Skill.Role role){
        return getSkills()
                .stream()
                .filter(s -> s.isRole(role))
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
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Adds a skill to skills only if the person does not already have that skill
     * @param skill The skill to add
     */
    public void addSkill(Skill skill) throws DuplicateObjectException {
        if (!skills.contains(skill) &&
                !skills
                    .stream()
                    .filter(s -> s.getShortName().toLowerCase().equals(skill.getShortName().toLowerCase()))
                    .findAny()
                    .isPresent()) {
            this.skills.add(skill);
        }
        else {
            throw new DuplicateObjectException();
        }
    }

    /**
     * Adds a list of skills to the persons skills
     * @param skills Skills to be added to person
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
}
