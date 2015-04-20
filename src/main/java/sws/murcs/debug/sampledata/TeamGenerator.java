package sws.murcs.debug.sampledata;

import sws.murcs.model.Person;
import sws.murcs.model.Team;

import java.util.ArrayList;

/**
 * Generates random teams with people
 */
public class TeamGenerator implements Generator<Team> {
    private String[] teamNames = {"Foo", "Bar", "New team", "SENGineers", "Fred's Team"};
    private String[] descriptions = {NameGenerator.getLoremIpsum()};
    private float probOfScrumMaster = 0.5f;
    private float probOfProductOwner = 0.5f;
    private final Generator<Person> personGenerator;

    /**
     * Instantiates a new Team generator.
     */
    public TeamGenerator(){
        personGenerator = new PersonGenerator();
    }

    /**
     * Instantiates a new Team generator.
     * @param personGenerator person generator to use.
     * @param teamNames team names to generate from.
     * @param descriptions descriptions to generate from.
     * @param probOfProductOwner probability of a product owner to use.
     * @param probOfScrumMaster probability of a scrum master to use.
     */
    public TeamGenerator(Generator<Person> personGenerator, String[] teamNames, String[] descriptions, float probOfProductOwner, float probOfScrumMaster){
        this.personGenerator = personGenerator;
        this.teamNames = teamNames;
        this.descriptions = descriptions;
        this.probOfProductOwner = probOfProductOwner;
        this.probOfScrumMaster = probOfScrumMaster;
    }

    /**
     * Generates a new random team.
     * @return a new random team.
     */
    @Override
    public Team generate() {
        Team team = new Team();

        String shortName = NameGenerator.randomElement(teamNames);
        String longName = shortName + NameGenerator.random(10);

        String description = NameGenerator.randomElement(descriptions);

        Person productOwner;
        Person scrumMaster;

        int memberCount = NameGenerator.random(3, 15);

        ArrayList<Person> members = new ArrayList<>();
        for (int i = 0; i < memberCount; ++i){
            Person p = personGenerator.generate();
            if (!members.stream().filter(person -> p.equals(person)).findAny().isPresent()) {
                members.add(p);
            }
        }

        productOwner = members.get(0);
        scrumMaster = members.get(1);

        try {
            team.setShortName(shortName);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
            //Do nothing, don't have to deal with the exception if only generating test data.
        }

        team.setLongName(longName);
        team.setDescription(description);

        try {
            team.setScrumMaster(scrumMaster);
            team.setProductOwner(productOwner);
            team.addMembers(members);
        } catch (Exception e) {
            //Do nothing, don't have to deal with the exception if only generating test data.
            e.printStackTrace();
            return null;
        }

        return team;
    }
}
