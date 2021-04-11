package edu.coen390.androidapp.Model;

import java.io.Serializable;

public class Professor extends User implements Serializable {
    public Professor() {
    }

    public Professor(long id, String[] courses, String firstName, String lastName, String userName, String password) {
        super(id, courses, firstName, lastName, userName, password);
    }
}
