package application.tool.activity.message.object;

public class MessageForConversation {
    private String from;
    private String body;
    private long time;

    public MessageForConversation() {
    }

    public MessageForConversation(String from, String body, long time) {
        this.from = from;
        this.body = body;
        this.time = time;
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
