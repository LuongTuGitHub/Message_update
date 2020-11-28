package application.tool.activity.message.object;

public class KeyInbox {
    public String getKey(String PersonOne, String PersonTwo) {
        String result = "";
        if (PersonOne.compareTo(PersonTwo) > 0) {
            result += ("" + PersonOne.hashCode());
            result += ("" + PersonTwo.hashCode());
        } else {
            result += ("" + PersonTwo.hashCode());
            result += ("" + PersonOne.hashCode());
        }
        return "inbox" + result;
    }
}
