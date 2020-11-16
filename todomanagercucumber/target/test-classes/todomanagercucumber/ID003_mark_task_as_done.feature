Feature:
As a student, I mark a task as done on my course to do list, so I can track my accomplishments.

  Background:
    Given the API server is running
    And the following courses exist in the system
      | title    | completed | active | description                 |
      | ECSE429  | false     | true   | Software Validiation        |
      | COMP512	 | false     | true   |	Distributed System          |
      | ECSE416	 | true      | false  |	Telecommunication           |
      | COMP202  | true      | true   | Introduction to Programming |
      | GEOG203  | true      | false  | Environmental System        |
      And the following tasks are associated with 'ECSE 429'
      | title                    | doneStatus | description                                 |
      | Assignment1              | true       | Interview some engineers                    |
      | Assignment2              | false      | Write research paper based on the interview |
      | Project Deliverable 1    | false      | Unit test                                   |
      | Project Deliverable 2    | false      | Cucumber test   |
      And the following tasks are associated with 'COMP512'
      | title               | doneStatus | description              |
      | Quiz1         | true       | RMI implementation       |
      | Quiz2         | false      | Strict 2PL implementation|
      And the following tasks are associated with 'ECSE416'
      | title            | doneStatus      | description          |
      | Lab1             | true            | TCP                  |
      | Lab2             | true            | UDP                  |

  Scenario Outline: Mark a task with <title> that is not completed as complete (Normal flow)
    Given <title> is the title of the task
      And task with <title> is not completed
     When the user marks the task with <title> as completed
     Then the task with <title> is marked as completed
      And the task with <title> is returned with <status>

    Examples:
      | title        				| status |
      | Assignment2      			| 200    |
      | Project Deliverable 1      	| 200    |
      | Project Deliverable 2       | 200    |
      | Quiz2         				| 200    |

  Scenario Outline: Mark a task with <title> that is completed as complete (Alternative flow)
    Given <title> is the title of the task
      And task with <title> is completed
     When the user marks the task with <title> as completed
     Then the task with <title> is marked as completed
      And the task with <title> is returned with <status>

    Examples:
      | title        				| status |
      | Assignment1      			| 200    |
      | Lab2     	| 200    |
      | Lab1       | 200    |
      | Quiz1         				| 200    |
      
  Scenario Outline: Mark a non-exist task with <title> as completed (Error flow)
    Given <title> is the title of the task
     When the user marks the task with <title> as completed
     Then the operation fails with <status>

    Examples:
      | title        				| status |
      | NULL      						| 404    |
      | Midterm         				| 404    |