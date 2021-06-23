package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = getLogger(InMemoryMealRepository.class);
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, SecurityUtil.authUserId()));
    }

    @Override
    public synchronized Meal save(Meal meal, Integer authUserid) {
        log.info("save {}", meal);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setAuthUserId(authUserid);
            repository.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        if (!meal.getAuthUserId().equals(authUserid)) return null;
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, Integer authUserId) {
        log.info("delete {}", id);
        if (!repository.get(id).getAuthUserId().equals(authUserId)) return false;
        return repository.remove(id) != null;
    }

    @Override
    public Meal get(int id, Integer authUserId) {
        log.info("get {}", id);
        Meal meal = repository.get(id);
        return meal.getAuthUserId().equals(authUserId) ? meal : null;
    }

    @Override
    public List<Meal> getAll(Integer authUserId) {
        log.info("getAll");
        return repository.values()
                .stream()
                .filter(meal -> meal.getAuthUserId().equals(authUserId))
                .sorted((m1, m2) -> m2.getDate().compareTo(m1.getDate()))
                .collect(Collectors.toList());
    }
}

