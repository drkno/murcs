# Basil Fawlty: Where's Sybil?
# Manuel: ¿Que?
# Basil Fawlty: Where's Sybil?
# Manuel: Where's... the bill?
# Basil Fawlty: No, not a bill! I own the place!
@Manual
Feature: Estimating Stories

  Scenario: Change Backlog Estimate Type
    Given that I have selected a backlog
    And I change the estimate type of the backlog
    Then all the stories within the backlog with estimates update to that type

  Scenario: Estimate a Story
    Given that I have a Story that is within a backlog selected
    And the story has acceptance criteria
    Then I can change the estimation value from the given estimation scale

  Scenario: Estimate a Story without AC's
    Given that I have a Story that is within a backlog selected
    And the story doesn't have acceptance criteria
    Then I can't change the estimation value

  Scenario: Estimate an orphaned Story
    Given that I have a Story that is not within a backlog selected
    Then I can't change the estimation value for the story