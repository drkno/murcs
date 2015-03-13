package sws.project.model;

import java.util.ArrayList;

/**
 * 11/03/2015
 *
 * @author Dion
 */
public class RelationalModel {

    private Project project;
    private ArrayList<Person> unassignedPeople;
    private ArrayList<Team> unassignedTeams;
    private ArrayList<Skill> skills;

    public RelationalModel() {
        this.unassignedPeople = new ArrayList<>();
        this.unassignedTeams = new ArrayList<>();
        this.skills = new ArrayList<>();
    }


    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ArrayList<Person> getUnassignedPeople() {
        return unassignedPeople;
    }

    public void setUnassignedPeople(ArrayList<Person> unassignedPeople) {
        this.unassignedPeople = unassignedPeople;
    }

    public ArrayList<Team> getUnassignedTeams() {
        return unassignedTeams;
    }

    public void setUnassignedTeams(ArrayList<Team> unassignedTeams) {
        this.unassignedTeams = unassignedTeams;
    }

    public ArrayList<Skill> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<Skill> skills) {
        this.skills = skills;
    }
}
