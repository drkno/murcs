@Manual
Feature: Working with tabs
  # Acceptance tests for managing tabs

  Scenario: Creating a new tab with the new tab button
    When I press the "New Tab" button
    Then a new model tab is opened
    And is selected

  Scenario: Creating a new tab with the shortcut
    When I press the "New Tab" shortcut
    Then a new model tab is opened
    And is selected

  Scenario: Closing a tab with the close button
    Given I am viewing a tab
    When I press the "Close" button
    Then the tab is closed
    And the next tab is selected (if available)

  Scenario: Closing a tab with the close shortcut
    Given I am viewing a tab
    When I press the "Close Tab" shortcut
    Then the tab should close

  Scenario: Routing navigation through tabs
    Given I am viewing a tab
    Then the navigation buttons should be disabled if I can't go in their respective directions on the current tab
    And using the navigation buttons should work within my current tab

  Scenario: Showing and Hiding the Display List
    Given I am viewing a tab
    And I toggle the sidebar
    Then the side panel should toggle only within my current tab

  Scenario: Reordering tabs
    Given I am viewing a tab
    Then I should be able to reorder the tabs

  Scenario: Dragging tabs between windows
    Given I have two windows with tabs open
    Then I should be able to drag tabs between these windows

  Scenario: Closing Child Windows when dragging tab away
    Given I have a child window with one tab
    And I drag the tab to another window
    Then the child window should close

  Scenario: Closing child windows with no tabs
    Given I have a child window with one tab
    And I close the tab
    Then the window should close

  Scenario: Moving add button
    Given I have a window with tabs open
    Then the add button should reposition when I add and remove tabs

