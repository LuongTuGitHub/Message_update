package application.tool.activity.message;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import application.tool.activity.message.qr_code.QrCode;

public class CreateQrCodeActivity extends AppCompatActivity {
    Button create, down, exit;
    ImageView viewResult;
    EditText inputContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qr_code);
        Create();
        create.setOnClickListener(v -> {
            if (inputContent.getText().toString().trim().length() > 0) {
                viewResult.setImageBitmap(new QrCode().convertStringToQrCode(inputContent.getText().toString()));
                inputContent.setText("");
            }
        });
        down.setOnClickListener(v -> {
            BitmapDrawable result = (BitmapDrawable) viewResult.getDrawable();
            if (result != null) {
                MediaStore.Images.Media.insertImage(getContentResolver(), result.getBitmap(), "createQrCode" + inputContent.getText().toString().hashCode(), null);
                Toast.makeText(this, "Saved !", Toast.LENGTH_SHORT).show();
            }
        });
        exit.setOnClickListener(v -> finish());
    }

    public void Create() {
        create = findViewById(R.id.createQrCode);
        down = findViewById(R.id.downLoadQrCode);
        viewResult = findViewById(R.id.showQrCode);
        inputContent = findViewById(R.id.inputContentConvert);
        exit = findViewById(R.id.exitCreateQrCode);
    }
}
