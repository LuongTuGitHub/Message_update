package application.tool.activity.message.algorithm;

import java.util.ArrayList;

import application.tool.activity.message.object.MessageForConversation;

public class PositionTo {
    public boolean checkPosition(int position, String user, ArrayList<MessageForConversation> messages) {
        int length = messages.size();
        if (!messages.get(position).getFrom().equals(user)) {
            if (position <= length - 1) {////////////////////////////////// TH
                if (position == length - 1) {
                    return type(messages.get(length - 1).getType());
                } else {
                    if(position>0){
                        if(type(messages.get(position).getType())){
                            if(!messages.get(position).getFrom().equals(messages.get(position+1).getFrom())){
                                return true;
                            }else {
                                return false;
                            }
                        }else {
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }
    public boolean type(int type) {
        switch (type) {
            case 3:
            case 2:
            case 4:
                return false;
        }
        return true;
    }
}
