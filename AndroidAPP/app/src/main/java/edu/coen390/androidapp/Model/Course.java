package edu.coen390.androidapp.Model;

import java.io.Serializable;

public class Course implements Serializable {

    private int courseID;
    private int courseInvigilatorID;
    private String courseTitle;
    private String courseCode;
    private String courseDescription;

    public Course(int courseID, String courseTitle, String courseCode) {
        this.courseID = courseID;
        this.courseTitle = courseTitle;
        this.courseCode = courseCode;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getCourseInvigilatorID() {
        return courseInvigilatorID;
    }

    public void setCourseInvigilatorID(int courseInvigilatorID) {
        this.courseInvigilatorID = courseInvigilatorID;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }
}
