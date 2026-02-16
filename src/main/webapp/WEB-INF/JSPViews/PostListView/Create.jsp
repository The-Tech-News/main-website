<%@page import="java.util.ArrayList"%>
<%@page import="Models.Objects.Category"%>
<%@page import="Models.Objects.User"%>
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
            <h2>Create new post</h2>
            <form method="POST" action="<%= request.getContextPath()%>/admin/posts?action=create">
                <div class="mb-3">
                    <label for="title" class="form-label">Post title</label>
                    <input type="text" id="title" name="title" class="form-control">
                </div>
                <div class="mb-3">
                    <label for="content" class="form-label">Content</label>
                    <textarea class="form-control" id="content" name="content" rows="3"></textarea>
                </div>
                <div class="mb-3">
                    <label for="userId" class="form-label">User ID</label>
                    <input type="text" id="userId" name="userId" class="form-control" value="<%= request.getAttribute("userId") %>" readonly>
                </div>
                <div class="mb-3">
                    <label for="category" class="form-label">Category</label>
                    <select class="form-select" id="categoryId" name="categoryId">
                        <option selected>Choose...</option>
                        <%
                            ArrayList<Category> categories = (ArrayList<Category>) request.getAttribute("categories");
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
