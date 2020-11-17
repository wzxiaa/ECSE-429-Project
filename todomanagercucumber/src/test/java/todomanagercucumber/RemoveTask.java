package todomanagercucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RemoveTask extends BaseSteps {
    @Given("^(.+) is the title of a course on the system$")
    public void is_the_title_of_a_course_on_the_system(String projecttitle) throws Throwable {
        JSONObject project = findProjectByName(projecttitle);
        p_id = project.getInt("id");
    }

    @When("^the student requests to delete an existing task with title (.+)$")
    public void the_student_requests_to_delete_an_existing_task_with_title(String todotitle) throws Throwable {
        JSONObject todo = findTodoByName(todotitle);
        int t_id = todo.getInt("id");
        deleteTaskByIds(p_id, t_id);
    }

    @When("^the student requests to delete all tasks from (.+)$")
    public void the_student_requests_to_delete_all_tasks_from(String projecttitle) throws Throwable {
        deleteAllTask(projecttitle);
    }

    @Then("^(.+) is returned.$")
    public void is_returned(String status) throws Throwable {
        assertEquals( Integer.parseInt(status), httpresponse.getStatus());
    }

    @Then("^the (.+) todos from (.+) are removed$")
    public void the_todos_from_are_removed(String m, String projecttitle) throws Throwable {
        JSONObject project = findProjectByName(projecttitle);
        assertEquals(true, project== null);
    }
}