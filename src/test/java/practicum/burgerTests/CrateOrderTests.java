package practicum.burgerTests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;
import practicum.pojo.CreateOrder;
import practicum.pojo.CreateUser;
import practicum.steps.OrderSteps;
import io.restassured.response.Response;
import practicum.steps.UserSteps;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

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
                .statusCode(200)
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
                .statusCode(200)
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
                .statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка ошибки при создании заказа без добавления ингредиентов")
    public void checkCreateOrderWithoutIngredients(){
        CreateOrder order = new CreateOrder(List.of());
        Response response = orderSteps.createOrder(order);
        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }
}
