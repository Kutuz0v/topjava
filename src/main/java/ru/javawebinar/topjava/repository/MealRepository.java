package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.Collection;
import java.util.List;

public interface MealRepository {
    // null if updated meal do not belong to userId
    Meal save(Meal meal, Integer authUserId);

    // false if meal do not belong to userId
    boolean delete(int id, Integer authUserId);

    // null if meal do not belong to userId
    Meal get(int id, Integer authUserId);

    // ORDERED dateTime desc
    List<Meal> getAll(Integer authUserId);
}
