#Basil Fawlty: Where's Sybil?
#Manuel: ¿Que?
#Basil Fawlty: Where's Sy-bil?
#Manuel: Where's... the bill?
#Basil Fawlty: No, no, not a bill! I own the place!
@Manual
Feature: Sprint Maintenance

  Scenario: Create a new Sprint from the file menu
    When I select new Sprint from the file menu
    And I fill in valid information in the popup
    And Click Ok
    Then A Sprint is made with the given information

  Scenario: Create a new Sprint using the add button
    Given I have selected the Sprint view from the display list type
    And I click on the add button
    And I fill in valid information in the popup
    When Click Ok
    Then A Sprint is made with the given information

  Scenario: Remove Sprint using the remove button
    Given there is a Sprint
    And I have selected the Sprint view from the display list type
    And a Sprint is selected from the list
    When I click on the remove button
    Then the Sprint is removed from the list

  Scenario: Edit a Sprint
    Given there is a Sprint
    And I have selected the Sprint view from the display list type
    And a Sprint is selected from the list
    When I edit the values of the Sprint
    Then the Sprint updates to the values given

  Scenario: Create a Sprint without a Backlog
    Given I have a Sprint creation window open
    And I try and create a Sprint without a Backlog
    Then an error message will be displayed
    And the Sprint will not be created

  Scenario: Create a Sprint without a Team
    Given I have a Sprint creation window open
    And I try and create a Sprint without a Team
    Then an error message will be displayed
    And the Sprint will not be created

  Scenario: Create a Sprint without a Release
    Given I have a Sprint creation window open
    And I try and create a Sprint without a Release
    Then an error message will be displayed
    And the Sprint will not be created

  Scenario: Create a Sprint with an empty short name
    Given I have a story creation window open
    And I do not specify a short name
    Then an error message will be displayed
    And the Sprint will not be created

  Scenario: Adding a non-ready a Story to a Sprint
    Given I have a sprint creation window open
    And I add select a story to add to the sprint
    And the story is not ready
    Then I am asked to set the story state to ready

  Scenario: Adding a Story to a Sprint
    Given I have a Sprint creation window open
    And I add a Story to the Sprint
    Then the Story is displayed in the stories list

  Scenario: Remove a Story from a Sprint
    Given I have a Sprint creation window open
    And I remove a Sprint from the Story
    Then I am asked to confirm

  Scenario: Create a Story with invalid start end dates
    Given I have a Sprint creation window open
    And the end date is before the start date
    Then an error message will be displayed
    And the Sprint will not be created

  Scenario: Create a Story with empty start end dates
    Given I have a Sprint creation window open
    And the end date or start date is not set
    Then an error message will be displayed
    And the Sprint will not be created

  Scenario: Create a Story with invalid end date
    Given I have a Sprint creation window open
    And I supply an end date after the release date
    Then an error message will be displayed
    And the Sprint will not be created


