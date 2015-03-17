@Manual @WIP
Feature: Create a new Project from the project creation popup

  Scenario: Open popup window
    When I click the File menu
    And I click the New selection
    And I click the Project selection
    Then the popup shows

  Scenario: Invalid credentials
    Given The Short Name field is blank
    When I click OK
    Then An error is displayed

  Scenario: Valid Credentials
    Given A Short Name is specified
    And The specified name is unique
    When I click OK
    Then The popup goes away

  Scenario: Cancel button pressed
    Given The Create Project Popup is shown
    When I click Cancel
    Then The popup goes away