 #I knowa nothing
@Manuel
Feature: Responsive Forms
  Scenario: Resizing Main Window
    Given I am editing a a model object
    And I resize the window
    Then all buttons are still accessible

  Scenario: Resizing Create Dialog
    Given I am creating a model object
    Then I cannot resize the window

