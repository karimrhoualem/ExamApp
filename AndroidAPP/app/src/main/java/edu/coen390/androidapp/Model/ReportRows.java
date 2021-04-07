package edu.coen390.androidapp.Model;

public class ReportRows {
    private int id;
    private String courseId;
    private String studentId;
    private String studentSeat;
    private int isSignedOut;

    public ReportRows(int id, String courseId, String studentId, String studentSeat, int isSignedOut) {
        this.id = id;
        this.courseId = courseId;
        this.studentId = studentId;
        this.studentSeat = studentSeat;
        this.isSignedOut = isSignedOut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentSeat() {
        return studentSeat;
    }

    public void setStudentSeat(String studentSeat) {
        this.studentSeat = studentSeat;
    }

    public int isSignedOut() {
        return isSignedOut;
    }

    public void setSignedOut(int signedOut) {
        isSignedOut = signedOut;
    }
}
