package todomanagercucumber;

import io.cucumber.java.an.E;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.UnirestInstance;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BaseSteps {

    protected static String pathToJar = "/Users/wenzongxia/Documents/GitHub/ECSE429/ECSE-429-Project/todomanagercucumber/src/test/java/todomanagercucumber/runTodoManagerRestAPI-1.5.5.jar";
    protected static final String BASE_URL = "http://localhost:4567";
    protected static Process serverProcess = null;
    protected static UnirestInstance unirest = Unirest.primaryInstance();
    protected static final int STATUS_OK = 200;
    protected static final int STATUS_CREATED = 201;
    protected static final int STATUS_BAD_REQUEST = 400;
    protected static final int STATUS_NOT_FOUND = 404;

    public static void stopServer() {
        System.out.println("Terminating server...");
        serverProcess.destroy();
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void startServer() {
        try {
            System.out.println("Starting server...");
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

}
