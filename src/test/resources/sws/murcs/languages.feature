# For a moment, nothing happened.
# Then, after a second or so, nothing continued to happen.
@Manual
Feature: Languages

  Scenario: Change Language
    Given that I have the application open
    When I select a different language
    Then the language will change for the application