package application.tool.activity.message.object;

public class ConvertConversation {
    private Conversation conversation;
    private String key;

    public ConvertConversation(Conversation conversation, String key) {
        this.conversation = conversation;
        this.key = key;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public String getKey() {
        return key;
    }
}
