@Manual
  Feature: Story Completeness

    Scenario: Indication of individual story size and completeness
      Given I select a story
      When I add tasks to the story
      Then there should be a visual indicator for story completeness

    Scenario: Indication of sprint size and completeness
      Given I select a sprint
      When I add stories to the sprint
      Then there should be a visual indicator for sprint completeness

    Scenario: Indication of sprint size and completeness on a scrumboard
      Given I select a sprint
      And I navigate to its associated scrumboard
      When I update stories in the sprint
      Then there should be a visual indicator for sprint and story completeness

    Scenario: Ordering stories alphabetically in a sprint
      Given I want to order stories by alphabetical order
      When I click the column header
      Then the stories should be sorted alphabetically

    Scenario: Ordering stories by estimation in a sprint
      Given I want to order stories by estimation
      When I click the column header
      Then the stories should be sorted by estimation value