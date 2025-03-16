package ru.yandex.praktikum;

import lombok.AllArgsConstructor;

import java.util.List;

@lombok.Data
@AllArgsConstructor
public class Ingredients {
    private boolean success;
    private List<Data> data;
}
