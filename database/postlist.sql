CREATE TABLE Post (
    id INT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    categoryId INT NOT NULL,
    title NVARCHAR(100) NOT NULL UNIQUE,
    content NVARCHAR(MAX) NOT NULL,
    isHidden BIT NOT NULL DEFAULT 0
);

////////////////////////////PROCEDURE//////////////////////////////////

////INSERT///////
CREATE PROCEDURE sp_InsertPost
    @title NVARCHAR(255),
    @content NVARCHAR(MAX),
    @userId INT,
    @categoryId INT
AS
BEGIN
    -- validate title & content
    IF (@title IS NULL OR LTRIM(RTRIM(@title)) = '')
        THROW 50000, 'Title cannot be empty', 1;

    IF (@content IS NULL OR LTRIM(RTRIM(@content)) = '')
        THROW 50000, 'Content cannot be empty', 1;

    -- validate userId
    IF NOT EXISTS (SELECT 1 FROM [User] WHERE id = @userId)
        THROW 50000, 'User not found', 1;

    -- validate categoryId
    IF NOT EXISTS (SELECT 1 FROM Category WHERE id = @categoryId)
        THROW 50000, 'Category not found', 1;

    -- check isHidden
    IF EXISTS (
        SELECT 1 FROM Category 
        WHERE id = @categoryId AND isHidden = 1
    )
        THROW 50000, 'Category is hidden', 1;

    INSERT INTO Post(title, content, userId, categoryId, createdAt)
    VALUES (@title, @content, @userId, @categoryId, GETDATE());
END


//////UPDATE////////
CREATE PROCEDURE sp_UpdatePost
    @postId INT,
    @title NVARCHAR(255),
    @content NVARCHAR(MAX),
    @categoryId INT
AS
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Post WHERE id = @postId)
        THROW 50000, 'Post not found', 1;

    IF (@title IS NULL OR LTRIM(RTRIM(@title)) = '')
        THROW 50000, 'Title cannot be empty', 1;

    IF (@content IS NULL OR LTRIM(RTRIM(@content)) = '')
        THROW 50000, 'Content cannot be empty', 1;

    IF EXISTS (
        SELECT 1 FROM Category 
        WHERE id = @categoryId AND isHidden = 1
    )
        THROW 50000, 'Category is hidden', 1;

    UPDATE Post
    SET title = @title,
        content = @content,
        categoryId = @categoryId
    WHERE id = @postId;
END



/////DELETE///////
CREATE PROCEDURE sp_DeletePost
    @postId INT
AS
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Post WHERE id = @postId)
        THROW 50000, 'Post not found', 1;

    DELETE FROM Post WHERE id = @postId;
END




