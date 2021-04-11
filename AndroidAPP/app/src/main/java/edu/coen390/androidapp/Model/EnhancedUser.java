package edu.coen390.androidapp.Model;

import java.io.Serializable;
import java.util.List;

public class EnhancedUser implements Serializable {
    private long id;
    private List<String> courses;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;

    public EnhancedUser() {}

    public EnhancedUser(long id, List<String> courses, String firstName, String lastName, String userName, String password) {
        this.id = id;
        this.courses = courses;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
    }

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "EnhancedUser{" +
                "id=" + id +
                ", courses=" + courses +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
