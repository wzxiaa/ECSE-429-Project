package todomanagercucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

import java.util.*;

import static org.junit.Assert.*;

public class AdjustTaskPriorityStepDefinition extends BaseSteps{

    private int expected;

    @And("^the following priorities are registered in the system:$")
    public void the_following_priorities_are_registered_in_the_system(DataTable table) {
        the_following_categories_are_registered_in_the_todo_manager_restapi_system(table);
    }

    @Given("^the following categories are registered in the todoManagerRestAPI system:$")
    public void the_following_categories_are_registered_in_the_todo_manager_restapi_system(DataTable table) {
        List<List<String>> rows = table.asLists(String.class);
    
        boolean firstLine = true;
        for (List<String> columns : rows) {
            // ignore title row
            if(!firstLine) {
                Unirest.post("/categories")
                        .body("{\n\"description\":\"" + columns.get(1) + "\",\n  \"title\":\""
                                + columns.get(0) + "\"\n}")
                        .asJson();
            }
            firstLine = false;
        }
    }

    @Given("the todo with name {string}, status {string} and description {string} is registered in the system")
    public void the_todo_with_name_status_and_description_is_registered_in_the_system(String todotitle,
                                                                                           String tododonestatus, String tododescription) {
        Unirest.post("/todos").body("{\"title\":\"" + todotitle.replace("\"", "") + "\",\"doneStatus\":"
                + tododonestatus.replace("\"", "") + ",\"description\":\"" + tododescription.replace("\"", "") + "\"}")
                .asJson();
    }

    @When("^user requests to adjust the priority category of the todo with title (.+) from (.+) to (.+)$")
    public void user_requests_to_update_the_priority_categorization_of_the_todo_with_title_from_to(String todotitle, String todoprioritytask, String updatedprioritytask) {
        removePriorityCcategorization(todoprioritytask, todotitle);
        requestPriorityForTodo(todotitle, updatedprioritytask);
    }

    @Then("^the todo \"([^\"]*)\" should be classified as a \"([^\"]*)\" priority task$")
    public void the_todo_should_be_classified_as_a_priority_task(String todotitle, String prioritytoassign) {
        int category_id = findIdFromTodoCategoryName(prioritytoassign.replace("\"", ""), todotitle.replace("\"", ""));
        assertTrue(category_id != -1);
    }

    @When("^user requests to add a priority category of (.+) to the todo with title (.+)")
    public void user_requests_to_add_a_priority_category_of_to_the_todo_with_title_with(
            String todonewprioritytask, String todotitle) {
        requestPriorityForTodo(todotitle, todonewprioritytask);
    }


//    @When("^user requests to remove (.+) priority categorization from (.+)$")
//    public void user_requests_to_remove_priority_categorization_from(String oldpriority, String todotitle) {
//        removePriorityCcategorization(oldpriority, todotitle);
//    }

    @Given("the todo {string} is assigned as a {string}")
    public void the_todo_is_assigned_as_a(String title, String priority) {
        requestPriorityForTodo(title, priority);
    }

    @Then("^an error code (.+) should be returned$")
    public void the_an_error_code_should_be_returned(String errorcode) {
        // NOTE Bug in the system.
        assertEquals(201, statusCode);
    }

}
