package todomanagercucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddPriorityToTask extends BaseStepDefinition {

    @Given("the todo with name {string}, done status {string} and description {string} is registered in the system")
    public void the_todo_with_name_done_status_and_description_is_registered_in_the_system(String todotitle, String tododonestatus, String tododescription) {
        createTodo(todotitle, tododonestatus, tododescription);
    }

    @When("user requests to categorize todo with title {string} as {string} priority")
    public void when_user_requests_to_categorize_todo_with_title_as_priority(String todotitle, String prioritytoassign) {
        categorizeTaskWithTitleAsPriority(todotitle, prioritytoassign);
    }

    @Then("^the \"([^\"]*)\" should be classified as a \"([^\"]*)\" priority task$")
    public void the_should_be_classified_as_a_priority_task(String todotitle, String prioritytoassign) {
        int category_id = findIdFromTodoCategoryName(prioritytoassign.replace("\"", ""), todotitle.replace("\"", ""));
        assertTrue(category_id != -1);
    }

    @And("^the todo (.+) is assigned as a (.+) priority task$")
    public void the_todo_is_assigned_as_a_priority_task(String todotitle, String originalpriority) {
        when_user_requests_to_categorize_todo_with_title_as_priority(todotitle, originalpriority);
    }

    @When("^user requests to remove (.+) priority categorization from (.+)$")
    public void user_requests_to_remove_priority_categorization_from(String oldpriority, String todotitle) {
        removePriorityCcategorization(oldpriority, todotitle);
    }

    @Then("^the system should output an error message$")
    public void the_system_should_output_an_error_message() {
        assertEquals(errorMessage, "Could not find parent thing for relationship todos/-1/categories");
    }
}
