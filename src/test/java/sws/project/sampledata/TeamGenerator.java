package sws.project.sampledata;

import sws.project.model.Person;
import sws.project.model.Team;

import java.util.ArrayList;

/**
 * Generates random teams with people
 */
public class TeamGenerator implements Generator<Team> {
    private String[] teamNames = new String[]{"Foo", "Bar", "New team", "SENGineers", "Fred's Team"};
    private String[] descriptions = new String[]{NameGenerator.getLoremIpsum()};

    private float probOfScrumMaster = 0.5f;
    private float probOfProductOwner = 0.5f;

    private Generator<Person> personGenerator;

    public TeamGenerator(){
        personGenerator = new PersonGenerator();
    }

    public TeamGenerator(Generator<Person> personGenerator, String[] teamNames, String[] descriptions, float probOfProductOwner, float probOfScrumMaster){
        this.personGenerator = personGenerator;
        this.teamNames = teamNames;
        this.descriptions = descriptions;

        this.probOfProductOwner = probOfProductOwner;
        this.probOfScrumMaster = probOfScrumMaster;
    }

    @Override
    public Team generate() {
        Team team = new Team();

        String shortName = NameGenerator.randomElement(teamNames);
        String longName = shortName + NameGenerator.random(10);

        String description = NameGenerator.randomElement(descriptions);

        Person productOwner = null;
        Person scrumMaster = null;

        int memberCount = NameGenerator.random(3, 10);

        ArrayList<Person> members = new ArrayList<>();
        for (int i = 0; i < memberCount; ++i){
            Person p = personGenerator.generate();
            if (NameGenerator.random() < probOfProductOwner/memberCount)
                productOwner = p;
            if (NameGenerator.random() < probOfScrumMaster/memberCount)
                scrumMaster = p;

            members.add(p);
        }

        if (probOfProductOwner == 1)
            productOwner = members.get(0);

        if (probOfScrumMaster == 1)
            scrumMaster = members.get(1);

        try {
            team.setShortName(shortName);
            team.setLongName(longName);

            team.setDescription(description);

            team.setScrumMaster(scrumMaster);
            team.setProductOwner(productOwner);

            team.getMembers().addAll(members);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return team;
    }
}
