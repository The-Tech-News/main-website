package Models.Objects;

public class User {

    private final int id;
    private final String email;
    private final String pwdHash;
    private final String name;
    private final int groupId;

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

    public String getPwdHash() {
        return pwdHash;
    }

    public String getName() {
        return name;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getId() {
        return id;
    }
}
