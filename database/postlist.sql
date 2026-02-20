CREATE TABLE Post (
    id INT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    categoryId INT NOT NULL,
    title NVARCHAR(100) NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    isHidden BIT NOT NULL DEFAULT 0
);

////////////////////////////PROCEDURE//////////////////////////////////

////INSERT///////
CREATE PROCEDURE sp_InsertPost
    @title NVARCHAR(100),
    @content NVARCHAR(MAX),
    @userId INT,
    @categoryId INT
AS
BEGIN
    IF (@title IS NULL OR LTRIM(RTRIM(@title)) = '')
        THROW 50000, 'Title cannot be empty', 1;

    IF (@content IS NULL OR LTRIM(RTRIM(@content)) = '')
        THROW 50000, 'Content cannot be empty', 1;

    IF NOT EXISTS (SELECT 1 FROM [User] WHERE id = @userId)
        THROW 50000, 'User not found', 1;

    IF NOT EXISTS (SELECT 1 FROM Category WHERE id = @categoryId)
        THROW 50000, 'Category not found', 1;

    IF EXISTS (
        SELECT 1 FROM Category 
        WHERE id = @categoryId AND isHidden = 1
    )
        THROW 50000, 'Category is hidden', 1;

    INSERT INTO Post(title, content, userId, categoryId)
    VALUES (@title, @content, @userId, @categoryId);
END

//////UPDATE////////
CREATE PROCEDURE sp_UpdatePost
    @postId INT,
    @title NVARCHAR(100),
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

    IF NOT EXISTS (SELECT 1 FROM Category WHERE id = @categoryId)
        THROW 50000, 'Category not found', 1;

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
CREATE PROCEDURE sp_HidePost
    @postId INT
AS
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Post WHERE id = @postId)
        THROW 50000, 'Post not found', 1;

    UPDATE Post
    SET isHidden = 1
    WHERE id = @postId;
END




