package application.tool.activity.message.object;

public class Person {
    private String email;
    private int id;

    public int getId() {
        return id;
    }

    public Person() {
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public Person(int id, String email) {
        this.email = email;
        this.id = id;
    }
}
