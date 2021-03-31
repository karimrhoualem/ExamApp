package edu.coen390.androidapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.examapp.R;

import java.util.List;

import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.CourseAdapter;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.User;

public class CourseActivity extends AppCompatActivity {


    private static final String TAG = "CourseActivity";
    protected ListView courseListView;
    protected DatabaseHelper dbHelper;
    protected List<Course> courses;
    protected int invigilator_id;
    private TextView userNameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        setupUI();
    }

    private void setupUI () {

        dbHelper = new DatabaseHelper(this);
        courseListView = findViewById(R.id.courseListView);
        userNameTextView = findViewById(R.id.userNameTextView);


        dbHelper.insertCourse(new Course(-1, 1,"Numerical Methods", "ENGR 391"));
        dbHelper.insertCourse(new Course(-1, 2,"Fundamentals of Electrical Power", "ELEC 331"));
        dbHelper.insertCourse(new Course(-1, 1,"Digital Systems Design II", "COEN 313"));


        Intent intent = getIntent();
        User invigilator = (User) intent.getSerializableExtra("invigilatorObject");
        userNameTextView.setText(invigilator.getUserName());

        loadListView(invigilator.getId());


        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(view.getContext(), VerificationModeActivity.class);
                Course currentCourse = new Course(courses.get(position).getId(),courses.get(position).getInvigilator_id(),courses.get(position).getTitle(),courses.get(position).getCode());
                intent.putExtra(VerificationModeActivity.COURSE_INTENT, currentCourse);
                startActivity(intent);
            }
        });

    }

    private void loadListView(long invigilatorID) {

        courses = dbHelper.getCourses(invigilatorID);
        CourseAdapter adapter = new CourseAdapter(CourseActivity.this,courses);
        courseListView.setAdapter(adapter);


    }



}