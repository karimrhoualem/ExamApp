package edu.coen390.androidapp.Model;

import java.io.Serializable;
import java.util.Arrays;

public class Student extends User implements Serializable {
    public Student(){};

    public Student(int id, String[] courses, String firstName, String lastName, String username, String password) {
        super(id, courses, firstName, lastName, username, password);
    }

    @Override
    public String toString() {
        return "Student{" +
                "ID=" + getId() +
                ", courses=" + Arrays.toString(getCourses()) +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                '}';
    }
}
