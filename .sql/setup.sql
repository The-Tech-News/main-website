USE [master];
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
    [isEnabled] BOOLEAN NOT NULL,
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

    IF PATINDEX('^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$', @email) <> 0
        BEGIN
            RAISERROR ('Email is not in correct format.', 16, 1);
            RETURN;
        END

    IF NOT EXISTS (
        SELECT 1
            FROM [Role]
            WHERE groupName = @groupId
    ) BEGIN
        RAISERROR ('Group Id did not found.', 16, 1);
		RETURN;
    END

    INSERT INTO [User] ([email], [pwdHash], [name], [groupId], [isEnabled])
        VALUES (@email, @pwdHash, @name, @groupId, 1);
END
GO

INSERT INTO [Role] ([id], [groupName])
    VALUES  (1, 'Admin'),
            (2, 'Editor');

EXEC NewUser N'administrator@example.com', 'e10adc3949ba59abbe56e057f20f883e', 'Administrator', 1;
GO