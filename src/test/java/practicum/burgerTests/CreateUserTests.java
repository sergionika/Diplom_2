package practicum.burgerTests;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import practicum.pojo.CreateUser;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import practicum.steps.UserSteps;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;


public class CreateUserTests extends BaseTest{
    private CreateUser user;
    private final UserSteps userSteps = new UserSteps();
    private String tokenForDelete;
    private Response response;
    private final String emailForUser = "user" + System.currentTimeMillis() + "@yandex.ru";

    @Before
    public void setUp(){
        user = new CreateUser(emailForUser, "123456", "Stitch");
        response = userSteps.createUser(user);
        tokenForDelete = response.path("accessToken");
    }

    @After
    public void tearDown(){
        if(tokenForDelete != null){
            userSteps.deleteUser(tokenForDelete);
        }
    }

    @Test
    @DisplayName("Создание нового пользователя")
    @Description("Проверка создание пользователя с валидными параметрами")
    public void checkCreateCorrectUser(){
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Повторное создание пользователя")
    @Description("Проверка ошибки при повторном создании пользователя с одинаковыми данными")
    public void checkCreateDuplicateUser(){
        Response dublResponse = userSteps.createUser(user);
        dublResponse.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }
}
