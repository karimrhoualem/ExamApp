package edu.coen390.androidapp.Model;

import java.io.Serializable;
import java.util.Arrays;

public class Invigilator extends User implements Serializable {
    public Invigilator() { super(); }
    public Invigilator(long id, String[] courses, String firstName, String lastName, String userName, String password) {
        super(id, courses, firstName, lastName, userName, password);
    }

    @Override
    public String toString() {
        return "Invigilator" +
                "ID=" + getId() +
                ", courses=" + Arrays.toString(getCourses()) +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                '}';
    }
}
