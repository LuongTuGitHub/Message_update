package application.tool.activity.message.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import application.tool.activity.message.R;
import application.tool.activity.message.ResultFindActivity;

public class ToolbarFragment extends Fragment {
    public Button openMenu, search, scanQrCode;
    EditText inputContent;
    FirebaseUser user;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_toolbar, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        openMenu = view.findViewById(R.id.openMenu);
        search = view.findViewById(R.id.search);
        scanQrCode = view.findViewById(R.id.scanQrCode);
        inputContent = view.findViewById(R.id.inputContentSearch);
        search.setOnClickListener(v -> {
            if (inputContent.getVisibility() == View.INVISIBLE) {
                inputContent.setVisibility(View.VISIBLE);
            } else {
                if (inputContent.getText().toString().equals("")) {
                    inputContent.setVisibility(View.INVISIBLE);
                } else {
                    Intent intent = new Intent(getActivity(), ResultFindActivity.class);
                    intent.putExtra("content",inputContent.getText().toString());
                    inputContent.setText("");
                    startActivity(intent);
                }
            }
        });
        return view;
    }
}
