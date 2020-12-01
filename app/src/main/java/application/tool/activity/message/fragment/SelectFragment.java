package application.tool.activity.message.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
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
import androidx.annotation.RequiresApi;

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

import application.tool.activity.message.CreateQrCodeActivity;
import application.tool.activity.message.EditActivity;
import application.tool.activity.message.MainActivity;
import application.tool.activity.message.R;
import application.tool.activity.message.ScanQrCodeActivity;
import application.tool.activity.message.ViewProfileActivity;
import application.tool.activity.message.adapter.SelectAdapter;
import application.tool.activity.message.list.SelectList;
import application.tool.activity.message.object.Person;
import application.tool.activity.message.object.Select;
import application.tool.activity.message.sqlite.AccountShare;

public class SelectFragment extends Fragment {
    public ListView list;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;
    UserFragment userFragment;
    public ArrayList<String> listAccount;
    public ArrayList<String> listFriend;
    public ArrayList<Select> arrayList;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
        arrayList = new SelectList().getList();
        SelectAdapter adapter = new SelectAdapter(arrayList);
        list.setAdapter(adapter);
        return view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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


}
