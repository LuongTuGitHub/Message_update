package application.tool.activity.message.object;

public class PersonInConversation {
    public String nickName, email;

    public PersonInConversation() {
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PersonInConversation(String nickName, String email) {
        this.nickName = nickName;
        this.email = email;
    }
}
