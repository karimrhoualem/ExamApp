package edu.coen390.androidapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.examapp.R;

import java.util.List;

import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.ReportRow;
import edu.coen390.androidapp.Model.Student;

/*
 * Good example for UI: https://www.tutorialspoint.com/how-to-add-table-rows-dynamically-in-android-layout
 */

public class InvigilatorReportActivity extends AppCompatActivity {
    private Course course;
    private TableLayout tableLayout;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invigilator_report);

        Intent intent = getIntent();
        course = (Course) intent.getSerializableExtra(InvigilatorActivity.COURSE_INTENT);

        setupUI();
        generateTableHead();
        generateTableBody();
    }

    private void generateTableHead() {
        TableRow tr_head = new TableRow(this);
        tr_head.setBackgroundColor(Color.GRAY);

        TextView examId = new TextView(this);
        examId.setText("Exam");
        examId.setTextColor(Color.WHITE);
        tr_head.addView(examId);

        TextView courseId = new TextView(this);
        courseId.setText("Course");
        courseId.setTextColor(Color.WHITE);
        tr_head.addView(courseId);

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

                TextView courseId = new TextView(this);
                courseId.setText(String.valueOf(row.getCourseId()));
                courseId.setTextColor(Color.BLACK);
                tr_body.addView(courseId);

                TextView studentId = new TextView(this);
                studentId.setText(String.valueOf(row.getStudentId()));
                studentId.setTextColor(Color.BLACK);
                tr_body.addView(studentId);

                TextView studentName = new TextView(this);
                studentName.setText(student.getFirstName() + " " + student.getLastName());
                studentName.setTextColor(Color.BLACK);
                tr_body.addView(studentName);

                TextView seatNumber = new TextView(this);
                seatNumber.setText(String.valueOf(row.getStudentSeat()));
                seatNumber.setTextColor(Color.BLACK);
                tr_body.addView(seatNumber);

                CheckBox signedOut = new CheckBox(this);
                tr_body.addView(signedOut);
                if (row.isSignedOut() == 1) {
                    signedOut.setChecked(true);
                }
                else {
                    signedOut.setChecked(false);
                }

                signedOut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                       @Override
                       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                           if (signedOut.isChecked()) {
                               int ret = dbHelper.updateSignOutStatus(course, row, 1);
                               if (ret > 0) {
                                   Toast.makeText(InvigilatorReportActivity.this,
                                           "Student with ID: " + row.getStudentId() + " has been successfully signed out.",
                                           Toast.LENGTH_LONG).show();
                               }
                           }
                           else {
                               dbHelper.updateSignOutStatus(course, row, 0);
                           }
                       }
                   }
                );

                tableLayout.addView(tr_body);
            }
        }
    }

    private void setupUI() {
        dbHelper = new DatabaseHelper(InvigilatorReportActivity.this);
        tableLayout = findViewById(R.id.tableLayout);
        tableLayout.removeAllViews();
        tableLayout.setStretchAllColumns(true);
    }
}