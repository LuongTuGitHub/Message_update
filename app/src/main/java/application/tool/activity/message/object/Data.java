package application.tool.activity.message.object;

public class Data {
    private String type,from,body, conversation;

    public Data() {
    }

    public Data(String type, String from, String body, String conversation) {
        this.type = type;
        this.from = from;
        this.body = body;
        this.conversation = conversation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }
}
