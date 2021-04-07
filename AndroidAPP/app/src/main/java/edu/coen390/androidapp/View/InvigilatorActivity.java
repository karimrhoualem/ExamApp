package edu.coen390.androidapp.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.examapp.R;

import java.io.Serializable;

import edu.coen390.androidapp.Controller.Config;
import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Model.Course;

public class InvigilatorActivity extends AppCompatActivity implements Serializable {
    public static final String COURSE_INTENT = "COURSE";
    private static final String TAG = "VerificationMode";

    private Course course;
    private Button facialRecognitionButton;
    private Button cardScannerButton;
    private Button manualVerification;
    private Button generateReportButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invigilator);

        Intent intent = getIntent();
        course = (Course) intent.getSerializableExtra(COURSE_INTENT);
        Log.d(TAG, "After getIntent " + course.toString());
        createExamTable();

        setupUI();
    }

    private void setupUI() {
        facialRecognitionButton = findViewById(R.id.facialRecognitionButton);
        cardScannerButton = findViewById(R.id.cardScannerButton);
        manualVerification = findViewById(R.id.manualVerificationButton);
        generateReportButton = findViewById(R.id.generateReportButton);

        facialRecognitionButton.setOnClickListener(v -> {
            Intent intent = new Intent(InvigilatorActivity.this, LiveFeedActivity.class);
            intent.putExtra(COURSE_INTENT, course);
            startActivity(intent);
        });

        cardScannerButton.setOnClickListener(v -> {
            Intent intent = new Intent(InvigilatorActivity.this, CardScanActivity.class);
            intent.putExtra(COURSE_INTENT, course);
            startActivity(intent);
        });

        manualVerification.setOnClickListener(v -> {
            Intent intent = new Intent(InvigilatorActivity.this, ManualVerification.class);
            intent.putExtra(COURSE_INTENT, course);
            startActivity(intent);
        });

        generateReportButton.setOnClickListener(v -> {
            Intent intent = new Intent(InvigilatorActivity.this, InvigilatorReportActivity.class);
            intent.putExtra(COURSE_INTENT, course);
            startActivity(intent);
        });
    }

    private void createExamTable() {
        String courseExamTableName = course.getCode().
                replaceAll("\\s+", "") + "Exam";
        Config.EXAM_TABLE_NAME.put(course.getCode(), courseExamTableName);
        dbHelper = new DatabaseHelper(this);
        dbHelper.createExamTable(course);
    }
}