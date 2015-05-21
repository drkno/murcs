@Manual
Feature: Backlog Maintenance

  Scenario: Create a new Backlog from the file menu
    When I click the 'File' menu
    And I click on the 'New' sub menu
    And I click on 'Backlog'
    Then the popup shows

  Scenario: Create a new Backlog using the add button
    Given I have selected the Backlog view from the display list type
    When I click on the add button
    Then the create new Backlog popup shows

  Scenario: Remove Backlog using the remove button
    Given I have selected the Backlog view from the display list type
    And a Backlog is selected from the list
    When I click on the remove button
    Then the Backlog is removed from the list

  Scenario: Short name unspecified
    Given I am editing a Backlog
    And the Short Name field is blank
    When I click OK
    Then an error is displayed

  Scenario: Short name is not unique
    Given I am editing a Backlog
    And the Short Name is specified
    And the Short Name is not unique
    When I click OK
    Then an error is displayed

  Scenario: Valid Credentials
    Given I am creating a new Backlog
    And a Short Name is specified
    And the specified name is unique
    And a PO is specified
    When I click OK
    Then the popup goes away
    And the Backlog is added to the list

  Scenario: Cancel button pressed
    Given The Create Project Popup is shown
    When I click Cancel
    Then the popup goes away