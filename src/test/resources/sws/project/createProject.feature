@Manual
Feature: Create a new Project from the project creation popup

  Scenario: Open popup window
    When I click the File menu
    And I click on the new sub menu
    And I click on Project
    Then the popup shows

  Scenario: Invalid credentials
    Given I am creating a new team
    And the Short Name field is blank
    When I click OK
    Then an error is displayed

  Scenario: Valid Credentials
    Given A Short Name is specified
    And The specified name is unique
    When I click OK
    Then the popup goes away

  Scenario: Cancel button pressed
    Given The Create Project Popup is shown
    When I click Cancel
    Then the popup goes away