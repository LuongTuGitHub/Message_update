package application.tool.activity.message.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import application.tool.activity.message.R;

public class FormFragment extends Fragment {
    TextInputEditText inputEmail, inputPassword, inputRepeatPassword;
    TextInputLayout inputLayout, inputLayout2;
    CheckBox rememberMe;
    TextView message;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form, container, false);
        inputLayout = view.findViewById(R.id.textInputLayout3);
        inputEmail = view.findViewById(R.id.inputEmail);
        inputLayout2 = view.findViewById(R.id.textInputLayout2);
        inputPassword = view.findViewById(R.id.inputPassword);
        inputRepeatPassword = view.findViewById(R.id.inputRepeatPassword);
        rememberMe = view.findViewById(R.id.rememberMe);
        message = view.findViewById(R.id.messageError);
        return view;
    }
}
