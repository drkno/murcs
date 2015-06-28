@Manual
Feature: Changing Story State

  Scenario: Changing Story State to Ready
  Given there is a story
  And that story is in a backlog
  And I have created an acceptance condition
  And the story has been estimated
  Then I can set the story's state to Ready

  Scenario: Changing the Story state to None
  Given there is a story
  Then I can set the story's state to None