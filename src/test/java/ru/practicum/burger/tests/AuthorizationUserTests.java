package ru.practicum.burger.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.pojo.CreateUser;
import ru.practicum.pojo.LoginUser;
import ru.practicum.steps.UserSteps;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.apache.http.HttpStatus.*;

public class AuthorizationUserTests extends BaseTest{
    private CreateUser user;
    private LoginUser loginUser;
    private final UserSteps userSteps = new UserSteps();
    private String tokenForDelete;
    private String emailForUser = "user" + System.currentTimeMillis() + "@yandex.ru";

    @Before
    public void setUp(){
        user = new CreateUser(emailForUser, "123456", "Stitch");
        loginUser = new LoginUser(user.getEmail(), user.getPassword());
        Response firstResponse  = userSteps.createUser(user);
        tokenForDelete = firstResponse.path("accessToken");
    }

    @After
    public void tearDown(){
        if(tokenForDelete != null){
            userSteps.deleteUser(tokenForDelete);
        }
    }

    @Test
    @DisplayName("Вход под существующим пользователем")
    @Description("Проверка авторизауции с данными существующего пользователя")
    public void checkAuthCorrectUser(){
        Response response = userSteps.authUser(loginUser);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Авторизация с неккоректным логином")
    @Description("Проверка авторизации с невалидным полем email")
    public void checkAuthWithNotCorrectUserEmail(){
        loginUser.setEmail("notCorrecteEmail@yandex.cooooom");
        Response response = userSteps.authUser(loginUser);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация с неккоректным паролем")
    @Description("Проверка авторизации с невалидным полем password")
    public void checkAuthWithNotCorrectUserPassword(){
        loginUser.setPassword("неправильный_пароль");
        Response response = userSteps.authUser(loginUser);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
