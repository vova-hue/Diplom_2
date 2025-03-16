import client.REST;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import ru.yandex.praktikum.User;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest {
    static Faker faker = new Faker();
    private final String name = faker.name().firstName();
    private final String password = faker.name().lastName();
    private final String email = name + "@" + password + ".ru";
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site/";

    User user = new User(email, password, name);
    User userWithoutName = new User(email, password);
    User userWithoutPassword = new User(email, null, name);
    User userWithoutEmail = new User(null, password, name);

    REST client = new REST(BASE_URI);

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUniqUserTest() {
        ValidatableResponse response = client.create(user);
        createUserResponse(response, 200, null, true);
    }

    @Test
    @DisplayName("Создание двух одинаковых пользователей")
    public void createTwoIdenticalUsersTest() {
        ValidatableResponse firstResponse = client.create(user);
        firstResponse.assertThat().statusCode(200);
        ValidatableResponse secondResponse = client.create(user);
        createUserResponse(secondResponse, 403, "User already exists", false);
    }

    @Test
    @DisplayName("Создание пользователя без name")
    public void createUserWithoutNameTest() {
        ValidatableResponse response = client.create(userWithoutName);
        createUserResponse(response, 403, "Email, password and name are required fields", false);
    }

    @Test
    @DisplayName("Создание пользователя без password")
    public void createUserWithoutPasswordTest() {
        ValidatableResponse response = client.create(userWithoutPassword);
        createUserResponse(response, 403, "Email, password and name are required fields", false);
    }

    @Test
    @DisplayName("Создание пользователя без email")
    public void createUserWithoutEmailTest() {
        ValidatableResponse response = client.create(userWithoutEmail);
        createUserResponse(response, 403, "Email, password and name are required fields", false);
    }


    @Step("Ответ на запрос /api/auth/register")
    public void createUserResponse(ValidatableResponse response, int code, String message, boolean success) {
        response.assertThat()
                .statusCode(code)
                .body("message", equalTo(message))
                .body("success", equalTo(success));
    }
}
