package application.tool.activity.message;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ResultFindActivity extends AppCompatActivity {
    Button exit, find;
    EditText input;
    ListView showResult;
    ArrayList<String> result;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_find);
        String content = getIntent().getStringExtra("content");
        exit = findViewById(R.id.exitFind);
        find = findViewById(R.id.find);
        input = findViewById(R.id.inputContentFind);
        showResult = findViewById(R.id.showListResult);
        exit.setOnClickListener(v -> finish());
        find.setOnClickListener(v -> {
            if (input.getVisibility() == View.VISIBLE) {
                if (input.getText().toString().length() == 0) {
                    input.setVisibility(View.INVISIBLE);
                } ////////////////////////////// TODO

            } else {
                input.setVisibility(View.INVISIBLE);
            }
        });

    }
}
