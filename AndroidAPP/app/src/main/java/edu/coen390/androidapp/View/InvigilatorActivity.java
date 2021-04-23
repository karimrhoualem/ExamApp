package edu.coen390.androidapp.View;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.Serializable;
import edu.coen390.androidapp.Controller.Config;
import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Controller.SharedPreferenceHelper;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.Source;
import edu.coen390.androidapp.R;

public class InvigilatorActivity extends AppCompatActivity implements Serializable {
    public static final String COURSE_INTENT = "COURSE";
    public static final String COURSE_INTENT_PLUS_SOURCE = "COURSE_PLUS_SOURCE";
    private static final String TAG = "VerificationMode";

    private Course course;
    private Button facialRecognitionButton;
    private Button cardScannerButton;
    private Button manualVerification;
    private Button generateReportButton;
    private DatabaseHelper dbHelper;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private ConstraintLayout invigilatorConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invigilator);

        Log.d(TAG, "Oncreate called in " + getLocalClassName());

        sharedPreferenceHelper = new SharedPreferenceHelper(this);

        Intent intent = getIntent();

        String source = sharedPreferenceHelper.getSource();
        if (source != "") {
            if (source.equals(Source.INVIGILATORREPORT_ACTIVITY.name()) ||
                source.equals(Source.CARDSCAN_ACTIVITY.name()) ||
                source.equals(Source.LIVEFEED_ACTIVITY.name()) ||
                source.equals(Source.MANUALVERIFICATION_ACTIVITY.name()))
            {
                String courseCode = sharedPreferenceHelper.getCourseCode();
                Course tempCourse = new Course();
                tempCourse.setCode(courseCode);
                course = sharedPreferenceHelper.getProfile(tempCourse);
            }
            else {
                course = (Course) intent.getSerializableExtra(COURSE_INTENT);
            }
        }
        else {
            course = (Course) intent.getSerializableExtra(COURSE_INTENT);
        }

        Course retrievedCourse = sharedPreferenceHelper.getProfile(course);
        if (retrievedCourse != null) {
            course = retrievedCourse;
        }

        createExamTable();

        setupUI();
    }

    private void setupUI() {
        facialRecognitionButton = findViewById(R.id.facialRecognitionButton);
        cardScannerButton = findViewById(R.id.cardScannerButton);
        manualVerification = findViewById(R.id.manualVerificationButton);
        generateReportButton = findViewById(R.id.generateReportButton);
        invigilatorConstraintLayout = findViewById(R.id.invigilatorConstraintLayout);
        displayCourseInfo();

        facialRecognitionButton.setOnClickListener(v -> {
            Intent intent = new Intent(InvigilatorActivity.this,
                    LiveFeedActivity.class);
            intent.putExtra(COURSE_INTENT, course);
            startActivity(intent);
        });

        cardScannerButton.setOnClickListener(v -> {
            Intent intent = new Intent(InvigilatorActivity.this,
                    CardScanActivity.class);
            intent.putExtra(COURSE_INTENT, course);
            startActivity(intent);
        });

        manualVerification.setOnClickListener(v -> {
            Intent intent = new Intent(InvigilatorActivity.this,
                    ManualVerificationActivity.class);
            intent.putExtra(COURSE_INTENT, course);
            startActivity(intent);
        });

        generateReportButton.setOnClickListener(v -> {
            Intent intent = new Intent(InvigilatorActivity.this,
                    InvigilatorReportActivity.class);
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

    private void displayCourseInfo() {
        TextView courseTextView = new TextView(InvigilatorActivity.this);
        courseTextView.setText("Course: " + '\n' + course.getTitle() + " - " + course.getCode());
        courseTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        courseTextView.setTypeface(null, Typeface.BOLD);
        courseTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f);
        invigilatorConstraintLayout.addView(courseTextView);
    }
}