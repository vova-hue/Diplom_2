package ru.yandex.praktikum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Credentials {
    private String email;
    private String password;
    private String name;

    public static Credentials fromUser(User user) {
        return new Credentials(user.getEmail(), user.getPassword(), null);
    }

    public static Credentials nameUser(User user) {
        return new Credentials(null, null, user.getName());
    }
}
