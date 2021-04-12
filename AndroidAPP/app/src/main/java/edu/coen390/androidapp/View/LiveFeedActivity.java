package edu.coen390.androidapp.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Controller.SharedPreferenceHelper;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.HttpRequest;
import edu.coen390.androidapp.Model.Source;
import edu.coen390.androidapp.Model.Student;
import edu.coen390.androidapp.R;


public class LiveFeedActivity extends AppCompatActivity {
    public static final String KEY_URL_TO_LOAD = "KEY_URL_TO_LOAD";
    //TODO: change URLs
    @VisibleForTesting
   public static final String WEB_FORM_URL = "http://192.168.2.135:5000/video_feed";
   public static final String JSON_STUDENT_URL = "http://192.168.2.135:5000/person_info";
    //public static final String JSON_STUDENT_URL = "http://192.168.0.166:5000/";
    private static final String TAG = "LiveFeedActivity";
    private JSONObject studentInformation;
    private WebView myWebView;
    private DatabaseHelper dbHelper;
    private Course course;
    private TextView studentName;
    private TextView studentID;
    private TextView seatNumber;
    private ImageView imageView;
    private Button backButton;
    private Timer timer;
    private TimerTask timerTask;
    private SharedPreferences sharedPreferences;
    private SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_feed);

        setupUI();

        Intent intent = getIntent();
        course = (Course) intent.getSerializableExtra(InvigilatorActivity.COURSE_INTENT);

        sharedPreferenceHelper = new SharedPreferenceHelper(LiveFeedActivity.this);
        Course retrievedCourse = sharedPreferenceHelper.getProfile(course);
        if (retrievedCourse != null) {
            course = retrievedCourse;
        }
    }

    private void setupUI() {
        studentName = findViewById(R.id.studentNameTextView);
        studentID = findViewById(R.id.studentIDTextView);
        seatNumber = findViewById(R.id.seatNumberTextView);
        imageView = findViewById(R.id.successMessageImageView);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Toast.makeText(this, "Facial Recognition Cancelled.", Toast.LENGTH_SHORT).show();
            endActivity(course);
        });

        dbHelper = new DatabaseHelper(this);
        timer = new Timer();
    }

    @Override
    protected void onStart() {
        super.onStart();

        launchWebView();

        try {
            timer.scheduleAtFixedRate(timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        studentInformation = HttpRequest.getJSONObjectFromURL(JSON_STUDENT_URL);
                        Student student = HttpRequest.getStudentFromJSONObject(studentInformation);

                        boolean isStudentConfirmed;
                        if (student != null) {
                            isStudentConfirmed = dbHelper.isStudentRegisteredInCourse(student, course);
                        } else {
                            isStudentConfirmed = false;
                        }

                        boolean isStudentSeated;
                        if(student != null){
                            isStudentSeated = dbHelper.isStudentSeated(student, course);
                        }else{
                            isStudentSeated = false;
                        }

                        if (isStudentConfirmed) {
                            if (!isStudentSeated) {
                                int seat = course.getSeats().getNextSeat(student);
                                if (seat != -1) {
                                    int status = dbHelper.insertInExamTable(student, course, seat);
                                    if (status == 1) {
                                        String name = student.getFirstName() + " " + student.getLastName();
                                        runOnUiThread(() -> {
                                            studentName.setText(name);
                                            studentID.setText(Integer.toString((int) student.getId()));
                                            seatNumber.setText(Integer.toString(seat));
                                            Drawable drawable = ContextCompat.getDrawable(
                                                    LiveFeedActivity.this, R.drawable.success);
                                            imageView.setImageDrawable(drawable);
                                            Toast.makeText(LiveFeedActivity.this,
                                                    "Student with ID: " + student.getId() + " has been successfully confirmed. " +
                                                            "Returning to previous page.",
                                                    Toast.LENGTH_LONG).show();
                                        });
                                        Thread thread = new Thread(() -> {
                                            try {
                                                Thread.sleep(5000);
                                                cancel();
                                                endActivity(course);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                                        thread.start();
                                        thread.join();
                                    } else {
                                        runOnUiThread(() -> Toast.makeText(LiveFeedActivity.this, "Error inserting student in exam table. Please try again.",
                                                Toast.LENGTH_SHORT).show());
                                    }
                                } else {
                                    runOnUiThread(() -> Toast.makeText(LiveFeedActivity.this, "Error assigning seat to student. Please try again.",
                                            Toast.LENGTH_SHORT).show());
                                }
                            } else {
                                int seat = dbHelper.getStudentSeat(student, course);
                                if (seat != -1) {
                                    String name = student.getFirstName() + " " + student.getLastName();
                                    runOnUiThread(() -> {
                                        studentName.setText(name);
                                        studentID.setText(Integer.toString((int) student.getId()));
                                        seatNumber.setText(Integer.toString(seat));
                                        Drawable drawable = ContextCompat.getDrawable(
                                                LiveFeedActivity.this, R.drawable.success);
                                        imageView.setImageDrawable(drawable);
                                        Toast.makeText(LiveFeedActivity.this,
                                                "Student with ID: " + student.getId() + " has been successfully confirmed. " +
                                                        "Returning to previous page.",
                                                Toast.LENGTH_LONG).show();
                                    });
                                    Thread thread = new Thread(() -> {
                                        try {
                                            Thread.sleep(5000);
                                            cancel();
                                            endActivity(course);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    thread.start();
                                    thread.join();
                                }
                            }
                        } else {
                            runOnUiThread(() -> {
                                studentName.setText("N/A");
                                studentID.setText("N/A");
                                seatNumber.setText("N/A");
                                Drawable drawable = ContextCompat.getDrawable(
                                        LiveFeedActivity.this, R.drawable.failure);
                                imageView.setImageDrawable(drawable);
                                Toast.makeText(LiveFeedActivity.this,
                                        "Cannot identify student. Please try again.",
                                        Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 1000);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void launchWebView() {
        myWebView = findViewById(R.id.webView);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        myWebView.loadUrl(WEB_FORM_URL);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
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
            case android.R.id.home:
                endActivity(course);
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void endActivity(Course course) {
        sharedPreferenceHelper.saveProfile(course);
        sharedPreferenceHelper.saveSource(Source.LIVEFEED_ACTIVITY);
        sharedPreferenceHelper.saveCourseCode(course.getCode());
        LiveFeedActivity.this.finish();
    }
}