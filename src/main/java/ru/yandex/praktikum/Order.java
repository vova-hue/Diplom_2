package ru.yandex.praktikum;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Order {
    private List<String> ingredients;
}
