package edu.coen390.androidapp.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.examapp.R;
import java.util.List;
import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Controller.SharedPreferenceHelper;
import edu.coen390.androidapp.CourseAdapter;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.Invigilator;
import edu.coen390.androidapp.Model.Professor;
import edu.coen390.androidapp.Model.User;
import edu.coen390.androidapp.Model.UserType;

public class CourseActivity extends AppCompatActivity {
    public static final String CourseIntentKey = "COURSE_INTENT_KEY";
    private static final String TAG = "CourseActivity";
    protected ListView courseListView;
    protected DatabaseHelper dbHelper;
    protected List<Course> courses;
    private TextView userNameTextView;
    private User user;
    private SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        setupUI();
        Log.i(TAG, "on create");



    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "on resume");


    }


    private void setupUI() {
        Log.i(TAG, "setupUI");
        dbHelper = new DatabaseHelper(this);
        courseListView = findViewById(R.id.courseListView);
        userNameTextView = findViewById(R.id.userNameTextView);

        Intent intent = getIntent();

        sharedPreferenceHelper = new SharedPreferenceHelper(CourseActivity.this);

        user = (User) intent.getSerializableExtra(CourseIntentKey);


        //this works
        //User retrievedUser = sharedPreferenceHelper.getInvigilator();


        //the issue is returning a USER. Need to return an INVIGILATOR or PROFESSOR
        User retrievedUser = sharedPreferenceHelper.getUser();

        if (retrievedUser != null) {
            Log.d(TAG, "if statement" + retrievedUser.getUserName());
            user = retrievedUser;
        }

        if (user instanceof Invigilator) {
            userNameTextView.setText("Invigilator:" + "  " + user.getUserName());
            Log.d(TAG, "invigilator " + user.getUserName());
            loadListView(user.getId(), UserType.INVIGILATOR);

            //maybe this should be save Invigilator, but don't know how.
            // Invigilator invigilator = (Invigilator) user;
            //sharedPreferenceHelper.saveInvigilator(invigilator);

            //this works as long as getting the user returns an invigilator
            sharedPreferenceHelper.saveUser(user, UserType.INVIGILATOR);

            courseListView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent1 = new Intent(view.getContext(), InvigilatorActivity.class);
                intent1.putExtra(InvigilatorActivity.COURSE_INTENT, getCourse(position));
                startActivity(intent1);


            });
        } else if (user instanceof Professor) {
            userNameTextView.setText("Professor:" + "  " + user.getUserName());
            loadListView(user.getId(), UserType.PROFESSOR);

            //maybe this should be save Professor
            sharedPreferenceHelper.saveUser(user, UserType.PROFESSOR);

            courseListView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent1 = new Intent(view.getContext(), ProfessorActivity.class);
                intent1.putExtra(ProfessorActivity.COURSE_INTENT, getCourse(position));
                startActivity(intent1);


            });
        }

        User user = (User) intent.getSerializableExtra(CourseIntentKey);
        if (user instanceof Invigilator) {
            userNameTextView.setText("Invigilator:" + "  " + user.getUserName());
            loadListView(user.getId(), UserType.INVIGILATOR);

            courseListView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent1 = new Intent(view.getContext(), InvigilatorActivity.class);
                intent1.putExtra(InvigilatorActivity.COURSE_INTENT, getCourse(position));
                startActivity(intent1);
            });
        } else if (user instanceof Professor) {
            userNameTextView.setText("Professor:" + "  " + user.getUserName());
            loadListView(user.getId(), UserType.PROFESSOR);

            courseListView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent1 = new Intent(view.getContext(), ProfessorActivity.class);
                intent1.putExtra(ProfessorActivity.COURSE_INTENT, getCourse(position));
                startActivity(intent1);
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "on start");

    }



    private Course getCourse(int position) {
        return new Course(courses.get(position).getId(),
                courses.get(position).getTitle(),
                courses.get(position).getCode(),
                courses.get(position).getNumOfStudents());
    }

    private void loadListView(long id, UserType userType) {
        courses = dbHelper.getCourses(id, userType);
        Log.d(TAG, courses.toString());
        CourseAdapter adapter = new CourseAdapter(CourseActivity.this, courses);
        courseListView.setAdapter(adapter);
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