package application.tool.activity.message.check;

import java.util.ArrayList;

import application.tool.activity.message.object.PersonInConversation;

public class CheckUserInConversation {
    public boolean returnResult(String user, ArrayList<PersonInConversation> personInConversationArrayList) {
        for (int i = 0; i < personInConversationArrayList.size(); i++) {
            if (personInConversationArrayList.get(i).getPerson().equals(user)) {
                return true;
            }
        }
        return false;
    }

    public String getNameConversation(String user, ArrayList<PersonInConversation> personInConversationArrayList) {
        if (personInConversationArrayList.size() == 2) {
            for (int i = 0; i < personInConversationArrayList.size(); i++) {
                if (!personInConversationArrayList.get(i).getPerson().equals(user)) {
                    return personInConversationArrayList.get(i).getPerson();
                }
            }
        }
        return "Group Chat";
    }
}
