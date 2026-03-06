<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List, Models.Objects.Comment, Models.Objects.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Comments</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/comment.css">
        <script src="${pageContext.request.contextPath}/js/global/comment.js" defer></script>
    </head>
    <body>
        <%
            String postIdParam = request.getParameter("postid");
            Object postIdAttr = request.getAttribute("postId");
            String postIdStr = postIdParam != null ? postIdParam : (postIdAttr != null ? String.valueOf(postIdAttr) : "0");
            int postId = 0;
            try { postId = Integer.parseInt(postIdStr); } catch (Exception ignore) {}

            List<Comment> comments = (List<Comment>) request.getAttribute("comments");
            if (comments == null) comments = java.util.Collections.emptyList();

            User currentUser = (User) session.getAttribute("loggedUser");
        %>

        <div class="comments-section" data-postid="<%=postId%>">
            <h3 class="comments-title">Comments (<span class="comments-count"><%=comments.size()%></span>)</h3>

            <div class="comment-form-wrap">
                <form id="comment-form" action="${pageContext.request.contextPath}/comment?action=create" method="post">
                    <input type="hidden" name="postid" value="<%=postId%>">
                    <textarea name="content" id="comment-content" rows="3" placeholder="Write a comment..."></textarea>
                    <div class="comment-form-actions">
                        <button type="submit" class="btn btn-primary">Post Comment</button>
                    </div>
                </form>
            </div>

            <ul class="comment-list">
                <% for (Comment c : comments) { %>
                    <li class="comment-item" data-id="<%=c.getId()%>">
                        <div class="comment-meta">
                            <span class="comment-author">User <%=c.getUserId()%></span>
                            <span class="comment-date"><%= (c.getCreatedAt() != null ? c.getCreatedAt().toString() : "") %></span>
                        </div>
                        <%
                            String raw = c.getContent() == null ? "" : c.getContent();
                            String escaped = raw.replace("&", "&amp;").replace("<", "&lt;")
                                    .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;");
                        %>
                        <div class="comment-body"><%= escaped %></div>
                        <div class="comment-actions">
                            <% boolean canDelete = false;
                               if (currentUser != null) {
                                   canDelete = (currentUser.getId() == c.getUserId()) || (currentUser.getGroupId() == 1);
                               }
                            %>
                            <% if (canDelete) { %>
                                <form class="comment-delete-form" action="${pageContext.request.contextPath}/comment?action=delete" method="post" onsubmit="return confirm('Delete this comment?');">
                                    <input type="hidden" name="id" value="<%=c.getId()%>">
                                    <button type="submit" class="btn btn-link delete-btn">Delete</button>
                                </form>
                            <% } %>
                        </div>
                    </li>
                <% } %>
            </ul>
        </div>
    </body>
</html>
