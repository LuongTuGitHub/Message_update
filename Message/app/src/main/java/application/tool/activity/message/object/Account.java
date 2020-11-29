package application.tool.activity.message.object;

public class Account {
    private String email, password;

    public String getEmail() {
        return email;
    }

    public Account() {
    }

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
