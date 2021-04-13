package edu.coen390.androidapp.View;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Controller.SharedPreferenceHelper;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.ReportRow;
import edu.coen390.androidapp.Model.Source;
import edu.coen390.androidapp.Model.Student;
import edu.coen390.androidapp.R;

public class ProfessorReportActivity extends AppCompatActivity {
    public static final String REPORT_INTENT = "REPORT_INTENT";
    private Course course;
    private TableLayout tableLayout;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_report);

        Intent intent = getIntent();
        course = (Course) intent.getSerializableExtra(ProfessorActivity.COURSE_INTENT);

        setupUI();
        generateTableHead();
        generateTableBody();
    }

    private void generateTableHead() {
        TableRow tr_head = new TableRow(this);
        tr_head.setBackgroundColor(Color.GRAY);

        TextView examId = new TextView(this);
        examId.setText("#");
        examId.setTextColor(Color.WHITE);
        tr_head.addView(examId);

        TextView studentId = new TextView(this);
        studentId.setText("Student ID");
        studentId.setTextColor(Color.WHITE);
        tr_head.addView(studentId);

        TextView studentName = new TextView(this);
        studentName.setText("Name");
        studentName.setTextColor(Color.WHITE);
        tr_head.addView(studentName);

        TextView seatNumber = new TextView(this);
        seatNumber.setText("Seat");
        seatNumber.setTextColor(Color.WHITE);
        tr_head.addView(seatNumber);

        TextView signedOut = new TextView(this);
        signedOut.setText("Signed Out");
        signedOut.setTextColor(Color.WHITE);
        tr_head.addView(signedOut);

        tableLayout.addView(tr_head);
    }

    private void generateTableBody() {
        List<ReportRow> listOfRows = dbHelper.getReportRows(course);

        if (listOfRows != null) {
            for (ReportRow row : listOfRows) {
                Student student = dbHelper.getStudent(Long.parseLong(row.getStudentId()));

                TableRow tr_body = new TableRow(this);
                tr_body.setBackgroundColor(Color.WHITE);

                TextView examId = new TextView(this);
                examId.setText(String.valueOf(row.getId()));
                examId.setTextColor(Color.BLACK);
                tr_body.addView(examId);

                TextView studentId = new TextView(this);
                studentId.setText(String.valueOf(row.getStudentId()));
                studentId.setTextColor(Color.BLACK);
                tr_body.addView(studentId);

                TextView studentName = new TextView(this);
                studentName.setText(String.format("%s %s", student.getFirstName(), student.getLastName()));
                studentName.setTextColor(Color.BLACK);
                tr_body.addView(studentName);

                TextView seatNumber = new TextView(this);
                seatNumber.setText(String.valueOf(row.getStudentSeat()));
                seatNumber.setTextColor(Color.BLACK);
                tr_body.addView(seatNumber);

                TextView signedOut = new TextView(this);
                int signOutStatus = row.isSignedOut();
                if (signOutStatus == 1) {
                    signedOut.setText("Yes");
                } else {
                    signedOut.setText("No");
                }
                signedOut.setTextColor(Color.BLACK);
                tr_body.addView(signedOut);

                tableLayout.addView(tr_body);
            }
        }
    }

    private void setupUI() {
        dbHelper = new DatabaseHelper(ProfessorReportActivity.this);
        tableLayout = findViewById(R.id.professorTableLayout);
        tableLayout.removeAllViews();
        tableLayout.setStretchAllColumns(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId())
        {
            case R.id.logout:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            case android.R.id.home:
                endActivity(course);
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void endActivity(Course course) {
        SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(ProfessorReportActivity.this);
        sharedPreferenceHelper.saveProfile(course);
        sharedPreferenceHelper.saveSource(Source.PROFESSORREPORT_ACTIVITY);
        sharedPreferenceHelper.saveCourseCode(course.getCode());
        ProfessorReportActivity.this.finish();
    }
}