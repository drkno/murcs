@Manual
  Feature: Error reporting to a server

    Scenario: An error occurs in the application
      Given there is an error in the application
      When the code does not handle it correctly
      Then the user is prompted to report the problem

    Scenario: Manually report a problem
      Given the user wants to provide feedback
      When the user clicks the feedback menu item
      Then the user is presented with a dialog allowing them to give feedback