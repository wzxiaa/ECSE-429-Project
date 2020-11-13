package todomanagercucumber;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BaseSteps {

    public static final String BASE_URL = "http://localhost:4567";
    private static Process serverProcess;

    public static void stopServer() {
        System.out.println("Terminating server...");
        serverProcess.destroy();
    }

    public static void startServer() {
        try {
            System.out.println("Starting server...");
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", "../runTodoManagerRestAPI-1.5.5.jar");
            if (serverProcess != null) {
                System.out.println("Server process started!");
                serverProcess.destroy();
            }
            serverProcess = pb.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
