<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>Edit Ads</title>
</head>
<body>

<h2>Sửa quảng cáo</h2>

<form action="ads?action=edit" method="post">

    <input type="hidden" name="id" value="${ads.id}">

    Post ID:<br>
    <input type="number" name="postId" value="${ads.postId}"><br><br>

    Title:<br>
    <input type="text" name="title" value="${ads.title}"><br><br>

    Image URL:<br>
    <input type="text" name="uriImage" value="${ads.uriImage}"><br><br>

    <button type="submit">Update</button>

</form>

</body>
</html>