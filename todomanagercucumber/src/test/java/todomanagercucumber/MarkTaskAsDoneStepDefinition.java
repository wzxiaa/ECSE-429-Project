package todomanagercucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import kong.unirest.json.JSONObject;

import static org.junit.Assert.*;

public class MarkTaskAsDoneStepDefinition extends BaseStepDefinition {

    @Given("^(.+) is the title of the task$")
    public void is_the_title_of_the_task(String title) throws Throwable {

    }

    @When("^the user marks the task with (.+) as completed$")
    public void the_user_marks_the_task_with_as_completed(String title) throws Throwable {
        markTaskAsDone(title);
    }

    @Then("^the task with (.+) is marked as completed$")
    public void the_task_with_is_marked_as_completed(String title) throws Throwable {
        JSONObject task = findTodoByName(title);
        System.out.println(task.toString());
        boolean doneStatus = Boolean.parseBoolean((String)task.get("doneStatus"));
        assertEquals(true, doneStatus);

    }

    @Then("^the operation fails with (.+)$")
    public void the_operation_fails_with(String status) throws Throwable {
        assertEquals(Integer.parseInt(status), httpresponse.getStatus());
    }

    @And("^task with (.+) is not completed$")
    public void task_with_is_not_completed(String title) throws Throwable {
        JSONObject task = findTodoByName(title);
        boolean doneStatus = Boolean.parseBoolean((String)task.get("doneStatus"));
        assertEquals(false, doneStatus);
    }

    @And("^the task with (.+) is returned with (.+)$")
    public void the_task_with_is_returned_with(String title, String status) throws Throwable {
        int actual_Status = httpresponse.getStatus();
        assertEquals(Integer.parseInt(status), actual_Status);
    }

    @And("^task with (.+) is completed$")
    public void task_with_is_completed(String title) throws Throwable {
        JSONObject task = findTodoByName(title);
        boolean doneStatus = Boolean.parseBoolean((String)task.get("doneStatus"));
        assertEquals(true, doneStatus);
    }
}
