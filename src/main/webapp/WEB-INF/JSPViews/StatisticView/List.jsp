<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>

<!DOCTYPE html>
<html lang="en" data-bs-theme="auto">
    <head>
        <title>Statistics</title>
        <%@include file="/WEB-INF/JSPViews/global/htmlHead.jsp" %>
    </head>
    <body>
        <%@include file="/WEB-INF/JSPViews/global/pageHeader.jsp" %>

        <main class="container py-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h1 class="h4 mb-0">Statistics</h1>

                <form class="d-flex gap-2" method="get" action="<%= request.getContextPath()%>/admin/stat">
                    <input type="hidden" name="action" value="list"/>
                    <input class="form-control form-control-sm" style="max-width:120px"
                           type="number" min="1" name="top" placeholder="top"
                           value="<%= (request.getAttribute("top") == null ? "" : request.getAttribute("top"))%>"/>
                    <button class="btn btn-sm btn-primary" type="submit">Apply</button>
                    <a class="btn btn-sm btn-outline-secondary"
                       href="<%= request.getContextPath()%>/admin/stat?action=list">All</a>
                </form>
            </div>

            <%
                ArrayList<int[]> stats = (ArrayList<int[]>) request.getAttribute("stats");
                if (stats == null || stats.isEmpty()) {
            %>
            <div class="alert alert-info">No stats yet.</div>
            <%
            } else {
            %>
            <div class="card shadow-sm">
                <div class="card-body">
                    <table class="table table-sm table-striped mb-0">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Post ID</th>
                                <th>Count</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                int i = 1;
                                for (int[] row : stats) {
                                    int postId = row[0];
                                    int count = row[1];
                            %>
                            <tr>
                                <td><%= i++%></td>
                                <td>
                                    <a href="<%= request.getContextPath()%>/post?id=<%= postId%>"><%= postId%></a>
                                </td>
                                <td><%= count%></td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
            <%
                }
            %>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
        <%@include file="/WEB-INF/JSPViews/global/htmlScripts.jsp" %>
    </body>
</html>