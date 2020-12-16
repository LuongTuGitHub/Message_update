package application.tool.activity.message.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import application.tool.activity.message.R;
import application.tool.activity.message.adapter.ItemOnClickListener;
import application.tool.activity.message.adapter.NotificationAdapter;
import application.tool.activity.message.object.Notification;

import static application.tool.activity.message.module.Firebase.NOTIFICATION;
import static application.tool.activity.message.module.Notification.MESSAGE;
import static application.tool.activity.message.module.Notification.REQUEST;
import static application.tool.activity.message.module.Notification.RESPONSE;

public class NotificationActivity extends AppCompatActivity implements ItemOnClickListener {
    private ArrayList<Notification> notifications;
    private NotificationAdapter adapter;
    private DatabaseReference refDb;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(notifications, this);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();

        Button btSearch = findViewById(R.id.bt_search);
        RecyclerView rvShowNotification = findViewById(R.id.rvShowNotification);
        Button bt_exit = findViewById(R.id.bt_exit_notification);

        rvShowNotification.setLayoutManager(manager);
        rvShowNotification.setAdapter(adapter);

        btSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, ContentFindActivity.class);
            startActivity(intent);
        });
        bt_exit.setOnClickListener(v -> finish());

        loadNotification();
    }

    private void loadNotification() {
        refDb.child(NOTIFICATION).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    Notification notification = snapshot.getValue(Notification.class);
                    if (notification != null) {
                        notifications.add(notification);
                        adapter.notifyDataSetChanged();
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
        switch (notifications.get(position).getType()) {
            case RESPONSE:
            case REQUEST: {
                Intent intent = new Intent(this, ViewProfileActivity.class);
                intent.putExtra("email", notifications.get(position).getFrom());
                intent.putExtra("status", false);
                startActivity(intent);
                break;
            }
            case MESSAGE: {
                Intent intent = new Intent(this, ConversationActivity.class);
                intent.putExtra("key", notifications.get(position).getKey());
                intent.putExtra("status", false);
                startActivity(intent);
                break;
            }
        }

    }
}