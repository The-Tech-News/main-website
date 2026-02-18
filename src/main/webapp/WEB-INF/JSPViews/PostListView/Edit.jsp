<%@page import="Models.Objects.Post"%>
<%@page import="java.util.ArrayList"%>
<%@page import="Models.Objects.Category"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Admin Post - Edit</title>
<%@include file="/WEB-INF/JSPViews/global/htmlHead.jsp" %>
    </head>
    <body>
<%@include file="/WEB-INF/JSPViews/global/pageHeader.jsp" %>
        <div class="col-lg-8 mx-auto">
            <h2>Create new post</h2>
            <% 
                ArrayList<Category> categories = (ArrayList<Category>) request.getAttribute("categories");
                Post post = (Post) request.getAttribute("post");
            %>
            <form method="POST" action="<%= request.getContextPath()%>/admin/posts?action=edit&id=<%= post.getId() %>">
                <div class="mb-3">
                    <label for="title" class="form-label">Post title</label>
                    <input type="text" id="title" name="title" class="form-control" value="<%= post.getTitle() %>">
                </div>
                <div class="mb-3">
                    <label for="content" class="form-label">Content</label>
                    <textarea class="form-control" id="content" name="content" rows="3"><%= post.getContent() %></textarea>
                </div>
                <div class="mb-3">
                    <label for="category" class="form-label">Category</label>
                    <select class="form-select" id="categoryId" name="categoryId">
                        <option selected>Choose...</option>
                        <%
                            for (Category c : categories) {
                        %>
                        <option value="<%= c.getId() %>"><%= c.getName() %></option>
                        <%
                            }
                        %>
                    </select>
                </div>
                <div class="mb-3">
                    <button type="submit" class="btn btn-primary">Submit</button>
                </div>                
            </form>
        </div>
<%@include file="/WEB-INF/JSPViews/global/htmlScripts.jsp" %>
    </body>
</html>
