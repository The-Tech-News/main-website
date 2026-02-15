<%@page contentType="text/html" pageEncoding="UTF-8"%>

<h2>Create Post</h2>

<form action="${pageContext.request.contextPath}/admin/posts?action=create"
      method="post">

    Category ID:
    <input type="number" name="categoryId" required><br><br>

    Title:
    <input type="text" name="title" required><br><br>

    Content:<br>
    <textarea name="content" rows="5" cols="40" required></textarea><br><br>

    <button type="submit">Create</button>
</form>
