package application.tool.activity.message.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import application.tool.activity.message.fragment.ConversationFragment;
import application.tool.activity.message.fragment.ExtensionsFragment;
import application.tool.activity.message.fragment.HomeFragment;
import application.tool.activity.message.fragment.NotificationFragment;

public class ContentViewPagerAdapter extends FragmentStatePagerAdapter {
    public ContentViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 1:
                return new ConversationFragment();
            case 2:
                return new NotificationFragment();
            case 3:
                return new ExtensionsFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
