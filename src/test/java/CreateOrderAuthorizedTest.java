import client.REST;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.Ingredients;
import ru.yandex.praktikum.Order;
import ru.yandex.praktikum.User;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class CreateOrderAuthorizedTest {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site/";
    static REST client = new REST(BASE_URI);

    private final String firstIngredient;
    private final String secondIngredient;
    private final Boolean isSuccess;
    private final int code;
    private final String message;

    List<String> ingredients;
    static Faker faker = new Faker();
    private final String name = faker.name().firstName();
    private final String password = faker.name().lastName();
    private final String email = name + "@" + password + ".ru";
    User user = new User(email, password, name);
    private String accessToken;

    @Parameterized.Parameters
    public static Object[][] data() {
        Ingredients response = client.getInfoIngredients();
        String hashFirstIngredient = response.getData().get(0).get_id();
        String hashSecondIngredient = response.getData().get(1).get_id();
        String badFirstIngredient = hashFirstIngredient + "123";
        String badSecondIngredient = hashSecondIngredient + "123";
        return new Object[][]{
                {hashFirstIngredient, hashSecondIngredient, true, 200, null},
                {badFirstIngredient, badSecondIngredient, null, 500, null},
                {null, null, false, 400, "One or more ids provided are incorrect"},
        };
    }

    public CreateOrderAuthorizedTest(String firstIngredient, String secondIngredient, Boolean isSuccess, int code, String message) {
        this.firstIngredient = firstIngredient;
        this.secondIngredient = secondIngredient;
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

    @Before
    public void before() {
        ValidatableResponse response = client.create(user);
        Assume.assumeTrue(response.extract().statusCode() == 200);
        accessToken = response.extract().jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void orderCreateNonAuthorizedTest() {
        ingredients = Arrays.asList(firstIngredient, secondIngredient);
        Order order = new Order(ingredients);
        ValidatableResponse response = client.createOrderAuthorized(order, accessToken);
        orderCreateResponse(response, code, isSuccess, message);
    }

    @After
    public void after() {
        ValidatableResponse response = client.deleteUser(accessToken);
        Assume.assumeTrue(response.extract().statusCode() == 202);
    }

    @Step("Ответ на запрос /api/orders")
    public void orderCreateResponse(ValidatableResponse response, int code, Boolean success, String message) {
        response.assertThat()
                .statusCode(code);
        if (success != null) {
            if (Boolean.TRUE.equals(success)) {
                response.body("success", equalTo(success));
                response.body("name", notNullValue());
                response.body("name", not(equalTo("")));
            } else {
                response.body("success", equalTo(success));
                response.body("message", equalTo(message));
            }
        }
    }
}
