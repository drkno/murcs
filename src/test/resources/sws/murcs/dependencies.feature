@Manual
  Feature: Dependencies

    Scenario: Viewing dependencies
      Given I have a story selected
      Then the dependencies of the story are displayed

    Scenario: Adding a dependency
      Given I have a story selected
      When I select a new non-cyclic dependency
      Then it is added to the dependencies of the story

    Scenario: Adding a cyclic dependency
      Given I have a story selected
      When I select a new cyclic dependency
      Then an error is displayed
      And the dependency is not added

    Scenario: Removing a dependency
      Given I have a story selected
      And the story has at least 1 dependency
      When I click the remove dependency button
      Then the dependency is removed from the story

    Scenario: Depth of a dependency
      Given I have a story selected
      And the story has at least 1 dependency
      Then the dependencies show their maximum depth

    Scenario: Dependency reporting
      Given I have a story with at least 1 dependency
      When I generate a report
      Then the dependency is displayed within the report