@Manual
Feature: Release Maintenance

  Scenario: Create a new Release from the file menu
    When I click the File menu
    And I click the New selection
    And I click Release
    Then The creation popup shows

  Scenario: Create a new release using the add button
    Given I have selected the release view from the display list type
    When I click on the add button
    Then the create new release popup shows

  Scenario: Remove release using the remove button
    Given I have selected the release view from the display list type
    And a release is selected from the list
    When I click on the remove button
    Then the release is removed from the list

  Scenario: Short name unspecified
    Given I am editing a release
    And the Short Name field is blank
    When I click OK
    Then an error is displayed

  Scenario: Short name is not unique
    Given I am editing a release
    And the Short Name is specified
    And the Short Name is not unique
    When I click OK
    Then an error is displayed

  Scenario: Valid Credentials
    Given I am creating a new release
    And a Short Name is specified
    And the specified name is unique
    When I click OK
    Then the popup goes away
    And the release is added to the list

  Scenario: Cancel button pressed
    Given The Create Project Popup is shown
    When I click Cancel
    Then the popup goes away

  @WIP
  Scenario: Project Unspecified
    Given I have added all info apart from project
    When I click OK
    Then An error will appear

  @WIP
  Scenario: Valid Project
    Given I add all required information
    When I click OK
    Then The popup goes away
    And The release is added to the list