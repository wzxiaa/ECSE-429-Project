package todomanagercucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SetUpStepDefinition extends BaseStepDefinition {

    @Given("^the API server is running$")
    public void the_api_server_is_running() throws Throwable {
        assertEquals(true, isAlive());
    }

    @Before
    public static void setupForAllTests() {
        serverProcess = null;
        unirest.config().reset();
        startServer();
        initilizeVariables();
    }

    @After
    public static void tearDownAllTests() {
        stopServer();
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

    @And("^the following todos exist in the system$")
    public void the_following_todos_exist_in_the_system(DataTable table) throws Throwable {
        ArrayList<List<String>> data = new ArrayList<>(table.asLists(String.class));
        List<String> headers = data.get(0);
        data.remove(0);
        for(List<String> sublist: data) {
            JSONObject tmp_body = new JSONObject();
            for(int i=0; i<sublist.size(); i++) {
                if (i==1) {
                    tmp_body.put(headers.get(i), Boolean.parseBoolean(sublist.get(i)));
                } else {
                    tmp_body.put(headers.get(i), sublist.get(i));
                }
            }

            HttpResponse<JsonNode> create_project = unirest.post(BASE_URL + "/todos").body(tmp_body.toString()).asJson();
            assertEquals(STATUS_CREATED, create_project.getStatus());
        }
    }

    @And("^the following categories exist in the system$")
    public void the_following_categories_exist_in_the_system(DataTable table) throws Throwable {
        ArrayList<List<String>> data = new ArrayList<>(table.asLists(String.class));
        List<String> headers = data.get(0);
        data.remove(0);
        for(List<String> sublist: data) {
            JSONObject tmp_body = new JSONObject();
            for(int i=0; i<sublist.size(); i++) {
                tmp_body.put(headers.get(i), sublist.get(i));
            }

            HttpResponse<JsonNode> create_project = unirest.post(BASE_URL + "/categories").body(tmp_body.toString()).asJson();
            assertEquals(STATUS_CREATED, create_project.getStatus());
        }
    }

    @Given("the following tasks are associated with {string}")
    public void the_following_tasks_are_associated_with(String string, io.cucumber.datatable.DataTable dataTable) {
        System.out.println(string);
        JSONObject project = findProjectByName(string);
        if (project != null) {
            int project_id = project.getInt("id");
            List<Integer> todos_ids = new ArrayList<>();
            ArrayList<List<String>> data = new ArrayList<>(dataTable.asLists(String.class));
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

        }
    }
}
