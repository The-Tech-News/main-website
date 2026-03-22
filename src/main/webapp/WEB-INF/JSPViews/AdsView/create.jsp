<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>Create Ads</title>
</head>
<body>

<h2>Tạo quảng cáo</h2>

<form action="ads?action=create" method="post">

    Post ID:<br>
    <input type="number" name="postId"><br><br>

    Title:<br>
    <input type="text" name="title"><br><br>

    Image URL:<br>
    <input type="text" name="uriImage"><br><br>

    <button type="submit">Create</button>

</form>

</body>
</html>