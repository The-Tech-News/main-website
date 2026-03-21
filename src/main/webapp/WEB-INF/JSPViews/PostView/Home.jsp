<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="Models.Objects.Post"%>

<!DOCTYPE html>
<html lang="en" data-bs-theme="auto">
    <head>
        <title>The Tech News - Home</title>
        <%@include file="/WEB-INF/JSPViews/global/htmlHead.jsp" %>
    </head>
    <body>
        <%@include file="/WEB-INF/JSPViews/global/pageHeader.jsp" %>

        <main class="container py-4">
            <div class="d-flex align-items-center justify-content-between mb-3">
                <div>
                    <h1 class="h3 mb-1">The Tech News</h1>
                    <p class="text-muted mb-0">Latest posts</p>
                </div>
            </div>

            <%
                ArrayList<Post> posts = (ArrayList<Post>) request.getAttribute("posts");
                if (posts == null || posts.isEmpty()) {
            %>
            <div class="alert alert-info">No posts yet.</div>
            <%
            } else {
                for (Post p : posts) {
            %>
            <div class="card mb-3 shadow-sm">
                <div class="card-body">
                    <h2 class="h5 mb-2"><%= p.getTitle()%></h2>

                    <div class="text-muted small">
                        AuthorId: <%= p.getUserId()%> · CategoryId: <%= p.getCategoryId()%>
                        <% if (p.isHidden()) { %>
                        · <span class="badge text-bg-warning">Hidden</span>
                        <% }%>
                    </div>

                    <div class="mt-3">
                        <a class="btn btn-primary btn-sm"
                           href="<%= request.getContextPath()%>/post?id=<%= p.getId()%>">
                            Read
                        </a>
                    </div>
                </div>
            </div>
            <%
                    }
                }
            %>
        </main>

        <%@include file="/WEB-INF/JSPViews/global/htmlScripts.jsp" %>
    </body>
</html>