<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
    <head>
        <title>Admin Post - Create</title>
<%@include file="/WEB-INF/JSPViews/global/htmlHead.jsp" %>
    </head>
    <body>
<%@include file="/WEB-INF/JSPViews/global/pageHeader.jsp" %>
        <h2>Post List</h2>
        <a href="${pageContext.request.contextPath}/admin/posts?action=create">
            Create New Post
        </a>
        <table border="1">
            <tr>
                <th>ID</th>
                <th>Title</th>
                <th>User ID</th>
                <th>Action</th>
            </tr>
            <c:forEach var="p" items="${posts}">
                <tr>
                    <td>${p.id}</td>
                    <td>${p.title}</td>
                    <td>${p.userId}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/admin/posts?action=edit&id=${p.id}">
                            Edit
                        </a>

                        <form action="${pageContext.request.contextPath}/admin/posts?action=hide"
                              method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${p.id}">
                            <button type="submit">Hide</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
<%@include file="/WEB-INF/JSPViews/global/htmlScripts.jsp" %>
    </body>
</html>


