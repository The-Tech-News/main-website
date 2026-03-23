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
                    <div class="mt-3" style="white-space: pre-wrap;" id="post-content"></div>

                    <!-- Comment section -->
                    <div class="comments-section mt-5" data-postid="<%= post.getId()%>">
                        <h4 class="mb-4">Comments 
                            <span class="badge bg-secondary"><%= comments.size()%></span>
                        </h4>

                        <!-- Comment form -->
                        <div class="card mb-4 shadow-sm">
                            <div class="card-body">
                                <form id="comment-form" action="${pageContext.request.contextPath}/comment?action=create" method="post">
                                    <input type="hidden" name="postid" value="<%= post.getId()%>">
                                    <div class="mb-3">
                                        <!-- Toolbar -->
                                        <div class="btn-group mb-2">
                                            <button type="button" class="btn btn-sm btn-outline-secondary" onclick="formatText('bold')">
                                                <b>B</b>
                                            </button>
                                            <button type="button" class="btn btn-sm btn-outline-secondary" onclick="formatText('italic')">
                                                <i>I</i>
                                            </button>
                                        </div>

                                        <!-- Text area -->
                                        <textarea class="form-control" name="content" id="comment-content" rows="3"
                                                  placeholder="Write a comment..."></textarea>
                                    </div>
                                    <div class="text-end">
                                        <button type="submit" class="btn btn-primary px-4">Post</button>
                                    </div>
                                </form>
                            </div>
                        </div>

                        <!-- Comment list -->
                        <div class="comment-list">
                            <%
                                for (Comment c : comments) {
                                    String raw = c.getContent() == null ? "" : c.getContent();
                                    String escaped = raw.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;");
                                    boolean canDelete = (currentUser != null) ? (currentUser.getId() == c.getUserId() || currentUser.getGroupId() == 1) : false;
                            %>
                            <div class="card mb-3 shadow-sm comment-item" data-id="<%= c.getId()%>">
                                <div class="card-body">
                                    <!-- Header -->
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <div>
                                            <strong><%= users.get(c.getUserId())%></strong>
                                            <span class="text-muted small ms-2">
                                                <%= (c.getCreatedAt() != null ? c.getCreatedAt().toString() : "")%>
                                            </span>
                                        </div>

                                        <% if (canDelete) {%>
                                        <form class="comment-delete-form"
                                              action="${pageContext.request.contextPath}/comment?action=delete&id=<%= c.getId()%>"
                                              method="post"
                                              onsubmit="return confirm('Delete this comment?');">
                                            <button type="submit" class="btn btn-sm btn-outline-danger">Delete</button>
                                        </form>
                                        <% }%>
                                    </div>

                                    <!-- Content -->
                                    <p class="mb-0 comment-body markdown-content"><%= escaped%></p>
                                </div>
                            </div>
                            <% }%>
                        </div>
                    </div>
                </div>
            </div>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
        <%@include file="/WEB-INF/JSPViews/global/htmlScripts.jsp" %>
        <!-- MarkDown -->
        <script type="module">
            import { marked } from '<%= request.getContextPath()%>/lib/marked/lib/marked.esm.js';

            // Ensure the element exists before accessing it
            const postContentElement = document.getElementById('post-content');
            if (postContentElement) {
                // Fetch and sanitize the markdown content
                const markdownContent = `<%= post.getContent().replace("`", "\\`")%>`;

                // Parse and render the markdown content
                postContentElement.innerHTML = marked.parse(markdownContent);
            } else {
                console.error('Element with ID "post-content" not found.');
            }
        </script>
        <script type="module">
            import { marked } from '<%= request.getContextPath()%>/lib/marked/lib/marked.esm.js';

            // Render comments only
            document.querySelectorAll(".markdown-content").forEach(el => {
                const raw = el.textContent;
                el.innerHTML = marked.parse(raw);
            });
        </script>

    </body>
</html>