package edu.coen390.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.examapp.R;


public class MainActivity<RequestQueue> extends AppCompatActivity {


    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView = new WebView(MainActivity.this);
        setContentView(myWebView);

        myWebView.loadUrl("https://www.google.com");
    }


}

