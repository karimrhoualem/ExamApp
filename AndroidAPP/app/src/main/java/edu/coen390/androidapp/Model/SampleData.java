package edu.coen390.androidapp.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SampleData {
    public static ArrayList<Student> getSampleStudents(InputStream file) throws IOException {

        ArrayList<Student> students = new ArrayList<>();

        try{

            BufferedReader br = new BufferedReader(new InputStreamReader(file));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            String jsonString = sb.toString();

            JSONObject jsonObject = new JSONObject(jsonString);
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