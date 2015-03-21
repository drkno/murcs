@Manual
Feature: Person Maintenance

  Scenario: Create a new person from the file menu
    When I click the 'File' menu
    And I click on the 'New' sub menu
    And I click on 'Person'
    Then the popup shows

  Scenario: Create a new person using the add button
    Given I have selected the person view from the display list type
    When I click on the add button
    Then the create new person popup shows

  Scenario: Remove person using the remove button
    Given I have selected the person view from the display list type
    And a person is selected from the list
    When I click on the remove button
    Then the person is removed from the list

  Scenario: Short name unspecified
    Given I am editing a person
    And the Short Name field is blank
    When I click OK
    Then an error is displayed

  Scenario: Short name is not unique
    Given I am editing a person
    And the Short Name is specified
    And the Short Name is not unique
    When I click OK
    Then an error is displayed

  Scenario: Add a skill to a person
    Given I am editing a person
    When I select a skill from the skill drop down
    Then the skill is added

  Scenario: Remove a skill from a person
    Given I am editing a person
    And there is a skill in the persons skill list
    When I click the skills remove button
    Then the skill is removed

  Scenario: Person already has skill
    Given I am editing a person
    And the person has a skill
    When I select that skill from the list
    Then an error is displayed

  Scenario: Valid Credentials
    Given I am creating a new person
    And a Short Name is specified
    And the specified name is unique
    When I click OK
    Then the popup goes away
    And the person is added to the list

  Scenario: Cancel button pressed
    Given The Create Project Popup is shown
    When I click Cancel
    Then the popup goes away