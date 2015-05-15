@Manual
Feature: Button Placement on Dialogs

  Scenario: Default buttons on Delete dialog
    When I am shown a confirm dialog
    Then there is a default option selected

  Scenario: Default buttons on confirm exit dialog
    Given I am shown a confirm close dialog
    Then there is a default option selected

  Scenario: Button placement
    When I am shown a dialog
    Then the buttons are confirm/cancel options are on the right
    And special options are on the left
