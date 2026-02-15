<%@page import="java.util.ArrayList"%>
<%@page import="Models.Objects.Post"%>
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
        <div class="col-lg-8 mx-auto">
            <h2>Post List</h2>
            <br>
            <button type="button" class="btn btn-primary">Create new post</button>
            <div class="my-3 p-3 bg-body rounded shadow-sm">
                <div class="list-group">
                    <% 
                        ArrayList<Post> posts = (ArrayList<Post>) request.getAttribute("posts");
                        for (Post p : posts) {
                    %>
                    <a href="#" class="list-group-item list-group-item-action">
                        <div class="d-flex w-100 justify-content-between">
                            <h5 class="mb-1"><%= p.getTitle() %></h5>
                        </div>
                        <p class="mb-1">By user: <%= p.getUserId() %></p>
                        <p class="mb-1">Hidden: <%= p.isHidden() %></p>
                        <button type="button" class="btn btn-primary">Edit</button>
                        <button type="button" class="btn btn-danger">Delete</button>
                    </a>
                    <%
                        }
                    %>
                </div>
            </div>
        </div>
<%@include file="/WEB-INF/JSPViews/global/htmlScripts.jsp" %>
    </body>
</html>


