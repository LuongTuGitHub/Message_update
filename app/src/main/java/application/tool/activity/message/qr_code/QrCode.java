package application.tool.activity.message.qr_code;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class QrCode {
    public static Bitmap convertStringToQrCode(String string) {
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(string, BarcodeFormat.QR_CODE, 300, 300, null);
        } catch (Exception ignored) {

        }
        assert matrix != null;
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int i = 0; i < height; i++) {
            int offset = i * width;
            for (int j = 0; j < width; j++) {
                pixels[offset + j] = matrix.get(j, i) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static Bitmap getQrUser(String email) {
        String uri = "https://www.message.hus.com.vn/" + email;
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(uri, BarcodeFormat.QR_CODE, 300, 300, null);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        assert matrix != null;
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int i = 0; i < height; i++) {
            int offset = i * width;
            for (int j = 0; j < width; j++) {
                pixels[offset + j] = matrix.get(j, i) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
