package com.example.webbrowser;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class DynamicTabAdapter extends FragmentStateAdapter {
    private final List<Fragment> pages = new ArrayList<>();
    private final List<String> titles = new ArrayList<>();

    public DynamicTabAdapter(@NonNull FragmentActivity activity) { super(activity); }

    public void addTab(@NonNull Fragment fragment, @NonNull String title) {
        pages.add(fragment);
        titles.add(title);
        notifyItemInserted(pages.size() - 1);
    }

    public String getTitle(int pos) { return titles.get(pos); }

    public Fragment getTab(int pos) { return pages.get(pos); }

    public void updateTitle(int pos, String title) { titles.set(pos, title); }

    @NonNull
    @Override
    public Fragment createFragment(int pos) { return pages.get(pos); }

    @Override
    public int getItemCount() { return pages.size(); }
}
