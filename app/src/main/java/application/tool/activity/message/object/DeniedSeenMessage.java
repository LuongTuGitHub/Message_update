package application.tool.activity.message.object;

public class DeniedSeenMessage {
    private String person;
    private String key;

    public DeniedSeenMessage() {
    }

    public DeniedSeenMessage(String person, String key) {
        this.person = person;
        this.key = key;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
