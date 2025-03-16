import client.REST;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.Ingredients;
import ru.yandex.praktikum.Order;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;

@RunWith(Parameterized.class)
public class CreateOrderNonAuthorizedTest {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site/";
    static REST client = new REST(BASE_URI);

    private final String firstIngredient;
    private final String secondIngredient;
    private final Boolean isSuccess;
    private final int code;
    private final String message;

    List<String> ingredients;

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

    public CreateOrderNonAuthorizedTest(String firstIngredient, String secondIngredient, Boolean isSuccess, int code, String message) {
        this.firstIngredient = firstIngredient;
        this.secondIngredient = secondIngredient;
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void orderCreateNonAuthorizedTest() {
        ingredients = Arrays.asList(firstIngredient, secondIngredient);
        Order order = new Order(ingredients);
        ValidatableResponse response = client.createOrderNonAuthorized(order);
        orderCreateResponse(response, code, isSuccess, message);
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