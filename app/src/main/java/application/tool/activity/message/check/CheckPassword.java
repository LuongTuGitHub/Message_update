package application.tool.activity.message.check;

public class CheckPassword {
    private char[][] arrayLetter = new char[][]{
            new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'},
            new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'}
    };

    private char[] arrayLetterError = new char[]{'À', 'Á', 'Â', 'Ã', 'È', 'É',
            'Ê', 'Ì', 'Í', 'Ò', 'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â',
            'ã', 'è', 'é', 'ê', 'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý',
            'Ă', 'ă', 'Đ', 'đ', 'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ',
            'ạ', 'Ả', 'ả', 'Ấ', 'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ',
            'Ắ', 'ắ', 'Ằ', 'ằ', 'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ',
            'ẻ', 'Ẽ', 'ẽ', 'Ế', 'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ',
            'Ỉ', 'ỉ', 'Ị', 'ị', 'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ',
            'ổ', 'Ỗ', 'ỗ', 'Ộ', 'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ',
            'Ợ', 'ợ', 'Ụ', 'ụ', 'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ',
            'ữ', 'Ự', 'ự'
    };
    private char[] arrayCharacterSpecial = new char[]{'!', '@', '#', '$', '%', '^', '&', '*'};
    private char[] arrayDigit = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public boolean constrainDigit(String string) {
        for (int i = 0; i < string.length(); i++) {
            for (int j = 0; j < arrayDigit.length; j++) {
                if (string.charAt(i) == arrayDigit[j])
                    return true;
            }
        }

        return false;
    }

    public boolean constrainLetterLowCase(String string) {
        for (int i = 0; i < string.length(); i++) {
            for (int j = 0; j < arrayLetter[0].length; j++) {
                if (string.charAt(i) == arrayLetter[0][j])
                    return true;
            }
        }

        return false;
    }

    public boolean checkLetterError(String string) {
        for (int i = 0; i < string.length(); i++) {
            for (int j = 0; j < arrayLetterError.length; j++) {
                if (string.charAt(i) == arrayLetterError[j])
                    return false;
            }
        }
        return true;
    }

    public boolean constrainLetterUpperCase(String string) {
        for (int i = 0; i < string.length(); i++) {
            for (int j = 0; j < arrayLetter[0].length; j++) {
                if (string.charAt(i) == arrayLetter[1][j])
                    return true;
            }
        }
        return false;
    }

    public boolean constrainCharacterSpecial(String string) {
        for (int i = 0; i < string.length(); i++) {
            for (int j = 0; j < arrayCharacterSpecial.length; j++) {
                if (string.charAt(i) == arrayCharacterSpecial[j])
                    return true;
            }
        }
        return false;
    }

    public boolean verifyPassword(String password, String repeatPassword) {
        if (password.length() != repeatPassword.length())
            return false;
        else {
            for (int i = 0; i < password.length(); i++) {
                if (password.charAt(i) != repeatPassword.charAt(i))
                    return false;
            }
        }
        return true;
    }

}
