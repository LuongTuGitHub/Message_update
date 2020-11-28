package application.tool.activity.message.object;

public class Convert {
    public String byteToString(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            result += bytes[i];
        }

        return result;
    }

    public byte[] stringToByte(String string) {
        byte[] bytes = new byte[string.length()];
        for (int i = 0; i < string.length(); i++) {
            bytes[i] = (byte) Integer.parseInt(string.charAt(i) + "");
        }

        return bytes;
    }
}
