package edu.coen390.androidapp;

import androidx.appcompat.app.AppCompatActivity;

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
        Webview();
    }

    private void Webview (){
        myWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView = new WebView(FaceRecognition.this);
        setContentView(myWebView);
        myWebView.loadUrl("https://www.google.com");

    }
}