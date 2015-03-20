@Manual
Feature: Skill Maintenance

  Scenario: Create a new skill from the file menu
    When I click the 'File' menu
    And I click on the 'New' sub menu
    And I click on 'Skill'
    Then the popup shows

  Scenario: Create a new skill using the add button
    Given I have selected the skill view from the display list type
    When I click on the add button
    Then the create new skill popup shows

  Scenario: Remove skill using the remove button
    Given I have selected the skill view from the display list type
    And a skill is selected from the list
    When I click on the remove button
    Then the skill is removed from the list

  Scenario: Short name unspecified
    Given I am editing a skill
    And the Short Name field is blank
    When I click OK
    Then an error is displayed

  Scenario: Short name is not unique
    Given I am editing a skill
    And the Short Name is specified
    And the Short Name is not unique
    When I click OK
    Then an error is displayed

  Scenario: Valid Credentials
    Given I am creating a new skill
    And a Short Name is specified
    And the specified name is unique
    When I click OK
    Then the popup goes away
    And the skill is added to the list

  Scenario: Cancel button pressed
    Given The Create Project Popup is shown
    When I click Cancel
    Then the popup goes away