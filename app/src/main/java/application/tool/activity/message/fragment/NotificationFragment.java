package application.tool.activity.message.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import application.tool.activity.message.R;
import application.tool.activity.message.activity.ContentFindActivity;
import application.tool.activity.message.adapter.ItemOnClickListener;
import application.tool.activity.message.object.Notification;

import static application.tool.activity.message.module.Firebase.NOTIFICATION;

public class NotificationFragment extends Fragment implements ItemOnClickListener {
    private Button btSearch;
    private ArrayList<Notification> notifications;
    private DatabaseReference refDb;
    private FirebaseUser fUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        notifications = new ArrayList<>();

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();

        btSearch = view.findViewById(R.id.bt_search);
        btSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ContentFindActivity.class);
            startActivity(intent);
        });
        loadNotification();
        return view;
    }

    private void loadNotification() {
        refDb.child(NOTIFICATION).child(fUser.getEmail().hashCode()+"").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getValue()!=null){
                    Notification notification = snapshot.getValue(Notification.class);
                    if(notification!=null){
                        notifications.add(notification);


                        
                    }

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
    }

    @Override
    public void onClickItem(View view, int position) {

    }

}