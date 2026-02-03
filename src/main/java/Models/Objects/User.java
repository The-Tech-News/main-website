package Models.Objects;

public class User {
    private final int id;
    private String email;
    private String pwdHash;
    private String name;
    private int groupId;
    
    public User(int id, String email, String pwdHash, String name, int groupId) {
        this.id = id;
        this.email = email;
        this.pwdHash = pwdHash;
        this.name = name;
        this.groupId = groupId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwdHash() {
        return pwdHash;
    }

    public void setPwdHash(String pwdHash) {
        this.pwdHash = pwdHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }
}
