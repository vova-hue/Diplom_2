package client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.Credentials;
import ru.yandex.praktikum.Ingredients;
import ru.yandex.praktikum.Order;
import ru.yandex.praktikum.User;

import static io.restassured.RestAssured.given;

public class REST {

    private String baseURI;

    public REST(String baseURI) {
        this.baseURI = baseURI;
    }

    @Step("Создание пользователя")
    public ValidatableResponse create(User user) {
return given()
        .filter(new AllureRestAssured())
        .log()
        .all()
        .baseUri(baseURI)
        .header("Content-Type", "application/json")
        .body(user)
        .post("/api/auth/register")
        .then()
        .log()
        .all();
    }

    @Step("Авторизация пользователя в системе")
    public ValidatableResponse loginUser(Credentials credentials) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-Type", "application/json")
                .body(credentials)
                .post("/api/auth/login")
                .then()
                .log()
                .all();
    }

    @Step("Изменение данных авторизованного пользователя")
    public ValidatableResponse changeAuthorizedUser(String accessToken,Credentials credentials) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body(credentials)
                .patch("/api/auth/user")
                .then()
                .log()
                .all();
    }

    @Step("Изменение данных неавторизованного пользователя")
    public ValidatableResponse changeNonAuthorizedUser(Credentials credentials) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-Type", "application/json")
                .body(credentials)
                .patch("/api/auth/user")
                .then()
                .log()
                .all();
    }

    @Step("Получение данных об ингридиентах")
    public Ingredients getInfoIngredients() {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .get("/api/ingredients")
                .then()
                .log()
                .all()
                .extract()
                .body()
                .as(Ingredients.class);

    }

    @Step("Создание заказа без авторизации")
    public ValidatableResponse createOrderNonAuthorized(Order order) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-Type", "application/json")
                .body(order)
                .post("/api/orders")
                .then()
                .log()
                .all();
    }

    @Step("Создание заказа с авторизацией")
    public ValidatableResponse createOrderAuthorized(Order order, String accessToken) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-Type", "application/json")
                .header("authorization",accessToken)
                .body(order)
                .post("/api/orders")
                .then()
                .log()
                .all();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .header("authorization",accessToken)
                .delete("/api/auth/user")
                .then()
                .log()
                .all();
    }

    @Step("Получение заказов пользователя без авторизации")
    public ValidatableResponse getOrderNonAuthorized() {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .get("/api/orders")
                .then()
                .log()
                .all();
    }

    @Step("Получение заказов пользователя с авторизацией")
    public ValidatableResponse getOrderAuthorized(String accessToken) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseURI)
                .header("authorization",accessToken)
                .get("/api/orders")
                .then()
                .log()
                .all();
    }
}
