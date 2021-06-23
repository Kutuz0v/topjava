package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;


public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    private static ConfigurableApplicationContext appCtx;
    private final Integer authUserId = SecurityUtil.authUserId();
    private MealRestController controller;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        MealTo mealTo = new MealTo(id.isEmpty() ? null : Integer.valueOf(id),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")),
                Integer.parseInt(request.getParameter("authUserId")),
                false);

        log.info(mealTo.isNew() ? "Create {}" : "Update {}", mealTo);
        if (mealTo.isNew()) controller.create(mealTo);
        else controller.update(mealTo, mealTo.getId());
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                controller.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
                MealTo mealToCreate = new MealTo(null, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                        "",
                        1000,
                        authUserId,
                        false);
                request.setAttribute("meal", mealToCreate);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "update":
                MealTo mealToUpdate = controller.get(getId(request));
                request.setAttribute("meal", mealToUpdate);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
                log.info("getAll");
                request.setAttribute("meals", controller.getAll());
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
            default:
                response.setStatus(400);
                log.info("Bad Request, action={}", action);
                response.sendRedirect("meals");
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

    @Override
    public void init() throws ServletException {
        super.init();
        appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        controller = appCtx.getBean(MealRestController.class);
    }

    @Override
    public void destroy() {
        appCtx.close();
        super.destroy();
    }
}
