package application.tool.activity.message.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import application.tool.activity.message.R;
import application.tool.activity.message.object.Avatar;

public class UserFragment extends Fragment {
    public Button editAvatar, editBackground;
    public ImageView avatar, background;
    public TextView nameUser;
    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference reference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        nameUser = view.findViewById(R.id.name);
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();
        if (user != null) {
            if (user.getDisplayName() != null && (!user.getDisplayName().equals(""))) {
                nameUser.setText(user.getDisplayName());
            } else {
                nameUser.setText(user.getEmail());
            }
        }
        editAvatar = view.findViewById(R.id.editAvatar);
        editBackground = view.findViewById(R.id.editBackGround);
        avatar = view.findViewById(R.id.show_avatar);
        background = view.findViewById(R.id.show_label);
        initImage();
        return view;
    }

    private void initImage() {
        getImage("avatar");
        getImage("background");
    }

    public void getImage(String type) {
        if (type.equals("avatar")) {
            new Avatar(Objects.requireNonNull(user.getEmail()), type).setAvatar(avatar);
        } else if (type.equals("background")) {
            new Avatar(Objects.requireNonNull(user.getEmail()), type).setAvatar(background);
        }
    }
}
