package sws.project.model;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;

/**
 *
 */

public class Team extends Model{
    private Project project;

    private String shortName;
    private String longName;

    private String description;

    private ArrayList<Role> members = new ArrayList<>();

    public void addTeamMember(Person person){
        assertOnProject(person);

        //TODO generate default role and add person to team
    }

    public void removeTeamMember(Person person){
        members.remove(person);
    }

    private void assertOnProject(Person person){
        throw new NotImplementedException();
    }
}
