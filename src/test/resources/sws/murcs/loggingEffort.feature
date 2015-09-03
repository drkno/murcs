#  Manuel: [to the workman, two of whom are clean-shaven] Please, which one is man with beard?
@Manual
Feature: Logging Effort
  # Tests for logging effort

  Scenario: Logging Effort
    When I am logging effort
    Then I must specify a date
    And a description
    And a time which is non negative
    And a person

  Scenario: Logging Effort (seeing errors)
    When I try to log some invalid valid effort
    Then the parts of the form that aren't valid are highlighted in red

  Scenario: Logging Effort: Seeing past effort
    When I am viewing effort
    Then I can see all past effort entries
    And I have the option to remove effort entries
