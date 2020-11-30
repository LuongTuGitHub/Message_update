package application.tool.activity.message.object;

public class KeyConversation {
    private String name,key,nameGroup;

    public KeyConversation() {
    }

    public KeyConversation(String name, String key,String nameGroup) {
        this.name = name;
        this.key = key;
        this.nameGroup = nameGroup;
    }

    public String getNameGroup() {
        return nameGroup;
    }

    public void setNameGroup(String nameGroup) {
        this.nameGroup = nameGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
