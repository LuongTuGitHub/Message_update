package application.tool.activity.message.fragment;

import android.app.Fragment;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import application.tool.activity.message.R;
import application.tool.activity.message.adapter.SelectAdapter;
import application.tool.activity.message.object.Profile;
import application.tool.activity.message.object.Select;

public class InformationFragment extends Fragment {
    ArrayList<Select> arrayList;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ListView list = view.findViewById(R.id.listInformation);
        this.arrayList = new ArrayList<>();
        SelectAdapter adapter = new SelectAdapter(arrayList);
        list.setAdapter(adapter);
        /***
         * Profile profile = snapshot.getValue(Profile.class);
         *                 if(profile!=null){
         *                     arrayList.add(new Select(R.drawable.ic_baseline_person_24,profile.getName()));
         *                     arrayList.add(new Select(R.drawable.ic_baseline_add_location_alt_24,profile.getAddress()));
         *                     arrayList.add(new Select(R.drawable.ic_baseline_email_24,user.getEmail()));
         *                     arrayList.add(new Select(R.drawable.ic_baseline_calendar_today_24,profile.getDay()));
         *                     adapter.notifyDataSetChanged();
         *                 }
         */
        reference.child("profile" + Objects.requireNonNull(user.getEmail()).hashCode()).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Profile profile = snapshot.getValue(Profile.class);
                if (profile != null) {
                    arrayList.add(new Select(R.drawable.ic_baseline_person_24, profile.getName()));
                    String year = profile.getDay().substring(profile.getDay().length() - 4);
                    arrayList.add(new Select(R.drawable.age, Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(year) + " Year Old"));
                    arrayList.add(new Select(R.drawable.ic_baseline_location_on_24, profile.getAddress()));
                    arrayList.add(new Select(R.drawable.email_view_profile, user.getEmail()));
                    arrayList.add(new Select(R.drawable.ic_baseline_calendar_today_24, profile.getDay()));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}
