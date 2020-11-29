package application.tool.activity.message.check;

public class CheckFormatDate {
    private final char[] arrayDigit = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private boolean checkLength(String day) {
        return day.length() <= 10;
    }

    private boolean checkSlash(String day) {
        int count = 0;
        for (int i = 0; i < day.length(); i++) {
            if (day.charAt(i) == '/')
                count++;
        }
        return count == 2;
    }

    private boolean checkDigit(String day) {
        String[] array = day.split("/");
        if (array.length != 3)
            return false;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < array[i].length(); j++) {
                if (!check(array[i].charAt(j))) {
                    return false;
                }
            }
        }
        return checkValue(array[0], array[1], array[2]);
    }

    public boolean checkValue(String day, String mouth, String year) {
        return (Integer.parseInt(day) <= getDayOfMouth(mouth, year) || Integer.parseInt(day) > 0) && (getDayOfMouth(mouth, year) != -1);
    }

    public boolean checkYear(String year) {
        return Integer.parseInt(year) / 4 == 0;
    }

    public int getDayOfMouth(String mouth, String year) {

        int number = Integer.parseInt(mouth);
        switch (number) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                if (checkYear(year))
                    return 29;
                return 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
        }

        return -1;
    }

    public boolean checkFormat(String day) {
        if (checkDigit(day)) {
            if (checkLength(day)) {
                return checkSlash(day);
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean check(char i) {
        for (char c : arrayDigit) {
            if (i == c)
                return true;
        }
        return false;
    }

}
