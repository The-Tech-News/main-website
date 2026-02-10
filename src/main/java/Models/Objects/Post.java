package Models.Objects;

public class Post {

    private int id;
    private int userId;
    private int categoryId;
    private String title;
    private String content;
    private boolean isHidden;

  
    public Post() {
    }

    // constructor 
    public Post(int id, int userId, int categoryId,
                String title, String content, boolean isHidden) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
        this.isHidden = isHidden;
    }

    // getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getCategoryId() { return categoryId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public boolean isHidden() { return isHidden; }

 
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
