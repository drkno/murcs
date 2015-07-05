package sws.murcs.debug.sampledata;

import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.Skill;

/**
 * Generates random skills.
 */
public class SkillGenerator implements Generator<Skill> {
    /**
     * The max number of projects generated when stress level is low.
     */
    public static final int LOW_STRESS_MAX = 3;
    /**
     * The min number of projects generated when stress level is low.
     */
    public static final int LOW_STRESS_MIN = 1;

    /**
     * The max number of projects generated when stress level is medium.
     */
    public static final int MEDIUM_STRESS_MAX = 20;
    /**
     * The min number of projects generated when stress level is medium.
     */
    public static final int MEDIUM_STRESS_MIN = 3;

    /**
     * The max number of projects generated when stress level is high.
     */
    public static final int HIGH_STRESS_MAX = 100;
    /**
     * The min number of projects generated when stress level is high.
     */
    public static final int HIGH_STRESS_MIN = 20;

    /**
     * Names for skills.
     */
    private String[] skills = {
            "Falling over",
            "Backflips",
            "C#",
            "Running away screaming",
            "Ability to work under pressure ",
            "Accuracy",
            "Adaptability ",
            "Administering medication ",
            "Advising people",
            "Analyzing data",
            "Analyzing problems",
            "Assembling equipment",
            "Attention to detail",
            "Auditing financial data ",
            "Analytical skills",
            "Attention to details ",
            "Being thorough",
            "Brainstorming",
            "Budgeting",
            "Building new business ",
            "Business communication skills",
            "Business management skills",
            "Calculating data",
            "Categorizing records",
            "Checking for accuracy",
            "Coaching skills",
            "Collaborating ideas",
            "Collecting items",
            "Communicating with young or old people",
            "Comparing results",
            "Comprehending books or ideas",
            "Conducting interviews",
            "Conflict resolution ",
            "Confronting other people",
            "Constructing buildings",
            "Consulting organizations",
            "Counseling people ",
            "Creative thinking skills ",
            "Creating meaningful work",
            "Critical thinking skills",
            "Customer service skills",
            "Dealing with complaints",
            "Decision making skills",
            "Defining problems",
            "Delegating skills",
            "Designing systems",
            "Determination",
            "Developing plans for projects",
            "Diplomacy skills",
            "Displaying art",
            "Distributing products",
            "Dramatizing ideas",
            "Driving safely ",
            "Editing",
            "Effective listening skills ",
            "Effective study skills",
            "Encouraging people",
            "Enforcing rules",
            "Entertaining others",
            "Envisioning solutions or ideas",
            "Estimating project workload",
            "Ethics",
            "Evaluating programs",
            "Expressing feelings",
            "Expressing ideas",
            "Extracting information ",
            "Finding missing information",
            "Following instructions ",
            "Gathering information",
            "Generating accounts",
            "Goal setting ",
            "Initiator ",
            "Handling money",
            "Identifying problems",
            "Imagining innovative solutions",
            "Information management",
            "Inspecting buildings",
            "Inspecting equipment",
            "Interacting with various people ",
            "Interpersonal communication skills ",
            "Interpreting languages",
            "Interviewing ",
            "Inventing products/ideas",
            "Investigating solutions",
            "Knowledge of community ",
            "Knowledge of concepts and principles",
            "Knowledge of government affairs",
            "Leading teams",
            "Listening to people",
            "Maintain focus with interruptions",
            "Maintaining a high level of production",
            "Maintaining accurate records",
            "Maintaining emotional control under stress",
            "Maintaining files",
            "Maintaining schedules or times",
            "Making important decisions",
            "Managing organizations",
            "Managing people ",
            "Mediating between people",
            "Meeting deadlines",
            "Meeting new people ",
            "Motivating others",
            "Multi-tasking",
            "Navigating politics ",
            "Negotiating skills",
            "Operating equipment",
            "Organizing files",
            "Organizing tasks",
            "Patience",
            "Person management skills",
            "Performing clerical work ",
            "Performing numerical analysis",
            "Persuading others",
            "Planning meetings ",
            "Planning organizational needs",
            "Predicting future trends",
            "Preparing written communications",
            "Prioritization skills",
            "Problem analysis skills",
            "Problem solving skills",
            "Product promotion",
            "Promoting events",
            "Proposing ideas",
            "Providing customer service",
            "Providing discipline ",
            "Public speaking",
            "Questioning others",
            "Quick learning skills",
            "Raising funds",
            "Reading ",
            "Recognizing problems",
            "Recruiting",
            "Rehabilitating people",
            "Relating to others",
            "Reliability",
            "Remembering information",
            "Repairing equipment ",
            "Reporting data",
            "Researching ",
            "Resolving conflicts ",
            "Resourcefulness",
            "Responsibility",
            "Results orientated ",
            "Risk taking",
            "Running meetings",
            "Sales ability",
            "Screening telephone calls",
            "Self-motivated ",
            "Selling ideas",
            "Selling products or services",
            "Serving people",
            "Setting performance standards",
            "Setting up demonstrations",
            "Sketching charts or diagrams",
            "Strategic thinking",
            "Suggesting courses of action",
            "Summarizing data",
            "Supervising employees",
            "Supervising operations",
            "Supporting others ",
            "Taking decisive action",
            "Taking inititiave",
            "Taking personal responsibility",
            "Teaching skills ",
            "Team building",
            "Teamwork skills",
            "Technical work",
            "Thinking logically",
            "Time management skills",
            "Training skills",
            "Translating words",
            "Using computers",
            "Verbal communication skills",
            "Working creatively  ",
            "Working with statistics",
            "Writing clearly and concisely",
            "Writing letters, papers, or proposals",
            "Baking",
            "Canning or preserving",
            "Changing a tire on a car",
            "Changing a light bulb",
            "Checking the oil in a car",
            "Cleaning your residence",
            "Clearing a drain",
            "Clearing the table",
            "Cooking",
            "Cardiopulmonary Resuscitation",
            "Driving ",
            "Drying clothes and dishes",
            "Dusting furniture",
            "First Aid",
            "Folding clothes, towels, or sheets",
            "Following a recipe",
            "Making a household budget",
            "Mopping the floor",
            "Mowing the lawn",
            "Organizing a closet, cupboard, shed, attic or garage",
            "Painting a room",
            "Plumbing",
            "Raking leaves",
            "Setting the table",
            "Sweeping the floor",
            "Taking out the trash",
            "Tracking personal finances",
            "Vacuuming",
            "Vegetable gardening",
            "Wallpapering",
            "Washing clothes, dishes, windows or the car",
            "Caring",
            "Common sense",
            "Cooperation",
            "Curiosity",
            "Effort ",
            "Flexibility",
            "Friendship",
            "Initiative",
            "Integrity",
            "Organization",
            "Patience",
            "Perseverance",
            "Problem solving",
            "Responsibility",
            "Sense of humor",
    };

    /**
     * Instantiates a new random skill generator.
     */
    public SkillGenerator() {

    }

    /**
     * Instantiates a new random skill generator.
     * @param skillsList skills to generate from.
     */
    public SkillGenerator(final String[] skillsList) {
        this.skills = skillsList;
    }

    @Override
    public final Skill generate() {
        Skill skill = new Skill();

        String name = GenerationHelper.randomElement(skills);
        String description = NameGenerator.randomDescription();

        try {
            skill.setShortName(name);
        }
        catch (Exception e) {
            ErrorReporter.get().reportErrorSecretly(e, "SkillGenerator: setting short name failed");
            return null;
            //Do nothing, don't have to deal with the
            // exception if only generating test data.
        }

        skill.setLongName(name);
        skill.setDescription(description);

        return skill;
    }
}
