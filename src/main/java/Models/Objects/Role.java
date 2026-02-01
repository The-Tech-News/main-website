package Models.Objects;

public class Role {
    private final int id;
    private String groupName;
    
    public Role(int id, String groupName) {
        this.id = id;
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getId() {
        return id;
    }
}
