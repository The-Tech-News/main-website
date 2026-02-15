<%@page contentType="text/html" pageEncoding="UTF-8"%>

<h2>Edit Post</h2>

<form action="${pageContext.request.contextPath}/admin/posts?action=edit"
      method="post">

    <input type="hidden" name="id" value="${post.id}">

    Title:
    <input type="text" name="title" value="${post.title}" required><br><br>

    Content:<br>
    <textarea name="content" rows="5" cols="40" required>
${post.content}
    </textarea><br><br>

    <button type="submit">Update</button>
</form>
