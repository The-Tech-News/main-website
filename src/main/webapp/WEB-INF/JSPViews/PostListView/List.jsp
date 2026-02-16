<%@page import="java.util.ArrayList"%>
<%@page import="Models.Objects.Post"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
            <a href="<%= request.getContextPath() %>/admin/posts?action=create" class="btn btn-primary">Create new post</a>
            <div class="album py-5">
                <div class="container">
                    <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3">
                        <% 
                            ArrayList<Post> posts = (ArrayList<Post>) request.getAttribute("posts");
                            for (Post p : posts) {
                        %>
                        <div class="col">
                            <div class="card shadow-sm">
                                <div class="card-body">
                                    <p class="card-text"><%= p.getTitle() %></p>
                                    <div class="d-flex justify-content-between align-items-center">
                                        <div class="btn-group">
                                            <a href="<%= request.getContextPath() %>/admin/posts?action=edit&id=<%= p.getId() %>" class="btn btn-sm btn-outline-secondary">Edit</a>
                                            <a href="<%= request.getContextPath() %>/admin/posts?action=delete&id=<%= p.getId() %>" class="btn btn-sm btn-outline-secondary">Delete</a>
                                        </div>
                                        <small class="text-body-secondary">By user Id <%= p.getUserId() %></small>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <%
                            }
                        %>
                    </div>
                </div>
            </div>
        </div>
<%@include file="/WEB-INF/JSPViews/global/htmlScripts.jsp" %>
    </body>
</html>
