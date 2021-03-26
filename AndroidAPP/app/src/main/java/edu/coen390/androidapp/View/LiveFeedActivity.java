package edu.coen390.androidapp.View;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.examapp.R;

import edu.coen390.androidapp.Model.Course;

public class LiveFeedActivity extends AppCompatActivity {

    public static final String KEY_URL_TO_LOAD = "KEY_URL_TO_LOAD";
    private static final String TAG = "LiveFeedActivity";


    @VisibleForTesting
    protected static final String WEB_FORM_URL = "http://www.google.com";

    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_feed);
        launchWebView();

        Intent intent = getIntent();
        Course course = (Course)intent.getSerializableExtra("selected_course");
        Log.d(TAG,"after getIntent "+ course);
    }

    private void launchWebView(){
        myWebView = (WebView) findViewById(R.id.webView);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        myWebView.loadUrl(WEB_FORM_URL);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
    }
}