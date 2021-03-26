package edu.coen390.androidapp.View;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import com.example.examapp.R;

import edu.coen390.androidapp.Controller.DatabaseHelper;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private WebView myWebView;
    private Button biometricRecognition;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "Creating DB helper object.");

        dbHelper = new DatabaseHelper(this);
        dbHelper.testMethod(); // Test method. Must call getWritableDatabase() or getReadableDatabase() for DB to be created.

        Log.d(TAG, "DB helper object created.");

        setupUI();
    }

    private void setupUI (){
        biometricRecognition = (Button) findViewById(R.id.BiometricRecognition);
        biometricRecognition.setOnClickListener(v -> openFaceRecognitionActivity());
    }

    private void openFaceRecognitionActivity() {
        Intent intent = new Intent (this, LiveFeedActivity.class);
        intent.putExtra("invigilator_id",1);
        startActivity(intent);
    }
}

