<%@page import="Models.Objects.User"%>
<% User u = (User) request.getSession().getAttribute("loggedUser");%>
<div class="container">
    <header class="border-bottom lh-1 py-3">
        <div class="row flex-nowrap justify-content-between align-items-center">
            <div class="col-4 pt-1">
                <a class="link-secondary" target="_blank" href="https://github.com/The-Tech-News/main-website">View on GitHub</a>
            </div>
            <div class="col-4 text-center">
                <a class="blog-header-logo text-body-emphasis text-decoration-none" href="<%= request.getContextPath()%>/">The Tech News</a>
            </div>
            <div class="col-4 d-flex justify-content-end align-items-center">
                <% if (u == null) {%>
                <a href="<%= request.getContextPath()%>/auth?action=signin" class="btn btn-sm btn-outline-secondary">Login</a>
                <a href="<%= request.getContextPath()%>/auth?action=signup" class="btn btn-sm btn-outline-secondary">Sign up</a>
                <% } else {%>
                <a href="<%= request.getContextPath()%>/auth?action=logout" class="btn btn-sm btn-outline-secondary">Logout</a>
                <% } %>
            </div>
        </div>
    </header>
    <% if (u != null) {%>
    <div class="nav-scroller py-1 mb-3 border-bottom">
        <nav class="nav nav-underline">
            <!-- Admin Panel -->
            <a class="nav-item nav-link link-body-emphasis" href="#">Welcome, <%= u.getName()%></a>
            <% if (u.getGroupId() == 1) {%>
            <a class="nav-item nav-link link-body-emphasis" href="<%= request.getContextPath()%>/admin/category">Category Editing</a>
            <a class="nav-item nav-link link-body-emphasis" href="<%= request.getContextPath()%>/admin/stat">View Stat</a>
            <% }%>
            <a class="nav-item nav-link link-body-emphasis" href="<%= request.getContextPath()%>/admin/posts">Post Editing</a>
        </nav>
    </div>
    <% }%>
</div>