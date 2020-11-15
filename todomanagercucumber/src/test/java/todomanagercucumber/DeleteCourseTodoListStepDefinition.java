package todomanagercucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DeleteCourseTodoListStepDefinition extends BaseSteps {

    @When("^the user deletes the course to do list for the course with (.+)$")
    public void the_user_deletes_the_course_to_do_list_for_the_course_with(String title) throws Throwable {
        JSONObject project = findProjectByName(title);
        int p_id = project.getInt("id");
        response = unirest.delete(BASE_URL + "/projects" + "/" + p_id).asJson();
    }

    @When("^the user deletes the non-exist course to do list for the course with (.+)$")
    public void the_user_deletes_the_nonexist_course_to_do_list_for_the_course_with(String title) throws Throwable {
        JSONObject project = findProjectByName(title);
        assertEquals(null, project);
        try {
            int p_id = project.getInt("id");
        } catch (Exception e) {
            response = unirest.delete(BASE_URL + "/projects" + "/" + p_id).asJson();
        }
    }

    @Then("^the course to do list is deleted and returns (.+)$")
    public void the_course_to_do_list_is_deleted_and_returns(String status) throws Throwable {
        int actual_status = response.getStatus();
        assertEquals(Integer.parseInt(status), actual_status);
    }

    @Then("^the course to do list cannot be deleted and returns (.+)$")
    public void the_course_to_do_list_cannot_be_deleted_and_returns(String status) throws Throwable {
        int actual_status = response.getStatus();
        assertEquals(Integer.parseInt(status), actual_status);
    }

    @And("^the class with (.+) has associated tasks$")
    public void the_class_with_has_associated_tasks(String title) throws Throwable {
        JSONObject project = findProjectByName(title);
        JSONArray todos = project.getJSONArray("tasks");
        assertEquals(true, (todos.length()>0));
    }

    @And("^no tasks are associated with course (.+)$")
    public void no_tasks_are_associated_with_course(String title) throws Throwable {
        List<Integer> tasksof = new ArrayList<>();
        JSONArray todos = findAllTodos().getJSONObject(0).getJSONArray("todos");
        for(int i=0; i<todos.length(); i++) {
            JSONObject todo = todos.getJSONObject(i);
            if(todo.has("tasksof")) {
                JSONObject project = (JSONObject)(todo.getJSONArray("tasksof").get(0));
                tasksof.add(project.getInt("id"));
            }
        }
        assertEquals(false, tasksof.contains(this.p_id));
    }

    @And("^the class (.+) has no associated tasks$")
    public void the_class_has_no_associated_tasks(String title) throws Throwable {
        JSONObject project = findProjectByName(title);
        assertEquals(false, project.has("tasks"));
    }
}