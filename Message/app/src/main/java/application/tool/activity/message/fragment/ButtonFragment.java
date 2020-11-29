package application.tool.activity.message.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import application.tool.activity.message.ContentActivity;
import application.tool.activity.message.R;
import application.tool.activity.message.check.CheckEmail;
import application.tool.activity.message.check.CheckPassword;
import application.tool.activity.message.object.Account;
import application.tool.activity.message.object.Person;
import application.tool.activity.message.sqlite.AccountShare;

public class ButtonFragment extends Fragment {
    Button login, signUp, resetPassword;
    FormFragment formFragment;
    private static boolean toggle = false;
    private static boolean toggleReset = false;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_button, container, false);
        formFragment = (FormFragment) getActivity().getFragmentManager().findFragmentById(R.id.fragment);
        login = view.findViewById(R.id.login);
        signUp = view.findViewById(R.id.signUp);
        resetPassword = view.findViewById(R.id.resetPassword);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        login.setOnClickListener(v -> {
            formFragment.inputPassword.setVisibility(View.VISIBLE);
            toggleReset = false;
            if (toggle) {
                toggle = false;
                formFragment.inputLayout.setVisibility(View.INVISIBLE);
            } else {
                formFragment.inputLayout.setVisibility(View.INVISIBLE);
                if (!Objects.requireNonNull(formFragment.inputEmail.getText()).toString().isEmpty() && !Objects.requireNonNull(formFragment.inputPassword.getText()).toString().isEmpty()) {
                    if (new CheckEmail().EmailFormat(formFragment.inputEmail.getText().toString())) {
                        if (new CheckEmail().ProviderEnable(formFragment.inputEmail.getText().toString())) {
                            auth.signInWithEmailAndPassword(formFragment.inputEmail.getText().toString(), formFragment.inputPassword.getText().toString()).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getActivity(), ContentActivity.class);
                                    intent.putExtra("email", formFragment.inputEmail.getText().toString());
                                    intent.putExtra("password", formFragment.inputPassword.getText().toString());
                                    if (formFragment.rememberMe.isChecked()) {
                                        new AccountShare(getActivity()).dropAccount();
                                        new AccountShare(getActivity()).addAccount(formFragment.inputEmail.getText().toString(), formFragment.inputPassword.getText().toString());
                                        startActivity(intent);
                                        getActivity().finish();
                                    } else {
                                        new AccountShare(getActivity()).dropAccount();
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                } else {
                                    formFragment.message.setText("Check Your Password Or Account Not Exist");
                                    formFragment.message.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            formFragment.message.setText("Provide Not Disable");
                            formFragment.message.setVisibility(View.VISIBLE);
                        }
                    } else {
                        formFragment.message.setText("Email Error Format");
                        formFragment.message.setVisibility(View.VISIBLE);
                    }
                } else {
                    formFragment.message.setText("Field Is Empty");
                    formFragment.message.setVisibility(View.VISIBLE);
                }
            }
        });
        signUp.setOnClickListener(v -> {
            CheckEmailVerify verify = new CheckEmailVerify();
            verify.execute(formFragment.inputEmail.getText().toString());
            Toast.makeText(getActivity(), verify.getVerify() + "", Toast.LENGTH_SHORT).show();
            formFragment.inputPassword.setVisibility(View.VISIBLE);
            formFragment.inputLayout.setVisibility(View.VISIBLE);
            toggleReset = false;
            if (!toggle) {
                toggle = true;
                formFragment.inputLayout.setVisibility(View.VISIBLE);
            } else {
                if ((!Objects.requireNonNull(formFragment.inputEmail.getText()).toString().isEmpty()) && (!Objects.requireNonNull(formFragment.inputPassword.getText()).toString().isEmpty())
                        && (!Objects.requireNonNull(formFragment.inputRepeatPassword.getText()).toString().isEmpty())) {
                    if (new CheckEmail().ProviderEnable(formFragment.inputEmail.getText().toString())) {
                        if (new CheckEmail().EmailFormat(formFragment.inputEmail.getText().toString())) {
                            if (formFragment.inputPassword.getText().toString().equals(formFragment.inputRepeatPassword.getText().toString())) {
                                if (new CheckPassword().constrainLetterUpperCase(formFragment.inputPassword.getText().toString())) {
                                    if (new CheckPassword().constrainLetterLowCase(formFragment.inputPassword.getText().toString())) {
                                        if (new CheckPassword().checkLetterError(formFragment.inputPassword.getText().toString())) {
                                            if (new CheckPassword().constrainDigit(formFragment.inputPassword.getText().toString())) {
                                                if (new CheckPassword().constrainCharacterSpecial(formFragment.inputPassword.getText().toString())) {
                                                    if (formFragment.inputPassword.getText().toString().length() > 6) {
                                                        new Handler().postDelayed(() -> {
                                                            if (verify.getVerify().equals("true")) {
                                                                auth.createUserWithEmailAndPassword(formFragment.inputEmail.getText().toString(), formFragment.inputPassword.getText().toString()).addOnCompleteListener(task -> {
                                                                    if (task.isSuccessful()) {
                                                                        toggle = true;
                                                                        formFragment.inputRepeatPassword.setVisibility(View.INVISIBLE);
                                                                        reference.child("account" + formFragment.inputEmail.getText().toString().hashCode()).setValue(new Account(formFragment.inputEmail.getText().toString(), formFragment.inputPassword.getText().toString()));
                                                                        reference.child("list_account").push().setValue(new Person(1, formFragment.inputEmail.getText().toString()));
                                                                        Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        formFragment.message.setText("Account Exist");
                                                                        formFragment.message.setVisibility(View.VISIBLE);
                                                                    }
                                                                });
                                                            } else {
                                                                formFragment.message.setText("Email Not Exist");
                                                                formFragment.message.setVisibility(View.VISIBLE);
                                                            }
                                                        }, 3000);
                                                    } else {
                                                        formFragment.message.setText("Length Password Less Than 6 Character");
                                                        formFragment.message.setVisibility(View.VISIBLE);
                                                    }
                                                } else {
                                                    formFragment.message.setText("Password Not Constrain Letter @,#,!");
                                                    formFragment.message.setVisibility(View.VISIBLE);
                                                }
                                            } else {
                                                formFragment.message.setText("Password Not Constrain Digit 0-9");
                                                formFragment.message.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            formFragment.message.setText("Password Constrain Letter á,à,ă,Â .... ");
                                            formFragment.message.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        formFragment.message.setText("Password Not Constrain Letter a-z");
                                        formFragment.message.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    formFragment.message.setText("Password Not Constrain Letter A-Z");
                                    formFragment.message.setVisibility(View.VISIBLE);
                                }
                            } else {
                                formFragment.message.setText("RepeatPassword Different");
                                formFragment.message.setVisibility(View.VISIBLE);
                            }
                        } else {
                            formFragment.message.setText("Error Format Email");
                            formFragment.message.setVisibility(View.VISIBLE);
                        }
                    } else {
                        formFragment.message.setText("Domain Disable");
                        formFragment.message.setVisibility(View.VISIBLE);
                    }
                } else {
                    formFragment.message.setText("Field Is Empty");
                    formFragment.message.setVisibility(View.VISIBLE);
                }
            }
        });
        resetPassword.setOnClickListener(v -> {
            if (!toggleReset) {
                toggleReset = true;
                formFragment.inputLayout2.setVisibility(View.INVISIBLE);
                formFragment.inputRepeatPassword.setVisibility(View.INVISIBLE);
            } else {
                ///////////////////////////////////////////////////////////////
                if (!Objects.requireNonNull(formFragment.inputEmail.getText()).toString().isEmpty()) {
                    if (new CheckEmail().EmailFormat(formFragment.inputEmail.getText().toString())) {
                        if (new CheckEmail().ProviderEnable(formFragment.inputEmail.getText().toString())) {
                            auth.sendPasswordResetEmail(formFragment.inputEmail.getText().toString()).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Send!\n Please! Check Your Email", Toast.LENGTH_SHORT).show();
                                } else {
                                    formFragment.message.setText("Account Not Registered");
                                    formFragment.message.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            formFragment.message.setText("Provide Disable");
                            formFragment.message.setVisibility(View.VISIBLE);
                        }
                    } else {
                        formFragment.message.setText("Error Format Email");
                        formFragment.message.setVisibility(View.VISIBLE);
                    }
                } else {
                    formFragment.message.setText("Field Is Empty");
                    formFragment.message.setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }

    public static class CheckEmailVerify extends AsyncTask<String, Void, String> {
        private String verify = "";
        final String api = "031ac1cff643b7fc87cd85605cca722f";
        StringBuilder stringBuilder = new StringBuilder();

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("https://apilayer.net/api/check?access_key=" + api + "&email=" + strings[0] + "&smtp=1&format=1");
                InputStreamReader inputStreamReader = new InputStreamReader(url.openConnection().getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            try {
                JSONObject jsonObject = new JSONObject(string);
                verify = jsonObject.getBoolean("stmp_check") + "";
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }

        public String getVerify() {
            return verify;
        }
    }

}
