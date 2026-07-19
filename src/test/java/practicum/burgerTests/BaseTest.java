package practicum.burgerTests;

import io.restassured.RestAssured;
import org.junit.Before;

public class BaseTest {
    @Before
    public void serUo(){
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/";
    }
}
