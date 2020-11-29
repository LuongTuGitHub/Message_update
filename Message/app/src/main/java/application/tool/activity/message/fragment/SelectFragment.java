package application.tool.activity.message.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import application.tool.activity.message.EditActivity;
import application.tool.activity.message.MainActivity;
import application.tool.activity.message.R;
import application.tool.activity.message.ViewProfileActivity;
import application.tool.activity.message.adapter.SelectAdapter;
import application.tool.activity.message.list.SelectList;
import application.tool.activity.message.object.Person;
import application.tool.activity.message.object.Select;
import application.tool.activity.message.sqlite.AccountShare;

public class SelectFragment extends Fragment {
    private final static int UPDATE_PROFILE = 20;
    ListView list;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;
    UserFragment userFragment;
    ArrayList<String> listAccount;
    ArrayList<String> listFriend;

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select, container, false);
        list = view.findViewById(R.id.show_select);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        userFragment = (UserFragment) getActivity().getFragmentManager().findFragmentById(R.id.fragment3);
        user = auth.getCurrentUser();
        listFriend = getListFriend();
        listAccount = getListAccount();
        ArrayList<Select> arrayList = new SelectList().getList();
        SelectAdapter adapter = new SelectAdapter(arrayList);
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view1, position, id) -> {
            switch (arrayList.get(position).getId()) {
                case R.drawable.ic_baseline_exit_to_app_24:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Log Out").setPositiveButton("Confirm", (dialog, which) -> {
                        auth.signOut();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        new AccountShare(getActivity()).dropAccount();
                        startActivity(intent);
                        getActivity().finish();
                    }).setNegativeButton("Cancel", (dialog, which) -> {
                    });
                    builder.create().show();
                    break;
                case R.drawable.ic_baseline_person_24:
                    Intent toViewProfile = new Intent(getActivity(), ViewProfileActivity.class);
                    startActivity(toViewProfile);
                    break;
                case R.drawable.ic_baseline_edit_24:
                    Intent toEditProfile = new Intent(getActivity(), EditActivity.class);
                    startActivityForResult(toEditProfile, UPDATE_PROFILE);
                    break;
                case R.drawable.ic_baseline_person_add_24:
                    View viewAddFriend = LayoutInflater.from(getActivity()).inflate(R.layout.alert_add_friend, null);
                    AlertDialog.Builder alertAddFriend = new AlertDialog.Builder(getActivity());
                    alertAddFriend.setView(viewAddFriend);
                    TextInputEditText person = viewAddFriend.findViewById(R.id.namePerson);
                    Button check = viewAddFriend.findViewById(R.id.check);
                    TextView status = viewAddFriend.findViewById(R.id.showStatus);
                    check.setOnClickListener(v -> {
                        if (!Objects.requireNonNull(person.getText()).toString().equals("")) {
                            if (checkAccount(person.getText().toString())) {
                                reference.child("friend" + Objects.requireNonNull(user.getEmail()).hashCode()).push().setValue(new Person(1, person.getText().toString()));
                                reference.child("friend" + person.getText().toString().hashCode()).push().setValue(new Person(1, user.getEmail()));
                                Toast.makeText(getActivity(), "Success !", Toast.LENGTH_SHORT).show();
                            } else {
                                status.setVisibility(View.VISIBLE);
                                status.setText("Account Not Exist Or Is Friend Or Your Self");
                            }
                        } else {
                            status.setVisibility(View.VISIBLE);
                            status.setText("Field Is Empty");
                        }
                    });
                    alertAddFriend.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                    alertAddFriend.create().show();
                    break;
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_PROFILE) {
            if (data != null) {
                if (user != null) {
                    if (user.getDisplayName() != null && (!user.getDisplayName().equals(""))) {
                        userFragment.nameUser.setText(user.getDisplayName());
                    } else {
                        userFragment.nameUser.setText(user.getEmail());
                    }
                }
            }
        }
    }

    public ArrayList<String> getListAccount() {
        ArrayList<String> arrayList = new ArrayList<>();
        reference.child("list_account").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue(Person.class) != null) {
                    Person person = snapshot.getValue(Person.class);
                    assert person != null;
                    arrayList.add(person.getEmail());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return arrayList;
    }

    private ArrayList<String> getListFriend() {
        ArrayList<String> list = new ArrayList<>();
        reference.child("friend" + Objects.requireNonNull(user.getEmail()).hashCode()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue(Person.class) != null) {
                    Person person = snapshot.getValue(Person.class);
                    assert person != null;
                    list.add(person.getEmail());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list;
    }


    private boolean checkAccount(String email) {
        ArrayList<String> arrayList = listAccount;
        ArrayList<String> list = listFriend;
        if (Objects.equals(user.getEmail(), email)) {
            return false;
        } else {
            int count = 0;
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).equals(email)) {
                    for (int j = 0; j < list.size(); j++) {
                        if (list.get(j).equals(email)) {
                            return false;
                        } else {
                            count++;
                        }
                    }
                    return count == list.size();
                }
            }
        }
        return false;
    }

}
