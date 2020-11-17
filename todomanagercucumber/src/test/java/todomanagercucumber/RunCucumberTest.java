package todomanagercucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"}, features = "/Users/wenzongxia/Documents/GitHub/ECSE429/ECSE-429-Project/todomanagercucumber/src/test/resources/todomanagercucumber/ID004_remove_task_from_course.feature") // features="/Users/wenzongxia/Documents/GitHub/ECSE429/ECSE-429-Project/todomanagercucumber/src/test/resources/todomanagercucumber/ID008_query_all_imcomplete_high_priority_tasks.feature
public class RunCucumberTest {

}
