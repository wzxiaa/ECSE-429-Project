package todomanagercucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@CucumberOptions(features = "classpath:todomanagercucumber/ID005_create_new_course.feature")
public class CreateCourseStepDefinition extends BaseSteps{

    @Before
    public void intializeServer() {
        startServer();
    }

    @After
    public void teardown() {
        stopServer();
    }

    @Given("^the API server is running$")
    public void the_api_server_is_running() throws Throwable {
        startServer();
    }

    @Given("^(.+) is the title of the new course$")
    public void is_the_title_of_the_new_course(String title) throws Throwable {
        throw new PendingException();
    }

    @Given("^(.+) is the id of the new course$")
    public void is_the_id_of_the_new_course(String id) throws Throwable {
        throw new PendingException();
    }

    @When("^the user post the request$")
    public void the_user_post_the_request() throws Throwable {
        throw new PendingException();
    }

    @Then("^the new course with (.+) will be created$")
    public void the_new_course_with_will_be_created(String title) throws Throwable {
        throw new PendingException();
    }

    @Then("^no new course will be created$")
    public void no_new_course_will_be_created() throws Throwable {
        throw new PendingException();
    }

    @And("^the newly created course will be returned to the user$")
    public void the_newly_created_course_will_be_returned_to_the_user() throws Throwable {
        throw new PendingException();
    }

    @And("^(.+) is the active status of the new course$")
    public void is_the_active_status_of_the_new_course(String isactive) throws Throwable {
        throw new PendingException();
    }

    @And("^(.+) is the description of the new course$")
    public void is_the_description_of_the_new_course(String description) throws Throwable {
        throw new PendingException();
    }

    @And("^the user will receive an error message that creating with id is not allowed$")
    public void the_user_will_receive_an_error_message_that_creating_with_id_is_not_allowed() throws Throwable {
        throw new PendingException();
    }
}
