package edu.coen390.androidapp.Model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Course implements Serializable {
    private long id;
    private String title;
    private String code;
    private int numOfStudents;
    private Seat seats;

    public Course() {}

    public Course(long id, String title, String code, int numOfStudents) {
        this.id = id;
        this.title = title;
        this.code = code;
        this.numOfStudents = numOfStudents;
        seats = new Seat(numOfStudents);
    }

    public Course(long id, String title, String code) {
        this.id = id;
        this.title = title;
        this.code = code;
    }

    public Course(String title, String code) {
        this.title = title;
        this.code = code;
    }

    //check this
    @NotNull
    @Override
    public String toString() {
        return title + " " + code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Seat getSeats() {
        return seats;
    }

    public void setSeats(Seat seats) {
        this.seats = seats;
    }

    public int getNumOfStudents() {
        return numOfStudents;
    }

    public void setNumOfStudents(int numOfStudents) {
        this.numOfStudents = numOfStudents;
    }
}
