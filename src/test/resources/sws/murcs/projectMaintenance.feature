@Manual
Feature: Project Maintenance

  Scenario: Create a project from file menu
    When I click the 'File' menu
    And I click on the 'New' sub menu
    And I click on 'Project'
    Then the popup shows

  Scenario: Create a new project using the add button
    Given I have selected the project view from the display list type
    When I click on the add button
    Then the create new project popup shows

  Scenario: Remove project using the remove button
    Given I have selected the project view from the display list type
    And a project is selected from the list
    When I click on the remove button
    Then the project is removed from the list

  Scenario: Short name unspecified
    Given I am creating a new project
    And the Short Name field is blank
    When I click OK
    Then an error is displayed

  Scenario: Short name is not unique
    Given I am creating a new project
    And the Short Name is specified
    And the Short Name is not unique
    When I click OK
    Then an error is displayed

  Scenario: Valid credentials
    Given I am creating a new project
    And a Short Name is specified
    And the specified name is unique
    When I click OK
    Then the popup goes away
    And the project is added to the list

  Scenario: Cancel button pressed
    Given I am creating a new project
    When I click Cancel
    Then the popup goes away