package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import static ru.javawebinar.topjava.util.MealsUtil.getTos;
import static ru.javawebinar.topjava.util.MealsUtil.DEFAULT_CALORIES_PER_DAY;
import static ru.javawebinar.topjava.util.ValidationUtil.*;

import java.util.List;

@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public MealTo create(MealTo mealTo, Integer authUserId) {
        Meal meal = mealTo.toMeal();
        checkNew(meal);
        return repository.save(meal, authUserId)
                .toMealTo();
    }

    public void delete(int id, Integer authUserId) {
        checkNotFoundWithId(repository.delete(id, authUserId), id);
    }

    public MealTo get(int id, Integer authUserId) {
        return checkNotFoundWithId(repository.get(id, authUserId), id)
                .toMealTo();
    }

    public List<MealTo> getAll(Integer authUserId) {
        return MealsUtil.getTos(repository.getAll(authUserId), DEFAULT_CALORIES_PER_DAY);
    }

    public void update(MealTo mealTo, Integer authUserId) {
        Meal meal = mealTo.toMeal();
        assureIdConsistent(meal, meal.getId());
        checkNotFoundWithId(repository.save(meal, authUserId), meal.getId());
    }
}