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
import java.util.jar.JarException;

import static org.junit.Assert.*;

//@CucumberOptions(features = "classpath:todomanagercucumber/ID007_query_incomplete_tasks.feature")
public class QueryIncompleteTasksStepDefinition extends BaseSteps {

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

//    @Then("^(.+) todos will be returned$")
//    public void todos_will_be_returned(String ntodos) throws Throwable {
//        assertEquals(expected_incompleted_todos_of_course.size(), actual_incompleted_todos_of_course.size());
//    }

    @Then("^no todos will be returned$")
    public void no_todos_will_be_returned() throws Throwable {
        if(actual_incompleted_todos_of_course == null) {
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

    @And("^the following courses exist in the system$")
    public void the_following_courses_exist_in_the_system(DataTable table) throws Throwable {
        ArrayList<List<String>> data = new ArrayList<>(table.asLists(String.class));
        List<String> headers = data.get(0);
        data.remove(0);
        for(List<String> sublist: data) {
            JSONObject tmp_body = new JSONObject();
            for(int i=0; i<sublist.size(); i++) {
                if (i==1 || i==2) {
                    tmp_body.put(headers.get(i), Boolean.parseBoolean(sublist.get(i)));
                } else {
                    tmp_body.put(headers.get(i), sublist.get(i));
                }
            }
//            System.out.println(tmp_body);

            HttpResponse<JsonNode> create_project = unirest.post(BASE_URL + "/projects").body(tmp_body.toString()).asJson();
            assertEquals(STATUS_CREATED, create_project.getStatus());
        }
    }

    @And("^the following tasks are associated with \\\"([^\\\"]*)\\\"$")
    public void the_following_tasks_are_associated_with_ecse_429(String classname, DataTable table) throws Throwable {
        System.out.println(classname);
        JSONObject project = findProjectByName(classname);
        if (project != null) {
            int project_id = project.getInt("id");
            List<Integer> todos_ids = new ArrayList<>();
            ArrayList<List<String>> data = new ArrayList<>(table.asLists(String.class));
            List<String> headers = data.get(0);
            data.remove(0);
            for(List<String> sublist: data) {
                JSONObject todo_body = new JSONObject();
                for(int i=0; i<sublist.size(); i++) {
                    if(i==1) {
                        System.out.println(Boolean.parseBoolean(sublist.get(i)));
                        todo_body.put(headers.get(i), Boolean.parseBoolean(sublist.get(i)));
                    } else {
                        todo_body.put(headers.get(i), sublist.get(i));
                    }
                }

                HttpResponse<JsonNode> create_todo = unirest.post(BASE_URL + "/todos").body(todo_body.toString()).asJson();
                assertEquals(STATUS_CREATED, create_todo.getStatus());
                int todo_id = create_todo.getBody().getObject().getInt("id");

                JSONObject req_body = createJSONObjectSingleRow("id", Integer.toString(project_id));
                HttpResponse<JsonNode> temp_res = unirest.post(BASE_URL + "/todos" + "/" + todo_id + "/tasksof").body(req_body.toString()).asJson();
                assertEquals(STATUS_CREATED, temp_res.getStatus());

                boolean status = todo_body.getBoolean("doneStatus");
                if (!status) {
                    todos_ids.add(todo_id);
                }
            }
            expected_incompleted_todos_of_course.put(project_id, todos_ids);
            System.out.println("expected_incompleted_todos_of_course.size: "+ expected_incompleted_todos_of_course.size());
        } else {
            fail();
        }
    }


    @And("^the returned tasks of (.+) all marked as incomplete$")
    public void the_returned_tasks_of_all_marked_as_incomplete(String title) throws Throwable {
        Iterator<Integer> itr = actual_incompleted_todos_of_course.keySet().iterator();
        while(itr.hasNext()){
            int todo_id = itr.next();
            JSONObject todo = findTodoByID(todo_id);
            assertEquals(false, Boolean.parseBoolean(todo.getJSONArray("todos").getJSONObject(0).getString("doneStatus")));
        }
    }

    // Helper functions

    protected static HashMap<Integer, Boolean> findIncompletedTasksWithProject(String title) {
        HashMap<Integer, Boolean> incompleted = new HashMap<>();
        JSONObject project = findProjectByName(title);
//        System.out.println(project.toString());
        if(project==null) {
            return null;
        }
        try {
            JSONArray todos = project.getJSONArray("tasks");
            System.out.println("todos : " + todos.toString());
            for(Object todo: todos) {
                JSONObject obj = (JSONObject) todo;
                int todo_id = obj.getInt("id");
                boolean status = Boolean.parseBoolean(findTodoByID(obj.getInt("id")).getJSONArray("todos").getJSONObject(0).getString("doneStatus"));
                if(!status) {
                    incompleted.put(todo_id, status);
                }
            }
            return incompleted;
        }catch (JSONException e) {
            System.out.println(project.toString());
            return null;
        }
    }
}