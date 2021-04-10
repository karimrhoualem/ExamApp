package edu.coen390.androidapp.Controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.examapp.R;
import com.google.gson.Gson;

import edu.coen390.androidapp.Model.Course;

public class SharedPreferenceHelper {
    /**
     * The SharedPreferences object for data storage.
     */
    private SharedPreferences sharedPreferences;

    /**
     * The context from Activities for accessing SharedPreferences files.
     */
    private Context context;

    /**
     * Constructor used to create a SharePreferenceHelper object
     * @param context The context from the executing Activity.
     */
    public SharedPreferenceHelper(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.courses_file), Context.MODE_PRIVATE);
    }

    /**
     * Saves the Course passed as a parameter to SharePreferences file.
     * @param course The Course object to be saved.
     */
    public void saveProfile(Course course) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(course);
        editor.putString(course.getCode(), json);
        editor.commit();
    }

    /**
     * Gets the Course from the SharedPreferences file.
     * @return The Course object.
     */
    public Course getProfile(Course course) {
        String json = sharedPreferences.getString(course.getCode(), "");
        if (!json.equals("")) {
            Gson gson = new Gson();
            return gson.fromJson(json, Course.class);
        }
        else {
            return null;
        }
    }
}
