package edu.coen390.androidapp.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import com.example.examapp.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.User;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.Student;

/**
 * Database helper class serves as the controller and main API through which
 * the database will be accessed.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * Tag used for logger.
     */
    private static final String TAG = "DatabaseHelper";

    /**
     * Stores the SQL database name.
     */
    private static final String DATABASE_NAME = Config.DATABASE_NAME;

    /**
     * Stores the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * String separator for storing/retrieving arrays from DB.
     */
    public static String strSeparator = "__,__";

    /**
     * Stores the context that's received from the activity.
     */
    private Context context;

    /**
     * DatabaseHelper constructor.
     * @param context The context from the calling activity.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        Log.d(TAG, "Inside DatabaseHelper constructor.");
    }

    /**
     * Called when a DatabaseHelper object is created and creates the DB tables.
     * @param sqLiteDatabase The reference to the SQLite database being created.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.d(TAG, "DatabaseHelper onCreate method called.");

        //Store SQL queries in strings.
        String CREATE_INVIGILATOR_TABLE = "CREATE TABLE " + Config.INVIGILATOR_TABLE_NAME + " ("
                + Config.INVIGILATOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.INVIGILATOR_FIRST_NAME + " TEXT NOT NULL, "
                + Config.INVIGILATOR_LAST_NAME + " TEXT NOT NULL, "
                + Config.INVIGILATOR_USERNAME + " TEXT NOT NULL, "
                + Config.INVIGILATOR_PASSWORD + " TEXT NOT NULL"
                + ")";
        Log.d(TAG, "Table created with this query: " + CREATE_INVIGILATOR_TABLE);

        String CREATE_COURSE_TABLE = "CREATE TABLE " + Config.COURSE_TABLE_NAME + " ("
                + Config.COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.COURSE_INVIGILATOR_ID + " INTEGER, "
                + Config.COURSE_TITLE + " TEXT NOT NULL, "
                + Config.COURSE_CODE + " TEXT NOT NULL, "
                + Config.COURSE_DESCRIPTION + " TEXT, "
                + "FOREIGN KEY" + " (" + Config.COURSE_INVIGILATOR_ID + ") "
                + "REFERENCES " + Config.INVIGILATOR_TABLE_NAME + " ("
                + Config.INVIGILATOR_ID + ")"
                + ")";
        Log.d(TAG, "Table created with this query: " + CREATE_COURSE_TABLE);

        String CREATE_STUDENT_TABLE = "CREATE TABLE " + Config.STUDENTS_TABLE_NAME + " ("
                + Config.STUDENT_ID + " INTEGER PRIMARY KEY, "
                + Config.STUDENT_COURSE_ID + " TEXT, "
                + Config.STUDENT_FIRST_NAME + " TEXT NOT NULL, "
                + Config.STUDENT_LAST_NAME + " TEXT NOT NULL, "
//                + Config.STUDENT_USERNAME + "TEXT NOT NULL, " // TODO: to be implemented in Sprint 3 for Student Reports
//                + Config.STUDENT_PASSWORD + "TEXT NOT NULL, " // TODO: to be implemented in Sprint 3 for Student Reports
                + "FOREIGN KEY" + " (" + Config.STUDENT_COURSE_ID + ") "
                + "REFERENCES " + Config.COURSE_TABLE_NAME + " ("
                + Config.COURSE_ID + ")"
                + ")";
        Log.d(TAG, "Table created with this query: " + CREATE_STUDENT_TABLE);

        //Create the tables.
        sqLiteDatabase.execSQL(CREATE_INVIGILATOR_TABLE);
        sqLiteDatabase.execSQL(CREATE_COURSE_TABLE);
        sqLiteDatabase.execSQL(CREATE_STUDENT_TABLE);
    }

    /**
     * Called when the database version is upgraded.
     * @param sqLiteDatabase The reference to the SQLite database being created.
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //To be handled in the event that the database version ever needs to be updated.
    }

    public long insertStudent(Student student) {
        long id = -1;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.STUDENT_ID, student.getID());
        contentValues.put(Config.STUDENT_COURSE_ID, convertArrayToString(student.getCourses()));
        contentValues.put(Config.STUDENT_FIRST_NAME, student.getFirstName());
        contentValues.put(Config.STUDENT_LAST_NAME, student.getLastName());

        try {
            id = db.insertOrThrow(Config.STUDENTS_TABLE_NAME, null, contentValues);
        }
        catch(SQLiteException e) {
            Log.d(TAG, "Exception: " + e.getMessage());
            Toast.makeText(context, "Operation Failed: "+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
        finally {
            db.close();
        }

        return id;
    }

    public boolean isStudentRegisteredInCourse(Student student, Course course) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String selectQuery = "SELECT * " + " FROM " + Config.STUDENTS_TABLE_NAME
                + " WHERE " + Config.STUDENT_ID
                + " = " + student.getID();
        Log.d(TAG, selectQuery);

        try {
            cursor = db.rawQuery(selectQuery, null);

            if(cursor != null)
            {
                if(cursor.moveToFirst())
                {
                    String courses = cursor.getString(cursor.getColumnIndex(Config.STUDENT_COURSE_ID));
                    String[] coursesArray = convertStringToArray(courses);

                    for (String c : coursesArray) {
                        if (c.equals(course.getCode())) {
                            return true;
                        }
                    }
                }
            }
        }
        catch(Exception e){
            Log.d(TAG, "Exception: " + e.getMessage());
        }
        finally {
            if(cursor != null)
                cursor.close();

            db.close();
        }

        return false;
    }

    public void testMethod() {
        SQLiteDatabase db = this.getWritableDatabase();
    }

    /**
     * Used to convert an array into a comma-separated string to be stored in the DB.
     * @param array Array of values to be converted to comma-separated strings
     * @return Comma-separated string equivalent of the array
     */
    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0; i < array.length; i++) {
            str = str + array[i];

            // Do not append comma at the end of last element
            if(i < array.length-1){
                str = str + strSeparator;
            }
        }
        return str;
    }

    /**
     * Used to convert a comma-separated string to an array.
     * @param str The comma-separated string to be converted into an array.
     * @return The equivalent array
     */
    public static String[] convertStringToArray(String str){
        String[] arr = str.split(strSeparator);
        return arr;
    }



    //get all courses assigned to an invigilator based on the invigilator primary key id
    public List<Course> getCourses(long invigilatorID){

        Log.i(TAG,"getCourses was accessed");

        SQLiteDatabase db = this.getReadableDatabase();


        String selectQuery = "SELECT  * FROM " + Config.COURSE_TABLE_NAME + " WHERE " + Config.COURSE_INVIGILATOR_ID + " = " + invigilatorID;
        Log.d(TAG, selectQuery);

        Cursor cursor = null;
        try{
            cursor = db.rawQuery(selectQuery, null);
            if(cursor!=null){
                if(cursor.moveToFirst()){

                    List<Course> courseList = new ArrayList<>();

                    do{
                        //getting info from cursor, creating a new Course object with info, adding this course obj to courseList
                        //this is for one row
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COURSE_ID));
                        invigilatorID = cursor.getInt(cursor.getColumnIndex(Config.COURSE_INVIGILATOR_ID));
                        String title = cursor.getString(cursor.getColumnIndex(Config.COURSE_TITLE));
                        String code = cursor.getString(cursor.getColumnIndex(Config.COURSE_CODE));

                        courseList.add(new Course(id,invigilatorID,title,code));

                    }while(cursor.moveToNext()); //go to next row of db

                    return courseList;
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

    public long insertCourse(Course course){

        if(!checkCourseAlreadyExists(course)) {

            long id = -1;

            //we want to write to database so we choose getWritableDatabase()
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(Config.COURSE_INVIGILATOR_ID, course.getInvigilator_id());
            contentValues.put(Config.COURSE_TITLE, course.getTitle());
            contentValues.put(Config.COURSE_CODE, course.getCode());

            try {
                id = db.insertOrThrow(Config.COURSE_TABLE_NAME, null, contentValues);
            } catch (SQLException e) {
                Log.d(TAG, "Exception: " + e.getMessage());
                Toast.makeText(context, "Operation Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();

            } finally {
                db.close();
            }
            return id;
        }

        else
            return 0;
    }

    public boolean checkCourseAlreadyExists(Course course){

        SQLiteDatabase db = this.getReadableDatabase();
        String courseCode = course.getCode();

       String selectQuery = "SELECT  * FROM " + Config.COURSE_TABLE_NAME + " WHERE " + Config.COURSE_CODE + " =?";
       Cursor cursor = db.rawQuery(selectQuery, new String[] {courseCode});

       int cursorCount = cursor.getCount();
       db.close();

       if(cursorCount <= 0){
           return false;
       }
       else
           return true;
    }



    /**
     * Add Invigilator to Data Base
     * @param user The invigilator to be added
     */
    public void addInvigilator(User user){
        SQLiteDatabase db = this.getWritableDatabase();

        // Create content values that hold information
        long userId = -1;
        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.INVIGILATOR_FIRST_NAME, user.getFirstName());
        contentValues.put(Config.INVIGILATOR_LAST_NAME, user.getLastName());
        contentValues.put(Config.INVIGILATOR_USERNAME, user.getUserName());
        contentValues.put(Config.INVIGILATOR_PASSWORD, user.getPassword());

        // Insert row - Throws Exception
        try{
             userId = db.insertOrThrow(Config.INVIGILATOR_TABLE_NAME, null, contentValues);

        }catch (SQLException e ){
            Log.d(TAG, "Exception: " + e.getMessage());
            Toast.makeText(context, "Operation Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally {
            db.close();
        }
    }

    /**
     * Verify if User (Invigilator) is in the Data Base.
     * @param UserName The username string to be verified.
     * @param Password The password string to be verified.
     * @return True if the invigilator is in the data base or false if he/she is not
     */
    public boolean verifyInvigilator(String UserName, String Password){

        SQLiteDatabase db = this.getReadableDatabase();
        // Array of columns to fetch
        String[] columnInvigilatorTable = {
                Config.INVIGILATOR_ID
        };

        // Selecting the desired criteria
        String selection = Config.INVIGILATOR_USERNAME + " = ?" + " AND " + Config.INVIGILATOR_PASSWORD + " = ?";

        // Selection arguments
        String[] selectionArg = {UserName, Password};

        // Query user table
        Cursor cursor = db.query(Config.INVIGILATOR_TABLE_NAME,
                columnInvigilatorTable,
                selection,
                selectionArg,
                null,
                null,
                null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    /**
     * Get a particular invigilator from the data base.
     * @param userID The id of the invigilator to be recovered
     * @return The invigilator that was recovered from the data base
     */
    public User getInvigilator(long userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Selecting desired criteria
        String selectQuery = "SELECT * " + " FROM " + Config.INVIGILATOR_TABLE_NAME + " WHERE " + Config.INVIGILATOR_ID + " = " + userID;

        try {
             cursor = db.rawQuery(selectQuery, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int id = cursor.getInt(cursor.getColumnIndex(Config.INVIGILATOR_ID));
                    String firstName = cursor.getString(cursor.getColumnIndex(Config.INVIGILATOR_FIRST_NAME));
                    String lastName = cursor.getString(cursor.getColumnIndex(Config.INVIGILATOR_LAST_NAME));
                    String userName = cursor.getString(cursor.getColumnIndex(Config.INVIGILATOR_USERNAME));
                    String password = cursor.getString(cursor.getColumnIndex(Config.INVIGILATOR_PASSWORD));

                    return new User(id, firstName, lastName, userName, password);
                }
             }
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
    return new User();
    }


    /**
     * Get a particular invigilator from the data base.
     * @param user_name The id of the invigilator to be recovered
     * @return The invigilator that was recovered from the data base
     */
    public User getInvigilator(String user_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Selecting desired criteria
        String selectQuery = "SELECT * " + " FROM " + Config.INVIGILATOR_TABLE_NAME + " WHERE " + Config.INVIGILATOR_USERNAME + " = '" + user_name + "'";

        try {
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int id = cursor.getInt(cursor.getColumnIndex(Config.INVIGILATOR_ID));
                    String firstName = cursor.getString(cursor.getColumnIndex(Config.INVIGILATOR_FIRST_NAME));
                    String lastName = cursor.getString(cursor.getColumnIndex(Config.INVIGILATOR_LAST_NAME));
                    String userName = cursor.getString(cursor.getColumnIndex(Config.INVIGILATOR_USERNAME));
                    String password = cursor.getString(cursor.getColumnIndex(Config.INVIGILATOR_PASSWORD));

                    return new User(id, firstName, lastName, userName, password);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return new User();
    }

}

