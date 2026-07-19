package practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import practicum.pojo.CreateOrder;

import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step
    public Response createOrder(CreateOrder order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step
    public Response createOrderWithAuth(CreateOrder order, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post("/api/orders");
    }
}
