package application.tool.activity.message.get;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import application.tool.activity.message.object.Account;

public class QRCode {
    Account account;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser user;


    public QRCode(Account acc) {
        this.account = acc;
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
    }

    public String getStringLogin() {
        String email = "";
        String password = "";
        if (account != null) {
            email = account.getEmail();
            password = account.getPassword();
        }
        return "log:" + email + ":" + password;
    }

    public String getStringAddFriend() {
        String email = "";
        if (account != null) {
            email = account.getEmail();
        }
        return "add:" + email;
    }

}
