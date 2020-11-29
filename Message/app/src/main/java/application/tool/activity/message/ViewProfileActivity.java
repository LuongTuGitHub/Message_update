package application.tool.activity.message;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class ViewProfileActivity extends AppCompatActivity {
    Button returnContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        returnContent = findViewById(R.id.returnContent);
        returnContent.setOnClickListener(v -> finish());
    }
}
