package todomanagercucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

import static org.junit.Assert.*;

public class ChangeTaskDescription extends BaseStepDefinition {

    @Given("^(.*) is the title of a todo registered on the system$")
    public void selected_Title_Is_The_Title_Of_A_Todo_Registered_On_The_System(String selectedTitle) {
        assertNotNull(findTodoByName(selectedTitle));
    }

    @Given("^(.*) is not a title of a todo registered on the system$")
    public void selected_Title_Is_Not_The_Title_Of_A_Todo_Registered_On_The_System(String selectedTitle) {
        assertNull(findTodoByName(selectedTitle));
    }

    @When("^the user requests to set the description of the todo with title \"([^\"]*)\" to \"([^\"]*)\"$")
    public void the_User_Requests_To_Set_The_Description_Of_The_Todo_With_Title_Selected_Title_To_New_Description(
            String selectedTitle, String newDescription) {
        originalTodoList = Unirest.get("/todos").asJson().getBody().getObject();
        JSONObject todo = findTodoByName(selectedTitle);
        if (todo == null) {
            response = Unirest.post("/todos/-1").asJson().getBody().getObject();
            return;
        }
        originalValue = new JSONObject(todo.toString());
        int id = todo.getInt("id");
        todo.remove("id");
        todo.put("description", newDescription);
        todo.put("doneStatus", todo.getString("doneStatus").equalsIgnoreCase("true"));
        response = Unirest.put("/todos/" + id).body(todo).asJson().getBody().getObject();
    }

    @Then("^the description of the todo with title \"([^\"]*)\" will be changed to \"([^\"]*)\"$")
    public void the_Description_Of_The_Todo_Will_Be_Changed_To_New_Description(String selectedTitle, String newDescription) {
        assertEquals(findTodoByName(selectedTitle).getString("description"), newDescription);
    }

    @And("^the user will be given the updated version of the todo where the description is (.*)$")
    public void the_User_Will_Be_Given_The_Updated_Version_Of_The_Todo_Where_The_Description_Is_New_Description(String newDescription) {
        assertEquals(response.getString("description"), newDescription);
    }

    @When("^the user requests to remove the description of the todo with title (.*)$")
    public void the_User_Requests_To_Remove_The_Description_Of_The_Todo_With_Title_Selected_Title(String selectedTitle) {
        the_User_Requests_To_Set_The_Description_Of_The_Todo_With_Title_Selected_Title_To_New_Description(selectedTitle, null);
    }

    @Then("^the description of the todo with title (.*) will be removed$")
    public void the_Description_Of_The_Todo_Will_Be_Removed(String selectedTitle) {
        assertEquals(findTodoByName(selectedTitle).getString("description"), "");
    }

    @And("the user will be given the update version of the todo with an empty description")
    public void the_User_Will_Be_Given_The_Update_Version_Of_The_Todo_With_An_Empty_Description() {
        assertEquals(response.getString("description"), "");
    }

    @When("^the user requests to change the description of the todo with title (.*)$")
    public void the_User_Requests_To_Change_The_Description_Of_The_Todo_With_Title_Selected_Title(String selectedTitle) {
        the_User_Requests_To_Set_The_Description_Of_The_Todo_With_Title_Selected_Title_To_New_Description(selectedTitle, "Default, whatever I don't care"
        );
    }

    @Then("^no todo on the system will be modified$")
    public void no_Todo_On_The_System_Will_Be_Modified() {
        assertEquals(originalTodoList, Unirest.get("/todos").asJson().getBody().getObject());
    }

    @And("the user will receive an error message that the specified todo does not exist")
    public void the_User_Will_Receive_An_Error_Message_That_The_Specified_Todo_Does_Not_Exist() {
        assertEquals(response.getJSONArray("errorMessages").get(0),
                "No such todo entity instance with GUID or ID -1 found");
    }
}
