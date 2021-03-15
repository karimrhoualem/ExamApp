package edu.coen390.androidapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import com.example.examapp.R;


public class MainActivity extends AppCompatActivity {
    WebView myWebView;
    Button biometricRecognition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
    }

    private void setupUI (){
        biometricRecognition = (Button) findViewById(R.id.BiometricRecognition);
        biometricRecognition.setOnClickListener(v -> openFaceRecognitionActivity());
    }

    private void openFaceRecognitionActivity() {
        Intent intent = new Intent (this, FaceRecognition.class);
        startActivity(intent);
    }
}

