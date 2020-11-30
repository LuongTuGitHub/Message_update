package application.tool.activity.message.object;

import java.util.ArrayList;

public class Conversation {
    private String name;
    private ArrayList<PersonInConversation> personInConversationArrayList;
    private ArrayList<MessageForConversation> messageForConversationArrayList;
    private ArrayList<DeniedSeenMessage> deniedSeenMessageArrayList;

    public Conversation() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Conversation(String name, ArrayList<PersonInConversation> personInConversationArrayList, ArrayList<MessageForConversation> messageForConversationArrayList, ArrayList<DeniedSeenMessage> deniedSeenMessageArrayList) {
        this.name = name;
        this.personInConversationArrayList = personInConversationArrayList;
        this.messageForConversationArrayList = messageForConversationArrayList;
        this.deniedSeenMessageArrayList = deniedSeenMessageArrayList;
    }

    public ArrayList<PersonInConversation> getPersonInConversationArrayList() {
        return personInConversationArrayList;
    }

    public void setPersonInConversationArrayList(ArrayList<PersonInConversation> personInConversationArrayList) {
        this.personInConversationArrayList = personInConversationArrayList;
    }

    public ArrayList<MessageForConversation> getMessageForConversationArrayList() {
        return messageForConversationArrayList;
    }

    public void setMessageForConversationArrayList(ArrayList<MessageForConversation> messageForConversationArrayList) {
        this.messageForConversationArrayList = messageForConversationArrayList;
    }

    public ArrayList<DeniedSeenMessage> getDeniedSeenMessageArrayList() {
        return deniedSeenMessageArrayList;
    }

    public void setDeniedSeenMessageArrayList(ArrayList<DeniedSeenMessage> deniedSeenMessageArrayList) {
        this.deniedSeenMessageArrayList = deniedSeenMessageArrayList;
    }
}
