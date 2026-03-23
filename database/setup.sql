USE [master];
GO

IF EXISTS (SELECT 1 FROM sys.databases WHERE name = N'technewsdb')
BEGIN
    RAISERROR ('technewdb is already existed', 20, -1) WITH LOG;
    RETURN;
END
GO

CREATE DATABASE [technewsdb];
GO

USE [technewsdb];
GO

/** TABLE: User **/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [User] (
    [id] INT IDENTITY PRIMARY KEY,
    [email] NVARCHAR(255) UNIQUE NOT NULL,
    [pwdHash] VARCHAR(MAX) NOT NULL,
    [name] NVARCHAR(255) NOT NULL,
    [isEnabled] BIT NOT NULL,
    [groupId] INT NOT NULL
)
GO

/** TABLE: Role **/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [Role] (
    [id] INT IDENTITY PRIMARY KEY,
    [groupName] NVARCHAR(255) NOT NULL
)
GO

/** Procedure: NewUser  **/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE OR ALTER PROCEDURE NewUser
    @email NVARCHAR(255),
    @pwdHash VARCHAR(MAX),
    @name NVARCHAR(255),
    @groupId INT
AS BEGIN
    IF 
        @email IS NULL OR DATALENGTH(@email) = 0 OR
        @pwdHash IS NULL OR DATALENGTH(@pwdHash) = 0 OR
        @name IS NULL OR DATALENGTH(@name) = 0 OR
        @groupId IS NULL
        BEGIN
            RAISERROR ('A value is either NULL or empty is invalid.', 16, 1);
            RETURN;
        END

    IF EXISTS (
        SELECT 1
            FROM [User]
            WHERE email = @email
    ) BEGIN
        RAISERROR ('A user with duplicated email is found.', 16, 1);
		RETURN;
    END

    IF PATINDEX('^[A-Za-z0-9\.]{1,64}@[A-Za-z0-9]{1,64}\.[A-Za-z]{1,3}$', @email) <> 0
        BEGIN
            RAISERROR ('Email is not in correct format.', 16, 1);
            RETURN;
        END

    IF NOT EXISTS (
        SELECT 1
            FROM [Role]
            WHERE [id] = @groupId
    ) BEGIN
        RAISERROR ('Group Id did not found.', 16, 1);
		RETURN;
    END

    INSERT INTO [User] ([email], [pwdHash], [name], [groupId], [isEnabled])
        VALUES (@email, @pwdHash, @name, @groupId, 1);
END
GO

SET IDENTITY_INSERT [dbo].[Role] ON;
GO
INSERT INTO [dbo].[Role] ([id], [groupName])
    VALUES  (1, 'Admin'),
            (2, 'Editor');
GO
SET IDENTITY_INSERT [dbo].[Role] OFF;
GO

EXEC NewUser N'administrator@example.com', 'e10adc3949ba59abbe56e057f20f883e', 'Administrator', 1;
GO

EXEC NewUser N'anhvlt.2006@outlook.com', 'ff028e1c6f8d43e18a6e0c5426d41683', 'Vo Luu Tuong Anh', 1;
GO

EXEC NewUser N'vuongkienhao2006@gmail.com', '7b8b8a24e8594958bc89054224529d1e', 'Vuong Kien Hao', 1;
GO

EXEC NewUser N'longvcl173@gmail.com', '38b9bbe09e1f4884a9d81193e97b417b', 'Le Tuan Kiet', 1;
GO

EXEC NewUser N'duynvce191176@gmail.com', 'a5047408405f4ac593b3d2c61a7c71c4', 'Vu Duy', 1;
GO

/** TABLE: Category **/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [Category] (
    [id] INT IDENTITY PRIMARY KEY,
    [name] NVARCHAR(50) UNIQUE NOT NULL,
    [description] NVARCHAR(250) NOT NULL,
)
GO

/** Procedure: NewCategory **/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE OR ALTER PROCEDURE NewCategory
    @name NVARCHAR(50),
    @description NVARCHAR(250)
AS BEGIN
    IF @name IS NULL OR DATALENGTH(@name) = 0 BEGIN
        RAISERROR ('A category name must not be empty', 16, 1);
        RETURN;
    END

    IF EXISTS (
        SELECT 1
            FROM [Category] AS c
            WHERE c.name = @name
    ) BEGIN
        RAISERROR ('A category with name is already existed', 16, 1);
        RETURN;
    END

    INSERT INTO [Category] ([name], [description])
        VALUES (@name, @description);
END
GO

/** Procedure: EditCategory **/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE OR ALTER PROCEDURE EditCategory
    @oldName NVARCHAR(50),
    @newName NVARCHAR(50),
    @description NVARCHAR(250)
AS BEGIN
    IF @oldName IS NULL OR DATALENGTH(@oldName) = 0 BEGIN
        RAISERROR ('An old category name must not be empty', 16, 1);
        RETURN;
    END

    IF @newName IS NULL OR DATALENGTH(@newName) = 0 BEGIN
        RAISERROR ('A new category name must not be empty', 16, 1);
        RETURN;
    END

    IF NOT EXISTS (
        SELECT 1
            FROM [Category] AS c
            WHERE c.name = @oldName
    ) BEGIN
        RAISERROR ('A category with name does not exist. Try to create new one instead', 16, 1);
        RETURN;
    END

    IF EXISTS (
        SELECT 1
            FROM [Category] AS c
            WHERE c.name = @newName
    ) BEGIN
        RAISERROR ('A category with that new name does exist. Try another name instead', 16, 1);
        RETURN;
    END

    DECLARE @id INT = (
        SELECT TOP(1) [id]
            FROM [Category] AS c
            WHERE c.name = @oldName
    )

    UPDATE [Category]
        SET [name] = @newName, [description] = @description
        WHERE [id] = @id
END
GO

EXEC NewCategory 'windows', 'A category about Windows';
GO
EXEC NewCategory 'linux', 'A category about Linux';
GO

/** TABLE: Post **/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [Post] (
    [id] INT IDENTITY PRIMARY KEY,
    [userId] INT NOT NULL,
    [categoryId] INT NOT NULL,
    [title] NVARCHAR(100) NOT NULL,
    [content] NVARCHAR(MAX) NOT NULL,
    [isHidden] BIT NOT NULL DEFAULT 0
)
GO

/** Procedure: sp_InsertPost **/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE OR ALTER PROCEDURE sp_InsertPost
    @title NVARCHAR(100),
    @content NVARCHAR(MAX),
    @userId INT,
    @categoryId INT
AS
BEGIN
    IF (@title IS NULL OR LTRIM(RTRIM(@title)) = '') BEGIN
        RAISERROR ('Title cannot be empty', 16, 1);
        RETURN;
    END

    IF (@content IS NULL OR LTRIM(RTRIM(@content)) = '') BEGIN
        RAISERROR ('Content cannot be empty', 16, 1);
        RETURN;
    END

    IF NOT EXISTS (
            SELECT 1 
                FROM [User] 
                WHERE [id] = @userId
    ) BEGIN
        RAISERROR ('User not found', 16, 1);
        RETURN;
    END

    IF NOT EXISTS (
            SELECT 1
                FROM [Category]
                WHERE [id] = @categoryId
    ) BEGIN
        RAISERROR ('Category not found', 16, 1);
        RETURN;
    END

    INSERT INTO [Post] ([title], [content], [userId], [categoryId])
        VALUES (@title, @content, @userId, @categoryId);
END
GO

/** Procedure: sp_UpdatePost **/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE OR ALTER PROCEDURE sp_UpdatePost
    @postId INT,
    @title NVARCHAR(100),
    @content NVARCHAR(MAX),
    @categoryId INT
AS
BEGIN
    IF NOT EXISTS (
            SELECT 1
                FROM [Post]
                WHERE [id] = @postId
    ) BEGIN
        RAISERROR ('Post not found', 16, 1);
        RETURN;
    END

    IF (@title IS NULL OR LTRIM(RTRIM(@title)) = '') BEGIN
        RAISERROR ('Title cannot be empty', 16, 1);
        RETURN;
    END

    IF (@content IS NULL OR LTRIM(RTRIM(@content)) = '') BEGIN
        RAISERROR ('Content cannot be empty', 16, 1);
        RETURN;
    END

    IF NOT EXISTS (
            SELECT 1
                FROM [Category]
                WHERE [id] = @categoryId
    ) BEGIN
        RAISERROR ('Category not found', 16, 1);
        RETURN;
    END

    UPDATE [Post]
        SET [title] = @title, [content] = @content, [categoryId] = @categoryId
        WHERE id = @postId;
END
GO

/** Procedure: sp_HidePost **/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE OR ALTER PROCEDURE sp_HidePost
    @postId INT
AS
BEGIN
    IF NOT EXISTS (
            SELECT 1
                FROM [Post]
                WHERE [id] = @postId
    )
    BEGIN
        RAISERROR ('Post not found', 16, 1);
        RETURN;
    END

    UPDATE [Post]
        SET isHidden = 1
        WHERE [id] = @postId
END
GO

/** Procedure: sp_UnhidePost **/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE OR ALTER PROCEDURE sp_UnhidePost
    @postId INT
AS
BEGIN
    IF NOT EXISTS (
            SELECT 1
                FROM [Post]
                WHERE [id] = @postId
    )
    BEGIN
        RAISERROR ('Post not found', 16, 1);
        RETURN;
    END

    UPDATE [Post]
        SET isHidden = 0
        WHERE [id] = @postId
END
GO

EXECUTE sp_InsertPost 'About Windows', 'Windows is an operating system by Microsoft', 1, 1;
GO

/** TABLE: PostStat **/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

IF OBJECT_ID('dbo.PostStat', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.PostStat (
        [postId] INT PRIMARY KEY,
        [count] INT NOT NULL DEFAULT(0),
        [timestamp] DATETIME2 NULL
    );
END
GO

INSERT INTO dbo.PostStat([postId],[count]) VALUES (1, 0)
GO

/** TABLE: Post **/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [Comment] (
    [id] INT IDENTITY PRIMARY KEY,
    [userId] INT NOT NULL,
    [postId] INT NOT NULL,
    [content] NVARCHAR(MAX) NOT NULL,
    [createdAt] DATETIME DEFAULT GETUTCDATE(),
    [isHidden] BIT DEFAULT 0
)
GO

-- Foreign keys
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER TABLE dbo.Comment
    ADD CONSTRAINT FK_Comment_User FOREIGN KEY (userId) REFERENCES dbo.[User](id);
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER TABLE dbo.Comment
    ADD CONSTRAINT FK_Comment_Post FOREIGN KEY (postId) REFERENCES dbo.Post(id);
GO

-- Indexes for lookup performance
CREATE INDEX IX_Comment_PostId ON dbo.Comment(postId);
GO

CREATE INDEX IX_Comment_UserId ON dbo.Comment(userId);
GO

-- Procedure: sp_CreateComment
CREATE OR ALTER PROCEDURE sp_CreateComment
    @userId INT,
    @postId INT,
    @content NVARCHAR(MAX)
AS
BEGIN
    IF @userId IS NULL OR @postId IS NULL OR @content IS NULL OR LTRIM(RTRIM(@content)) = ''
    BEGIN
        RAISERROR('Invalid input for creating comment.', 16, 1);
        RETURN;
    END

    IF NOT EXISTS (SELECT 1 FROM dbo.[User] WHERE id = @userId AND isEnabled = 1)
    BEGIN
        RAISERROR('User not found or disabled.', 16, 1);
        RETURN;
    END

    IF NOT EXISTS (SELECT 1 FROM dbo.Post WHERE id = @postId AND isHidden = 0)
    BEGIN
        RAISERROR('Post not found or hidden.', 16, 1);
        RETURN;
    END

    INSERT INTO dbo.Comment (userId, postId, content)
        VALUES (@userId, @postId, @content);
END
GO

-- Procedure: sp_HideComment
-- Hides a comment if the requester is the owner or an admin (groupId = 1)
CREATE OR ALTER PROCEDURE sp_HideComment
    @commentId INT,
    @requesterId INT
AS
BEGIN
    IF @commentId IS NULL OR @requesterId IS NULL
    BEGIN
        RAISERROR('Invalid input for hiding comment.', 16, 1);
        RETURN;
    END

    DECLARE @ownerId INT;
    SELECT @ownerId = userId FROM dbo.Comment WHERE id = @commentId;

    IF @ownerId IS NULL
    BEGIN
        RAISERROR('Comment not found.', 16, 1);
        RETURN;
    END

    IF @ownerId = @requesterId
    BEGIN
        UPDATE dbo.Comment SET isHidden = 1 WHERE id = @commentId;
        RETURN;
    END

    -- check if requester is admin
    IF EXISTS (SELECT 1 FROM dbo.[User] u WHERE u.id = @requesterId AND u.groupId = 1)
    BEGIN
        UPDATE dbo.Comment SET isHidden = 1 WHERE id = @commentId;
        RETURN;
    END

    RAISERROR('Permission denied to hide comment.', 16, 1);
    RETURN;
END
GO