package Models.Objects;

public class Ads {

    // id của quảng cáo (Primary Key trong database)
    private int id;

    // id của bài viết mà quảng cáo thuộc về
    private int postId;

    // tiêu đề của quảng cáo
    private String title;

    // đường dẫn hình ảnh của quảng cáo
    private String uriImage;

    /**
     * Constructor rỗng.
     *
     * Dùng khi: - Tạo object Ads trước rồi set dữ liệu sau - Framework hoặc thư
     * viện cần constructor mặc định
     */
    public Ads() {
    }

    /**
     * Constructor đầy đủ tham số.
     *
     * Dùng để khởi tạo object Ads với đầy đủ thông tin.
     *
     * @param id id của quảng cáo
     * @param postId id bài viết liên kết với quảng cáo
     * @param title tiêu đề quảng cáo
     * @param uriImage đường dẫn hình ảnh quảng cáo
     */
    public Ads(int id, int postId, String title, String uriImage) {

	// gán giá trị id cho thuộc tính id của object
	this.id = id;

	// gán giá trị postId cho thuộc tính postId
	this.postId = postId;

	// gán tiêu đề quảng cáo
	this.title = title;

	// gán đường dẫn hình ảnh
	this.uriImage = uriImage;
    }

    /**
     * Lấy id của quảng cáo.
     *
     * @return id quảng cáo
     */
    public int getId() {

	// trả về id
	return id;
    }

    /**
     * Cập nhật id cho quảng cáo.
     *
     * @param id id mới của quảng cáo
     */
    public void setId(int id) {

	// gán id mới
	this.id = id;
    }

    /**
     * Lấy id của bài viết liên kết với quảng cáo.
     *
     * @return postId của bài viết
     */
    public int getPostId() {

	// trả về postId
	return postId;
    }

    /**
     * Cập nhật postId của quảng cáo.
     *
     * @param postId id bài viết mới
     */
    public void setPostId(int postId) {

	// gán postId mới
	this.postId = postId;
    }

    /**
     * Lấy tiêu đề quảng cáo.
     *
     * @return title của quảng cáo
     */
    public String getTitle() {

	// trả về tiêu đề
	return title;
    }

    /**
     * Cập nhật tiêu đề quảng cáo.
     *
     * @param title tiêu đề mới
     */
    public void setTitle(String title) {

	// gán tiêu đề mới
	this.title = title;
    }

    /**
     * Lấy đường dẫn hình ảnh của quảng cáo.
     *
     * @return uriImage đường dẫn hình ảnh
     */
    public String getUriImage() {

	// trả về đường dẫn ảnh
	return uriImage;
    }

    /**
     * Cập nhật đường dẫn hình ảnh của quảng cáo.
     *
     * @param uriImage đường dẫn hình ảnh mới
     */
    public void setUriImage(String uriImage) {

	// gán đường dẫn ảnh mới
	this.uriImage = uriImage;
    }
}
