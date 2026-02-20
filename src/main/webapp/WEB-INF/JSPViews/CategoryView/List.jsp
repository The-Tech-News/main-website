<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="Models.Objects.Category"%>
<%@page import="Models.Objects.Category"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Admin Category - List</title>
<%@include file="/WEB-INF/JSPViews/global/htmlHead.jsp" %>
    </head>
    <body>
<%@include file="/WEB-INF/JSPViews/global/pageHeader.jsp" %>
        <div class="col-lg-8 mx-auto">
            <div class="my-3 p-3 bg-body rounded shadow-sm">
                <h6 class="border-bottom pb-2 mb-0">All categories</h6>
                <%
                    List<Category> cat = (List<Category>) request.getAttribute("CategoryList");
                    for (Category c : cat) {
                %>
                <div class="d-flex text-body-secondary pt-3">
                    <div class="pb-3 mb-0 small lh-sm border-bottom w-100">
                        <div class="d-flex justify-content-between">
                            <strong class="text-gray-dark">
                                <code><%= c.getName() %></code>
                            </strong>
                            <a href="<%= request.getContextPath()%>/admin/category?action=edit&oldName=<%= c.getName() %>">Edit <%= c.getName() %></a>
                        </div>
                        <span class="d-block"><%= c.getDescription() %></span>
                    </div>
                </div>
                <%
                    }
                %>
                <small class="d-block text-end mt-3">
                    <a href="<%= request.getContextPath()%>/admin/category?action=create">Add new category</a>
                </small>
            </div>
        </div>
<%@include file="/WEB-INF/JSPViews/global/htmlScripts.jsp" %>
    </body>
</html>
