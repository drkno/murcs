@Manual
Feature: Story Maintenance

  Scenario: Create a new Story from the file menu
    When I select new story from the file menu
    And I fill in valid information in the popup
    And Click Ok
    Then A story is made with the given information

  Scenario: Create a new story using the add button
    Given I have selected the story view from the display list type
    And I click on the add button
    And I fill in valid information in the popup
    When Click Ok
    Then A story is made with the given information

  Scenario: Remove story using the remove button
    Given there is a story
    And I have selected the story view from the display list type
    And a story is selected from the list
    When I click on the remove button
    Then the story is removed from the list

  Scenario: Edit a story
    Given there is a story
    And I have selected the story view from the display list type
    And a story is selected from the list
    When I edit the values of the story
    Then the story updates to the values given

  Scenario: Create a story without a Creator
    Given I have a story creation window open
    And I try and create a story without a creator
    Then an error message will be displayed
    And the story will not be created