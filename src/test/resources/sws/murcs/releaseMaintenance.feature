@Manual
@ReleaseMaintenance
Feature: Release Maintenance

  Scenario: Create a new Release from the file menu
    When I select new release from the file menu
    And I fill in valid information in the popup
    And Click Ok
    Then A release is made with the given information

  Scenario: Create a new release using the add button
    Given I have selected the release view from the display list type
    And I click on the add button
    And I fill in valid information in the popup
    When Click Ok
    Then A release is made with the given information

  Scenario: Remove release using the remove button
    Given there is a release
    And I have selected the release view from the display list type
    And a release is selected from the list
    When I click on the remove button
    Then the release is removed from the list

  Scenario: Edit a release
    Given there is a release
    And I have selected the release view from the display list type
    And a release is selected from the list
    When I edit the values of the release
    Then the release updates to the values given