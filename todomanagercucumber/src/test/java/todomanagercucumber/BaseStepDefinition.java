package todomanagercucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.an.E;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import kong.unirest.*;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class BaseStepDefinition {

    protected static String pathToJar = "runTodoManagerRestAPI-1.5.5.jar";
    protected static final String BASE_URL = "http://localhost:4567";
    protected static Process serverProcess = null;
    protected static UnirestInstance unirest = Unirest.primaryInstance();
    protected static final int STATUS_OK = 200;
    protected static final int STATUS_CREATED = 201;
    protected static final int STATUS_BAD_REQUEST = 400;
    protected static final int STATUS_NOT_FOUND = 404;

    // variables used for each tests
    protected static String errorMessage;
    protected static int statusCode;
    protected static List<HttpResponse<JsonNode>> responsesList;
    protected static JSONObject originalValue;
    protected static JSONObject originalTodoList;
    protected static JSONArray taskList;
    protected static int counter;
    protected static JSONObject body;
    protected static JSONObject response;
    protected static HttpResponse<JsonNode> httpresponse;
    protected static JSONArray tasklist;
    protected static HashMap<Integer, Boolean> actual_incompleted_todos_of_course;
    protected static HashMap<Integer, List<Integer>> expected_incompleted_todos_of_course;
    protected static JSONObject todoItem;
    protected static int p_id;

    public static void stopServer() {
        System.out.println("Terminating server...");
        serverProcess.destroy();

        try {
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initilizeVariables() {
        counter = 0;
        statusCode = 0;
        errorMessage = "";
        response = null;
        responsesList = new ArrayList<>();
        originalValue = null;
        originalTodoList = null;
        taskList = null;
        httpresponse = null;
        actual_incompleted_todos_of_course = new HashMap<>();
        expected_incompleted_todos_of_course = new HashMap<>();
        p_id = -1;
        todoItem = null;

    }

    protected static void startServer() {
        try {
            System.out.println("Starting server...");

            body = new JSONObject();
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("java", "-jar" ,pathToJar);
            if (serverProcess != null) {
                System.out.println("not null");
                serverProcess.destroy();
            }
            serverProcess = pb.start();
            System.out.println(serverProcess);
            Thread.sleep(1000);
            Unirest.config().defaultBaseUrl(BASE_URL);
            System.out.println("server started...");
            final InputStream is = serverProcess.getInputStream();
            final BufferedReader output = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String line = output.readLine();
                System.out.println(line);
                if (line != null && line.contains("Running on 4567")) {
                    unirest = Unirest.primaryInstance();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static boolean isAlive() {
        try {
            int status = unirest.get(BASE_URL).asString().getStatus();
            if (status == STATUS_OK) return true;
            return false;
        } catch (UnirestException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected static JSONObject findProjectByName(String projectName) {
        JSONObject response = Unirest.get(BASE_URL + "/projects").asJson().getBody().getObject();
        for (Object proj : response.getJSONArray("projects")) {
            JSONObject project = (JSONObject) proj;
            if (project.getString("title").equals(projectName)) {
                return project;
            }
        }
        return null;
    }

    public static int findIdFromTodoName(String todo_name) {
        JSONObject todo = findTodoByName(todo_name);
        if (todo == null) return -1;
        return todo.getInt("id");
    }


    protected static JSONObject findTodoByName(String todoName) {
        JSONObject response = Unirest.get(BASE_URL + "/todos").asJson().getBody().getObject();
        for (Object proj : response.getJSONArray("todos")) {
            JSONObject todo = (JSONObject) proj;
            if (todo.getString("title").equals(todoName)) {
                return todo;
            }
        }
        return null;
    }

    protected static JSONObject findTodoByID(int todo_id) {
        HttpResponse<JsonNode> response = Unirest.get(BASE_URL + "/todos" + "/" + todo_id).asJson();
        if(response.isSuccess()) {
            JSONObject todo = response.getBody().getObject();
            return todo;
        }
        return null;
    }

    protected static JSONObject createJSONObjectSingleRow(String key, String value) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, value);
        return jsonObject;
    }

    protected static JSONArray findAllTodos() {
        JSONArray response = Unirest.get(BASE_URL + "/todos").asJson().getBody().getArray();
        return response;
    }

    public static int findIdFromTodoCategoryName(String category_name, String todo_name) {
        JSONObject response = Unirest.get("/todos").asJson().getBody().getObject();
        int id = -1;

        for (Object todo : response.getJSONArray("todos")) {
            JSONObject t = (JSONObject) todo;
            if (t.getString("title").equals(todo_name)) {
                int todo_id = t.getInt("id");
                JSONArray response_cat = Unirest.get("/todos/" + todo_id + "/categories").asJson()
                        .getBody().getObject().getJSONArray("categories");
                for (Object cat : response_cat) {
                    JSONObject c = (JSONObject) cat;
                    if (c.getString("title").equals(category_name)) {
                        id = c.getInt("id");
                        break;
                    }
                }
            }
        }

        return id;
    }

    public static JSONArray getProjectTasks(String projectName) {
        JSONObject proj = findProjectByName(projectName);
        if (proj == null) return null;
        int id = proj.getInt("id");
        return Unirest.get("/projects/" + id + "/tasks")
                .asJson().getBody().getObject().getJSONArray("todos");
    }
    
    public static HttpResponse<JsonNode> requestPriorityForTodo(String title, String priority) {
        int id = findIdFromTodoName(title.replace("\"", ""));

        HttpResponse<JsonNode> response = Unirest.post("/todos/" + id +"/categories")
                .body("{\n\"title\":\"" + priority.replace("\"", "") + "\"\n}\n").asJson();
        
        statusCode = response.getStatus();
        if(statusCode != 200 && statusCode != 201) {
            errorMessage = response.getBody().getObject().getJSONArray("errorMessages").getString(0);
        }
        return response;
    }

    public static void markTaskAsDone(String title) {
        JSONObject todo = findTodoByName(title);
        int todo_id;
        JSONObject body = new JSONObject();
        body.put("doneStatus", true);
        if(todo == null) {
            httpresponse = Unirest.post("/todos/" + "null").body(body).asJson();
        } else {
            todo_id = todo.getInt("id");
            body.put("doneStatus", true);
            httpresponse = Unirest.post("/todos/" + todo_id).body(body).asJson();
        }
    }
    public static void assertDoneStatusEquals(JSONObject todo, boolean val) {
        assertNotNull(todo);
        assertTrue(todo.getString("doneStatus").equalsIgnoreCase(val + ""));
    }

    //when_user_requests_to_categorize_todo_with_title_as_priority
    public static void categorizeTaskWithTitleAsPriority(String todotitle, String prioritytoassign) {
        // Find ID of Task todo_title
        int id = findIdFromTodoName(todotitle.replace("\"", ""));

        HttpResponse<JsonNode> response = Unirest.post("/todos/" + id +"/categories")
                .body("{\n\"title\":\"" + prioritytoassign.replace("\"", "") + "\"\n}\n").asJson();

        statusCode = response.getStatus();
        if(statusCode != 200 && statusCode != 201) {
            errorMessage = response.getBody().getObject().getJSONArray("errorMessages").getString(0);
        }
    }


    // the_todo_with_name_done_status_and_description_is_registered_in_the_system(String todotitle, String tododonestatus, String tododescription)
    public static void createTodo(String todotitle, String tododonestatus, String tododescription) {
        Unirest.post("/todos")
                .body("{\"title\":\"" + todotitle.replace("\"", "") + "\",\"doneStatus\":"
                        + tododonestatus.replace("\"", "") + ",\"description\":\"" + tododescription.replace("\"", "") + "\"}")
                .asJson();
    }


    public void removePriorityCcategorization(String oldpriority, String todotitle) {
        int category_id = findIdFromTodoCategoryName(oldpriority.replace("\"", ""), todotitle.replace("\"", ""));
        int todo_id = findIdFromTodoName(todotitle.replace("\"", ""));

        Unirest.delete("/todos/" + todo_id + "/categories/" + category_id).header("Content-Type", "application/json")
                .asJson();
    }

    public JSONObject addTodoByRow(List<String> columns) {
        String title = "\"title\":\"" + columns.get(0) + "\"";
        String doneStatus = "\"doneStatus\":" + columns.get(1);
        String description = "\"description\":\"" + columns.get(2) + "\"";
        JSONObject todoObj = Unirest.post("/todos")
                .body("{\n" + title + ",\n" + doneStatus + ",\n" + description + "\n}").asJson().getBody().getObject();
        if (columns.size() == 4) {
            requestPriorityForTodo(columns.get(0), columns.get(3));
        }
        return todoObj;
    }


    public static void deleteTaskByIds(int project_id, int todo_id) {
        httpresponse = unirest.delete(BASE_URL + "/todos" + "/" + todo_id + "/tasksof" + "/" + project_id).asJson();
        //statusCode = response.getStatus()
    }

    public static void deleteAllTask(String projecttitle) {
        JSONObject project = findProjectByName(projecttitle);

        p_id = project.getInt("id");
        //JSONArray response = Unirest.get(BASE_URL + "/projects" + "/" + p_id + "/" + "tasks").asJson().getBody().getArray();

        JSONArray todos = project.getJSONArray("tasks");
        //System.out.println("todos : " + todos.toString());
        for (Object todo : todos) {
            JSONObject obj = (JSONObject) todo;
            int todo_id = obj.getInt("id");
            responsesList.add(unirest.delete(BASE_URL + "/todos" + "/" + todo_id + "/tasksof" + "/" + p_id).asJson());
        }
    }
}
