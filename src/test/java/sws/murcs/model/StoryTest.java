package sws.murcs.model;

import org.junit.Before;
import org.junit.Test;
import sws.murcs.debug.sampledata.NameGenerator;
import sws.murcs.debug.sampledata.PersonGenerator;
import sws.murcs.debug.sampledata.StoryGenerator;

import static org.junit.Assert.*;

public class StoryTest {
    private StoryGenerator storyGenerator;
    private PersonGenerator personGenerator;

    @Before
    public void setup(){
        personGenerator = new PersonGenerator();
        storyGenerator = new StoryGenerator(personGenerator);
    }

    @Test
    public void testGetSetDescription() throws Exception {
        Story story = new Story();

        //Test for ten different values
        for (int i = 0; i < 10; i++) {
            String description = NameGenerator.randomString(10);
            story.setDescription(description);

            assertEquals("The getter should return the value entered in the setter", description, story.getDescription());
        }
    }

    @Test
    public void testGetSetCreator() throws Exception {
        Story story = new Story();

        //Test for ten different values
        for (int i = 0; i < 10; i++) {
            Person creator = personGenerator.generate();
            story.setCreator(creator);

            assertEquals("The getter should return the value entered in the setter", creator, story.getCreator());
        }
    }

    @Test
    public void testToString() throws Exception {
        //Test for a few different values
        for (int i = 0; i < 10; i++){
            Story story = storyGenerator.generate();
            assertEquals("The short name of the story and the .toString representation should be the same!", story.getShortName(), story.toString());
        }
    }

    @Test
    public void testEquals() throws Exception {
        Story s1 = storyGenerator.generate();

        assertTrue("The story should be equal to itself", s1.equals(s1));
        assertFalse("A story should not be equal to null", s1.equals(null));

        Story s2 = storyGenerator.generate();
        //Ensure s2 has a different name
        s2.setShortName(s1.getShortName() + "1");
        assertFalse("A story should not be equal to a story with a different name", s1.equals(s2));
        assertFalse("Equals should be reflexive", s2.equals(s1));

        s2.setShortName(s1.getShortName());
        assertTrue("A story should be equal to a story with the same name", s1.equals(s2));
        assertTrue("Equals should be reflexive", s2.equals(s1));
    }
}