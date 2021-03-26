package edu.coen390.androidapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.examapp.R;

import java.util.List;

import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.CourseAdapter;
import edu.coen390.androidapp.Model.Course;

public class CourseActivity extends AppCompatActivity {


    private static final String TAG = "CourseActivity";
    protected ListView courseListView;
    protected DatabaseHelper dbHelper;
    protected List<Course> courses;
    Long course_id;
    Long invigilator_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        setupUI();
    }

    private void setupUI () {

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.insertCourse(new Course(-1, 1,"ENGR", "391"));
        dbHelper.insertCourse(new Course(-1, 2,"ELEC", "331"));
        dbHelper.insertCourse(new Course(-1, 1,"COEN", "313"));



        courseListView = findViewById(R.id.courseListView);

        Bundle bundle = getIntent().getExtras();
        invigilator_id = bundle.getLong("invigilator_id");

        loadListView(invigilator_id);

        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(view.getContext(), LiveFeedActivity.class);
                Long course_id = courses.get(position).getId();
                intent.putExtra("course_id", course_id);
                intent.putExtra("invigilator_id", invigilator_id);
                startActivity(intent);
            }
        });

    }

    private void loadListView(long invigilatorID) {

         courses = dbHelper.getCourses(invigilatorID);

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, courses);
         //courseListView.setAdapter(adapter);

        CourseAdapter adapter = new CourseAdapter(CourseActivity.this,courses);
        courseListView.setAdapter(adapter);


    }



}