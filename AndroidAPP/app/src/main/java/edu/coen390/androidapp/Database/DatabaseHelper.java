package edu.coen390.androidapp.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.coen390.androidapp.Model.Course;

//Need to write this function, get Courses

public class DatabaseHelper {

/*
    //get all assignments of a course based on course id
    public List<Course> getCourses(long invigilatorID){

        //Log.i(TAG,"get Courses was accessed");

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Config.TABLE_ASSIGNMENT_NAME + " WHERE " + Config.COLUMN_ASSIGNMENT_COURSE_ID + " = " + courseID;

        Cursor cursor = null;
        try{
            cursor = db.rawQuery(selectQuery, null);

            if(cursor!=null){
                if(cursor.moveToFirst()){

                    List<Course> courseList = new ArrayList<>();

                    do{
                        //getting info from cursor, creating a new Course object with info, adding this course obj to courseList
                        //this is for one row
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ASSIGNMENT_ID));
                        int invigilatorID = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ASSIGNMENT_COURSE_ID));
                        String title = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSIGNMENT_TITLE));
                        String code = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ASSIGNMENT_GRADE));

                        courseList.add(new Course(id,invigilatorID,title,code));

                    }while(cursor.moveToNext()); //go to next row of db

                    return assignmentList;
                }
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.getMessage());
        }finally{
            if(cursor!=null)
                cursor.close();

            db.close();
        }
        return Collections.emptyList();

    }


*/
}
