package application.tool.activity.message;

import java.util.ArrayList;
import java.util.List;

import application.tool.activity.message.object.Message;

public class PositionTo {
    public boolean checkPosition(int position, String user, List<Message> messages) {
        int length = messages.size();
<<<<<<< HEAD:app/src/main/java/application/tool/activity/message/algorithm/PositionTo.java
        if (!messages.get(position).getFrom().equals(user) && position <= length - 1 && position >= 0) {
            if (position > 0) {
                if (position == length - 2 && messages.get(position).getFrom().equals(messages.get(position + 1).getFrom())) {
                    return false;
                }
                int index;
                for (index = position; index < length - 2; index++) {
                    if (!messages.get(index).getFrom().equals(messages.get(index + 1).getFrom())) {
                        break;
                    }
                }
                for (; index >= position; index--) {
                    if (type(messages.get(index).getType())) {
                        break;
=======
        if (!messages.get(position).getFrom().equals(user)) {
            if (position <= length - 1) {
                if (position >= 0) {
                    if (position > 0) {
                        if (position == length - 2) {
                            if (messages.get(position).getFrom().equals(messages.get(position + 1).getFrom())) {
                                return false;
                            }
                        }
                        int index;
                        for (index = position; index < length - 2; index++) {
                            if (!messages.get(index).getFrom().equals(messages.get(index + 1).getFrom())) {
                                break;
                            }
                        }
                        for (; index >= position; index--) {
                            if (type(messages.get(index).getType())) {
                                break;
                            }
                        }
                        return index == position;
                    }
                    if (length == 1) {
                        return type(messages.get(position).getType());
                    } else {
                        if (messages.get(position + 1).getFrom().equals(user)) {
                            return type(messages.get(position).getType());
                        } else {
                            return false;
                        }
>>>>>>> master:app/src/main/java/application/tool/activity/message/PositionTo.java
                    }
                }
                return index == position;
            }
            if (length == 1) {
                return type(messages.get(position).getType());
            }
            if (messages.get(position + 1).getFrom().equals(user)) {
                return type(messages.get(position).getType());
            }
            return false;
        }
        return false;
    }
<<<<<<< HEAD:app/src/main/java/application/tool/activity/message/algorithm/PositionTo.java
=======

>>>>>>> master:app/src/main/java/application/tool/activity/message/PositionTo.java
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
