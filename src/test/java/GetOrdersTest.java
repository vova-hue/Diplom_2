import client.REST;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.Ingredients;
import ru.yandex.praktikum.Order;
import ru.yandex.praktikum.User;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;

public class GetOrdersTest {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site/";
    static REST client = new REST(BASE_URI);
    static Faker faker = new Faker();
    private final String name = faker.name().firstName();
    private final String password = faker.name().lastName();
    private final String email = name + "@" + password + ".ru";
    User user = new User(email, password, name);
    private String accessToken;
    List<String> ingredients;

    @Before
    public void before() {
        Ingredients responseIngredient = client.getInfoIngredients();
        String firstIngredient = responseIngredient.getData().get(0).get_id();
        String secondIngredient = responseIngredient.getData().get(1).get_id();
        ValidatableResponse response = client.create(user);
        Assume.assumeTrue(response.extract().statusCode() == 200);
        accessToken = response.extract().jsonPath().getString("accessToken");
        ingredients = Arrays.asList(firstIngredient, secondIngredient);
        Order order = new Order(ingredients);
        client.createOrderAuthorized(order, accessToken);
    }

    @Test
    @DisplayName("Получение заказов с авторизацией")
    public void getOrderAuthorizedTest() {
        ValidatableResponse response = client.getOrderAuthorized(accessToken);
        getOrderResponse(response, 200, true);
    }

    @Test
    @DisplayName("Получение заказов без авторизацией")
    public void getOrderNonAuthorizedTest() {
        ValidatableResponse response = client.getOrderNonAuthorized();
        getOrderResponse(response, 401, false);
    }

    @After
    public void after() {
        ValidatableResponse response = client.deleteUser(accessToken);
        Assume.assumeTrue(response.extract().statusCode() == 202);
    }

    @Step("Ответ на запрос /api/orders")
    public void getOrderResponse(ValidatableResponse response, int code, Boolean success) {
        response.assertThat().statusCode(code);
        if (success) {
            response.assertThat().body("success", equalTo(success));
            response.assertThat().body("orders", Matchers.not(Matchers.nullValue()));
            response.assertThat().body("total", Matchers.not(Matchers.nullValue()));
            response.assertThat().body("totalToday", Matchers.not(Matchers.nullValue()));
        } else {
            response.assertThat().body("message", equalTo("You should be authorised"));
        }
    }
}
