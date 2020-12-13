package application.tool.activity.message.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import application.tool.activity.message.R;
import application.tool.activity.message.activity.LoginActivity;
import application.tool.activity.message.adapter.ExtensionAdapter;
import application.tool.activity.message.adapter.ItemOnClickListener;
import application.tool.activity.message.object.Extension;

import static application.tool.activity.message.module.Firebase.STATUS;

public class ExtensionsFragment extends Fragment implements ItemOnClickListener {
    private ArrayList<Extension> extensions;
    private RecyclerView rvEx;
    private FirebaseAuth fAuth;
    private FirebaseUser fUser;
    private DatabaseReference refDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_extensions, container, false);

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        extensions = new ArrayList<>();
        extensions.add(new Extension(R.drawable.padlock));
        extensions.add(new Extension(R.drawable.create));
        extensions.add(new Extension(R.drawable.ic_baseline_qr_code_24));
        extensions.add(new Extension(R.drawable.song));
        extensions.add(new Extension(R.drawable.logout));
        rvEx = view.findViewById(R.id.rvExtension);
        ExtensionAdapter adapter = new ExtensionAdapter(extensions, this);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
        rvEx.setLayoutManager(manager);
        rvEx.setAdapter(adapter);
        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClickItem(View view, int position) {
        switch (extensions.get(position).getID()) {
            case R.drawable.logout:
                refDb.child(STATUS).child(fUser.getEmail().hashCode() + "")
                        .setValue("offline")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                            }
                        });
                break;
            case R.drawable.create:
                break;
        }
    }
}