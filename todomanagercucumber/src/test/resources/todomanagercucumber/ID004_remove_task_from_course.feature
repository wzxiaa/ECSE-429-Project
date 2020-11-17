Feature: ID_004 Remove task from to do list

As a student, I remove an unnecessary task from my course to do list, so I can forget about it.

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
      | Assignment2           | false      | Write research paper based on the interview |
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

  Scenario Outline: Remove a valid task from todo list (Normal Flow)
    Given <projectTitle> is the title of a course on the system
      And the course with the title <projectTitle> has some tasks
     When the student requests to delete an existing task with title <todoTitle>
     Then <status> is returned
    Examples:
      | projectTitle | todoTitle   | status |
      | ECSE429      | Assignment1 | 200    |
      | ECSE429      | Assignment2 | 200    |
      | COMP512      | Quiz1       | 200    |
      | COMP512      | Quiz2       | 200    |

  Scenario Outline: Remove all tasks from course (Alternate Flow)
    Given <projectTitle> is the title of a course on the system
      And the course with the title <projectTitle> has some tasks
     When the student requests to delete all tasks from <projectTitle>
     Then the <m> todos from <projectTitle> are removed
      And <status> is returned
    Examples:
      | projectTitle | m | status |
      | ECSE429      | 2 | 200    |
      | COMP512      | 2 | 200    |

  Scenario Outline: Remove non-existing task (Error Flow)
    Given <todoTitle> is a not a course on the system
     When the student requests to delete a task with title <todoTitle>
     Then <status> is returned
    Examples:
      | todoTitle | status |
      | COMP205   | 404    |
      | COMP206   | 404    |


