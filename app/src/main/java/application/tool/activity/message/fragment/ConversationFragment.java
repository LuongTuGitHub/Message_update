package application.tool.activity.message.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import application.tool.activity.message.activity.AddConversationActivity;
import application.tool.activity.message.activity.ContentActivity;
import application.tool.activity.message.activity.ContentFindActivity;
import application.tool.activity.message.activity.ConversationActivity;
import application.tool.activity.message.adapter.ConversationAdapter;
import application.tool.activity.message.adapter.ItemOnClickListener;
import application.tool.activity.message.object.Conversation;
import application.tool.activity.message.object.PersonInConversation;

import static application.tool.activity.message.module.Firebase.CONVERSATION;

public class ConversationFragment extends Fragment implements ItemOnClickListener {
    public Button search, btAdd;
    private View view;
    private ArrayList<String> conversations;
    private DatabaseReference refDb;
    private FirebaseUser fUser;
    private ConversationAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_conversation, container, false);
        Init();
        search.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(getActivity(), ContentFindActivity.class);
            startActivity(intent);
        });
        btAdd.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddConversationActivity.class);
            startActivity(intent);
        });
        loadConversation();
        return view;
    }

    private void Init() {
        conversations = new ArrayList<>();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        btAdd = view.findViewById(R.id.btAddConversation);
        refDb = FirebaseDatabase.getInstance().getReference();
        search = view.findViewById(R.id.btSearchConversation);
        RecyclerView recyclerView = view.findViewById(R.id.rvListConversation);
        adapter = new ConversationAdapter(conversations, this);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    public void loadConversation() {
        refDb.child(CONVERSATION).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    Conversation conversation = snapshot.getValue(Conversation.class);
                    assert conversation != null;
                    for (PersonInConversation person : conversation.getPersons()) {
                        if (person.getEmail().equals(fUser.getEmail())) {
                            conversations.add(snapshot.getKey());
                            adapter.notifyDataSetChanged();
                        }
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
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        intent.putExtra("status", false);
        intent.putExtra("key", conversations.get(position));

        startActivity(intent);
    }
}