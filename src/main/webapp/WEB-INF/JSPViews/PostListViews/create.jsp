<%@page contentType="text/html" pageEncoding="UTF-8"%>

<h2>Create Post</h2>

<form action="posts?action=create" method="post">
    User ID: <input type="number" name="userId" required><br>
    Category ID: <input type="number" name="categoryId" required><br>
    Title: <input type="text" name="title" required><br>
    Content:<br>
    <textarea name="content" required></textarea><br>
    <button type="submit">Create</button>
</form>
