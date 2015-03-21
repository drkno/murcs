package sws.project.sampledata;

import sws.murcs.model.Person;
import sws.murcs.model.Project;
import sws.murcs.model.Skill;
import sws.murcs.model.Team;

/**
 * Prints random generated data
 */
public class SampleData {
    public static void main(String[] args){
        Generator<Project> projectGenerator = new ProjectGenerator();

        Project project = projectGenerator.generate();

        printIndented("Project: ", 0);
        printProject(project, 1);
    }

    private static void printProject(Project project, int indent){
        printIndented("Name: " + project.getShortName(), indent);
        printIndented("Description: " + project.getShortName(), indent);

        printIndented("Teams: ", indent);
        for (int i = 0; i < project.getTeams().size(); ++i) {
            printIndented("Team " + i + ":", indent + 1);
            printTeam(project.getTeams().get(i), indent + 1);
        }
    }

    private static void printTeam(Team team, int indent){
        printIndented("Name: " + team.getShortName(), indent);
        printIndented("Description: " + team.getDescription(), indent);

        printIndented("Members: ", indent);
        for (int i = 0; i < team.getMembers().size(); ++i){
            printIndented("Person " + i + ":", indent + 1);
            printPerson(team.getMembers().get(i), indent + 2);
        }
    }

    private static void printPerson(Person person, int indent){
        printIndented("Name: " + person.getShortName(), indent);
        printIndented("UserId: " + person.getUserId(), indent);

        printIndented("Skills: ", indent);
        for (int i = 0; i < person.getSkills().size(); ++i){
            printIndented("Skill " + i + ":", indent + 1);
            printSkill(person.getSkills().get(i), indent + 2);
        }
    }

    private static void printSkill(Skill skill, int indent){
        printIndented("Name: " + skill.getShortName(), indent);
        printIndented("Description: " + skill.getDescription(), indent);
    }

    private static void printIndented(String text, int indent){
        for (int i = 0; i < indent; ++i){
            text = "\t" + text;
        }

        System.out.println(text);
    }
}
