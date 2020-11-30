package application.tool.activity.message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import application.tool.activity.message.object.Account;

public class GenerateCode extends AppCompatActivity {

    public static final int QR_CODE_LENGTH = 700;
    Bitmap bitmap;

    EditText editText;
    ImageView image;
    Button saveButton;
    Button generateButton;
    Button generateAddCodeButton;
    Button generateLoginCodeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        editText = findViewById(R.id.content1);
        image = findViewById(R.id.image1);
        generateButton = findViewById(R.id.generate1);
        saveButton = findViewById(R.id.save1);
        saveButton.setVisibility(View.INVISIBLE);
        generateAddCodeButton = findViewById(R.id.generateAddCode);
        generateLoginCodeButton = findViewById(R.id.generateLoginCode);

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().length() == 0)
                    Toast.makeText(GenerateCode.this, "Enter a vail text", Toast.LENGTH_SHORT).show();
                else {
                    bitmap = textToBitmap(editText.getText().toString());
                    image.setImageBitmap(bitmap);
                    saveButton.setVisibility(View.VISIBLE);
                    saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "code", null);
                            Toast.makeText(GenerateCode.this, "Saved", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        generateAddCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = textToBitmap(getStringAdd());
                editText.setText("");
                image.setImageBitmap(bitmap);
                saveButton.setVisibility(View.VISIBLE);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "add-code", null);
                        Toast.makeText(GenerateCode.this, "Saved", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        generateLoginCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = textToBitmap(getStringLogin());
                editText.setText("");
                image.setImageBitmap(bitmap);
                saveButton.setVisibility(View.VISIBLE);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "login-code", null);
                        Toast.makeText(GenerateCode.this, "Saved", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public Bitmap textToBitmap(String value) {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(value, BarcodeFormat.DATA_MATRIX.QR_CODE, QR_CODE_LENGTH, QR_CODE_LENGTH, null);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixel = new int[bitMatrixHeight * bitMatrixWidth];
        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixel[offset + x] = bitMatrix.get(x, y) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixel, 0, 700, 0, 0, bitMatrixWidth, bitMatrixHeight);

        return bitmap;
    }

    Account account;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser user;

    public String getStringLogin() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference.child("Account" + user.getEmail()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    account = snapshot.getValue(Account.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String email = "";
        String password = "";
        if (account != null) {
            email = account.getEmail();
            password = account.getPassword();
        }
        return "log:" + email + ":" + password;
    }

    public String getStringAdd() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference.child("account" + user.getEmail()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    account = snapshot.getValue(Account.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String email = "";
        if (account != null) {
            email = account.getEmail();
        }
        return "add:" + email;
    }
}