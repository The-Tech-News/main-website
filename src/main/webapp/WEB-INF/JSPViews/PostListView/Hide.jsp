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
            <form method="POST" action="<%= request.getContextPath()%>/admin/posts?action=hide">
                <div class="mb-3">
                    <label for="id" class="form-label">Post ID</label>
                    <input type="text" id="id" name="id" class="form-control" value="<%= request.getAttribute("id")%>" readonly>
                </div>
                <div class="mb-3">
                    <button type="submit" class="btn btn-primary">Submit</button>
                </div>
            </form>
        </div>
<%@include file="/WEB-INF/JSPViews/global/htmlScripts.jsp" %>
    </body>
</html>
