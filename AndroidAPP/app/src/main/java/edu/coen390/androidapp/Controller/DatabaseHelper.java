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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.Invigilator;
import edu.coen390.androidapp.Model.Professor;
import edu.coen390.androidapp.Model.Student;
import edu.coen390.androidapp.Model.UserType;

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
    private final Context context;


    /**
     * DatabaseHelper constructor.
     *
     * @param context The context from the calling activity.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.d(TAG, "Inside DatabaseHelper constructor.");
    }

    /**
     * Used to convert an array into a comma-separated string to be stored in the DB.
     *
     * @param array Array of values to be converted to comma-separated strings
     * @return Comma-separated string equivalent of the array
     */
    public static String convertArrayToString(String[] array) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            str.append(array[i]);

            // Do not append comma at the end of last element
            if (i < array.length - 1) {
                str.append(strSeparator);
            }
        }
        return str.toString();
    }

    // =============================== Student Table Methods ===============================

    /**
     * Used to convert a comma-separated string to an array.
     *
     * @param str The comma-separated string to be converted into an array.
     * @return The equivalent array
     */
    public static String[] convertStringToArray(String str) {
        return str.split(strSeparator);
    }

    /**
     * Called when a DatabaseHelper object is created and creates the DB tables.
     *
     * @param sqLiteDatabase The reference to the SQLite database being created.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "DatabaseHelper onCreate method called.");
        createTestTables(sqLiteDatabase);   //TODO: to be replaced when cloud database is setup.
    }

    //TODO: to be replaced when cloud database is setup.
    public void createTestData() {
        createTestInvigilators();
        createTestProfessors();
        createTestCourses();
        createTestStudents();
    }

    //TODO: to be replaced when cloud database is setup.
    private void createTestProfessors() {
        Professor professor1 = new Professor(-1, new String[]{"ENGR 391", "COMP 472"},
                "Dwight", "Schrute", "d_schrute", "1234");
        Professor professor2 = new Professor(-1, new String[]{"ENGR 391", "COMP 472"},
                "Michael", "Scott", "m_scott", "5678");
        addProfessor(professor1);
        addProfessor(professor2);
    }

    //TODO: to be replaced when cloud database is setup.
    private void createTestStudents() {
        Student student1 = new Student(26603157, new String[]{"ENGR 391", "COMP 472"},
                "Karim", "Rhoualem", "k_rhoua", "1234");
        Student student2 = new Student(12345678, new String[]{"ENGR 391", "ELEC 331"},
                "Jane", "Doe", "j_doe", "5678");
        Student obama = new Student(45454545, new String[]{"ENGR 391", "COMP 472"},
                "Barack", "Obama", "b_obama", "1234");
        Student tawfiq = new Student(40000390, new String[]{"ENGR 391", "COEN 313"},
                "Tawfiq", "Jawhar", "t_jawhar", "1234");
        Student hamill = new Student(40102453, new String[]{"ENGR 391", "COEN 313"},
                "Mark", "Hamill", "m_hamill", "1234");
        Student alec = new Student(40103773, new String[]{"ENGR 391", "COEN 313"},
                "Alec", "Wolfe", "a_wolfe", "1234");
        insertStudent(student1);
        insertStudent(student2);
        insertStudent(obama);
        insertStudent(tawfiq);
        insertStudent(hamill);
        insertStudent(alec);
    }

    //TODO: to be replaced when cloud database is setup.
    private void createTestInvigilators() {
        Invigilator invigilator1 = new Invigilator(-1, new String[]{"ENGR 391", "COMP 472"},
                "John", "Doe", "j_doe", "1234");
        Invigilator invigilator2 = new Invigilator(-1, new String[]{"ENGR 391", "COMP 472"},
                "Karen", "Land", "k_lan", "5678");
        addInvigilator(invigilator1);
        addInvigilator(invigilator2);
    }

    //TODO: to be replaced when cloud database is setup.
    private void createTestCourses() {
        insertCourse(new Course(-1, "Numerical Methods", "ENGR 391", 100));
        insertCourse(new Course(-1, "Fundamentals of Electrical Power", "ELEC 331", 50));
        insertCourse(new Course(-1, "Digital Systems Design II", "COEN 313", 40));
        insertCourse(new Course(-1, "Artificial Intelligence", "COMP 472", 70));
    }

    //TODO: to be replaced when cloud database is setup.
    private void createTestTables(SQLiteDatabase sqLiteDatabase) {
        //Store SQL queries in strings.
        String CREATE_INVIGILATOR_TABLE = "CREATE TABLE " + Config.INVIGILATOR_TABLE_NAME + " ("
                + Config.INVIGILATOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.INVIGILATOR_COURSES + " TEXT, "
                + Config.INVIGILATOR_FIRST_NAME + " TEXT NOT NULL, "
                + Config.INVIGILATOR_LAST_NAME + " TEXT NOT NULL, "
                + Config.INVIGILATOR_USERNAME + " TEXT NOT NULL, "
                + Config.INVIGILATOR_PASSWORD + " TEXT NOT NULL, "
                + "FOREIGN KEY" + " (" + Config.INVIGILATOR_COURSES + ") "
                + "REFERENCES " + Config.COURSE_TABLE_NAME + " ("
                + Config.COURSE_ID + ")"
                + ")";
        Log.d(TAG, "Table created with this query: " + CREATE_INVIGILATOR_TABLE);

        String CREATE_PROFESSOR_TABLE = "CREATE TABLE " + Config.PROFESSOR_TABLE_NAME + " ("
                + Config.PROFESSOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.PROFESSOR_COURSES + " TEXT, "
                + Config.PROFESSOR_FIRST_NAME + " TEXT NOT NULL, "
                + Config.PROFESSOR_LAST_NAME + " TEXT NOT NULL, "
                + Config.PROFESSOR_USERNAME + " TEXT NOT NULL, "
                + Config.PROFESSOR_PASSWORD + " TEXT NOT NULL, "
                + "FOREIGN KEY" + " (" + Config.PROFESSOR_COURSES + ") "
                + "REFERENCES " + Config.COURSE_TABLE_NAME + " ("
                + Config.COURSE_ID + ")"
                + ")";
        Log.d(TAG, "Table created with this query: " + CREATE_PROFESSOR_TABLE);

        String CREATE_COURSE_TABLE = "CREATE TABLE " + Config.COURSE_TABLE_NAME + " ("
                + Config.COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.COURSE_TITLE + " TEXT NOT NULL, "
                + Config.COURSE_CODE + " TEXT NOT NULL, "
                + Config.COURSE_DESCRIPTION + " TEXT, "
                + Config.COURSE_TOTAL_ENROLLED + " INTEGER NOT NULL"
                + ")";
        Log.d(TAG, "Table created with this query: " + CREATE_COURSE_TABLE);

        String CREATE_STUDENT_TABLE = "CREATE TABLE " + Config.STUDENTS_TABLE_NAME + " ("
                + Config.STUDENT_ID + " INTEGER PRIMARY KEY, "
                + Config.STUDENT_COURSES + " TEXT, "
                + Config.STUDENT_FIRST_NAME + " TEXT NOT NULL, "
                + Config.STUDENT_LAST_NAME + " TEXT NOT NULL, "
                + Config.STUDENT_USERNAME + " TEXT NOT NULL, "
                + Config.STUDENT_PASSWORD + " TEXT NOT NULL, "
                + "FOREIGN KEY" + " (" + Config.STUDENT_COURSES + ") "
                + "REFERENCES " + Config.COURSE_TABLE_NAME + " ("
                + Config.COURSE_ID + ")"
                + ")";
        Log.d(TAG, "Table created with this query: " + CREATE_STUDENT_TABLE);

        //Create the tables.
        sqLiteDatabase.execSQL(CREATE_INVIGILATOR_TABLE);
        sqLiteDatabase.execSQL(CREATE_PROFESSOR_TABLE);
        sqLiteDatabase.execSQL(CREATE_COURSE_TABLE);
        sqLiteDatabase.execSQL(CREATE_STUDENT_TABLE);
    }

    /**
     * Called when the database version is upgraded.
     *
     * @param sqLiteDatabase The reference to the SQLite database being created.
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //To be handled in the event that the database version ever needs to be updated.
    }

    /**
     * Add Student to Database
     * @param student
     * @return
     */
    public long insertStudent(Student student) {
        long id = -1;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Config.STUDENT_ID, student.getId());
        contentValues.put(Config.STUDENT_COURSES, convertArrayToString(student.getCourses()));
        contentValues.put(Config.STUDENT_FIRST_NAME, student.getFirstName());
        contentValues.put(Config.STUDENT_LAST_NAME, student.getLastName());
        contentValues.put(Config.STUDENT_USERNAME, student.getUserName());
        contentValues.put(Config.STUDENT_PASSWORD, student.getPassword());

        try {
            id = db.insertOrThrow(Config.STUDENTS_TABLE_NAME, null, contentValues);
        } catch (SQLiteException e) {
            Log.d(TAG, "Exception: " + e.getMessage());
            //Toast.makeText(context, "Operation Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }

        return id;
    }

    /**
     * Get a specific student from database
     * @param studentId The Student that we are retrieving from database
     * @return The student object
     */
    public Student getStudent(long studentId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        // Selecting desired criteria
        String selectQuery = "SELECT * " + " FROM " + Config.STUDENTS_TABLE_NAME+ " WHERE " + Config.STUDENT_ID + " = " + studentId;

        try {
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int id = cursor.getInt(cursor.getColumnIndex(Config.STUDENT_ID));
                    String firstName = cursor.getString(cursor.getColumnIndex(Config.STUDENT_FIRST_NAME));
                    String lastName = cursor.getString(cursor.getColumnIndex(Config.STUDENT_LAST_NAME));
                    String[] courses = convertStringToArray(cursor.getString(cursor.getColumnIndex(Config.STUDENT_COURSES)));
                    String username = cursor.getString(cursor.getColumnIndex(Config.STUDENT_USERNAME));
                    String password = cursor.getString(cursor.getColumnIndex(Config.STUDENT_PASSWORD));

                    return new Student(id, courses, firstName, lastName, username, password);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return new Student();
    }

    /**
     * Verify if student is registered in a specific course
     * @param student
     * @param course
     * @return
     */

    public boolean isStudentRegisteredInCourse(Student student, Course course) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String selectQuery = "SELECT *" + " FROM " + Config.STUDENTS_TABLE_NAME
                + " WHERE " + Config.STUDENT_ID
                + " = " + student.getId();
        Log.d(TAG, selectQuery);

        try {
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String courses = cursor.getString(cursor.getColumnIndex(Config.STUDENT_COURSES));
                    String[] coursesArray = convertStringToArray(courses);

                    for (String c : coursesArray) {
                        if (c.equals(course.getCode())) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();

            db.close();
        }

        return false;
    }


    // =============================== Course Table Methods ===============================


    /**
     * Insert Course into database
     * @param course
     * @return
     */
    public long insertCourse(Course course) {

        if (!checkCourseAlreadyExists(course)) {

            long id = -1;

            //we want to write to database so we choose getWritableDatabase()
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(Config.COURSE_TITLE, course.getTitle());
            contentValues.put(Config.COURSE_CODE, course.getCode());
            contentValues.put(Config.COURSE_TOTAL_ENROLLED, course.getNumOfStudents());

            try {

                id = db.insertOrThrow(Config.COURSE_TABLE_NAME, null, contentValues);
            } catch (SQLException e) {
                Log.d(TAG, "Exception: " + e.getMessage());
                Toast.makeText(context, "Operation Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();

            } finally {
                db.close();
            }
            return id;
        } else
            return 0;
    }


    // =============================== Invigilator Table Methods ===============================

    /**
     * Get all courses from a specific invigilator or professor
     * @param userID
     * @return
     */
    public List<Course> getCourses(long userID, UserType userType) {
        Log.i(TAG, "getCourses was accessed");

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = null;
        if (userType == UserType.INVIGILATOR) {
            selectQuery = "SELECT * FROM " + Config.INVIGILATOR_TABLE_NAME + " WHERE " + Config.INVIGILATOR_ID + " = " + userID;
            Log.d(TAG, selectQuery);
        }
        else if (userType == UserType.PROFESSOR) {
            selectQuery = "SELECT * FROM " + Config.PROFESSOR_TABLE_NAME + " WHERE " + Config.PROFESSOR_ID + " = " + userID;
            Log.d(TAG, selectQuery);
        }

        Cursor cursor = null;
        Cursor courseCursor = null;
        try {
            cursor = db.rawQuery(selectQuery, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    List<Course> courseList = new ArrayList<>();

                    String[] courses = null;
                    if (userType == UserType.INVIGILATOR) {
                        courses = convertStringToArray(cursor.getString(cursor.getColumnIndex(Config.INVIGILATOR_COURSES)));
                    }
                    else if (userType == UserType.PROFESSOR) {
                        courses = convertStringToArray(cursor.getString(cursor.getColumnIndex(Config.PROFESSOR_COURSES)));
                    }

                    for (String course : courses) {
                        String courseQuery = "SELECT * FROM " + Config.COURSE_TABLE_NAME + " WHERE " + Config.COURSE_CODE + " = " + '"' + course + '"';
                        courseCursor = db.rawQuery(courseQuery, null);
                        if (courseCursor != null) {
                            if (courseCursor.moveToFirst()) {
                                int id = courseCursor.getInt(courseCursor.getColumnIndex(Config.COURSE_ID));
                                String title = courseCursor.getString(courseCursor.getColumnIndex(Config.COURSE_TITLE));
                                String code = courseCursor.getString(courseCursor.getColumnIndex(Config.COURSE_CODE));
                                int totalNumStudents = courseCursor.getInt(courseCursor.getColumnIndex(Config.COURSE_TOTAL_ENROLLED));

                                courseList.add(new Course(id, title, code, totalNumStudents));
                            }
                        }
                    }
                    return courseList;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        } finally {
            if (cursor != null && courseCursor != null)
                cursor.close();

            db.close();
        }
        return Collections.emptyList();
    }

    /**
     * Verify if a course exists
     * @param course
     * @return
     */
    public boolean checkCourseAlreadyExists(Course course) {
        SQLiteDatabase db = this.getReadableDatabase();
        String courseCode = course.getCode();

        String selectQuery = "SELECT  * FROM " + Config.COURSE_TABLE_NAME + " WHERE " + Config.COURSE_CODE + " =?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{courseCode});

        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        return cursorCount > 0;
    }

    /**
     * Add Invigilator to Data Base
     *
     * @param invigilator The invigilator to be added
     */
    public void addInvigilator(Invigilator invigilator) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Create content values that hold information
        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.INVIGILATOR_FIRST_NAME, invigilator.getFirstName());
        contentValues.put(Config.INVIGILATOR_LAST_NAME, invigilator.getLastName());
        contentValues.put(Config.INVIGILATOR_COURSES, convertArrayToString(invigilator.getCourses()));
        contentValues.put(Config.INVIGILATOR_USERNAME, invigilator.getUserName());
        contentValues.put(Config.INVIGILATOR_PASSWORD, invigilator.getPassword());

        // Insert row - Throws Exception
        try {
            db.insertOrThrow(Config.INVIGILATOR_TABLE_NAME, null, contentValues);
        } catch (SQLException e) {
            Log.d(TAG, "Exception: " + e.getMessage());
            Toast.makeText(context, "Operation Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

    public void addProfessor(Professor professor) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Create content values that hold information
        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.PROFESSOR_FIRST_NAME, professor.getFirstName());
        contentValues.put(Config.PROFESSOR_LAST_NAME, professor.getLastName());
        contentValues.put(Config.PROFESSOR_COURSES, convertArrayToString(professor.getCourses()));
        contentValues.put(Config.PROFESSOR_USERNAME, professor.getUserName());
        contentValues.put(Config.PROFESSOR_PASSWORD, professor.getPassword());

        // Insert row - Throws Exception
        try {
            db.insertOrThrow(Config.PROFESSOR_TABLE_NAME, null, contentValues);
        } catch (SQLException e) {
            Log.d(TAG, "Exception: " + e.getMessage());
            Toast.makeText(context, "Operation Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

    // =============================== Exam Table Methods ===============================

    /**
     * Verify if Invigilator (Invigilator) is in the Data Base.
     *
     * @param userName The username string to be verified.
     * @param password The password string to be verified.
     * @return True if the invigilator is in the data base or false if he/she is not
     */
    public boolean verifyInvigilator(String userName, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Array of columns to fetch
        String[] columnInvigilatorTable = {
                Config.INVIGILATOR_ID
        };

        // Selecting the desired criteria
        String selection = Config.INVIGILATOR_USERNAME + " = ?" + " AND " + Config.INVIGILATOR_PASSWORD + " = ?";

        // Selection arguments
        String[] selectionArg = {userName, password};

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

        return cursorCount > 0;
    }

    public boolean verifyProfessor(String userName, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Array of columns to fetch
        String[] columnInvigilatorTable = {
                Config.PROFESSOR_ID
        };

        // Selecting the desired criteria
        String selection = Config.PROFESSOR_USERNAME + " = ?" + " AND " + Config.PROFESSOR_PASSWORD + " = ?";

        // Selection arguments
        String[] selectionArg = {userName, password};

        // Query user table
        Cursor cursor = db.query(Config.PROFESSOR_TABLE_NAME,
                columnInvigilatorTable,
                selection,
                selectionArg,
                null,
                null,
                null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        return cursorCount > 0;
    }

    /**
     * Get a particular invigilator from the data base.
     * @param user_name The id of the invigilator to be recovered
     * @return The invigilator that was recovered from the data base
     */
    public Invigilator getInvigilator(String user_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Selecting desired criteria
        String selectQuery = "SELECT * " + " FROM " + Config.INVIGILATOR_TABLE_NAME + " WHERE " + Config.INVIGILATOR_USERNAME + " = '" + user_name + "'";

        try {
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int id = cursor.getInt(cursor.getColumnIndex(Config.INVIGILATOR_ID));
                    String[] courses = convertStringToArray(cursor.getString(cursor.getColumnIndex(Config.INVIGILATOR_COURSES)));
                    String firstName = cursor.getString(cursor.getColumnIndex(Config.INVIGILATOR_FIRST_NAME));
                    String lastName = cursor.getString(cursor.getColumnIndex(Config.INVIGILATOR_LAST_NAME));
                    String userName = cursor.getString(cursor.getColumnIndex(Config.INVIGILATOR_USERNAME));
                    String password = cursor.getString(cursor.getColumnIndex(Config.INVIGILATOR_PASSWORD));

                    return new Invigilator(id, courses, firstName, lastName, userName, password);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return new Invigilator();
    }

    public Professor getProfessor(String user_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Selecting desired criteria
        String selectQuery = "SELECT * " + " FROM " + Config.PROFESSOR_TABLE_NAME + " WHERE " + Config.PROFESSOR_USERNAME + " = '" + user_name + "'";

        try {
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int id = cursor.getInt(cursor.getColumnIndex(Config.PROFESSOR_ID));
                    String[] courses = convertStringToArray(cursor.getString(cursor.getColumnIndex(Config.PROFESSOR_COURSES)));
                    String firstName = cursor.getString(cursor.getColumnIndex(Config.PROFESSOR_FIRST_NAME));
                    String lastName = cursor.getString(cursor.getColumnIndex(Config.PROFESSOR_LAST_NAME));
                    String userName = cursor.getString(cursor.getColumnIndex(Config.PROFESSOR_USERNAME));
                    String password = cursor.getString(cursor.getColumnIndex(Config.PROFESSOR_PASSWORD));

                    return new Professor(id, courses, firstName, lastName, userName, password);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return new Professor();
    }

    /**
     * Create an Exam table for a particular course
     *
     * @param course The course conducting an examination
     */
    public void createExamTable(Course course) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String CREATE_EXAM_TABLE = "CREATE TABLE IF NOT EXISTS " +
                Config.EXAM_TABLE_NAME.get(course.getCode()) + " ("
                + Config.EXAM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.EXAM_COURSE_ID + " TEXT, "
                + Config.EXAM_STUDENT_ID + " INTEGER, "
                + Config.EXAM_STUDENT_SEAT + " INTEGER, "
                + "FOREIGN KEY" + " (" + Config.EXAM_COURSE_ID + ") "
                + "REFERENCES " + Config.COURSE_TABLE_NAME + " ("
                + Config.COURSE_ID + ") "
                + "FOREIGN KEY" + " (" + Config.EXAM_STUDENT_ID + ") "
                + "REFERENCES " + Config.STUDENTS_TABLE_NAME + " ("
                + Config.STUDENT_ID + ")"
                + ")";
        Log.d(TAG, "Table created with this query: " + CREATE_EXAM_TABLE);

        sqLiteDatabase.execSQL(CREATE_EXAM_TABLE);
    }

    /**
     * Assign Seat Number to student
     *
     * @param student Student that requires seating
     * @param course  Course that is hosting the examination
     */
    public void insertStudentSeat(Student student, Course course, int seat) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Create content values to hold information
        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.EXAM_COURSE_ID, course.getId());
        contentValues.put(Config.EXAM_STUDENT_ID, student.getId());
        contentValues.put(Config.EXAM_STUDENT_SEAT, seat);

        //Insert row - throw Exception
        try {
            db.insertOrThrow(Config.EXAM_TABLE_NAME.get(course.getCode()),
                    null, contentValues);
        } catch (SQLException e) {
            Log.d(TAG, "Exception: " + e.getMessage());
            Toast.makeText(context, "Operation Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }

    }


    // =============================== Helper Methods ===============================

    /**
     * Retrieve assigned student seat
     *
     * @param student Student that requires their seating to be retrieved
     * @param course  Course that is hosting the examination
     * @return Seat Number for @param student
     */
    public int getStudentSeat(Student student, Course course) {
        Log.i(TAG, "getStudentSeat was accessed");

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + Config.EXAM_TABLE_NAME.get(course.getCode())
                + " WHERE " + Config.EXAM_STUDENT_ID + " = " + student.getId();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int seat;
                    do {
                        //getting info from cursor, creating a new Course object with info,
                        //adding this course obj to courseList
                        //this is for one row
                        seat = cursor.getInt(cursor.getColumnIndex(Config.EXAM_STUDENT_SEAT));

                    } while (cursor.moveToNext()); //go to next row of db

                    return seat;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();

            db.close();
        }
        return -1;
    }

    /**
     * Verify if Student is already inserted into the exam table and assigned a seat
     *
     * @param student Student that requires verification
     * @param course  course being examined
     * @return if student is seated then return 1 (true)
     */
    public boolean isStudentSeated(Student student, Course course) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Array of columns to fetch
        String[] columnExamTable = {
                Config.EXAM_STUDENT_ID
        };

        // Selecting the desired criteria
        String selection = Config.EXAM_STUDENT_ID + " = ?";

        // Selection arguments
        String[] selectionArg = {String.valueOf(student.getId())};

        // Query user table
        Cursor cursor = db.query(Config.EXAM_TABLE_NAME.get(course.getCode()),
                columnExamTable,
                selection,
                selectionArg,
                null,
                null,
                null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        return cursorCount > 0;
    }
}

