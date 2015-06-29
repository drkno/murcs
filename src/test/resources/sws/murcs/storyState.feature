# Manuel: Ah, your horse. It win! It win!
# Basil Fawlty: [wanting him to keep quiet about his gambling venture] Shhh-shh-shh, Manuel. You - know - nothing.
# Manuel: You ALWAYS say, Mr. Fawlty, but I learn.
@Manual
Feature: Changing Story State

  Scenario: Changing Story State to Ready
  Given there is a story
  And that story is in a backlog
  And I have created an acceptance condition
  And the story has been estimated
  Then I can set the story's state to Ready

  Scenario: Changing story state to ready when not in a backlog
    Given there is a story
    And that story is not in a backlog
    Then I cannot set the story's state to Ready

  Scenario: Changing a story state to ready when missing acceptance conditions
    Given there is a story
    And I have not created an acceptance condition
    Then I cannot set the story's state to Ready

  Scenario: Changing a story state to Ready when not estimated
    Given there is a story
    And the story has not been estimated
    Then I cannot set the story's state to Ready

  Scenario: Changing the Story state to None
  Given there is a story
  Then I can set the story's state to None