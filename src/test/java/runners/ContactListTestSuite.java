package runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@SuppressWarnings({ "deprecation", "removal" })
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "stepdefinitions",
        tags = "not (@ignore or @bug or @known_bug)",
        plugin = {"pretty"}
)
public class ContactListTestSuite {
}
