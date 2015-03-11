package sws.project.sampledata;

import sws.project.model.Person;
import sws.project.model.Team;

import javax.lang.model.element.Name;
import java.util.ArrayList;

/**
 *
 */
public class TeamGenerator implements Generator<Team> {
    private Generator<Person> personGenerator;

    public TeamGenerator(){
        personGenerator = new PersonGenerator();
    }

    public TeamGenerator(Generator<Person> personGenerator){
        this.personGenerator = personGenerator;
    }

    @Override
    public Team generate() {
        Team team = new Team();

        String shortName = NameGenerator.randomString(10);
        String longName = shortName + NameGenerator.random(10);

        String description = NameGenerator.getLoremIpsum();

        Person productOwner = null;
        Person scrumMaster = null;

        int memberCount = NameGenerator.random(3, 9);
        ArrayList<Person> members = new ArrayList<>();
        for (int i = 0; i < memberCount; ++i){
            Person p = personGenerator.generate();
            if (NameGenerator.random() < 2/memberCount)
                productOwner = p;
            if (NameGenerator.random() < 2/memberCount)
                scrumMaster = p;

            members.add(p);
        }

        team.setShortName(shortName);
        team.setLongName(longName);

        team.setDescription(description);

        team.setScrumMaster(scrumMaster);
        team.setProductOwner(productOwner);

        return team;
    }
}
