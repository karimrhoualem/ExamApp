package edu.coen390.androidapp.View;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import edu.coen390.androidapp.Controller.SharedPreferenceHelper;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.Source;
import edu.coen390.androidapp.R;

public class ProfessorActivity extends AppCompatActivity {
    public static final String COURSE_INTENT = "COURSE";
    public static final String COURSE_INTENT_PLUS_SOURCE = "COURSE_PLUS_SOURCE";
    private static final String TAG = "ProfessorActivity";
    private ConstraintLayout constraintLayout;

    private Course course;
    private Button generateReportButton;
    private SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);
        sharedPreferenceHelper = new SharedPreferenceHelper(this);

        Intent intent = getIntent();

        String source = sharedPreferenceHelper.getSource();
        if (source != "") {
            if (source.equals(Source.PROFESSORREPORT_ACTIVITY.name()))
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

        setupUI();
    }

    private void setupUI() {
        generateReportButton = findViewById(R.id.professorGenerateReportButton);
        constraintLayout = findViewById(R.id.professorConstraintLayout);
        displayCourseInfo();
        generateReportButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfessorActivity.this, ProfessorReportActivity.class);
            intent.putExtra(COURSE_INTENT, course);
            startActivity(intent);
        });
    }

    private void displayCourseInfo() {
        TextView courseTextView = new TextView(ProfessorActivity.this);
        courseTextView.setText("Course: " + '\n' + course.getTitle() + " - " + course.getCode());
        courseTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        courseTextView.setTypeface(null, Typeface.BOLD);
        courseTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f);
        constraintLayout.addView(courseTextView);
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