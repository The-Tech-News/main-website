<%@page contentType="text/html" pageEncoding="UTF-8"%>

<h2>Edit Post</h2>

<form action="posts?action=edit" method="post">
    <input type="hidden" name="id" value="${post.id}">
    Title: <input type="text" name="title" value="${post.title}" required><br>
    Content:<br>
    <textarea name="content" required>${post.content}</textarea><br>
    <button type="submit">Update</button>
</form>
