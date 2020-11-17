package todomanagercucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONException;
import kong.unirest.json.JSONObject;

import java.util.*;

import static org.junit.Assert.*;

public class QueryIncompleteTasksStepDefinition extends BaseStepDefinition {


    @Given("^(.+) is the title of the class$")
    public void is_the_title_of_the_class(String title) throws Throwable {
        body.put("title", title);
    }

    @When("^the user requests the incomplete tasks for the course with (.+)$")
    public void the_user_requests_the_incomplete_tasks_for_the_course_with(String title) throws Throwable {
        actual_incompleted_todos_of_course = findIncompletedTasksWithProject(title);
    }

    @Then("the returned tasks all marked as incomplete")
    public void the_returned_tasks_all_marked_as_incomplete() {
        throw new io.cucumber.java.PendingException();
    }

    @Then("^no tasks will be returned$")
    public void no_todos_will_be_returned() throws Throwable {
        if (actual_incompleted_todos_of_course == null) {
            actual_incompleted_todos_of_course = new HashMap<>();
        }
        assertEquals(true, actual_incompleted_todos_of_course.isEmpty());
    }

    @Then("^(.+) todos will be returned for (.+)$")
    public void todos_will_be_returned_for(String ntodos, String title) throws Throwable {
        JSONObject project = findProjectByName(title);
        int p_id = project.getInt("id");
        assertEquals(expected_incompleted_todos_of_course.get(p_id).size(), actual_incompleted_todos_of_course.size());
    }

    @And("^the returned tasks of (.+) all marked as incomplete$")
    public void the_returned_tasks_of_all_marked_as_incomplete(String title) throws Throwable {
        Iterator<Integer> itr = actual_incompleted_todos_of_course.keySet().iterator();
        while (itr.hasNext()) {
            int todo_id = itr.next();
            JSONObject todo = findTodoByID(todo_id);
            assertEquals(false, Boolean.parseBoolean(todo.getJSONArray("todos").getJSONObject(0).getString("doneStatus")));
        }
    }

    protected static HashMap<Integer, Boolean> findIncompletedTasksWithProject(String title) {
        HashMap<Integer, Boolean> incompleted = new HashMap<>();
        JSONObject project = findProjectByName(title);
//        System.out.println(project.toString());
        if (project == null) {
            return null;
        }
        try {
            JSONArray todos = project.getJSONArray("tasks");
            System.out.println("todos : " + todos.toString());
            for (Object todo : todos) {
                JSONObject obj = (JSONObject) todo;
                int todo_id = obj.getInt("id");
                boolean status = Boolean.parseBoolean(findTodoByID(obj.getInt("id")).getJSONArray("todos").getJSONObject(0).getString("doneStatus"));
                if (!status) {
                    incompleted.put(todo_id, status);
                }
            }
            return incompleted;
        } catch (JSONException e) {
            System.out.println(project.toString());
            return null;
        }
    }
}
