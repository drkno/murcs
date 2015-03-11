package sws.project.sampledata;

import sws.project.model.Person;
import sws.project.model.Project;
import sws.project.model.Skill;
import sws.project.model.Team;

/**
 *
 */
public class SampleData {
    public static void main(String[] args){
        Generator<Project> projectGenerator = new ProjectGenerator();

        Project project = projectGenerator.generate();
        System.out.println(project);
    }

    private static void printProject(Project project){

    }

    private static void printTeam(Team team){

    }

    private static void printPerson(Person person){

    }

    private static void printSkill(Skill skill){

    }

    private static void printIndented(String text){

    }
}
