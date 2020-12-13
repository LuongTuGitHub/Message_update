package application.tool.activity.message.object;

import java.util.ArrayList;

public class Conversation {
    private ArrayList<PersonInConversation> persons;
    private ArrayList<Message> messages;
    private String name;
    public Conversation() {
    }

    public Conversation(ArrayList<PersonInConversation> persons, ArrayList<Message> messages, String name) {
        this.persons = persons;
        this.messages = messages;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<PersonInConversation> getPersons() {
        return persons;
    }

    public void setPersons(ArrayList<PersonInConversation> persons) {
        this.persons = persons;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
