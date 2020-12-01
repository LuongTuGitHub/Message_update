package application.tool.activity.message.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import application.tool.activity.message.R;
import application.tool.activity.message.alert.ConversationAlert;
import application.tool.activity.message.object.Avatar;
import application.tool.activity.message.object.Profile;

public class ToolbarMessageFragment extends Fragment {
    public Button back;
    Button showProfile;
    public ImageView image;
    TextView name;
    String person;
    String key;
    FirebaseDatabase database;
    DatabaseReference reference;
    private Profile profile;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_toolbar_message, container, false);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        back = view.findViewById(R.id.button2);
        image = view.findViewById(R.id.avatarPerson);
        name = view.findViewById(R.id.namePersonMessage);
        showProfile = view.findViewById(R.id.selectDialog);
        person = getActivity().getIntent().getStringExtra("person");
        key = getActivity().getIntent().getStringExtra("key");
        if (person != null) {
            if (!person.equals("Group Chat")) {
                reference.child("profile" + person.hashCode()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            profile = snapshot.getValue(Profile.class);
                            assert profile != null;
                            name.setText(profile.getName());
                        } else {
                            name.setText(person);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else {
                image.setBackgroundResource(R.drawable.teamwork);
            }
        }
        showProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!person.equals("Group Chat")){
                    new ConversationAlert(getActivity()).getAlertForCouple(person).show();
                }
            }
        });

        image.setOnClickListener(v -> {
            if (!person.equals("Group Chat")) {
                View profileDialog = LayoutInflater.from(getActivity()).inflate(R.layout.alert_profile, null);
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(getActivity());
                aBuilder.setView(profileDialog);
                TextView namePerson = profileDialog.findViewById(R.id.personName);
                ImageView avatarPerson = profileDialog.findViewById(R.id.personAvatar);
                ImageView backgroundPerson = profileDialog.findViewById(R.id.personBackground);
                new Avatar(person, "avatar").setAvatar(avatarPerson);
                new Avatar(person, "background").setAvatar(backgroundPerson);
                final AlertDialog alertDialog = aBuilder.create();
                if (profile != null) {
                    namePerson.setText(profile.getName());
                } else {
                    namePerson.setText(person);
                }
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                profileDialog.findViewById(R.id.closeDialog).setOnClickListener(v1 -> alertDialog.dismiss());
                alertDialog.show();
            }else {
                AlertDialog.Builder alertChangeNameGroup = new AlertDialog.Builder(getActivity());
                View alert = LayoutInflater.from(getActivity()).inflate(R.layout.alert_change_name_group,null);
                alertChangeNameGroup.setView(alert);
                final AlertDialog  dialog = alertChangeNameGroup.create();
                EditText inputName = alert.findViewById(R.id.enterName);
                Button confirmChange = alert.findViewById(R.id.confirmChange);
                confirmChange.setOnClickListener(v12 -> {
                    if((inputName.getText().toString().length()>6)&&(inputName.getText().toString().length()<30)){
                        reference.child("conversation/"+key+"/name").setValue(inputName.getText().toString());
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        loadNameGroupIfExist();
        return view;
    }
    public void loadNameGroupIfExist(){
        reference.child("conversation/"+key+"/name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(!snapshot.getValue().toString().equals("")){
                        name.setText(snapshot.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
