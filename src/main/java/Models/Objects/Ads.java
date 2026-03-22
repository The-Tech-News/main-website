package Models.Objects;

public class Ads {

    private final int id;
    private final int postId;
    private final String title;
    private final String uriImage;

    public Ads(int id, int postId, String title, String uriImage) {
        this.id = id;
        this.postId = postId;
        this.title = title;
        this.uriImage = uriImage;
    }

    public int getId() {
        return id;
    }

    public int getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public String getUriImage() {
        return uriImage;
    }
}
