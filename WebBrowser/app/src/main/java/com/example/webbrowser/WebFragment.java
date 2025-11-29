package com.example.webbrowser;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebFragment extends Fragment {

    private WebView webView;

    private static final String ARG_URL = "arg_url";
    private static final String KEY_CURRENT_URL = "current_url";

    private String initialUrl;
    private String currentUrl;

    public WebFragment() {}

    public static WebFragment newInstance(String url) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();

        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            initialUrl = getArguments().getString(ARG_URL);
        if (savedInstanceState != null)
            currentUrl = savedInstanceState.getString(KEY_CURRENT_URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_web, container, false);

        webView = root.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                currentUrl = url;
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String urlToLoad = (currentUrl != null) ? currentUrl : initialUrl;
        if (urlToLoad != null)
            webView.loadUrl(urlToLoad);
        else
            webView.loadUrl("https://www.google.com.tr");
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (webView != null && currentUrl != null)
            outState.putString(KEY_CURRENT_URL, currentUrl);
        else if (initialUrl != null)
            outState.putString(KEY_CURRENT_URL, initialUrl);

    }

    public void loadUrl(String url) {
        if (webView == null || url == null || url.isEmpty()) return;
        currentUrl = url;
        webView.loadUrl(url);
    }
}