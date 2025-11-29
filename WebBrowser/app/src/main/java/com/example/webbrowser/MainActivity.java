package com.example.webbrowser;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    DynamicTabAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        editText = findViewById(R.id.url);

        adapter = new DynamicTabAdapter(this);
        viewPager.setAdapter(adapter);

        openNewTab("https://google.com.tr");
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(adapter.getTitle(position))).attach();
        findViewById(R.id.newTab).setOnClickListener(v -> openNewTab("https://google.com.tr"));

        findViewById(R.id.go).setOnClickListener(v -> {
            String url = editText.getText().toString();
            if (url.isEmpty()) return;
            int current = viewPager.getCurrentItem();
            Fragment fragment = adapter.getTab(current);
            if (fragment instanceof WebFragment) {
                ((WebFragment) fragment).loadUrl(url);
                adapter.updateTitle(current, extractDomainFromUrl(url));
                TabLayout.Tab tab = tabLayout.getTabAt(current);
                if (tab != null)
                    tab.setText(adapter.getTitle(current));
            }
        });
    }

    private void openNewTab(String url) {
        String title = extractDomainFromUrl(url);
        adapter.addTab(WebFragment.newInstance(url), title);
        viewPager.setCurrentItem(adapter.getItemCount() - 1, true);
    }

    private String extractDomainFromUrl(String url) {
        int schemeIndex = url.indexOf("://");
        if (schemeIndex != -1)
            url = url.substring(schemeIndex + 3);
        int dotIndex = url.indexOf('.');
        if (dotIndex != -1)
            return url.substring(0, dotIndex);
        return "New Tab";
    }
}