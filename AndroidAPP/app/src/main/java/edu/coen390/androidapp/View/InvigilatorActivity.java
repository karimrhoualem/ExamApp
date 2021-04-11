package edu.coen390.androidapp.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

import edu.coen390.androidapp.Controller.Config;
import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.R;

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
        if(course != null)
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
            Intent intent = new Intent(InvigilatorActivity.this, ManualVerificationActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}