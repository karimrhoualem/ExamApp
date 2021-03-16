package edu.coen390.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.examapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class FaceRecognition extends AppCompatActivity {

    WebView myWebView;
    TextView personTextView;

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

        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest webResourceRequest) {
                view.loadUrl(webResourceRequest.getUrl().toString());
                return true;
            }
        });

        setContentView(myWebView);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        myWebView.loadUrl("http://192.168.2.135:5000/");

        try {
            setPersonTextView();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setPersonTextView() throws JSONException {
        personTextView =(TextView) findViewById(R.id.personTextView);
        String jsonData = "http://192.168.2.135:5000/person_info";
        JSONObject reader = new JSONObject(jsonData);
        JSONObject person = reader.getJSONObject("person");

        String info = "Student Name : " + person.getString("name")
                      + " Student ID :" + person.getString("ID");
        personTextView.setText(info);

    }


}