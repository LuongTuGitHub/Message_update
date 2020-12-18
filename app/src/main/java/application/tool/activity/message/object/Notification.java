package application.tool.activity.message.object;

public class Notification {
    private String type, from, body, key;
    private int day, mouth, year;
    private long time;

    public Notification() {
    }

    public Notification(String type, String from, String body, String key, int day, int mouth, int year, long time) {
        this.type = type;
        this.from = from;
        this.body = body;
        this.key = key;
        this.day = day;
        this.mouth = mouth;
        this.year = year;
        this.time = time;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMouth() {
        return mouth;
    }

    public void setMouth(int mouth) {
        this.mouth = mouth;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
