package ru.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.practicum.pojo.CreateOrder;

import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step("Создать заказ авторизованным пользоватлем")
    public Response createOrder(CreateOrder order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Создать заказ без авторизации")
    public Response createOrderWithAuth(CreateOrder order, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post("/api/orders");
    }
}
