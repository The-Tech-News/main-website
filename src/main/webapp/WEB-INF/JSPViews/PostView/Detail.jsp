<%@page import="java.util.HashMap"%>
<%@page import="java.lang.Object"%>
<%@page import="java.util.ArrayList"%>
<%@page import="Models.Objects.Category"%>
<%@page import="Models.Objects.Comment"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="Models.Objects.Post, Models.Objects.User, java.util.List"%>

<!DOCTYPE html>
<html lang="en" data-bs-theme="auto">
    <head>
        <title>The Tech News - Post</title>
        <%@include file="/WEB-INF/JSPViews/global/htmlHead.jsp" %>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/comment.css">
        <script src="${pageContext.request.contextPath}/js/global/comment.js" defer></script>
    </head>

    <body>
        <%@include file="/WEB-INF/JSPViews/global/pageHeader.jsp" %>

        <main class="container">
            <%
                Category category = (Category) request.getAttribute("category");
                HashMap<Integer, String> users = (HashMap<Integer, String>) request.getAttribute("users");
                Post post = (Post) request.getAttribute("post");
                ArrayList<Comment> comments = (ArrayList<Comment>) request.getAttribute("comment");
                User currentUser = (User) session.getAttribute("loggedUser");
            %>

            <nav aria-label="breadcrumb" class="mb-3">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item">
                        <a href="<%= request.getContextPath()%>/post">Home</a>
                    </li>
                    <li class="breadcrumb-item active" aria-current="page">Post</li>
                </ol>
            </nav>

            <div class="card shadow-sm">
                <div class="card-body">
                    <h1 class="h4 mb-2"><%= post.getTitle()%></h1>
                    <div class="text-muted small mb-3">
                        Author: <%= users.get(post.getUserId())%>
                        <br>
                        Category: <%= category.getName()%>
                        <% if (post.isHidden()) { %>
                        · <span class="badge text-bg-warning">Hidden</span>
                        <% }%>
                    </div>
                    <div class="mt-3" style="white-space: pre-wrap;">
                        <%= post.getContent()%>
                    </div>

                    <!-- Comment section -->
                    <div class="comments-section mt-4" data-postid="<%= post.getId()%>">
                        <h3 class="comments-title">Comments (<span class="comments-count"><%= comments.size()%></span>)</h3>

                        <div class="comment-form-wrap">
                            <form id="comment-form" action="${pageContext.request.contextPath}/comment?action=create" method="post">
                                <input type="hidden" name="postid" value="<%= post.getId()%>">
                                <textarea name="content" id="comment-content" rows="3" placeholder="Write a comment..."></textarea>
                                <div class="comment-form-actions">
                                    <button type="submit" class="btn btn-primary">Post Comment</button>
                                </div>
                            </form>
                        </div>

                        <ul class="comment-list">
                            <%
                                for (Comment c : comments) {
                                    String raw = c.getContent() == null ? "" : c.getContent();
                                    String escaped = raw.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;");
                                    boolean canDelete = false;
                                    if (currentUser != null) {
                                        canDelete = (currentUser.getId() == c.getUserId()) || (currentUser.getGroupId() == 1);
                                    }
                            %>
                            <li class="comment-item" data-id="<%= c.getId()%>">
                                <div class="comment-meta">
                                    <span class="comment-author"><%= users.get(c.getUserId())%></span>
                                    <span class="comment-date"><%= (c.getCreatedAt() != null ? c.getCreatedAt().toString() : "")%></span>
                                </div>
                                <div class="comment-body"><%= escaped%></div>
                                <div class="comment-actions">
                                    <% if (canDelete) {%>
                                    <form class="comment-delete-form" action="${pageContext.request.contextPath}/comment?action=delete" method="post" onsubmit="return confirm('Delete this comment?');">
                                        <input type="hidden" name="id" value="<%= c.getId()%>">
                                        <button type="submit" class="btn btn-link delete-btn">Delete</button>
                                    </form>
                                    <% } %>
                                </div>
                            </li>
                            <% }%>
                        </ul>
                    </div>
                </div>
            </div>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
        <%@include file="/WEB-INF/JSPViews/global/htmlScripts.jsp" %>
    </body>
</html>