Feature: Create a student with a name and date of birth

  Scenario: Save a student
    When I click the save button
    And The name field and dob field is filled out
    Then The the student is saved to application