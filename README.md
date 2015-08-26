# MURCS README
## Building
<b>NB:</b> The minimum Java version this application is tested to run with is 1.8u25 and above. Any version below this is unsupported. Java 1.8u40 or higher is highly recommended due to the security valnerabilities fixed in the runtime. Using an unsupported version of Java may result in the program not performing as intended by the developers.

### IntelliJ IDEA
1. From the 'File' menu, select 'Open'.
2. From the selection panel that appears select the project folder.
3. Click open.
4. Add a new run configuration with the following information.
    - Type: `Maven`
    - Goal: `compile`
5. Click run. Maven will then pull the dependencies and build the file.

### Eclipse

<i>This guide was written using Eclipse Juno. Other versions of the IDE may differ slightly from what is stated in the below instructions.</i>
1. From the 'Help' menu, select 'Eclipse Marketplace'.
2. In the Market Place locate and install the plugin 'Maven integration for eclipse'.
 - If this is already installed, please ensure it is updated to the latest version.
3. From the 'File' menu, select 'Import'.
4. Expand the item that says 'Maven' and select 'Existing Maven Projects'.
5. Using the 'Browse' button, select the folder containing the project (the folder that contains a `pom.xml` file).
6. Click 'Finish'. Importing could take a while because Eclipse is slow.<br>
If you have already correctly setup your JDK, skip to step 18.
7. Right click on the imported 'Murcs' project.
8. Under the 'Build Path' submenu, select 'Configure Build Path'.
9. Click on the item that says 'JRE System Library' then click the 'Edit' button.
10. Select the 'Execution environment' radio button, then click the 'Environments' button.
11. In the left pane, select 'Installed JREs', then in the right pane select 'Add'.
12. Select 'Standard VM' then click 'Next'.
13. Set 'JRE home' to the directory where java is installed. On the University of Canterbury Linux Mint lab machines, this is `/usr/lib/jvm/java-8-oracle` (as of 15/07/15).
14. Give the JRE a name, and click 'Finish'.
15. Ensure your new JRE has a tick in the checkbox next to it in the right hand pane then click 'OK'.
16. From the dropdown next to 'Execution environment' select the JRE you created.
17. Click 'Finish', then 'OK'.
18. Right click the file `pom.xml` in the project and under the submenu 'Run As' select the first 'Maven Build' item.
19. In 'Goals' type `compile` and give the configuration a name.
20. Press 'Run'. Provided Eclipse is setup correctly Maven will pull the required dependencies and build the project.

## Running
### In an IDE
1. Add a new run configuration, with the following information:
  - Main Class/Entry Point: `sws.murcs.view.App`
  - Command line arguments: None, unless using one specified below.
2. Press run. Provided the correct runtime is setup and Maven has been run at least once to pull the dependencies the application will run.

<i>Both IntelliJ and Eclipse allow you to bypass manual configuration by right clicking on the `sws.murcs.view.App` source file, right clicking and selecting 'Run'.</i>

### From the JAR
1. Open a terminal/command window and type the following:<br>
  `java -jar Murcs-<version>.jar`<br>
  Replacing `<version>` with the release version.
2. Press the return key, and the program should open.

<i>Some operating systems allow a .jar to be directly executed by double clicking on it in the file browser.</i>

### Command Line Options
A number of command line options can be used with the application.<br>
<b>NB:</b> These options are provided for convenience and <b><i>their use will not be supported in any way.</i></b>
- `debug [low|medium|high]`: Starts the application with random sample data pre-populating the Organisation. The additional `low|medium|high` option changes the amount of data generated. If it is not provided, `debug` will default to `low`.
- `sample`: Saves a sample organisation to a file called `sample.project` then quits the application. Made for use in conjunction with the `debug` option.

## Testing
All tests are located within the `/src/test` directory. Both IntelliJ and Eclipse provide options in their context menus for running tests.

### Unit Tests
Unit tests use the JUnit library. These tests are located within the `/src/test/java/sws/murcs/unit/` directory. Maven is configured to pull the required dependencies for running unit tests.

### Acceptance Tests
Acceptance tests are written as Gerkin feature files within the `/sws/test/resources/sws/murcs/` directory. Some of these tests are automated using Cucumber step definitions defined in the `/sws/test/java/sws/murcs/acceptance/` directory. Feature files that are not automated start with the tag `@Manual`. Maven is configured to pull the required dependencies for running automated acceptance tests.
As the program is evolving at a rapid pace, there is no current plans for automating more acceptance tests, as the overhead would be very high in maintaining them.

## Usage
Information about using the application and/or troubleshooting can be found in the userguide (doc/user_guide/seng302t1_user_guide.pdf)

For your convenience, a sample data file has been included with this release.