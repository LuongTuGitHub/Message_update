package application.tool.activity.message.object;

import java.util.ArrayList;

public class MessageForConversation {
    private String from;
    private String body;
    private int type;
    private long time;
    private ArrayList<String> denied;

    public MessageForConversation() {
    }

    public MessageForConversation(String from, String body, int type, long time, ArrayList<String> denied) {
        this.from = from;
        this.body = body;
        this.type = type;
        this.time = time;
        this.denied = denied;
    }

    public ArrayList<String> getDenied() {
        return denied;
    }

    public void setDenied(ArrayList<String> denied) {
        this.denied = denied;
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
