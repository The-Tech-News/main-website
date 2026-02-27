USE [technewsdb];
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF OBJECT_ID('dbo.Comment', 'U') IS NOT NULL
    DROP TABLE dbo.Comment;
GO

CREATE TABLE dbo.Comment (
    id INT IDENTITY PRIMARY KEY,
    userId INT NOT NULL,
    postId INT NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    createdAt DATETIME DEFAULT GETUTCDATE(),
    isHidden BIT DEFAULT 0
);
GO

-- Foreign keys
ALTER TABLE dbo.Comment
    ADD CONSTRAINT FK_Comment_User FOREIGN KEY (userId) REFERENCES dbo.[User](id);
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