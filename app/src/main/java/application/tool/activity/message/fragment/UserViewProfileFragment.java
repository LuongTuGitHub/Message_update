package application.tool.activity.message.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import application.tool.activity.message.R;
import application.tool.activity.message.object.Avatar;

public class UserViewProfileFragment extends Fragment {
    FirebaseUser user;
    ImageView background, avatar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_view_profile, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        background = view.findViewById(R.id.viewBackgroundProfile);
        avatar = view.findViewById(R.id.viewAvatar);
        new Avatar(user.getEmail(), "background").setAvatar(background);
        new Avatar(user.getEmail(), "avatar").setAvatar(avatar);
        return view;
    }
}
