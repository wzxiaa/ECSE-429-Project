Feature: Delete course to do list (ID_006)
    As a student, I remove a to do list for a class which I am no longer taking, to declutter my schedule.

  Background:
    Given the API server is running
      And the following courses exist in the system
      | title   | completed | active | description                 |
      | ECSE429 | false     | true   | Software Validiation        |
      | COMP512 | false     | true   | Distributed System          |
      | ECSE416 | true      | false  | Telecommunication           |
      | COMP202 | true      | true   | Introduction to Programming |
      | GEOG203 | true      | false  | Environmental System        |
      And the following tasks are associated with 'ECSE429'
      | title                 | doneStatus | description                                 |
      | Assignment1           | true       | Interview some engineers                    |
      | Assignment1           | false      | Write research paper based on the interview |
      | Project Deliverable 1 | false      | Unit test                                   |
      | Project Deliverable 2 | false      | Cucumber test                               |
      And the following tasks are associated with 'COMP512'
      | title | doneStatus | description               |
      | Quiz1 | true       | RMI implementation        |
      | Quiz2 | false      | Strict 2PL implementation |
      And the following tasks are associated with 'ECSE416'
      | title | doneStatus | description |
      | Lab1  | true       | TCP         |
      | Lab2  | true       | UDP         |

  Scenario Outline: Delete a course to do list that has tasks associated with it (Normal flow)
    Given <title> is the title of the class
      And the class with <title> has associated tasks
     When the user deletes the course to do list for the course with <title>
     Then the course to do list is deleted and returns <status>
      And no tasks are associated with course <title>

    Examples:
      | title   | status |
      | ECSE429 | 200    |
      | COMP512 | 200    |
      | ECSE416 | 200    |

  Scenario Outline: Delete a course to do list that does not have tasks associated with it (Alternative flow)
    Given <title> is the title of the class
      And the class <title> has no associated tasks
     When the user deletes the course to do list for the course with <title>
     Then the course to do list is deleted and returns <status>
      And no tasks are associated with course <title>
    Examples:
      | title   | status |
      | COMP202 | 200    |
      | GEOG203 | 200    |

  Scenario Outline: Delete a non-exist course to do list (Error flow)
    Given <title> is the title of the class
     When the user deletes the non-exist course to do list for the course with <title>
     Then the course to do list cannot be deleted and returns <status>
    Examples:
      | title   | status |
      | NULL    | 404    |
      | ECSE420 | 404    |
