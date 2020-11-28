package application.tool.activity.message.object;

public class PersonInConversation {
    private String person;

    public PersonInConversation() {
    }

    public PersonInConversation(String person) {
        this.person = person;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }
}
