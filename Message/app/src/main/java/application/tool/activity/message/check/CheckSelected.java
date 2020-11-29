package application.tool.activity.message.check;

import java.util.ArrayList;

import application.tool.activity.message.object.PersonInConversation;

public class CheckSelected {
    public boolean Check(String email, ArrayList<PersonInConversation> list){
        for (int i = 0; i <list.size() ; i++) {
            if(list.get(i).getPerson().equals(email)){
                return false;
            }
        }
        return true;
    }
}
