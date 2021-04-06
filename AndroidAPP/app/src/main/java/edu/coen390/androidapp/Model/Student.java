package edu.coen390.androidapp.Model;

import java.io.Serializable;
import java.util.Arrays;

public class Student implements Serializable {

    private int ID;
    private String[] courses;
    private String firstName;
    private String lastName;

    public Student(){};

    public Student(int ID, String[] courses, String firstName, String lastName) {
        this.ID = ID;
        this.courses = courses;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String[] getCourses() {
        return courses;
    }

    public void setCourses(String[] courses) {
        this.courses = courses;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Student{" +
                "ID=" + ID +
                ", courses=" + Arrays.toString(courses) +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
