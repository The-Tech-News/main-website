<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="Models.Objects.Post"%>

<!DOCTYPE html>
<html lang="en" data-bs-theme="auto">
<head>
    <title>The Tech News - Post</title>
    <%@include file="/WEB-INF/JSPViews/global/htmlHead.jsp" %>
</head>

<body>
    <%@include file="/WEB-INF/JSPViews/global/pageHeader.jsp" %>

    <main class="container py-4">
        <%
            Post post = (Post) request.getAttribute("post");
        %>

        <nav aria-label="breadcrumb" class="mb-3">
            <ol class="breadcrumb">
                <li class="breadcrumb-item">
                    <a href="<%= request.getContextPath() %>/post">Home</a>
                </li>
                <li class="breadcrumb-item active" aria-current="page">Post</li>
            </ol>
        </nav>

        <div class="card shadow-sm">
            <div class="card-body">
                <h1 class="h4 mb-2"><%= post.getTitle() %></h1>

                <div class="text-muted small mb-3">
                    AuthorId: <%= post.getUserId() %> · CategoryId: <%= post.getCategoryId() %>
                    <% if (post.isHidden()) { %>
                        · <span class="badge text-bg-warning">Hidden</span>
                    <% } %>
                </div>

                <hr/>

                <div class="mt-3" style="white-space: pre-wrap;">
                    <%= post.getContent() %>
                </div>

                <div class="mt-4">
                    <a class="btn btn-outline-secondary"
                       href="<%= request.getContextPath() %>/post">
                        Back
                    </a>
                </div>
            </div>
        </div>
    </main>

    <%@include file="/WEB-INF/JSPViews/global/htmlScripts.jsp" %>
</body>
</html>