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

    // variables used for each tests
    protected static String errorMessage;
    protected static int statusCode;
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
        originalValue = null;
        originalTodoList = null;
        taskList = null;
        httpresponse = null;
        actual_incompleted_todos_of_course = new HashMap<>();
        expected_incompleted_todos_of_course = new HashMap<>();
        p_id = -1;
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
}
