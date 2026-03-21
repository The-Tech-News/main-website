package Models.Objects;

import java.sql.Timestamp;

public class Comment {

    private final int id;
    private final int userId;
    private final int postId;
    private final String content;
    private final Timestamp createdAt;
    private final boolean isHidden;

    public Comment(int id, int userId, int postId, String content, Timestamp createdAt, boolean isHidden) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.content = content;
        this.createdAt = createdAt;
        this.isHidden = isHidden;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getPostId() {
        return postId;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public boolean isHidden() {
        return isHidden;
    }
}
