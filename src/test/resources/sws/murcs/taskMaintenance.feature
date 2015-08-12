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

