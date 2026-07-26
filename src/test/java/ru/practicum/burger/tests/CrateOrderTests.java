package ru.practicum.burger.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;
import ru.practicum.pojo.CreateOrder;
import ru.practicum.pojo.CreateUser;
import ru.practicum.steps.OrderSteps;
import io.restassured.response.Response;
import ru.practicum.steps.UserSteps;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.apache.http.HttpStatus.*;

public class CrateOrderTests extends BaseTest{
    private final OrderSteps orderSteps = new OrderSteps();
    private final UserSteps userSteps = new UserSteps();
    private String authToken;
    private final String emailForUser = "stitch" + System.currentTimeMillis() + "@yandex.ru";

    @After
    public void tearDown() {
        if (authToken != null) {
            userSteps.deleteUser(authToken);
        }
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка на создание заказа при отсутсвии авторизации")
    public void checkCreateOrderWithoutAuth(){
        CreateOrder order = new CreateOrder(List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa70"));
        Response response = orderSteps.createOrder(order);
        response.then()
                .statusCode(SC_OK)
                .body("name", notNullValue())
                .body("order.number", notNullValue())
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка на создание заказа под авторизованным пользователем")
    public void checkCreateOrderWithAuth(){
        CreateUser createUser = new CreateUser(emailForUser, "123456", "Stitch");
        CreateOrder order = new CreateOrder(List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa70"));
        Response firstResponse = userSteps.createUser(createUser);
        authToken = firstResponse.path("accessToken");
        Response response = orderSteps.createOrderWithAuth(order, authToken);
        response.then()
                .statusCode(SC_OK)
                .body("name", notNullValue())
                .body("order.number", notNullValue())
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа с невалидным ID ингредиента")
    @Description("Проверка ошибки при создание заказа с неккоректным ID ингредиента")
    public void checkCreateOrderWithIncorrectIdIngredient(){
        CreateOrder order = new CreateOrder(List.of("1h2nrkdsm231lmfdj5f9ds57"));
        Response response = orderSteps.createOrder(order);
        response.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка ошибки при создании заказа без добавления ингредиентов")
    public void checkCreateOrderWithoutIngredients(){
        CreateOrder order = new CreateOrder(List.of());
        Response response = orderSteps.createOrder(order);
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }
}
