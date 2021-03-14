package edu.coen390.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.examapp.R;

public class FaceRecognition extends AppCompatActivity {

    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);
        webView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void webView(){
        myWebView = (WebView) findViewById(R.id.webView);
        myWebView = new WebView(FaceRecognition.this);
        setContentView(myWebView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        myWebView.loadUrl("http://192.168.2.135:5000/");

    }
}