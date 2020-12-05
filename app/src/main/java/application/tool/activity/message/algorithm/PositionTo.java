package application.tool.activity.message.algorithm;

import java.util.ArrayList;

import application.tool.activity.message.object.MessageForConversation;

public class PositionTo {
    //    public boolean checkPosition(int position, String user, ArrayList<MessageForConversation> messages) {
//        int length = messages.size();
//        if (!messages.get(position).getFrom().equals(user)) {
//            int index = position;
//            if(index>0){
//                if (length == 1) {
//                    return type(messages.get(position).getType());
//                } else if(length>1){
//                    for (index = position; index < length - 2; index++) {
//                        if (!messages.get(index).getFrom().equals(messages.get(index - 1).getFrom())) {
//                            break;
//                        }
//                    }
//                    if (type(messages.get(index).getType())) {
//                        return true;
//                    } else {
//                        for (index = index; index >= position; index--) {
//                            if (type(messages.get(index).getType())) {
//                                break;
//                            }
//                        }
//                    }
//
//                    return index == position;
//                }
//            }else {
//                return type(messages.get(position).getType());
//            }
//        }
//        return false;
//    }
    public boolean checkPosition(int position, String user, ArrayList<MessageForConversation> messages) {
        if (position == 0)
            return true;
        if (messages.get(position).getFrom() != user)
            return messages.get(position).getFrom() != messages.get(position - 1).getFrom();
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
