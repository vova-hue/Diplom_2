import client.REST;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.Credentials;
import ru.yandex.praktikum.User;

import static org.hamcrest.CoreMatchers.equalTo;

public class LoginUserTest {
    static Faker faker = new Faker();
    private final String name = faker.name().firstName();
    private final String password = faker.name().lastName();
    private final String email = name + "@" + password + ".ru";
    private final String badEmail = "bad" + email;
    private final String badPassword = password + "bad";
    User user = new User(email, password, name);
    User badEmailUser = new User(badEmail, password, name);
    User badPasswordUser = new User(email, badPassword, name);
    REST client = new REST(BASE_URI);


    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site/";

    @Before
    public void before() {
        ValidatableResponse response = client.create(user);
        Assume.assumeTrue(response.extract().statusCode() == 200);
    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    public void loginUserTest() {
        Credentials credentials = Credentials.fromUser(user);
        ValidatableResponse response = client.loginUser(credentials);
        loginUserResponse(response, 200, null, true);
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным email")
    public void loginUserBadEmailTest() {
        Credentials credentials = Credentials.fromUser(badEmailUser);
        ValidatableResponse response = client.loginUser(credentials);
        loginUserResponse(response, 401, "email or password are incorrect", false);
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным password")
    public void loginUserBadPasswordTest() {
        Credentials credentials = Credentials.fromUser(badPasswordUser);
        ValidatableResponse response = client.loginUser(credentials);
        loginUserResponse(response, 401, "email or password are incorrect", false);
    }

    @Step("Ответ на запрос /api/auth/login")
    public void loginUserResponse(ValidatableResponse response, int code, String message, boolean success) {
        response.assertThat()
                .statusCode(code)
                .body("message", equalTo(message))
                .body("success", equalTo(success));
    }
}
