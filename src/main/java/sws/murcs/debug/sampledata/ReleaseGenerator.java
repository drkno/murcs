package sws.murcs.debug.sampledata;

import sws.murcs.model.Project;
import sws.murcs.model.Release;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates RANDOM releases.
 */
public class ReleaseGenerator implements Generator<Release> {

    /**
     * The max number of releases generated on low stress.
     */
    protected static final int LOW_STRESS_MIN = 1;
    /**
     * The min number of releases generated on low stress.
     */
    protected static final int LOW_STRESS_MAX = 10;

    /**
     * The max number of releases generated on medium stress.
     */
    protected static final int MEDIUM_STRESS_MIN = 10;
    /**
     * The min number of releases generated on medium stress.
     */
    protected static final int MEDIUM_STRESS_MAX = 20;

    /**
     * The max number of releases generated on high stress.
     */
    protected static final int HIGH_STRESS_MIN = 20;
    /**
     * The min number of releases generated on high stress.
     */
    protected static final int HIGH_STRESS_MAX = 40;

    /**
     * The RANDOM used for RANDOM numbers within this class.
     */
    private static final Random RANDOM = new Random();

    /**
     * A list of default names for releases. They're all birds names.
     */
    private String[] defaultNames = {"Albatross",
            "Black-browed",
            "Black-footed",
            "Laysan",
            "Short-tailed",
            "Shy",
            "Anhinga",
            "Auklet",
            "Cassin",
            "Crested",
            "Least",
            "Parakeet",
            "Rhinoceros",
            "Avocet",
            "Beardless-Tyrannulet",
            "Bittern",
            "American",
            "Least",
            "Blackbird",
            "Brewer",
            "Red-winged",
            "Rusty",
            "Tricolored",
            "Yellow-headed",
            "Bluebird",
            "Mountain",
            "Western",
            "Bobolink",
            "Booby",
            "Brown",
            "Red-footed",
            "Brant",
            "Bufflehead",
            "Bunting",
            "Indigo",
            "Lark",
            "Lazuli",
            "Painted",
            "McKay",
            "Snow",
            "Varied",
            "Bushtit",
            "Canvasback",
            "Cardinal",
            "Catbird",
            "Chat",
            "Chickadee",
            "Chestnut-backed",
            "Mexican",
            "Mountain",
            "Chukar",
            "Coot",
            "Cormorant",
            "Brandt",
            "Double-crested",
            "Neotropic",
            "Pelagic",
            "Cowbird",
            "Bronzed",
            "Brown-headed",
            "Crane",
            "Creeper",
            "Crossbill",
            "Crow",
            "American",
            "Fish",
            "Northwestern",
            "Cuckoo",
            "Curlew",
            "Dipper",
            "Dotterel",
            "Dove",
            "Common Ground",
            "Eurasian Collared",
            "Inca",
            "Mourning",
            "White-winged",
            "Dowitcher",
            "Long-billed",
            "Short-billed",
            "Duck",
            "American Black",
            "Falcated",
            "Fulvous Whistling",
            "Harlequin",
            "Long-tailed",
            "Ring-necked",
            "Ruddy",
            "Tufted",
            "Wood",
            "Dunlin",
            "Eagle",
            "Golden",
            "Bald",
            "Egret",
            "Cattle",
            "Great",
            "Reddish",
            "Snowy",
            "Falcon",
            "Peregrine",
            "Prairie",
            "Finch",
            "Cassin",
            "House",
            "Purple",
            "Flicker",
            "Flycatcher",
            "Ash-throated",
            "Brown-crested",
            "Buff-breasted",
            "Cordilleran",
            "Dusky",
            "Gray",
            "Hammond",
            "Olive-sided",
            "Pacific-slope",
            "Scissor-tailed",
            "Sulphur-bellied",
            "Vermilion",
            "Willow",
            "Yellow-bellied",
            "Fulmar",
            "Gadwall",
            "Gallinule",
            "Common",
            "Purple",
            "Gannet",
            "Gnatcatcher",
            "Black-tailed",
            "Blue-gray",
            "California",
            "Godwit",
            "Bar-tailed",
            "Hudsonian",
            "Marbled",
            "Goldeneye",
            "Barrow",
            "Common",
            "Goldfinch",
            "American",
            "Lawrence",
            "Lesser",
            "Goose",
            "Cackling",
            "Canada",
            "Greater White-fronted",
            "Ross",
            "Snow",
            "Grackle",
            "Boat-tailed",
            "Common",
            "Great-tailed",
            "Grebe",
            "Clark",
            "Eared",
            "Horned",
            "Pied-billed",
            "Red-necked",
            "Western",
            "Grosbeak",
            "Black-headed",
            "Blue",
            "Evening",
            "Pine",
            "Rose-breasted",
            "Guillemot",
            "Gull",
            "Bonaparte",
            "California",
            "Franklin",
            "Glaucous",
            "Glaucous-winged Gull",
            "Great Black-backed",
            "Heermann",
            "Herring",
            "Kelp",
            "Laughing",
            "Lesser Black-backed",
            "Mew",
            "Ring-billed",
            "Sabine",
            "Swallow-tailed",
            "Thayer",
            "Western",
            "Harrier",
            "Hawk",
            "Broad-winged",
            "Cooper",
            "Ferruginous",
            "Gray",
            "Harris",
            "Red-shouldered",
            "Red-tailed",
            "Rough-legged",
            "Sharp-shinned",
            "Swainson",
            "Zone-tailed",
            "Heron",
            "Black-crowned Night",
            "Great Blue",
            "Green",
            "Tricolored",
            "Yellow-crowned Night",
            "Hummingbird",
            "Allen",
            "Anna",
            "Black-chinned",
            "Blue-throated",
            "Broad-billed",
            "Broad-tailed",
            "Calliope",
            "Costa",
            "Lucifer",
            "Magnificent",
            "Rufous",
            "White-eared",
            "Ibis",
            "Glossy",
            "White",
            "White-faced",
            "Jackdaw",
            "Jaeger",
            "Long-tailed",
            "Parasitic",
            "Pomarine",
            "Jay",
            "Blue",
            "Florida Scrub",
            "Green",
            "Island Scrub",
            "Mexican",
            "Pinyon",
            "Steller",
            "Western Scrub",
            "Junco",
            "Dark-eyed",
            "Yellow-eyed",
            "Kestrel",
            "Killdeer",
            "Kingbird",
            "Cassin",
            "Eastern",
            "Tropical",
            "Western",
            "Kingfisher",
            "Kinglet",
            "Golden-crowned",
            "Ruby-crowned",
            "Kiskadee",
            "Kite",
            "Swallow-tailed",
            "White-tailed",
            "Kittiwake",
            "Knot",
            "Lark",
            "Limpkin",
            "Longspur",
            "Loon",
            "Arctic",
            "Common",
            "Pacific",
            "Red-throated",
            "Yellow-billed",
            "Magpie",
            "Black-billed",
            "Yellow-billed",
            "Mallard",
            "Martin",
            "Meadowlark",
            "Eastern",
            "Western",
            "Merganser",
            "Common",
            "Hooded",
            "Red-breasted",
            "Merlin",
            "Mockingbird",
            "Murre",
            "Murrelet",
            "Marbled",
            "Scripps",
            "Nighthawk",
            "Nutcracker",
            "Nuthatch",
            "White-breasted",
            "Pygmy",
            "Red-breasted",
            "Oriole",
            "Altamira",
            "Baltimore",
            "Bullock",
            "Hooded",
            "Orchard",
            "Scott",
            "Osprey",
            "Ovenbird",
            "Owl",
            "Barn",
            "Barred",
            "Burrowing",
            "Eastern Screech",
            "Elf",
            "Great Horned",
            "Long-eared",
            "Northern Pygmy",
            "Northern Saw-whet",
            "Short-eared",
            "Spotted",
            "Western Screech",
            "Whiskered Screech",
            "Oystercatcher",
            "Pelican",
            "American White",
            "Brown",
            "Petrel",
            "Black-capped",
            "Fea",
            "Hawaiian",
            "Pewee",
            "Greater",
            "Western Wood",
            "Phainopepla",
            "Phalarope",
            "Red",
            "Red-necked",
            "Wilson",
            "Pheasant",
            "Phoebe",
            "Black",
            "Eastern",
            "Say",
            "Pigeon",
            "Band-tailed",
            "Rock",
            "Pintail",
            "Pipit",
            "American",
            "Red-throated",
            "Plover",
            "American Golden",
            "Black-bellied",
            "Greater Sand",
            "Mountain",
            "Pacific Golden",
            "Piping",
            "Semipalmated",
            "Western Snowy",
            "Wilson",
            "Ptarmigan",
            "Puffin",
            "Atlantic",
            "Horned",
            "Tufted",
            "Pyrrhuloxia",
            "Quail",
            "California",
            "Gambel",
            "Montezuma",
            "Mountain",
            "Scaled",
            "Rail",
            "California Black",
            "King",
            "Ridgway",
            "Virginia",
            "Raven",
            "Chihuahuan",
            "Common",
            "Razorbill",
            "Redhead",
            "Redpoll",
            "Redstart",
            "American",
            "Painted",
            "Roadrunner",
            "Robin",
            "Ruff",
            "Sanderling",
            "Sandpiper",
            "Baird",
            "Buff-breasted",
            "Least",
            "Pectoral",
            "Rock",
            "Semipalmated",
            "Solitary",
            "Spotted",
            "Stilt",
            "Western",
            "White-rumped",
            "Wood",
            "Sapsucker",
            "Red-breasted",
            "Red-naped",
            "Williamson",
            "Yellow-bellied",
            "Scaup",
            "Greater",
            "Lesser",
            "Scoter",
            "Surf",
            "White-winged",
            "Shearwater",
            "Black-vented",
            "Buller",
            "Cory",
            "Flesh-footed",
            "Great",
            "Manx",
            "Pink-footed",
            "Short-tailed",
            "Sooty",
            "Shoveler",
            "Shrike",
            "Loggerhead",
            "Siskin",
            "Skimmer",
            "Skua",
            "Great",
            "South Polar",
            "Snipe",
            "Solitaire",
            "Sora",
            "Sparrow",
            "American Tree",
            "Bell",
            "Black-chinned",
            "Black-throated",
            "Botteri",
            "Brewer",
            "Chipping",
            "Clay-colored",
            "Fox",
            "Golden-crowned",
            "Grasshopper",
            "Harris",
            "House",
            "Lark",
            "Lincoln",
            "Nelson",
            "Olive",
            "Rufous-winged",
            "Rufous-crowned",
            "Savannah",
            "Song",
            "Swamp",
            "Vesper",
            "White-crowned",
            "White-throated",
            "Spoonbill",
            "Starling",
            "Starthroat",
            "Stilt",
            "Stint",
            "Stork",
            "Storm-Petrel",
            "Ashy",
            "Band-rumped",
            "Black",
            "Fork-tailed",
            "Leach",
            "Wilson",
            "Surfbird",
            "Swallow",
            "Bank",
            "Barn",
            "Cave",
            "Cliff",
            "Northern Rough-winged",
            "Tree",
            "Violet-green",
            "Swan",
            "Swift",
            "Vaux",
            "White-throated",
            "Tanager",
            "Flame-colored",
            "Hepatic",
            "Scarlet",
            "Summer",
            "Western",
            "Tattler",
            "Teal",
            "Blue-winged",
            "Cinnamon",
            "Green-winged",
            "Tern",
            "Arctic",
            "Black",
            "Caspian",
            "Common",
            "Elegant",
            "Forster",
            "Least",
            "Royal",
            "Sandwich",
            "Thrasher",
            "Bendire",
            "Brown",
            "California",
            "Curve-billed",
            "Long-billed",
            "Sage",
            "Thrush",
            "Gray-cheeked",
            "Hermit",
            "Swainson",
            "Varied",
            "Wood",
            "Titmouse",
            "Black-crested",
            "Bridled",
            "Oak",
            "Towhee",
            "Abert",
            "California",
            "Canyon",
            "Eastern",
            "Green-tailed",
            "Spotted",
            "Trogon",
            "Turkey",
            "Turnstone",
            "Black",
            "Ruddy",
            "Veery",
            "Verdin",
            "Vireo",
            "Bell",
            "Blue-headed",
            "Cassin",
            "Hutton",
            "Plumbeous",
            "Red-eyed",
            "Warbling",
            "Yellow-green",
            "Vulture",
            "Black",
            "Turkey",
            "Wagtail",
            "Warbler",
            "Blue-winged",
            "Black-and-white",
            "Blackburnian",
            "Blackpoll",
            "Black-throated Gray",
            "Black-throated Green",
            "Black-throated Blue",
            "Canada",
            "Chestnut-sided",
            "Golden-cheeked",
            "Grace",
            "Hermit",
            "Hooded",
            "Kentucky",
            "Lucy",
            "MacGillivray",
            "Magnolia",
            "Nashville",
            "Orange-crowned",
            "Palm",
            "Prothonotary",
            "Red-faced",
            "Tennessee",
            "Townsend",
            "Wilson",
            "Worm-eating",
            "Yellow",
            "Yellow-rumped",
            "Yellow-throated",
            "Waterthrush",
            "Waxwing",
            "Whimbrel",
            "Wigeon",
            "American",
            "Eurasian",
            "Willet",
            "Woodpecker",
            "Acorn",
            "Arizona",
            "Black-backed",
            "Downy",
            "Gila",
            "Golden-fronted",
            "Hairy",
            "Ladder-backed",
            "Lewis",
            "Nuttall",
            "Red-bellied",
            "White-headed",
            "Wren",
            "Bewick",
            "Cactus",
            "Canyon",
            "Carolina",
            "House",
            "Marsh",
            "Pacific",
            "Rock",
            "Wrentit",
            "Yellowlegs"
    };

    /**
     * A list of descriptions for releases.
     */
    private String[] descriptions = {"A release date",
            "The time when it has to be ready",
            "That's not enough time",
            "Another random release dates",
            "Is anyone even reading this",
            "I don't like doing work so don't release this"
    };

    /**
     * The pool of projects to be linked to the releases.
     */
    private List<Project> projectPool;
    /**
     * The project generator to be used with this releases generator.
     */
    private Generator<Project> projectGenerator;

    /**
     * Sets up a random release.
     */
    public ReleaseGenerator() {
        this.projectGenerator = new ProjectGenerator();
    }

    /**
     * Sets up a random release with a from one of the given descriptions.
     * @param newDescription The given descriptions
     * @param newProjectGenerator The generator to be used for the generation of projects
     */
    public ReleaseGenerator(final Generator<Project> newProjectGenerator, final String[] newDescription) {
        this.descriptions = newDescription;
        this.projectGenerator = newProjectGenerator;
    }

    /**
     * Sets the Project pool for the generator.
     * @param newProjectPool The project pool
     */
    public final void setProjectPool(final ArrayList<Project> newProjectPool) {
        this.projectPool = newProjectPool;
    }

    /**
     * Sets the project generator for use in creating more projects if necessary.
     * @param newProjectGenerator project generator to use while generating releases.
     */
    public final void setProjectGenerator(final Generator<Project> newProjectGenerator) {
        this.projectGenerator = newProjectGenerator;
    }

    /**
     * Generates a list of projects if there isn't already a pool of projects to choose from.
     * @param min The min number of projects
     * @param max The max number of projects
     * @return The array list of generated projects
     */
    private List<Project> generateProjects(final int min, final int max) {
        ArrayList<Project> generated = new ArrayList<>();
        int projectCount = NameGenerator.random(min, max);

        if (projectPool == null) {
            for (int i = 0; i < projectCount; i++) {
                Project newProject = projectGenerator.generate();
                if (!generated.stream().filter(project -> newProject.equals(project)).findAny().isPresent()) {
                    generated.add(newProject);
                }
            }
        } else {
            if (projectCount > projectPool.size()) {
                projectCount = projectPool.size();
            }

            for (int i = 0; i < projectCount; i++) {
                Project project = projectPool.remove(NameGenerator.random(projectPool.size()));
                generated.add(project);
            }

            for (Project project : generated) {
                projectPool.add(project);
            }
        }
        return generated;
    }

    @Override
    public final Release generate() {
        Release r = new Release();

        String shortName = NameGenerator.randomElement(defaultNames);
        String description = NameGenerator.randomElement(descriptions);
        LocalDate releaseDate = LocalDate.of(RANDOM.nextInt(130) + 1970, RANDOM.nextInt(12) + 1, RANDOM.nextInt(28)
                + 1);

        List<Project> projects = generateProjects(1, 5);

        try {
            r.setShortName(shortName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
            //Don't need to do anything here as it's just generation
        }

        try {
            projects.get(NameGenerator.random(projects.size())).addRelease(r);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        r.setDescription(description);
        r.setReleaseDate(releaseDate);

        return r;
    }
}
