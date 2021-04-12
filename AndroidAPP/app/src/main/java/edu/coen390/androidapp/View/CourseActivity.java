package edu.coen390.androidapp.View;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Controller.SharedPreferenceHelper;
import edu.coen390.androidapp.Model.CourseAdapter;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.Invigilator;
import edu.coen390.androidapp.Model.Professor;
import edu.coen390.androidapp.Model.User;
import edu.coen390.androidapp.Model.UserType;
import edu.coen390.androidapp.R;

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

    private void setupUI() {
        Log.i(TAG, "setupUI");

        dbHelper = new DatabaseHelper(this);
        courseListView = findViewById(R.id.courseListView);
        userNameTextView = findViewById(R.id.userNameTextView);

        sharedPreferenceHelper = new SharedPreferenceHelper(CourseActivity.this);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(CourseIntentKey);

        User retrievedUser = sharedPreferenceHelper.getUser();
        if (retrievedUser != null) {
            user = retrievedUser;
        }

        loadUser(user);
    }

    private void loadUser(User user) {
        if (user instanceof Invigilator) {
            userNameTextView.setText("User Mode: INVIGILATOR" + '\n' + "Name: " + user.getFirstName()
                                    + " " + user.getLastName() + '\n'
                                    + "Username: " + user.getUserName() + '\n');
            userNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f);
            userNameTextView.setTypeface(null, Typeface.BOLD);
//            userNameTextView.setTextColor(Color.RED);
//            userNameTextView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            loadListView(user.getId(), UserType.INVIGILATOR);

            courseListView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent1 = new Intent(view.getContext(), InvigilatorActivity.class);
                intent1.putExtra(InvigilatorActivity.COURSE_INTENT, getCourse(position));
                sharedPreferenceHelper.clearSource();
                startActivity(intent1);
            });
        }
        else if (user instanceof Professor) {
            userNameTextView.setText("User Mode: PROFESSOR" + '\n' + "Name:" + "  " + user.getFirstName()
                                    + " " + user.getLastName() + '\n'
                                    + "Username: " + user.getUserName() + '\n');
            userNameTextView.setTypeface(null, Typeface.BOLD);
            userNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f);
//            userNameTextView.setTextColor(Color.RED);
//            userNameTextView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            loadListView(user.getId(), UserType.PROFESSOR);

            courseListView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent1 = new Intent(view.getContext(), ProfessorActivity.class);
                intent1.putExtra(ProfessorActivity.COURSE_INTENT, getCourse(position));
                sharedPreferenceHelper.clearSource();
                startActivity(intent1);
            });
        }
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
        if (item.getItemId() == R.id.logout) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}