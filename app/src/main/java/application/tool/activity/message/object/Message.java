package application.tool.activity.message.object;

import java.util.ArrayList;

public class Message {
    private String from, body;
    private int type;
    private ArrayList<String> denied;
    private long time;
    private String forMessage;

    public Message(String from, String body, int type, ArrayList<String> denied, long time, String forMessage) {
        this.from = from;
        this.body = body;
        this.type = type;
        this.denied = denied;
        this.time = time;
        this.forMessage = forMessage;
    }

    public String getForMessage() {
        return forMessage;
    }

    public void setForMessage(String forMessage) {
        this.forMessage = forMessage;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Message() {
    }

    public Message(String from, String body, int type, ArrayList<String> denied, long time) {
        this.from = from;
        this.body = body;
        this.type = type;
        this.denied = denied;
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

    public ArrayList<String> getDenied() {
        if(denied==null){
            return new ArrayList<>();
        }
        return denied;
    }

    public void setDenied(ArrayList<String> denied) {
        this.denied = denied;
    }
}
