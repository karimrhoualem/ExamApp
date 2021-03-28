package edu.coen390.androidapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.examapp.R;

import java.io.Serializable;

import edu.coen390.androidapp.Controller.Config;
import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Model.Course;

public class VerificationModeActivity extends AppCompatActivity implements Serializable {

    public static final String COURSE_INTENT = "COURSE";
    private static final String TAG = "VerificationMode";

    private Course course;
    private Button facialRecognitionButton;
    private Button cardScannerButton;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_mode);

        facialRecognitionButton = findViewById(R.id.facialRecognitionButton);
        cardScannerButton = findViewById(R.id.cardScannerButton);

        Intent intent = getIntent();
        course = (Course)intent.getSerializableExtra(COURSE_INTENT);
        Log.d(TAG,"After getIntent "+ course.toString());
        createExamTable();

        setupUI();
    }

    private void setupUI() {
        facialRecognitionButton.setOnClickListener(v -> {
            Intent intent = new Intent(VerificationModeActivity.this, LiveFeedActivity.class);
            intent.putExtra(COURSE_INTENT, course);
            startActivity(intent);
        });

        cardScannerButton.setOnClickListener(v -> {
            // TODO: empty for now. To be completed in sprint 3 when we receive the card reader.
        });
    }

    private void createExamTable(){

        String courseExamTableName = course.getCode().
                replaceAll("\\s+", "") + "Exam";
        Config.EXAM_TABLE_NAME.put(course.getCode(), courseExamTableName);
        dbHelper = new DatabaseHelper(this);
        dbHelper.createExamTable(course);

    }
}