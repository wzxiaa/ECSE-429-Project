package todomanagercucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddPriorityToTask extends BaseSteps{

    String errorMessage;
    int statusCode;
    JSONObject originalValue;
    JSONObject response;
    JSONObject originalTodoList;
    JSONArray taskList;
    int counter;

    @Before
    public void initVars() {
        Unirest.config().defaultBaseUrl(BASE_URL);
        startServer();
        counter = 0;
        statusCode = 0;
        errorMessage = "";
        response = null;
        originalValue = null;
        originalTodoList = null;
        taskList = null;
    }

    @After
    public void after() {
        stopServer();
    }

    @Given("the todo with name {string}, done status {string} and description {string} is registered in the system")
    public void the_todo_with_name_done_status_and_description_is_registered_in_the_system(String todotitle, String tododonestatus, String tododescription) {
        Unirest.post("/todos")
                .body("{\"title\":\"" + todotitle.replace("\"", "") + "\",\"doneStatus\":"
                        + tododonestatus.replace("\"", "") + ",\"description\":\"" + tododescription.replace("\"", "") + "\"}")
                .asJson();
    }

    @When("user requests to categorize todo with title {string} as {string} priority")
    public void when_user_requests_to_categorize_todo_with_title_as_priority(String todotitle, String prioritytoassign) {
        // Find ID of Task todo_title
        int id = findIdFromTodoName(todotitle.replace("\"", ""));

        HttpResponse<JsonNode> response = Unirest.post("/todos/" + id +"/categories")
                .body("{\n\"title\":\"" + prioritytoassign.replace("\"", "") + "\"\n}\n").asJson();

        statusCode = response.getStatus();
        if(statusCode != 200 && statusCode != 201) {
            errorMessage = response.getBody().getObject().getJSONArray("errorMessages").getString(0);
        }
    }

    @Then("^the \"([^\"]*)\" should be classified as a \"([^\"]*)\" priority task$")
    public void the_should_be_classified_as_a_priority_task(String todotitle, String prioritytoassign) {
        int category_id = findIdFromTodoCategoryName(prioritytoassign.replace("\"", ""), todotitle.replace("\"", ""));
        assertTrue(category_id != -1);
    }

    @And("^the todo (.+) is assigned as a (.+) priority task$")
    public void the_todo_is_assigned_as_a_priority_task(String todotitle, String originalpriority) {
        when_user_requests_to_categorize_todo_with_title_as_priority(todotitle, originalpriority);
    }

    @When("^user requests to remove (.+) priority categorization from (.+)$")
    public void user_requests_to_remove_priority_categorization_from(String oldpriority, String todotitle) {
        int category_id = findIdFromTodoCategoryName(oldpriority.replace("\"", ""), todotitle.replace("\"", ""));
        int todo_id = findIdFromTodoName(todotitle.replace("\"", ""));

        Unirest.delete("/todos/" + todo_id + "/categories/" + category_id)
                .header("Content-Type", "application/json")
                .asJson();
    }

    @Then("^the system should output an error message$")
    public void the_system_should_output_an_error_message() {
        assertEquals(errorMessage, "Could not find parent thing for relationship todos/-1/categories");
        // TODO: One is empty, one has message, potential a bug
    }
}
