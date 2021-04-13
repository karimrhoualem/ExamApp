package edu.coen390.androidapp.Controller;

import android.content.Context;
import android.content.SharedPreferences;

import edu.coen390.androidapp.R;
import com.google.gson.Gson;

import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.Invigilator;
import edu.coen390.androidapp.Model.Professor;
import edu.coen390.androidapp.Model.Source;
import edu.coen390.androidapp.Model.User;
import edu.coen390.androidapp.Model.UserType;

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
     *
     * @param context The context from the executing Activity.
     */
    public SharedPreferenceHelper(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.courses_file), Context.MODE_PRIVATE);
    }

    /**
     * Saves the Course passed as a parameter to SharePreferences file.
     *
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
     *
     * @return The Course object.
     */
    public Course getProfile(Course course) {
        String json = sharedPreferences.getString(course.getCode(), "");
        if (!json.equals("")) {
            Gson gson = new Gson();
            return gson.fromJson(json, Course.class);
        } else {
            return null;
        }
    }

    public void saveUser(User user, UserType userType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        User currentUser = null;

        if(userType == UserType.INVIGILATOR){
            currentUser = (Invigilator) user;
            editor.putString("UserType", "Invigilator");
        }
        else if(userType == UserType.PROFESSOR){
            currentUser = (Professor) user;
            editor.putString("UserType", "Professor");
        }

        String json = gson.toJson(currentUser);

        editor.putString("User", json);
        editor.commit();
    }

    public User getUser() {
        String json = sharedPreferences.getString("User", "");
        String userType = sharedPreferences.getString("UserType","" );

        if (!json.equals("") && !userType.equals("")) {
            Gson gson = new Gson();
            if (userType.equals("Invigilator")) {
                return gson.fromJson(json, Invigilator.class);
            } else if (userType.equals("Professor")) {
                return gson.fromJson(json, Professor.class);
            }
            return null;
        }
        else {
            return null;
        }
    }

    public void saveSource(Source source) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Source", source.name());
        editor.apply();
    }

    public String getSource() {
        return sharedPreferences.getString("Source", "");
    }

    public void saveCourseCode(String code) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Course_Code", code);
        editor.commit();
    }

    public String getCourseCode() {
        return sharedPreferences.getString("Course_Code", "");
    }

    public void clearSource() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("Source");
        editor.commit();
    }
}