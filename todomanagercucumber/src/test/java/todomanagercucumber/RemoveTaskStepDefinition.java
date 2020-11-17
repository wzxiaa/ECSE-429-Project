package todomanagercucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RemoveTaskStepDefinition extends BaseStepDefinition {
    @Given("^(.+) is the title of a course on the system$")
    public void is_the_title_of_a_course_on_the_system(String projecttitle) throws Throwable {
        JSONObject project = findProjectByName(projecttitle);
        p_id = project.getInt("id");
    }

    @When("^the student requests to delete an existing task with title (.+)$")
    public void the_student_requests_to_delete_an_existing_task_with_title(String todotitle) throws Throwable {
        JSONObject todo = findTodoByName(todotitle);
        if(todo == null) {
            int todo_id = -1;
            responsesList.add(unirest.delete(BASE_URL + "/todos" + "/" + todo_id + "/tasksof" + "/" + 1).asJson());
        } else {
            int t_id = todo.getInt("id");
            deleteTaskByIds(p_id, t_id);
        }
    }

    @When("^the student requests to delete a task with title (.+)$")
    public void the_student_requests_to_delete_a_task_with_title(String todotitle) throws Throwable {
        JSONObject todo = findTodoByName(todotitle);
        if(todo == null) {
            int todo_id = -1;
            responsesList.add(unirest.delete(BASE_URL + "/todos" + "/" + todo_id).asJson());
        } else {
            int t_id = todo.getInt("id");
            deleteTaskByIds(p_id, t_id);
        }
    }

    @When("^the student requests to delete all tasks from (.+)$")
    public void the_student_requests_to_delete_all_tasks_from(String projecttitle) throws Throwable {
        deleteAllTask(projecttitle);
    }

    @Then("^(.+) is returned.$")
    public void is_returned(String status) throws Throwable {
        assertEquals(Integer.parseInt(status), httpresponse.getStatus());
    }

    @Then("^the (.+) todos from (.+) are removed$")
    public void the_todos_from_are_removed(String m, String projecttitle) throws Throwable {
        JSONObject project = findProjectByName(projecttitle);
        JSONArray todos = getProjectTasks(projecttitle);
        assertEquals(true, todos.isEmpty());
    }

    @And("^the course with the title (.+) has some tasks$")
    public void the_course_with_the_title_has_some_tasks(String projecttitle) throws Throwable {
        JSONObject project = findProjectByName(projecttitle);
        JSONArray todos = getProjectTasks(projecttitle);
        assertEquals(false, todos.isEmpty());
    }


    @Then("{int} is returned")
    public void is_returned(Integer int1) {
        for(HttpResponse<JsonNode> response: responsesList) {
            assertEquals((int)int1, response.getStatus());
        }
    }


    @Given("^(.+) is a not a course on the system$")
    public void is_a_not_a_course_on_the_system(String projectTitle) throws Throwable {
        JSONObject project = findProjectByName(projectTitle);
        assertEquals(true, project==null);
    }
}