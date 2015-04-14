package sws.murcs.debug.sampledata;

import sun.awt.IconInfo;
import sws.murcs.model.Release;
import sws.murcs.model.persistence.PersistenceManager;

import java.time.LocalDate;
import java.util.Random;

/**
 * Generates random releases
 */
public class ReleaseGenerator implements Generator<Release> {

    private String[] descriptions = {"A release date", "The time when it has to be ready", "That's not enough time"};

    /**
     * Sets up a random release
     */
    public ReleaseGenerator() {
    }

    /**
     * Sets up a random release with a from one of the given descriptions
     * @param descriptions The given descriptions
     */
    public ReleaseGenerator(String[] descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public Release generate() {
        Release r = new Release();

        Random random = new Random(47658758756875687L);

        String shortName = NameGenerator.randomName();
        String description = NameGenerator.randomElement(descriptions);
        LocalDate releaseDate = LocalDate.of(random.nextInt(10000), random.nextInt(12) + 1, random.nextInt(28) + 1);

        try {
            r.setShortName(shortName);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
            //Don't need to do anything here as it's just generation
        }
        r.setDescription(description);
        r.setReleaseDate(releaseDate);

        return r;
    }
}
