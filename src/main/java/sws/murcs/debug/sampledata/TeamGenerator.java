package sws.murcs.debug.sampledata;

import sws.murcs.model.Person;
import sws.murcs.model.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates random teams with people
 */
public class TeamGenerator implements Generator<Team> {
    public static final int LOW_STRESS_MAX = 5;
    public static final int LOW_STRESS_MIN = 1;

    public static final int MEDIUM_STRESS_MAX = 10;
    public static final int MEDIUM_STRESS_MIN = 5;

    public static final int HIGH_STRESS_MAX = 20;
    public static final int HIGH_STRESS_MIN = 10;

    private String[] teamNames = {"Foo", "Bar", "New team", "SENGineers", "Fred's Team"};
    private String[] descriptions = {NameGenerator.getLoremIpsum()};
    private float probOfScrumMaster = 0.5f;
    private float probOfProductOwner = 0.5f;

    private Generator<Person> personGenerator;
    private ArrayList<Person> personPool;

    /**
     * Instantiates a new Team generator.
     */
    public TeamGenerator() {
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
    public TeamGenerator(Generator<Person> personGenerator, String[] teamNames, String[] descriptions, float probOfProductOwner, float probOfScrumMaster) {
        this.personGenerator = personGenerator;
        this.teamNames = teamNames;
        this.descriptions = descriptions;
        this.probOfProductOwner = probOfProductOwner;
        this.probOfScrumMaster = probOfScrumMaster;
    }

    /**
     * Sets the person generator
     * @param personGenerator The person generator
     */
    public void setPersonGenerator(Generator<Person> personGenerator){
        this.personGenerator = personGenerator;
    }

    /**
     * Sets the person pool. If null, people will be randomly generated
     * @param personPool The person pool
     */
    public void setPersonPool(ArrayList<Person> personPool) {
        this.personPool = personPool;
    }

    /**
     * Generates the members of a team
     * @param min The min members
     * @param max The max members
     * @return The members
     */
    private ArrayList<Person> generateMembers(int min, int max){
        ArrayList<Person> generated = new ArrayList<>();
        int personCount = NameGenerator.random(min, max);

        //If we haven't been given a pool of person, make some up
        if (personPool == null){
            for (int i = 0; i < personCount; i++){
                Person newPerson = personGenerator.generate();
                if (!generated.stream().filter(person -> newPerson.equals(person)).findAny().isPresent()) {
                    generated.add(newPerson);
                }
            }
        }
        else{
            //If there are more person than we have just assign all of them
            if (personCount > personPool.size()) personCount = personPool.size();

            for (int i = 0; i < personCount; i++){
                //Remove the person so we can't pick it again. We'll put it back when we're done
                Person skill = personPool.remove(NameGenerator.random(personPool.size()));
                generated.add(skill);
            }

            //Put all the skills we took out back
            for (Person person : generated)
                personPool.add(person);
        }

        return generated;
    }

    @Override
    public Team generate() {
        Team team = new Team();

        String shortName = NameGenerator.randomElement(teamNames);
        String longName = shortName + NameGenerator.random(10);

        String description = NameGenerator.randomElement(descriptions);

        Person productOwner;
        Person scrumMaster;

        ArrayList<Person> members = generateMembers(3, 15);

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
