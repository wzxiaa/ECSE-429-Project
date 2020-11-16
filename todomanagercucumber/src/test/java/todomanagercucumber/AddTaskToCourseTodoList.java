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

public class AddTaskToCourseTodoList extends BaseSteps{

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

    @Given("the todo with name {string} and description {string} is registered in the system")
    public void the_todo_with_name_something_and_description_something_is_registered_in_the_system(String todotitle, String tododescription){
        Unirest.post("/todos")
                .body("{\"title\":\"" + todotitle + "\",\n\"description\":\"" + tododescription+ "\"\n}")
                .asJson();
    }

    @When("the user requests to add the todo with name {string} and description {string} to the course with title {string}")
    public void the_user_requests_to_add_the_todo_with_name_something_and_description_something_to_the_course_with_title_something(String todotitle, String tododescription, String coursetitle){
        int todoId= findIdFromTodoName(todotitle);
        Unirest.post("/todos/"+todoId+"/tasksof").body("{\"title\":\"" + coursetitle + "\"}").asJson();
    }

    @Then("the todo with name {string} should be added to the todo list of the course with title {string}")
    public void the_todo_with_name_something_should_be_added_to_the_todo_list_of_the_course_with_title_something(
            String todotitle, String coursetitle) {
        assertEquals(1, getProjectTasks(coursetitle).length());
    }

    @Given("the course with {string} is not in the system")
    public void the_course_with_something_is_not_in_the_system(String coursetitle) {
        assertEquals(null, findProjectByName(coursetitle));
    }

    @When("the user requests to add the course with {string} to the system")
    public void the_user_requests_to_add_the_course_with_something_to_the_system(String coursetitle) {
        the_course_with_title_something_is_registered_in_the_system(coursetitle);
    }

    @Given("the course with title {string} is registered in the system")
    public void the_course_with_title_something_is_registered_in_the_system(String coursetitle) {
        HttpResponse<JsonNode> gResponse = Unirest.post("/projects").body("{\"title\":\"" + coursetitle + "\"}")
                .asJson();
        response = gResponse.getBody().getObject();
        statusCode = gResponse.getStatus();
    }


    @And("the user requests to add the todo with name {string} and description {string} to the course {string}")
    public void the_user_requests_to_add_the_todo_with_name_something_and_description_something_to_the_course_something(String todotitle, String tododescription, String coursetitle){
        Unirest.post("/projects/"+findProjectByName(coursetitle).getInt("id")+"/tasks").body("{\"title\":\"" + todotitle+"\"}").asJson();
    }

    @Given("the todo with name {string} is registered in the system")
    public void the_todo_with_name_something_is_registered_in_the_system(String todotitle) {
        Unirest.post("/todos")
                .body("{\"title\":\"" + todotitle + "\"}")
                .asJson();
    }

    @And("the only course in the database is the course with title {string}")
    public void the_only_course_in_the_database_is_the_course_with_title_something(String coursetitle){
        the_course_with_title_something_is_registered_in_the_system(coursetitle);
        int length = Unirest.get("/projects").asJson().getBody().getObject().getJSONArray("projects").length();
        assertEquals(2, length);
    }

    @When("user requests to add the todo with name {string} to the project title {string}")
    public void user_requests_to_add_the_todo_with_name_something_to_the_project_title_something(String todotitle, String inavlidcoursettitle) {
        JSONObject course = findProjectByName(inavlidcoursettitle);
        if (course == null) {
            statusCode = Unirest.get("/projects/-1").asJson().getStatus();
        }
    }

    @Then("the system should output an error code {string}")
    public void the_system_should_output_an_error_code_something(String errorcode) {
        assertEquals(Integer.parseInt(errorcode),statusCode);
    }
}
