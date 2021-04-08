package edu.coen390.androidapp.Model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {

    private static final String TAG = "HttpRequest";

    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {

        try {
            HttpURLConnection urlConnection = null;
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            String jsonString = sb.toString();
            System.out.println("JSON: " + jsonString);

            return new JSONObject(jsonString);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        return null;
    }

    public static Student getStudentFromJSONObject(JSONObject studentInformation) {
        try {
            int studentID = studentInformation.getInt("ID");
            String studentName = studentInformation.getString("name");
            String studentFirstName;
            String studentLastName;
            if (studentName.contains(" ") && studentID != 0) {
                String[] names = studentName.split(" ", 2);
                studentFirstName = names[0];
                studentLastName = names[1];
            } else if (studentName.contains("_") && studentID != 0) {
                String[] names = studentName.split("_", 2);
                studentFirstName = names[0];
                studentLastName = names[1];

            } else {
                studentFirstName = "N/A";
                studentLastName = "N/A";
            }

            return new Student(studentID, null, studentFirstName, studentLastName, null, null);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
