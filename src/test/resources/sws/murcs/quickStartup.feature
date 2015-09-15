@Manual
Feature: Quick Startup

  Scenario: Application has an icon
    Given I am on a supported operating system
    When I open the application
    Then the application has an icon on each window
    And the application has an icon in operating specific contexts eg. task-bar, dock

  Scenario: Double click on Windows icon opens the application
    Given I am using Windows
    When I double click the application executable icon
    Then the application will open

  Scenario: Double click on OSX icon opens the application
    Given I am using OSX
    When I double click the application executable icon
    Then the application will open

  # Please note: the following scenario is very difficult to manually test.
  # Instead just check that the .sh file opens the application as expected.
  Scenario: Double click on Linux icon opens the application
    Given I am using Linux
    And the application is installed correctly
    When I double click the application desktop icon
    Then the application will open
