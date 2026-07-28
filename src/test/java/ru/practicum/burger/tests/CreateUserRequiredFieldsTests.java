package ru.practicum.burger.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.practicum.pojo.CreateUser;
import ru.practicum.steps.UserSteps;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.apache.http.HttpStatus.*;

@RunWith(Parameterized.class)
public class CreateUserRequiredFieldsTests extends BaseTest{
    private final UserSteps userSteps = new UserSteps();
    private String tokenForDelete;

    private final String email;
    private final String password;
    private final String name;

    public CreateUserRequiredFieldsTests(String email, String password, String name){
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters(name = "email={0}, password={1}, name={2}") // добавили аннотацию
    public static Object[][] getUserData() {
        return new Object[][] {
                {null, "12345", "Stitch"},
                {"stitchtest@yandex.ru", null, "Stitch"},
                {"stitchtest@yandex.ru", "12345", null}
        };
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля")
    @Description("Проверка ошибки при создание пользователя без одного из обязательных полей")
    public void checkCreateUserWithoutRequiredField(){
        CreateUser user = new CreateUser(email, password, name);
        Response response = userSteps.createUser(user);
        tokenForDelete = response.path("accessToken");
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {
        if (tokenForDelete != null) {
            userSteps.deleteUser(tokenForDelete);
        }
    }
}
