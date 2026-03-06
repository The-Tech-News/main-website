package Models.Objects;

import java.sql.Timestamp;

public class Comment {

    private int id;
    private int userId;
    private int postId;
    private String content;
    private Timestamp createdAt;
    private boolean isHidden;

    public Comment() {
    }

    public Comment(int id, int userId, int postId, String content,
            Timestamp createdAt, boolean isHidden) {
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

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
