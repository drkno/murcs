@Manual
Feature: Languages

  Scenario: Change Language
    Given that I have the application open
    And I select View > Languages
    And I select a different language
    Then the language will change for the application