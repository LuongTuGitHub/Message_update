package application.tool.activity.message.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

import application.tool.activity.message.R;
import application.tool.activity.message.qr_code.QrCode;

public class CreateQRCodeActivity extends AppCompatActivity {
    public Button btExit,btCreateQRCode,btDownQRCode;
    public EditText edtEnterContent;
    public ImageView ivShowQrCode;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public Switch swAuto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_q_r_code);

        Bitmap bitmap = QrCode.convertStringToQrCode("Hello");

        btExit = findViewById(R.id.bt_return_qr_code);
        btCreateQRCode = findViewById(R.id.bt_create_qr_code);
        btDownQRCode = findViewById(R.id.bt_download_qr_code);
        edtEnterContent = findViewById(R.id.edt_input_content);
        ivShowQrCode = findViewById(R.id.iv_show_code);
        swAuto = findViewById(R.id.sw_auto);

        ivShowQrCode.setImageBitmap(bitmap);

        btExit.setOnClickListener(v -> finish());
        btCreateQRCode.setOnClickListener(v -> {
            if(!edtEnterContent.getText().toString().trim().isEmpty()){
                Bitmap bitmapQR = QrCode.convertStringToQrCode(edtEnterContent.getText().toString().trim());
                ivShowQrCode.setImageBitmap(bitmapQR);
                edtEnterContent.setText("");
            }
        });
        edtEnterContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(swAuto.isChecked()){
                    if(!s.toString().trim().isEmpty()){
                        Bitmap bitmapQR = QrCode.convertStringToQrCode(s.toString());
                        ivShowQrCode.setImageBitmap(bitmapQR);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btDownQRCode.setOnClickListener(v -> {
            String Uuid = UUID.randomUUID().toString();
            BitmapDrawable drawable = (BitmapDrawable) ivShowQrCode.getDrawable();
            Bitmap bitmapQRCode = drawable.getBitmap();
            MediaStore.Images.Media.insertImage(getContentResolver(),bitmapQRCode,Uuid,null);
            Toast.makeText(this, "Saved !", Toast.LENGTH_SHORT).show();
        });
    }
}