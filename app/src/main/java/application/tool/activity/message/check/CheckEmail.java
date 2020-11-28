package application.tool.activity.message.check;

public class CheckEmail {
    private String[] provider = new String[]{"gmail.com", "hus.edu.vn", "yahoo.com"};

    public boolean ProviderEnable(String string) {
        if (EmailFormat(string)) {
            String domain = string.split("@")[1];
            for (int i = 0; i < provider.length; i++) {
                if (domain.trim().equals(provider[i]))
                    return true;
            }
        }
        return false;
    }

    public boolean EmailFormat(String string) {
        int count = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '@') {
                count++;
            }
        }
        return count == 1;
    }
}
