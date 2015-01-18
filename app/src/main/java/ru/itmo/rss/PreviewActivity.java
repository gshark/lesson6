package ru.itmo.rss;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class PreviewActivity extends Activity {
    public static final String PREVIEW_URL = "PREVIEW_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        String url = getIntent().getStringExtra(PREVIEW_URL);
        WebView webView = (WebView) findViewById(R.id.preview);
        webView.loadUrl(url);
    }
}