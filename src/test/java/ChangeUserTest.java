import client.REST;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.Credentials;
import ru.yandex.praktikum.User;

import static org.hamcrest.CoreMatchers.equalTo;

public class ChangeUserTest {
    static Faker faker = new Faker();
    private final String name = faker.name().firstName();
    private final String password = faker.name().lastName();
    private final String email = name + "@" + password + ".ru";
    private final String newName = "new" + name;
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site/";
    User user = new User(email, password, name);
    User newNameUser = new User(email, password, newName);
    REST client = new REST(BASE_URI);
    String accessToken;

    @Before
    public void before() {
        ValidatableResponse response = client.create(user);
        Assume.assumeTrue(response.extract().statusCode() == 200);
        accessToken = response.extract().jsonPath().get("accessToken");
    }

    @Test
    @DisplayName("Изменение авторизованного пользователя")
    public void changeAuthorizedUserTest() {
        Credentials credentials = Credentials.nameUser(newNameUser);
        ValidatableResponse response = client.changeAuthorizedUser(accessToken, credentials);
        changeAuthorizedUserResponse(response, 200, true, newName);
    }

    @Test
    @DisplayName("Изменение неавторизованного пользователя")
    public void changeNonAuthorizedUserTest() {
        Credentials credentials = Credentials.nameUser(newNameUser);
        ValidatableResponse response = client.changeNonAuthorizedUser(credentials);
        changeNonAuthorizedUserResponse(response, 401, false, "You should be authorised");
    }

    @After
    public void after() {
        ValidatableResponse response = client.deleteUser(accessToken);
        Assume.assumeTrue(response.extract().statusCode() == 202);
    }

    @Step("Пользователь успешно изменен")
    public void changeAuthorizedUserResponse(ValidatableResponse response, int code, boolean success, String newName) {
        response.assertThat()
                .statusCode(code)
                .body("success", equalTo(success))
                .body("user.name", equalTo(newName));
    }

    @Step("Изменения пользователя недоступно в неавторизованной зоне")
    public void changeNonAuthorizedUserResponse(ValidatableResponse response, int code, boolean success, String message) {
        response.assertThat()
                .statusCode(code)
                .body("message", equalTo(message))
                .body("success", equalTo(success));
    }
}
