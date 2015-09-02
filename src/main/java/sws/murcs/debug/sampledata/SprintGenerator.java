package sws.murcs.debug.sampledata;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.exceptions.CustomException;
import sws.murcs.exceptions.NotReadyException;
import sws.murcs.model.Backlog;
import sws.murcs.model.Release;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Team;

/**
 * Generates random Sprints with stories.
 */
public class SprintGenerator implements Generator<Sprint> {
    /**
     * The list of names to be used for sprints.
     */
    private static final String[] SPRINT_NAMES = {"absolutely",
            "adorable",
            "accepted",
            "acclaimed",
            "accomplish",
            "accomplishment",
            "achievement",
            "action",
            "active",
            "admire",
            "adventure",
            "affirmative",
            "affluent",
            "agree",
            "agreeable",
            "amazing",
            "angelic",
            "appealing",
            "approve",
            "aptitude",
            "attractive",
            "awesome",
            "beaming",
            "beautiful",
            "believe",
            "beneficial",
            "bliss",
            "bountiful",
            "bounty",
            "brave",
            "bravo",
            "brilliant",
            "bubbly",
            "calm",
            "celebrated",
            "certain",
            "champ",
            "champion",
            "charming",
            "cheery",
            "choice",
            "classic",
            "classical",
            "clean",
            "commend",
            "composed",
            "congratulation",
            "constant",
            "cool",
            "courageous",
            "creative",
            "cute",
            "dazzling",
            "delight",
            "delightful",
            "distinguished",
            "divine",
            "earnest",
            "easy",
            "ecstatic",
            "effective",
            "effervescent",
            "efficient",
            "effortless",
            "electrifying",
            "elegant",
            "enchanting",
            "encouraging",
            "endorsed",
            "energetic",
            "energized",
            "engaging",
            "enthusiastic",
            "essential",
            "esteemed",
            "ethical",
            "excellent",
            "exciting",
            "exquisite",
            "fabulous",
            "fair",
            "familiar",
            "famous",
            "fantastic",
            "favorable",
            "fetching",
            "fine",
            "fitting",
            "flourishing",
            "fortunate",
            "free",
            "fresh",
            "friendly",
            "fun",
            "funny",
            "generous",
            "genius",
            "genuine",
            "giving",
            "glamorous",
            "glowing",
            "good",
            "gorgeous",
            "graceful",
            "great",
            "green",
            "grin",
            "growing",
            "handsome",
            "happy",
            "harmonious",
            "healing",
            "healthy",
            "hearty",
            "heavenly",
            "honest",
            "honorable",
            "honored",
            "hug",
            "idea",
            "ideal",
            "imaginative",
            "imagine",
            "impressive",
            "independent",
            "innovate",
            "innovative",
            "instant",
            "instantaneous",
            "instinctive",
            "intuitive",
            "intellectual",
            "intelligent",
            "inventive",
            "jovial",
            "joy",
            "jubilant",
            "keen",
            "kind",
            "knowing",
            "knowledgeable",
            "laugh",
            "legendary",
            "light",
            "learned",
            "lively",
            "lovely",
            "lucid",
            "lucky",
            "luminous",
            "marvelous",
            "masterful",
            "meaningful",
            "merit",
            "meritorious",
            "miraculous",
            "motivating",
            "moving",
            "natural",
            "nice",
            "novel",
            "now",
            "nurturing",
            "nutritious",
            "okay",
            "one",
            "one-hundred percent",
            "open",
            "optimistic",
            "paradise",
            "perfect",
            "phenomenal",
            "pleasurable",
            "plentiful",
            "pleasant",
            "poised",
            "polished",
            "popular",
            "positive",
            "powerful",
            "prepared",
            "pretty",
            "principled",
            "productive",
            "progress",
            "prominent",
            "protected",
            "proud",
            "quality",
            "quick",
            "quiet",
            "ready",
            "reassuring",
            "refined",
            "refreshing",
            "rejoice",
            "reliable",
            "remarkable",
            "resounding",
            "respected",
            "restored",
            "reward",
            "rewarding",
            "right",
            "robust",
            "safe",
            "satisfactory",
            "secure",
            "seemly",
            "simple",
            "skilled",
            "skillful",
            "smile",
            "soulful",
            "sparkling",
            "special",
            "spirited",
            "spiritual",
            "stirring",
            "stupendous",
            "stunning",
            "success",
            "successful",
            "sunny",
            "super",
            "superb",
            "supporting",
            "surprising",
            "terrific",
            "thorough",
            "thrilling",
            "thriving",
            "tops",
            "tranquil",
            "transforming",
            "transformative",
            "trusting",
            "truthful",
            "unreal",
            "unwavering",
            "up",
            "upbeat",
            "upright",
            "upstanding",
            "valued",
            "vibrant",
            "victorious",
            "victory",
            "vigorous",
            "virtuous",
            "vital",
            "vivacious",
            "wealthy",
            "welcome",
            "well",
            "whole",
            "wholesome",
            "willing",
            "wonderful",
            "wondrous",
            "worthy",
            "wow",
            "yes",
            "yummy",
            "zeal",
            "zealous"};

    /**
     * The max number of sprints to generate at low stress.
     */
    public static final int LOW_STRESS_MAX = 5;

    /**
     * The min number of sprints to generate at low stress.
     */
    public static final int LOW_STRESS_MIN = 1;

    /**
     * The max number of sprints to generate at medium stress.
     */
    public static final int MEDIUM_STRESS_MAX = 20;

    /**
     * The min number of sprints to generate at medium stress.
     */
    public static final int MEDIUM_STRESS_MIN = 10;

    /**
     * The max number of sprints to generate at high stress.
     */
    public static final int HIGH_STRESS_MAX = 100;

    /**
     * The min number of sprints to generate at high stress.
     */
    public static final int HIGH_STRESS_MIN = 50;

    /**
     * A pool of releases to choose from when creating a sprint.
     */
    private List<Release> releasePool;

    /**
     * A pool of backlogs to choose from when creating a sprint.
     */
    private List<Backlog> backlogPool;

    /**
     * A pool of teams to choose from when creating a sprint.
     */
    private List<Team> teamPool;

    /**
     * Sets the pool of releases to assign from. If null, releases will be generated.
     * @param releases The releases pool
     */
    public final void setReleasePool(final List<Release> releases) {
        this.releasePool = releases;
    }

    /**
     * Sets the pool of backlogs to assign from. If null, releases will be generated.
     * @param backlogs The backlogs pool
     */
    public final void setBacklogPool(final List<Backlog> backlogs) {
        this.backlogPool = backlogs;
    }

    /**
     * Sets the pool of teams to assign from. If null, releases will be generated.
     * @param teams The teams pool
     */
    public final void setTeamPool(final List<Team> teams) {
        this.teamPool = teams;
    }

    @Override
    @SuppressWarnings("checkstyle:magicnumber")
    public final Sprint generate() {
        final int weeksBetweenStartEnd = 6;
        final int daysBeforeRelease = 30;
        Sprint sprint = new Sprint();
        try {
            StringBuilder sprintName = new StringBuilder(GenerationHelper.randomElement(SPRINT_NAMES));
            sprintName.setCharAt(0, Character.toUpperCase(sprintName.charAt(0)));
            sprint.setShortName(sprintName.toString());
        } catch (CustomException e) {
            ErrorReporter.get().reportErrorSecretly(e, "SprintGenerator: setting short name failed");
        }
        try {
            if (releasePool.size() > 0) {
                Release release = releasePool.get(GenerationHelper.random(releasePool.size()));
                sprint.setAssociatedRelease(release);
                LocalDate releaseDate = release.getReleaseDate();
                LocalDate endDate = releaseDate.minusDays(GenerationHelper.random(1, daysBeforeRelease));
                LocalDate startDate = endDate.minusWeeks(GenerationHelper.random(1, weeksBetweenStartEnd));
                sprint.setStartDate(startDate);
                sprint.setEndDate(endDate);
                sprint.setLongName(String.format("Sprint from %s to %s",
                        startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)));
            }
            else {
                return null;
            }
        }
        catch (CustomException e) {
            ErrorReporter.get().reportErrorSecretly(e, "SprintGenerator: setting release failed");
        }
        if (backlogPool.size() > 0) {
            Backlog backlog = backlogPool.get(GenerationHelper.random(backlogPool.size()));
            sprint.setBacklog(backlog);
            List<Story> stories = new ArrayList<Story>();

            stories.addAll(backlog.getPrioritisedStories());

            if (stories.size() > 0) {
                int numStories = GenerationHelper.random(stories.size() + 1);
                for (int i = 0; i < numStories; i++) {
                    try {
                        sprint.addStory(stories.remove(GenerationHelper.random(stories.size())));
                    } catch (NotReadyException e) {
                        ErrorReporter.get().reportErrorSecretly(e, "SprintGenerator: setting stories failed");
                    }
                }
            }

        }
        else {
            return null;
        }
        if (teamPool.size() > 0) {
            Team team = teamPool.get(GenerationHelper.random(teamPool.size()));
            sprint.setTeam(team);
        }
        else {
            return null;
        }
        sprint.setDescription(NameGenerator.randomDescription());

        return sprint;
    }
}