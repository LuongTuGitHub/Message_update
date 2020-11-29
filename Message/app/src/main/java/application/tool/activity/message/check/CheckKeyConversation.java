package application.tool.activity.message.check;

import java.util.ArrayList;

public class CheckKeyConversation {
    public boolean getResult(String key, ArrayList<String> arrayKey) {
        for (int i = 0; i < arrayKey.size(); i++) {
            if (arrayKey.get(i).equals(key)) {
                return false;
            }
        }


        return true;
    }
}
