#Mrs. Richards: [to Polly, acidly] Isn't there anyone else who could help me? Really! This is the most appalling service I've ever...
#Polly: What a good idea. Manuel? Could you lend Mrs. Richards your assistance in connection with her reservation?
#[Manuel looks around, confused]
#Mrs. Richards: [to Manuel] Now, I reserved a very quiet room with a bath and a sea view. I specifically asked for a sea view in my written confirmation, so please make sure I have it.
#Manuel: Que?
#Mrs. Richards: What?
#Manuel: Que?
#Mrs. Richards: Kay?
#Manuel: Si.
#Mrs. Richards: Sea?
#[Manuel nods]
#Mrs. Richards: Kay, sea? Kay sea? What are you trying to say?
#Manuel: No. No, no, no. Que... what.
#Mrs. Richards: Kay Watt?
#Manuel: Si, que: what.
#Mrs. Richards: C.K. Watt?
#Manuel: Yes!
#Mrs. Richards: Who is C. K. Watt?
#Manuel: Que?
#Mrs. Richards: Is he the manager, Mr. Watt?
#Manuel: Oh, manajer!
#Mrs. Richards: He is?
#Manuel: Ah, Mister Fawlty!
#Mrs. Richards: What?
#Manuel: Fawlty!
@Manual
Feature: Task Maintenance

  Scenario: Create a new Task
    Given that you have a story
    And you click the create task button
    Then a new task form will appear
    Given that you add appropriate information about the task
    And click create
    Then the new task will be added to the story

  Scenario: Update a Task
    Given that you have a task
    And you change the information about the task
    And the information is valid
    Then the task will be updated

  Scenario: Expand a task
    Given that you have a task
    And it is currently collapsed
    And you click the expand button
    Then the task will expand to show the description

  Scenario: Collapse a task
    Given that you have a task
    And it is currently expanded
    And you click the collapse button
    Then the task will collapse

  Scenario: Delete a task
    Given that you have a task
    And you click the delete button
    Then the task is deleted from the story

