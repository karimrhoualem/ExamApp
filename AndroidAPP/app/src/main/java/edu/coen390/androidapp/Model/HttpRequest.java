package edu.coen390.androidapp.Model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import edu.coen390.androidapp.Controller.DatabaseHelper;

public class HttpRequest {

    private static final String TAG = "HttpRequest";

    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {

        try {
            HttpURLConnection urlConnection;
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
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
            int studentID = Integer.parseInt(studentInformation.getString("ID"));
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<Student> getTestStudentFromJSON() throws IOException {


   /*     BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/Users/ahmed/Desktop/Concordia/Courses/Winter 2021/COEN390/Project/COEN390_Project/AndroidAPP/app/src/main/java/edu/coen390/androidapp/SampleData/sample-data.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();

        String line = "";
        while (true) {
            try {
                if (!((line = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            sb.append(line).append("\n");
        }
        br.close();*/

        String file = "/Users/ahmed/Desktop/Concordia/Courses/Winter 2021/COEN390/Project/COEN390_Project/AndroidAPP/app/src/main/java/edu/coen390/androidapp/SampleData/sample-data.json";
        String jsonString1 = new String(Files.readAllBytes((Paths.get(file))));
        String jsonString = "{\"people\":[{\"name\":\"Karim Rhoualem\",\"ID\":\"26603157\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Ahmed Ali\",\"ID\":\"40102454\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Megan Walbaum\",\"ID\":\"40068567\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Alec Wolfe\",\"ID\":\"40103773\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Tawfiq Jawhar\",\"ID\":\"40000390\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Bipinkumar Patel\",\"ID\":\"12345678\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"William Lynch\",\"ID\":\"23456789\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Barack Obama\",\"ID\":\"45454545\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Mark Hamill\",\"ID\":\"40102453\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Joe Biden\",\"ID\":\"40052335\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Esther Anil\",\"ID\":\"41022335\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Beyonce Carter\",\"ID\":\"42032735\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Nadine Caron\",\"ID\":\"43042635\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Jeremy Dutcher\",\"ID\":\"44052535\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Gong Li\",\"ID\":\"45062365\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Aamir Khan\",\"ID\":\"46072385\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Mackenzie Foy\",\"ID\":\"47082321\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Emmanuel Macron\",\"ID\":\"48092305\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Angela Merkel\",\"ID\":\"41042323\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Anjali Nair\",\"ID\":\"42052335\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Oprah Winfrey\",\"ID\":\"43062302\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Serena Williams\",\"ID\":\"44072305\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Shinzo Abe\",\"ID\":\"45082367\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Saikumar\",\"ID\":\"46092389\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Tsai Ing-wen\",\"ID\":\"47032335\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Xi Jinping\",\"ID\":\"48022354\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"YeoJeong Cho\",\"ID\":\"49082323\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]},{\"name\":\"Micheal Jordan\",\"ID\":\"41012301\",\"Courses\":[\"ENGR 391\",\"COMP 472\",\"ELEC 331\",\"COEN 313\"]}]}";
        ArrayList<Student> students = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(jsonString1);
            JSONArray jsonArray = jsonObject.getJSONArray("people");
            for(int i = 0; i < jsonArray.length() ; i++){
                JSONObject studentInformation = jsonArray.getJSONObject(i);
                int studentID = Integer.parseInt(studentInformation.getString("ID"));
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
                }else{
                    studentFirstName = "N/A";
                    studentLastName = "N/A";
                }
                JSONArray jsonCourses = studentInformation.getJSONArray("Courses");
               String [] courses = new String[jsonCourses.length()];
               for (int j = 0; j< courses.length; j++)
                   courses[j] = jsonCourses.getString(j);
                Student student = new Student(
                        studentID,
                        courses,
                        studentFirstName,
                        studentLastName,
                        "",
                        "");
                students.add(student);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return students;
    }
}
