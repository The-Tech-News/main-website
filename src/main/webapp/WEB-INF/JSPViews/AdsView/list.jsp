<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Ads List</title>
</head>
<body>

<h2>Danh sách quảng cáo</h2>

<a href="ads?action=create">Tạo quảng cáo</a>

<table border="1">
    <tr>
        <th>ID</th>
        <th>PostID</th>
        <th>Title</th>
        <th>Image</th>
        <th>Action</th>
    </tr>

    <c:forEach var="ads" items="${adsList}">
        <tr>
            <td>${ads.id}</td>
            <td>${ads.postId}</td>
            <td>${ads.title}</td>
            <td>
                <img src="${ads.uriImage}" width="120">
            </td>
            <td>
                <a href="ads?action=edit&id=${ads.id}">Edit</a>
                |
                <a href="ads?action=delete&id=${ads.id}">Delete</a>
            </td>
        </tr>
    </c:forEach>

</table>

</body>
</html>