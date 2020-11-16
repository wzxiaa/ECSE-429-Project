package todomanagercucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.an.E;
import io.cucumber.java.en.Given;
import kong.unirest.*;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BaseSteps {

    protected static String pathToJar = "runTodoManagerRestAPI-1.5.5.jar";
    protected static final String BASE_URL = "http://localhost:4567";
    protected static Process serverProcess = null;
    protected static UnirestInstance unirest = Unirest.primaryInstance();
    protected static final int STATUS_OK = 200;
    protected static final int STATUS_CREATED = 201;
    protected static final int STATUS_BAD_REQUEST = 400;
    protected static final int STATUS_NOT_FOUND = 404;

    protected static JSONObject body = null;
    protected static HttpResponse<JsonNode> response = null;
    protected static JSONArray tasklist = null;

    protected static HashMap<Integer, Boolean> actual_incompleted_todos_of_course = null;
    protected static HashMap<Integer, List<Integer>> expected_incompleted_todos_of_course = null;
    protected static int p_id;

    public static void stopServer() {
        System.out.println("Terminating server...");
        serverProcess.destroy();
        body = null;
        response = null;
        tasklist = null;
        actual_incompleted_todos_of_course = null;
        expected_incompleted_todos_of_course = null;
        p_id = -1;

        try {
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void startServer() {
        try {
            System.out.println("Starting server...");

            body = new JSONObject();
            actual_incompleted_todos_of_course = new HashMap<>();
            expected_incompleted_todos_of_course = new HashMap<>();
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
            System.out.println("before");
            int status = unirest.get(BASE_URL).asString().getStatus();
            System.out.println("after");
            System.out.println("status: " + status);
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
        // HttpResponse<JsonNode> response = Unirest.get(BASE_URL + "/todos").asJson().getBody().getObject();
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
}
