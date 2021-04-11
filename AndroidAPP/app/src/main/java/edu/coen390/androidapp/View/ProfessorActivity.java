package edu.coen390.androidapp.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.examapp.R;

import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Controller.SharedPreferenceHelper;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.Professor;
import edu.coen390.androidapp.Model.Source;

public class ProfessorActivity extends AppCompatActivity {
    public static final String COURSE_INTENT = "COURSE";
    public static final String COURSE_INTENT_PLUS_SOURCE = "COURSE_PLUS_SOURCE";
    private static final String TAG = "ProfessorActivity";

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
        generateReportButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfessorActivity.this, ProfessorReportActivity.class);
            intent.putExtra(COURSE_INTENT, course);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}