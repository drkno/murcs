package sws.murcs.debug.sampledata;

import sws.murcs.model.Person;
import sws.murcs.model.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates random teams with people.
 */
public class TeamGenerator implements Generator<Team> {
    /**
     * The max number of projects generated when stress level is low.
     */
    public static final int LOW_STRESS_MAX = 5;
    /**
     * The min number of projects generated when stress level is low.
     */
    public static final int LOW_STRESS_MIN = 1;

    /**
     * The max number of projects generated when stress level is medium.
     */
    public static final int MEDIUM_STRESS_MAX = 10;
    /**
     * The min number of projects generated when stress level is medium.
     */
    public static final int MEDIUM_STRESS_MIN = 5;

    /**
     * The max number of projects generated when stress level is high.
     */
    public static final int HIGH_STRESS_MAX = 20;
    /**
     * The min number of projects generated when stress level is high.
     */
    public static final int HIGH_STRESS_MIN = 10;

    /**
     * An array of team names.
     */
    private static final String[] PREDEFINED_TEAM_NAMES = {"-... .- - -- .- -.",
            "Team 10",
            "1",
            "120/-",
            "196 Jefe Shark Hats",
            "2+2=5",
            "Team 23,31",
            "2 Legit 2 Quit",
            "3H Sonic Death Monkey",
            "Team 420",
            "The 47",
            "4 Angry Monkeys",
            "4B4U",
            "4 Fast 4 Furious",
            "Abort, Retry, Ignore?",
            "AC Durand",
            "A Center for Ants",
            "Acme",
            "ACME, reloaded",
            "Ada",
            "A Different View",
            "Advil",
            "Team Advil",
            "Alarm Clock Catastrophe",
            "All-Day Solvers",
            "All I want is Bang Bang Bang!",
            "All-Volunteer Signal Corps",
            "Ambliguity",
            "Analysis Paralysis",
            "Andalusional",
            "The Angriest Little Warthog You Ever Saw",
            "Angry Monkeys",
            "Annyong",
            "Anonymice",
            "Team Aparecium",
            "Aquatic Groundhog Busters in Translation",
            "Arch Baddies",
            "Army of Darkness",
            "Ask to Answer",
            "Austin Drunks",
            "Avast, Ye Scurvy Landlubbers",
            "Awe and Shock",
            "Team Awesome",
            "Awesomesauce",
            "Baby Seals",
            "Bad Hair Day",
            "Bad Horse",
            "Banana Avenue",
            "BANANAPHONE",
            "Bananaphone",
            "Team Bananaphone",
            "Barf Barf Barf",
            "The 'B' Ark",
            "Barking Squids",
            "BAT",
            "Bat Alarm",
            "Batman and Robin",
            "Bay City Strollers",
            "Bean there, done that.",
            "Beat Poet Wizards",
            "Beeblebrox",
            "Bee Gees",
            "Beginner's Luck",
            "Belligerent Berkeley Badgers",
            "Beta Bots",
            "B-Factor",
            "Big Daddies",
            "Bigfield Fighting Koobish",
            "Biggest Fake Liar",
            "Bill Brasky",
            "Bioluminati",
            "BiPolar Bears",
            "the Birdie Squad",
            "Blackberry Pie",
            "Blackbird Pie",
            "Black Jelly Beanz",
            "Blind Pigs & Acorns",
            "Team Blood",
            "Blood & Bob",
            "Blood & Bones",
            "Blood & Booze",
            "Blue Carbuncle",
            "The Blue Carbuncle",
            "Blue Carbuncles",
            "Boar Cowbell",
            "Boneless Chicken Cabaret",
            "Team Bones",
            "Booyah",
            "Booyah!",
            "Boxfort Brigands",
            "Brains Aren't Everything",
            "Brains in Jars",
            "Brain Tweezers",
            "Briny Deep",
            "Team Briny Deep",
            "Brown All-Stars",
            "Brute Force",
            "Buckyballers",
            "Bulletproof Monks",
            "The Burninator",
            "Burninators",
            "The Burninators",
            "Burnin' Beaters",
            "Burning Bears",
            "Burning Leprechauns",
            "Burnin' Î·s",
            "Burton United 145",
            "Buttered Toast",
            "Buzz Lime Pi",
            "Buzz Lime Pi and Friends",
            "BWhite",
            "Cachers and Dashers",
            "Caffeiene Aftershocks",
            "Caffeine Aftershocks",
            "Cajun Pistols",
            "Caltech Puzzle Club",
            "Caramel Turtle Brownies",
            "Cardinal",
            "The Cat Is #1!!",
            "The Cat is #1 !!",
            "Central Poles",
            "Central Services West",
            "CGNU",
            "Chaos",
            "The Chaotic-Neutral Mid-Afternoon Archers What Arch at Tea-Time",
            "The Charlie Sheens",
            "Chasing Failure",
            "Cheese-Eating Surrender Monkeys",
            "Chilean Chasm Cannibals",
            "Chimera",
            "Chip 'n Reichen Rescue Rangers",
            "Chloe's Pooper Scoopers",
            "Team Chowder",
            "Team Chupacabra",
            "Cindy's Booze-Swilling Hedonism",
            "Cindy's Teetotal Abstemiousness",
            "Clan Not Appearing In This Event",
            "Claude E. Shannon has a Posse",
            "Clearly Ambiguous",
            "Cleaver's Cretins",
            "Clueless",
            "clueless",
            "Cocoa Puffs",
            "Codex",
            "Codex Ixtlilxochitl",
            "Code Yellow",
            "Code Yellow / Snow Job",
            "Coed Astronomy",
            "Coed astronomy",
            "coed astronomy",
            "Cold Burritos",
            "Colon-Inflating Rhinoceri",
            "Comic Sans Francisco",
            "Command Line Prompts",
            "Commonwealth",
            "Condimental Bratwurst",
            "Confidential Breakfast",
            "Consumer Recreation Services",
            "Continental Breakfast",
            "Couging it",
            "Cows Rule",
            "Cracking Good Toast",
            "C.R.A.N.E.A.",
            "CRANEA",
            "Crappy Raisins",
            "Crazy Cat Lady and the Puzzle Heads",
            "Crazy Robot",
            "CrazyRobot",
            "Creepius Adventure Team",
            "Creepy Monkey",
            "Cruciverbalists",
            "The Crunchberries",
            "Crypto-Fashionistas",
            "Cuatro Thinko",
            "Cultural Poncho",
            "Cylon and Garfunkel",
            "Dabney House",
            "Dang Bang",
            "Dashing Through the Snow",
            "DASH the way, uh-huh, uh-huh, we like it.",
            "Dash What She Said",
            "DCIB",
            "Death From Above",
            "Death of Dr. Zero",
            "The Deciders",
            "Decolletage Against The Machine",
            "Derelict Detectives",
            "Desert Steed",
            "Desert Taxi",
            "Desert taxi",
            "Desperaux",
            "Dessert Taxi",
            "Destroy Monaco",
            "Destroy Monaco!",
            "destroy monaco",
            "destroy monaco!",
            "Dirty Ashley",
            "Disobedient Children",
            "Disorganized Crime",
            "Divide and Conquer",
            "Doddering Fools",
            "Dodecaceratops",
            "Doggone It",
            "Doh! Boys",
            "Dominion Domination",
            "Domokun Allstars",
            "Donner, Party of 30",
            "Do Not Write Below This Line",
            "Don't Shave the Messenger",
            "DorkBots",
            "The Dot-Dashing Fists of Righteousness",
            "Double + Good",
            "Dragon Army",
            "Dr Horrible",
            "Dr. Horrible",
            "Drunkchair",
            "Drunken Spelunkin'",
            "Drunken Spider",
            "Drunken Spiders",
            "Dry Ice",
            "Dumbledore is Dead",
            "Dusty Heat Sink, Molten Pl.",
            "Dusty Heat Sink, Molten Plastic",
            "Earthquake Collaborative",
            "Eating Buildings",
            "Echo Chamber",
            "Team Ecru",
            "Edamame",
            "Edible Torus",
            "Team Egads!",
            "Eigenpirates!",
            "eleMENTAL",
            "e-LEMON-aters",
            "Elevensies",
            "EmporalFor",
            "Enigma",
            "Errant Gamers",
            "Events Of The Day",
            "Everybody Loves Nutella",
            "Everybody loves Nutella",
            "Everyday Heroes",
            "Everyone Loves Nutella",
            "Evil Geniuses for a Better Tomorrow",
            "Evil Geniuses for a Better Tomorrow!",
            "Evil League of Evil",
            "The Evil League of Evil",
            "Evil Masterminds",
            "The Evil Midnight Bombers What Bomb At Midnight",
            "Evil Wizards For A Better Tomorrow",
            "Team Extreme!",
            "Fantasticans",
            "Ferrous Phoenix",
            "The Fighting Mongooses",
            "Fish in the Percolator",
            "Fishstick Mess",
            "Fishtick Mess",
            "Fistic Mess",
            "Fitter Happier",
            "Foldable Tupperware",
            "Folsom Furlough",
            "Forks on Fire",
            "Four Angry Pencils",
            "Four Score and Seven",
            "Foxtrot Delta Bravo",
            "Frazzles Bedazzled Razzle-ma-dazzlers (with pizzaz)",
            "Friday the 13th Part VI",
            "Friends of Matt P.",
            "The Frumious Bandersnatch",
            "Team Fubstad",
            "FunFunFunFun",
            "Fun in the Fog",
            "Furry Animals",
            "Fuzzy Green Knittens",
            "Galactic Trendsetters",
            "The Gashing Dents",
            "Gender-Neutral Pac Person",
            "General Byzantines",
            "The Gentlemen Spies",
            "Geo-Puzzlers",
            "Geo-puzzlers",
            "Get on a Burning Raft",
            "Get On A Raft With Taft",
            "Get on a Raft with Taft",
            "Ghost Patrol",
            "Ghoul-done Golems",
            "Giant Die Protocol",
            "The Gipper",
            "GipperTech Security Solutions",
            "Give us the Coin and Nobody Gets Hurt",
            "GÃ¸ldÃªn GÃ¸lÃ¨ms",
            "Gloom Legends",
            "Glurmun",
            "glurmun",
            "Go Apple",
            "Go Apple!",
            "Go Banana",
            "Go Banana!",
            "Go Barrel",
            "God Knows Stiglers",
            "Go Figure",
            "Go Gadget Go",
            "The Goldbach Conjecture",
            "Golden Golems",
            "Team Goldfish",
            "The Goody-goody Leroy Browns",
            "Go Orange",
            "Go Pineapple!",
            "Gospel Tentacle Revival",
            "Gotham Knights",
            "Team Greaseball",
            "Grey Goo",
            "Grier Family Picnic",
            "Grillin' Ayn Rand",
            "Groovy Ghoulies",
            "Haberdashers",
            "Halcyon Hunters",
            "Hammer Squirrel",
            "Harish Memorial",
            "Hasty Scribbles",
            "Hella Minerals",
            "Helvetica Scenario",
            "The Herbs",
            "Here Be Dragons",
            "heXXes",
            "Hey Santina, It's Ryan",
            "hi5",
            "Team Hiphopopotamus",
            "The Hit List",
            "The Hive",
            "the hive",
            "Honey Badgers",
            "The Honey Badgers",
            "Honorary Waddle",
            "Hookd on Fonix",
            "Hooked on Phonics",
            "Hooligans",
            "Hostel California",
            "Hotfix",
            "The House of Wax",
            "H > S",
            "Hunters and Gatherers",
            "Hunt or Be Hunted",
            "Hunt or be Hunted",
            "Huskie ladies",
            "I can't read this",
            "I'd Buy That for a Dollar!",
            "IDK",
            "If there's a problem, yo, we'll solve it",
            "Illini Alumni",
            "I Love Lucas",
            "Immoral, Illegal & Fattening",
            "I'm On a Boat",
            "Incognito",
            "Team Infinite Wendy's",
            "The Inner Loop",
            "Inquisitive Robots",
            "Inspector Gadget",
            "Interesting Person, Inc",
            "In Theory",
            "Invictus",
            "Island",
            "i tre amici (maggiorato di uno)",
            "It's Complicated with Lucas",
            "JAJA!",
            "jaja!",
            "Jean Paul Sartre and the Existential Crisis",
            "Jean-Paul Sartre and the Existential Crisis",
            "Jen's Team",
            "Jessie and the Rippers",
            "JJ and the Jerks",
            "Joint Venture",
            "Judean Person's Front",
            "Judean Peoples' Front",
            "judean people's front",
            "Juiced",
            "Jumanji",
            "Just For the Halibut",
            "Just for the Halibut NYC",
            "Just Keep Swimming",
            "Kangaroo Kritters",
            "Kappa Sig!",
            "Karrots and Kielbasa",
            "Kazakhstan",
            "keyless entry",
            "Killer Bees",
            "King of the County",
            "Kitmund Fan Club",
            "Kittens Kittens Kittens",
            "Kittens, Kittens, Kittens",
            "kittens kittens kittens",
            "Knights Who Say Ni",
            "Know Nothings",
            "Team Lactose",
            "Lake Effect Snow",
            "Lakuni",
            "Languid Pandas",
            "The Lazy Bears",
            "Lead Pipe, Conservatory",
            "The League of Extradorinary Puzzlemen",
            "League of Extraordinary Puzzlemen",
            "The League of Extraordinary Puzzlemen",
            "Learninators",
            "Left Out",
            "Left Out East",
            "Lego Builders",
            "Lego My Ego",
            "Les Espions",
            "Let's Get Jakarta",
            "Liboncatipu",
            "Libra My Puzzle Pisces Alone",
            "LIFO",
            "Light Benders",
            "Little Lunch",
            "Loaded Bonbons",
            "Longshots",
            "Team Longshots",
            "Look, Nerdlings",
            "Loopie Moos",
            "Los Jefes",
            "Low Expectations",
            "lowkey",
            "LÃ¶s JÃ«fÃ«s",
            "Luckin' 'Eds",
            "Ludicrous Speed",
            "LXP",
            "Magellan",
            "Team Maize",
            "malo por naturaleza",
            "Mango Gelato",
            "Manic Sages",
            "Manifest Destiny",
            "Man In Spandex",
            "Marvelous Milk Duds",
            "The Master Theorem",
            "Matching Clipboard Ensemble",
            "Mathletes",
            "The Mathletes",
            "Max Power",
            "mbResoTrax",
            "M.C. Binary and the Five-Bit Crew",
            "Meat Machine",
            "Meat Wagon",
            "Medieval Cowboys",
            "Team Mediocrity",
            "Medusa Cascade",
            "Men in Slacks",
            "Metaphysical Plant",
            "MI7",
            "MichMash",
            "Midori No Kaiju",
            "Midori no Kaiju",
            "Midtown Doornail",
            "Midway Monsters",
            "The Mighty Tonguetippers",
            "Minimum Work Maximum Pay",
            "Minions",
            "Mixed Mammals",
            "Mixed mammals",
            "Mojado Style",
            "The Monastic Dream Team",
            "Mondegrue",
            "Monkeys with Symbols",
            "Monks and More",
            "Moops",
            "The Moops",
            "moosecapades",
            "More Honey, Shorter Flowers",
            "Mostly Harmless",
            "Moustache",
            "Mr. Picklehead and the Talking Bike",
            "Much Tensor",
            "Muffin and Fluffers",
            "Muffin & Fluffers",
            "Muhuhuhahaha",
            "Mu Mu Mu",
            "Muï¬ƒn and Fluï¬€ers",
            "MWT",
            "My spoon is too big",
            "Mystic Fish",
            "My World 2.0",
            "(name unknown)",
            "Neal & Eva",
            "Nekketsu High School Puzzle Solving Club",
            "Nerv",
            "Nevermind the Balrogs",
            "New Kids on the Block",
            "New Kids on the block",
            "Ninja Squad 35b",
            "No B",
            "Nomads",
            "No Name",
            "Noodlers",
            "Team Not Appearing in this DASH",
            "No Torching!",
            "Oberhashi",
            "Oberhasli",
            "Occam's Unicorn",
            "Occults Only",
            "Occults spinoff",
            "Oddjobs",
            "Odds 'n 'Eds",
            "Off Like a Dirty Shirt",
            "Team of Pain",
            "Okee Dokee",
            "Older and Wiser",
            "Om Nom Nom",
            "Om Nom Nom Nom",
            "One Clove",
            "Ongoing Doorhinge Debate",
            "Team Onigiri",
            "Orange Snood",
            "Order of the Knights of the Whacktogon",
            "osum",
            "Oversight Committee",
            "Ox on the Roof",
            "Pacific Trusters",
            "Palindrome",
            "P.A.S.T.",
            "Pedestrians",
            "The Pedestrians",
            "Pentropy",
            "Phantom Phoenix",
            "Phlogiston",
            "Physical Plant",
            "Picklepuss",
            "Pickles and Peaches",
            "Pinata Beaters",
            "Piranhas in a Bathtub",
            "Pirate Mojo",
            "pirate party",
            "Pisces",
            "pkkwip",
            "Plagued By Indecision",
            "Plagued by Indecision",
            "The Platonic Solids",
            "plugh",
            "PMB",
            "Poppycock",
            "Power and Glory",
            "Power Source",
            "Power Team",
            "Pretty Pretty Panda",
            "Pretty Pretty Pandas",
            "Probably Semaphore",
            "Procrastinated",
            "Project Electric Mayhem",
            "Project Electric Mayhem Alpha",
            "Team Prometheus",
            "Pug Smugglers",
            "Puzzle Fighters",
            "Puzzle Jumpers",
            "Puzzle Knights",
            "The Puzzle Ninjas",
            "Quadrivial Pursuit",
            "Quarks and Gluons",
            "Quisquose",
            "RadiKS",
            "Ragnarokstars",
            "Rainbow Unicorns",
            "Ram A Lam A Ding Dong",
            "Random",
            "Rebus Monkeys",
            "Reckless Endangerment",
            "Red 5",
            "Red Harvest",
            "The Red Sea DespARRRadoes",
            "Red Velvet",
            "Release the McMaken",
            "Resumed Mad",
            "RIPD",
            "Robot Unicorns",
            "Team Rocket",
            "Rosebud",
            "Rubicon",
            "Runnin' down a dream",
            "Rush Hour 4",
            "Sandwich!",
            "Sandy Cabbies",
            "Sassy Sauce Pots",
            "Saturday Knight Fever",
            "Saturday Night Lights",
            "The Sauce Is Bubbling",
            "Save the Manatees",
            "Scenic Travelers",
            "Schemedly McWang",
            "Schmedley McWang",
            "Schroedinger's Litter box",
            "Scooby Doobies",
            "Scootin' Around",
            "Screaming Squids",
            "Scrivener Castle",
            "SCRuBBers",
            "The SCSIs",
            "Serious Business",
            "Setec Astronomy",
            "Sevely Hillbillies",
            "Seven of Diamonds",
            "SF Ghost Busters",
            "SFU",
            "Shadow of the Pig",
            "Shagadelic",
            "Shakakaka",
            "Shake n' Bacon",
            "Shaq and Kobe",
            "Shaq & Kobe Return",
            "SharkBait",
            "Team SharkBait",
            "Sharks with Laser Beams",
            "Shave the manatees",
            "Shock and Awe",
            "Sidney Poitier Is Extremely Sexy",
            "Silly Hat Brigade",
            "The Silly Hat Brigade",
            "Silly Hats",
            "Silly Hats Only",
            "Silly Pandas",
            "Silver Autonomy",
            "Simple Gibberish Transforms",
            "Simple Minds",
            "Sister Act",
            "Slap Happy",
            "Slartibartfast",
            "Sleep Train",
            "Slugs",
            "Smash and Dash",
            "SmashTheDash",
            "Smokin' Bears",
            "The Smoking GNU",
            "Smoking High Monks",
            "Smoldering Yak",
            "SMRT",
            "Smugglers' 4",
            "Smurfs",
            "Snakes on a Bang",
            "Team Snot",
            "Snout",
            "Snow Job",
            "SoCo Puzzlers",
            "Sol Survivors",
            "Somerset Puzzlers",
            "Somethings",
            "Sonar Security",
            "Team South Bay",
            "Soviet Surprise",
            "Spaceball One",
            "Space Cops",
            "Space Horse",
            "Spark",
            "Team Spaz",
            "Team Spaz!",
            "Spicy Mustard",
            "Spider Gromits",
            "S.P.I.E.S.",
            "SPIES",
            "Spies",
            "Spinach Dip",
            "Sports Racers",
            "Square Puppies",
            "Team Squid",
            "Staggering Geniuses",
            "Stalkin' 'Eds",
            "Stanford Sinkhole",
            "Statler",
            "Still Trying to Decide",
            "Stoker? We hardly know 'er!",
            "Stonecutters",
            "Stone,Wheat,Stone,Wheat,Stone",
            "The Storks",
            "Straight Cash Homey",
            "Strawberry House",
            "Team Stronger",
            "Sub-Atomic Jive",
            "Suit Up!",
            "Summer Project",
            "Super Karate Monkey Death Car",
            "SUPER PALS",
            "Super Team Awesome",
            "S-wÃ¸rds-EcrÃ¼",
            "Taco Revolution",
            "Tag and Release",
            "Tag & Release",
            "Talkin' 'Eds",
            "Talkinâ€™ â€™Eds",
            "Talking Eds",
            "The Tan Gents",
            "The Taxidermists",
            "Team Team",
            "test",
            "Tetes Sans Poulets",
            "Tezam",
            "teZam",
            "Thank You Sanjay Gupta",
            "There Be Dragons",
            "There's a Shortage of Chairs",
            "They Came Out of the Trees, Man",
            "They Who Shall Not be Named (or Just Are Not Yet Named...)",
            "This Space Intentionally Left Blank",
            "Thoroughbreds of Sin",
            "TLA",
            "TLA Lovers Anonymous",
            "TLA Loves Alice",
            "Tonic Immobility",
            "Too Close To The Sun",
            "TooLateToApply",
            "Too Much Clue",
            "Top of the Bottoms",
            "Torrance Area Gamers Society",
            "Total Perspective Vortex Survivors",
            "Tracers",
            "Trust us, we're Scientists!",
            "Turbo Puzzle Fighters",
            "Tvelve Metchsteek",
            "The Twainsters",
            "Twainsters",
            "UFO",
            "ufo",
            "Undead Defense Corps(e)",
            "Undead Last",
            "Undead Person's Front",
            "Underdog Nillionaires",
            "Underground Rodeo",
            "Unibangers",
            "Unibangers (wheelless)",
            "Universal Solvent",
            "Unlicensed Nuclear Accelerator",
            "Unseen",
            "Team Unwanted Pregnancy",
            "Up Late",
            "The Usual Suspects",
            "Usual Suspects",
            "Valid Etcetera",
            "Valid etcetera",
            "varphi",
            "Vector Fields, Mountain Climbing, Shark Attacks and All that Jazz",
            "Very Knight!",
            "Vetri",
            "Vigilantes",
            "Viscosity Breakdown",
            "Volcanic",
            "Vulgar Boatmen",
            "Waldorf",
            "Team Walkin' Eds",
            "Walkin' Eds",
            "Team Wallace & Gromit",
            "Walter & Neil Escape from Guantanamo Bay",
            "Wandering Daffodils",
            "Wannabe Monks",
            "Warming Maylase",
            "Warning! Do Not Ingest",
            "Warning! Hallucinogenic",
            "Warning! Ill Tempered Sea Bass!",
            "Watermelon Sundial",
            "Weebles",
            "We're Doomed",
            "Wet Coodles",
            "Whack o' Lanterns",
            "What is a Puzzle?",
            "What is Leg?",
            "White Ninjas",
            "Who Are The Rosenbergs?",
            "The Wibbly Wobbly Timey Wimey Stuff",
            "WIDOW",
            "Wildcard",
            "Win, Lose, or Banana!",
            "The Wishful Order of Gluck",
            "Team With No Name",
            "The Wolfpack",
            "Wrong Ideas That Appeal To You",
            "Wutzitubout",
            "Wutziubout",
            "X11",
            "XX",
            "XXcalibur",
            "XX-Rated",
            "XX-Xtras",
            "Yar's Mom's Astronomy",
            "Yar’s Mom’s Astronomy (smart quotes)",
            "Yay!",
            "Yellow Jackets",
            "Yellow Snow",
            "Yes!!!!!",
            "YYDB",
            "Zupplers",
            "ZURB"
    };
    /**
     * Another list of team names.
     */
    private String[] teamNames = PREDEFINED_TEAM_NAMES;
    /**
     * A list of descriptions.
     */
    private String[] descriptions = {NameGenerator.getLoremIpsum()};
    /**
     * The probably of a person being a scrum master.
     */
    private float probOfScrumMaster;
    /**
     * The probability of a person being a product owner.
     */
    private float probOfProductOwner;

    /**
     * The person generator for this team generator.
     */
    private Generator<Person> personGenerator;
    /**
     * A pool of persons to use in this team.
     */
    private List<Person> personPool;

    /**
     * Instantiates a new Team generator.
     */
    public TeamGenerator() {
        personGenerator = new PersonGenerator();

        final float defaultProbability = 0.5f;
        probOfProductOwner = defaultProbability;
        probOfScrumMaster = defaultProbability;
    }

    /**
     * Instantiates a new Team generator.
     * @param generator person generator to use.
     * @param names team names to generate from.
     * @param newDescriptions descriptions to generate from.
     * @param productOwnerProbability probability of a product owner to use.
     * @param scrumMasterProbability probability of a scrum master to use.
     */
    public TeamGenerator(final Generator<Person> generator, final String[] names, final String[] newDescriptions,
                         final float productOwnerProbability, final float scrumMasterProbability) {
        this.personGenerator = generator;
        this.teamNames = names;
        this.descriptions = newDescriptions;
        this.probOfProductOwner = productOwnerProbability;
        this.probOfScrumMaster = scrumMasterProbability;
    }

    /**
     * Sets the person generator.
     * @param generator The person generator
     */
    public final void setPersonGenerator(final Generator<Person> generator) {
        this.personGenerator = generator;
    }

    /**
     * Sets the person pool. If null, people will be randomly generated.
     * @param persons The person pool
     */
    public final void setPersonPool(final List<Person> persons) {
        this.personPool = persons;
    }

    /**
     * Generates the members of a team.
     * @param min The min members
     * @param max The max members
     * @return The members
     */
    private List<Person> generateMembers(final int min, final int max) {
        List<Person> generated = new ArrayList<>();
        int personCount = NameGenerator.random(min, max);

        //If we haven't been given a pool of person, make some up
        if (personPool == null) {
            for (int i = 0; i < personCount; i++) {
                Person newPerson = personGenerator.generate();
                if (!generated.stream().filter(newPerson::equals).findAny().isPresent()) {
                    generated.add(newPerson);
                }
            }
        }
        else {
            //If there are more person than we have just assign all of them
            if (personCount > personPool.size()) {
                personCount = personPool.size();
            }

            for (int i = 0; i < personCount; i++) {
                // Remove the person so we can't pick it again.
                // We'll put it back when we're done
                Person person = personPool.remove(NameGenerator.random(personPool.size()));
                generated.add(person);
            }
        }
        return generated;
    }

    @Override
    public final Team generate() {
        final int longNameMax = 10;
        final int minMembers = 3;
        final int maxMembers = 15;

        Team team = new Team();

        String shortName = NameGenerator.randomElement(teamNames);
        String longName = shortName + NameGenerator.random(longNameMax);

        String description = NameGenerator.randomElement(descriptions);

        Person productOwner = null;
        Person scrumMaster = null;

        List<Person> members = generateMembers(minMembers, maxMembers);

        if (members.size() > 0) {
                productOwner = members.get(0);
        }
        if (members.size() > 1) {
            scrumMaster = members.get(1);
        }

        try {
            team.setShortName(shortName);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
            // Do nothing, don't have to deal with the exception
            // if only generating test data.
        }

        team.setLongName(longName);
        team.setDescription(description);

        try {
            if (scrumMaster != null) {
                team.setScrumMaster(scrumMaster);
            }
            if (productOwner != null) {
                team.setProductOwner(productOwner);
            }
            team.addMembers(members);
        } catch (Exception e) {
            // Do nothing, don't have to deal with the
            // exception if only generating test data.
            e.printStackTrace();
            return null;
        }

        return team;
    }
}
