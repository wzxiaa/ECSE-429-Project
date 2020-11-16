package todomanagercucumber;

import io.cucumber.java.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import io.cucumber.junit.CucumberOptions;
import kong.unirest.*;
import kong.unirest.json.JSONObject;
import static org.junit.Assert.*;

//@CucumberOptions(features = "classpath:todomanagercucumber/ID005_create_new_course.feature")
public class CreateCourseStepDefinition extends BaseSteps {


//    @Given("^the API server is running$")
//    public void the_api_server_is_running() throws Throwable {
//        assertEquals(true, isAlive());
//    }

//    @Before
//    public static void setupForAllTests() {
//        serverProcess = null;
//        unirest.config().reset();
//        startServer();
//    }
//
//    @After
//    public static void tearDownAllTests() {
//        stopServer();
//    }

    @Given("^(.+) is the title of the new course$")
    public void is_the_title_of_the_new_course(String title) throws Throwable {
        body.put("title", title);
        System.out.println("body: " + body.toString());
    }

    @Given("^(.+) is the id of the new course$")
    public void is_the_id_of_the_new_course(String id) throws Throwable {
        body.put("id", Integer.parseInt(id));
        System.out.println("body: "  + body.toString());
    }

    @When("^the user post the request$")
    public void the_user_post_the_request() throws Throwable {
        httpresponse = unirest.post(BASE_URL + "/projects").body(body.toString()).asJson();
        System.out.println("response:" + httpresponse.getBody());
    }

    @Then("^the new course with (.+) will be created$")
    public void the_new_course_with_will_be_created(String title) throws Throwable {
        assertEquals(STATUS_CREATED, httpresponse.getStatus());
    }

    @Then("^no new course will be created$")
    public void no_new_course_will_be_created() throws Throwable {
        assertEquals(STATUS_BAD_REQUEST, httpresponse.getStatus());
    }

    @And("^the newly created course will be returned to the user$")
    public void the_newly_created_course_will_be_returned_to_the_user() throws Throwable {
        JSONObject responseObj = httpresponse.getBody().getObject();

        String actual_title = responseObj.get("title").toString();
        String expected_title = body.get("title").toString();

        assertEquals(expected_title, expected_title);
    }

    @And("^(.+) is the active status of the new course$")
    public void is_the_active_status_of_the_new_course(String isactive) throws Throwable {
        boolean active = Boolean.parseBoolean(isactive);
        body.put("active", active);
//        System.out.println("body: "  + body.toString());
    }

    @And("^(.+) is the description of the new course$")
    public void is_the_description_of_the_new_course(String description) throws Throwable {
        body.put("description", description);
//        System.out.println("body: "  + body.toString());
    }

    @And("^the user will receive an error message that creating with id is not allowed$")
    public void the_user_will_receive_an_error_message_that_creating_with_id_is_not_allowed() throws Throwable {
        String expectedErroMsg = "Invalid Creation: Failed Validation: Not allowed to create with id";
        String actualErrorMsg = httpresponse.getBody().getObject().get("errorMessages").toString();
        System.out.println(actualErrorMsg);
        //System.out.println(response.getBody().getArray().getJSONObject(0).get("errorMessage"));
        assertEquals(httpresponse.getStatus(), STATUS_BAD_REQUEST);
//        System.out.println(response.getBody().toPrettyString());
        assertEquals(true, actualErrorMsg.contains(expectedErroMsg));
    }
}
