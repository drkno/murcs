package sws.project.model;

import java.util.ArrayList;

/**
 *
 */

public class Person extends Model {
    private ArrayList<Skill> skills = new ArrayList<>();

    public ArrayList<Skill> getSkills() {
        return skills;
    }
}
