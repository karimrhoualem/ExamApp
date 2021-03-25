package edu.coen390.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.examapp.R;

import java.util.Collections;

public class CourseActivity extends AppCompatActivity {

    protected ListView courseListView;
    //protected DatabaseHelper dbHelper;
    //protected List<Course> courses;
    Long course_id;
    Long invigilator_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
    }

    private void setupUI () {

        courseListView = findViewById(R.id.courseListView);

        Bundle bundle = getIntent().getExtras();
        invigilator_id = bundle.getLong("invigilator_id");

        loadListView();

        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(view.getContext(), FaceRecognition.class);
                //Long course_id = courses.get(position).getId();
                intent.putExtra("course_id", course_id);
                 intent.putExtra("invigilator_id", invigilator_id);
                startActivity(intent);
            }
        });

    }

    private void loadListView() {

         // courses = dbHelper.getCourses(invigilatorID);

            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, courses);
           // courseListView.setAdapter(adapter);


    }




}
