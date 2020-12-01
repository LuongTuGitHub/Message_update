package application.tool.activity.message.object;

public class MessageForConversation {
    private String from;
    private String body;
    private int type;
    private long time;

    public MessageForConversation() {
    }

    public MessageForConversation(String from, String body, int type, long time) {
        this.from = from;
        this.body = body;
        this.type = type;
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
