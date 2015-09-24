@Manual
Feature: Estimation Workspace
  The estimation workspace is used to display stories and change their estimate based on the position in the workspace.

  Scenario: The user can update a stories estimate by dragging it between estimates
    Given A story is dragged out of an estimate
    When The story is dragged onto another estimate
    Then The story estimate changes

  Scenario: The user drags the story back to the same estimate
    Given A story is dragged out of an estimate
    When The story is dragged back to the same estimate
    Then The story estimate remains the same

  Scenario: The estimation workspace is available via a tab
    Given I select the backlog type
    When I select the estimate workspace tab
    Then The estimate workspace is shown

  Scenario: The estimate workspace is full of stories
    Given There are more stories than can be shown in the default workspace height
    Then The workspace area can be scrolled vertically

  Scenario: A lot of stories in one estimate
    Given There are more stories in the estimate, then it has width
    Then The estimate height increases to add more space for the stories to flow

  Scenario: Resizing of the workspace
    When The workspace window is resized
    Then The stories in each estimate flow to fill space appropriately
    And the option to scroll vertically is available if needed
    But not the option to scroll horizontally

  Scenario: Add a story to the workspace from the backlog
    Given The backlog overview is displayed
    When I click on the add story to workspace button
    Then The story is added to the workspace

  Scenario: Remove a story to the workspace from the backlog
    Given The backlog overview is displayed
    When I click on the remove story from workspace button
    Then The story is removed from the workspace

  Scenario: Remove a story from workspace from within the workspace
    Given The estimation workspace is displayed
    When I click on the remove story from workspace button
    Then The story is removed from the workspace

  Scenario: Navigate to a story from within the workspace
    When I click on the navigate to story button
    Then The story overview is shown

  Scenario: A story can only appear once in the workspace
    When The a story is added to the workspace that is already in the workspace
    Then The story appears only once in the workspace