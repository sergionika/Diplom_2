package ru.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.practicum.pojo.CreateUser;
import ru.practicum.pojo.LoginUser;

import static io.restassured.RestAssured.given;
import static ru.practicum.endpoints.*;

public class UserSteps {

    @Step("Создание пользователя")
    public Response createUser(CreateUser user){
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(pointCreateUser);
    }

    @Step("Удалить пользователя")
    public void deleteUser(String deleteToken){
        given()
                .header("Authorization", deleteToken)
                .when()
                .delete(pointDeleteUser);
    }

    @Step("Авторизация пользователя")
    public Response authUser(LoginUser authUser){
        return given()
                .header("Content-type", "application/json")
                .body(authUser)
                .when()
                .post(pointAuthUser);
    }
}
