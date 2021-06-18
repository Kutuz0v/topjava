<%@ page import="ru.javawebinar.topjava.util.TimeUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Meals</title>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

    <link href="./style.css" rel="stylesheet" type="text/css">
</head>
<body>
<h2><a href="index.html">Home</a></h2>
<h3>Meals</h3>
<a href="meals?action=create">Add meal</a>
<hr>
<table border="1" cellpadding="8" cellspacing="0">
    <thead>
        <tr>
        <th>Дата/Время</th>
        <th>Описание</th>
        <th>Калории</th>
        <th></th>
        <th></th>
    </tr>
    </thead>
        <c:forEach var="meal" items="${mealList}">
            <jsp:useBean id="meal" scope="page" type="ru.javawebinar.topjava.model.MealTo"/>
            <tr class="${meal.excess ? 'red' : 'green'}">
                <td><%=TimeUtil.toString(meal.getDateTime())%></td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
                <td><a href="meals?action=update&id=${meal.id}">Update</a></td>
                <td><a href="meals?action=delete&id=${meal.id}">Delete</a></td>
            </tr>
        </c:forEach>

    </table>
</body>
</html>

