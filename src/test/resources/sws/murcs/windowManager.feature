@Manual
Feature: Window Manger
  Tracks the relative position of each window

  Scenario: Window is brought to the front
    Given More than one window is open
    When A Window is told to come to the front
    Then The Window is shown on
    And The Window manager has maintained the list order

  Scenario: Window is sent to the back
    Given More than one window is open
    When A window is told to go to the back
    Then The window hides and cries
    And The window manager has maintained the list order

  Scenario: User selects a partially hidden window
    Given More than one window is open
    When The partially hidden window is selected or given focus
    Then The window is brought to the front
    And The window manager has maintained the list order.
