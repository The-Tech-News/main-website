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

-- Code merge, by nguyenduyk19
-- Some encoding and coding style has been editied, due to limitation of sqlcmd

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

EXECUTE sp_InsertPost 'About Windows', 'Windows is an operating system by Microsoft', 1, 1;
GO
