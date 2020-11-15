Feature:
As a student, I query the incomplete tasks for a class I am taking, to help manage my time.

  Background:
    Given the API server is running
    And the following courses exist in the system
      | title    | completed | active | description                 |
      | ECSE429  | false     | true   | Software Validiation         |
      | COMP512	 | false     | true   |	Distributed System          |
      | ECSE416	 | true      | false  |	Telecommunication           |
      | COMP202  | true      | true   | Introduction to Programming |
      And the following tasks are associated with "ECSE429"
      | title                    | doneStatus | description                                 |
      | Assignment1              | true       | Interview some engineers                    |
      | Assignment1              | false      | Write research paper based on the interview |
      | Project Deliverable 1    | false      | Unit test                                   |
      | Project Deliverable 2    | false      | Cucumber test   |
      And the following tasks are associated with "COMP512"
      | title               | doneStatus | description              |
      | Assignment1         | true       | RMI implementation       |
      | Assignment1         | false      | Strict 2PL implementation|
      And the following tasks are associated with "ECSE416"
      | title            | doneStatus      | description          |
      | Lab1             | true            | TCP                  |
      | Lab2             | true            | UDP                  |

  Scenario Outline: Query incomplete tasks for a course that has incomplete tasks associated with it (Normal flow)
    Given <title> is the title of the class
     When the user requests the incomplete tasks for the course with title <title>
     Then <n_todos> todos will be returned
      And the returned tasks of <title> all marked as incomplete
    Examples:
      | title        | n_todos |
      | ECSE429      | 3 |
      | COMP512      | 1 |

  Scenario Outline: Query incomplete tasks for a course that does not has incomplete tasks associated with it (Alternate flow)
    Given <title> is the title of the class
     When the user requests the incomplete tasks for the course with <title>
     Then no todos will be returned
    Examples:
      | title       |
      | ECSE416     |

  Scenario Outline: Query incomplete tasks for a course with no tasks (Alternate flow)
    Given <title> is the title of the class
     When the user requests the incomplete tasks for the course with <title>
     Then no todos will be returned
    Examples:
      | title       |
      | COMP202     |

  Scenario Outline: Query incomplete tasks for a non-exist course (Error flow)
    Given <title> is the title of the class
     When the user requests the incomplete tasks for the course with <title>
      Then the user will receive an error telling them that the course doesn't exist on the system
    Examples:
      | title     |
      | NULL      |