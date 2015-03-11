package sws.project.model;

import java.util.ArrayList;

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
     * Adds a skill to skills
     * @param skill The skill to add
     */
    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }

    /**
     * Removes a skill from skills
     * @param skill The skill to remove
     */
    public void removeSkill(Skill skill) {
        this.skills.remove(skill);
    }
}
