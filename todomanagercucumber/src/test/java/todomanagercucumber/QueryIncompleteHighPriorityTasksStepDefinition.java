package todomanagercucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import io.cucumber.java.sl.In;
import io.cucumber.junit.CucumberOptions;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONException;
import kong.unirest.json.JSONObject;

import java.util.*;

import static org.junit.Assert.*;

//@CucumberOptions(features = "classpath:todomanagercucumber/ID007_query_incomplete_tasks.feature")
public class QueryIncompleteHighPriorityTasksStepDefinition extends BaseSteps {

    @When("^the user requests the incomplete HIGH priority tasks for the course with title (.+)$")
    public void the_user_queries_all_incomplete_tasks_with_high_priority_from_a_course_with_title(String title) {
        taskList = new JSONArray();
        JSONArray tasks = getProjectTasks(title);
        if (tasks == null) {
            //Problem: /projects/-1/tasks is a known bug, using /projects/-1 instead to show error
            response = Unirest.get("/projects/-1")
                    .asJson().getBody().getObject();
            return;
        }
        for (Object o : tasks) {
            System.out.println(o.toString());
            int id = ((JSONObject)o).getInt("id");
            JSONObject todo = (JSONObject) Unirest.get("/todos/" + id)
                    .asJson().getBody().getObject()
                    .getJSONArray("todos").get(0);
            int priorityID = ((JSONObject) ((JSONArray) todo.get("categories")).get(0)).getInt("id");
            System.out.println(priorityID);
            System.out.println((JSONObject) ((JSONArray) ( Unirest.get("/categories/" + priorityID).asJson().getBody().getObject()).get("categories")).get(0));
            String category = (String) ((JSONObject) ((JSONArray) ( Unirest.get("/categories/" + priorityID).asJson().getBody().getObject()).get("categories")).get(0)).get("title");
            System.out.println(category);
            if (todo.getString("doneStatus").equalsIgnoreCase("false") && category.equalsIgnoreCase("HIGH")) {
                taskList.put(todo);
            }
        }
    }

    @Given("^(.+) is the title of a class in the system$")
    public void is_the_title_of_a_class_in_the_system(String title) {
    }

    @Given("^(.+) is not a title of a class in the system$")
    public void is_not_a_title_of_a_class_in_the_system(String title) {
    }

    @And("^the class with title (.+) has incomplete tasks$")
    public void the_class_with_title_has_incomplete_tasks(String title) {
    }

    @And("^the class with title (.+) has no incomplete tasks$")
    public void the_class_with_title_has_no_incomplete_tasks(String title) {
    }

    @And("no todos are associated with {string}")
    public void noTodosAreAssociatedWith(String className) {
        assertEquals(getProjectTasks(className).length(), 0);
    }

    @And("^the class with title (.+) has no tasks$")
    public void the_class_with_title_has_no_tasks(String title) {
    }

    @Then("^(.*) todos will be returned$")
    public void nTodosWillBeReturned(int n) {
        assertEquals(n, taskList.length());
    }

    @And("^the user will receive an error telling them that the course doesn't exist in the system$")
    public void the_user_will_receive_an_error_telling_them_that_the_course_doesnt_exist_in_the_system() {
    }

    @And("^each todo returned will have a HIGH priority$")
    public void each_todo_returned_will_have_a_high_priority() {
    }

    @And("^each todo returned will be marked as done$")
    public void eachTodoReturnedWillBeMarkedAsDone() {
        for (Object o : taskList) {
            JSONObject todo = (JSONObject) o;
            assertDoneStatusEquals(todo, false);
        }
    }

    @Given("the following todos are associated with {string}")
    public void the_following_todos_are_associated_with_class(String className, DataTable table) {
        List<List<String>> rows = table.asLists(String.class);
        int projId = findProjectByName(className).getInt("id");
        boolean firstLine = true;
        for (List<String> columns : rows) {
            if(!firstLine) {
                int id = addTodoByRow(columns).getInt("id");
                Unirest.post("/todos/" + id + "/tasksof")
                        .body("{\"id\":\"" + projId + "\"}")
                        .asJson();
            }
            firstLine = false;
        }
    }
}