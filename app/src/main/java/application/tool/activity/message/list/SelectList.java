package application.tool.activity.message.list;

import java.util.ArrayList;

import application.tool.activity.message.R;
import application.tool.activity.message.object.Select;

public class SelectList {
    public ArrayList<Select> getList() {
        ArrayList<Select> arrayList = new ArrayList<>();
        arrayList.add(new Select(R.drawable.ic_baseline_person_24, "View Profile"));
        arrayList.add(new Select(R.drawable.ic_baseline_edit_24, "Edit Profile"));
        arrayList.add(new Select(R.drawable.ic_baseline_person_add_24, "Add Friend"));
        arrayList.add(new Select(R.drawable.create,"Create Qr Code"));
        arrayList.add(new Select(R.drawable.ic_baseline_sms_24, "SMS"));
        arrayList.add(new Select(R.drawable.ic_baseline_qr_code_scanner_24, "Scan Qr Code"));
        arrayList.add(new Select(R.drawable.ic_baseline_library_music_24, "Library Music"));
        arrayList.add(new Select(R.drawable.ic_baseline_exit_to_app_24, "Log Out"));
        return arrayList;
    }
}
