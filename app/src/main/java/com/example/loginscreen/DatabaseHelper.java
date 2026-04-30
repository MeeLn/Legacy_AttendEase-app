package com.example.loginscreen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "school.db";
    private static final int DATABASE_VERSION = 2; // Incremented database version

    public static final String TABLE_STUDENT = "student_table";
    public static final String COLUMN_STUDENT_ID = "student_id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_DEPARTMENT = "department";
    public static final String COLUMN_ROLL = "roll";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_CONFIRM_PASSWORD = "confirm_password";
    public static final String COLUMN_STUDENT_STATUS = "status"; // New column for student status

    private static final String STUDENT_TABLE_CREATE =
            "CREATE TABLE " + TABLE_STUDENT + " (" +
                    COLUMN_STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FIRST_NAME + " TEXT, " +
                    COLUMN_LAST_NAME + " TEXT, " +
                    COLUMN_DEPARTMENT + " TEXT, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_ROLL + " INTEGER, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_CONFIRM_PASSWORD + " TEXT, " +
                    COLUMN_STUDENT_STATUS + " TEXT DEFAULT 'inactive');"; // New column for student status

    public static final String TABLE_COURSE = "course_table";
    public static final String COLUMN_COURSE_ID = "course_id";
    public static final String COLUMN_COURSE_NAME = "course_name";
    public static final String COLUMN_COURSE_CREDITS = "course_credits";
    public static final String COLUMN_COURSE_STATUS = "course_status"; // New column for course status


    private static final String COURSE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_COURSE + " (" +
                    COLUMN_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_COURSE_NAME + " TEXT, " +
                    COLUMN_COURSE_CREDITS + " INTEGER, " +
                    COLUMN_COURSE_STATUS + " TEXT DEFAULT 'not active');"; // New column for course status

    public static final String TABLE_TEACHER = "teacher_table";
    public static final String COLUMN_TEACHER_ID = "teacher_id";
    public static final String COLUMN_TEACHER_FIRST_NAME = "first_name";
    public static final String COLUMN_TEACHER_LAST_NAME = "last_name";
    public static final String COLUMN_TEACHER_EMAIL = "teacher_email";
    public static final String COLUMN_TEACHER_PASSWORD = "teacher_password";
    public static final String COLUMN_TEACHER_CONFIRM_PASSWORD = "teacher_confirm_password";
    public static final String COLUMN_TEACHER_STATUS = "teacher_status"; // New column for teacher status

    private static final String TEACHER_TABLE_CREATE =
            "CREATE TABLE " + TABLE_TEACHER + " (" +
                    COLUMN_TEACHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TEACHER_FIRST_NAME + " TEXT, " +
                    COLUMN_TEACHER_LAST_NAME + " TEXT, " +
                    COLUMN_TEACHER_EMAIL + " TEXT, " +
                    COLUMN_TEACHER_PASSWORD + " TEXT, " +
                    COLUMN_TEACHER_CONFIRM_PASSWORD + " TEXT, " +
                    COLUMN_TEACHER_STATUS + " TEXT DEFAULT 'inactive');"; // New column for teacher status

    public static final String TABLE_DEPARTMENT = "department_table";
    public static final String COLUMN_DEPARTMENT_ID = "department_id";
    public static final String COLUMN_DEPARTMENT_NAME = "department_name";

    private static final String DEPARTMENT_TABLE_CREATE =
            "CREATE TABLE " + TABLE_DEPARTMENT + " (" +
                    COLUMN_DEPARTMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DEPARTMENT_NAME + " TEXT);";

    public static final String TABLE_ATTENDANCE = "attendance_table";
    public static final String COLUMN_ATTENDANCE_ID = "attendance_id";
    public static final String COLUMN_ATTENDANCE_DATE = "attendance_date";

    private static final String ATTENDANCE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_ATTENDANCE + " (" +
                    COLUMN_ATTENDANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_STUDENT_ID + " INTEGER, " +
                    COLUMN_COURSE_ID + " INTEGER, " +
                    COLUMN_ATTENDANCE_DATE + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_STUDENT_ID + ") REFERENCES " + TABLE_STUDENT + "(" + COLUMN_STUDENT_ID + "), " +
                    "FOREIGN KEY(" + COLUMN_COURSE_ID + ") REFERENCES " + TABLE_COURSE + "(" + COLUMN_COURSE_ID + "));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("DatabaseHelper", "DatabaseHelper constructor called");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "onCreate called");
        db.execSQL(STUDENT_TABLE_CREATE);
        db.execSQL(COURSE_TABLE_CREATE);
        db.execSQL(TEACHER_TABLE_CREATE);
        db.execSQL(DEPARTMENT_TABLE_CREATE);
        db.execSQL(ATTENDANCE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "onUpgrade called from " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPARTMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        onCreate(db);
    }

    // Method to delete a student
    public boolean deleteStudent(int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_STUDENT, COLUMN_STUDENT_ID + "=?", new String[]{String.valueOf(studentId)}) > 0;
    }

    // Method to delete a teacher
    public boolean deleteTeacher(int teacherId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TEACHER, COLUMN_TEACHER_ID + "=?", new String[]{String.valueOf(teacherId)}) > 0;
    }

    public Cursor getStudentData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_STUDENT;
        return db.rawQuery(query, null);
    }

    public Cursor getTeacherData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TEACHER; // Corrected query for teacher data
        return db.rawQuery(query, null);
    }

    public Cursor getCourseData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COURSE;
        return db.rawQuery(query, null);
    }

    public Cursor getDepartmentData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_DEPARTMENT;
        return db.rawQuery(query, null);
    }

    public boolean isDepartmentExists(String departmentName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COLUMN_DEPARTMENT_NAME };
        String selection = COLUMN_DEPARTMENT_NAME + " = ?";
        String[] selectionArgs = { departmentName };

        Cursor cursor = db.query(
                TABLE_DEPARTMENT,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public String getStudentName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { DatabaseHelper.COLUMN_FIRST_NAME, DatabaseHelper.COLUMN_LAST_NAME };
        String selection = DatabaseHelper.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_STUDENT,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String studentName = null;
        if (cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LAST_NAME));
            studentName = firstName + " " + lastName;
        }

        cursor.close();
        return studentName;
    }

    public String getTeacherName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { DatabaseHelper.COLUMN_TEACHER_FIRST_NAME, DatabaseHelper.COLUMN_TEACHER_LAST_NAME };
        String selection = DatabaseHelper.COLUMN_TEACHER_EMAIL + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_TEACHER,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String teacherName = null;
        if (cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEACHER_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEACHER_LAST_NAME));
            teacherName = firstName + " " + lastName;
        }

        cursor.close();
        return teacherName;
    }
    // Method to update course status
    public boolean updateCourseStatus(long courseId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_COURSE_STATUS, status);
        int rowsAffected = db.update(TABLE_COURSE, contentValues, COLUMN_COURSE_ID + " = ?", new String[]{String.valueOf(courseId)});
        return rowsAffected > 0;
    }

    public boolean toggleCourseStatus(long courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(
                TABLE_COURSE,
                new String[]{COLUMN_COURSE_STATUS},
                COLUMN_COURSE_ID + " = ?",
                new String[]{String.valueOf(courseId)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String currentStatus = cursor.getString(cursor.getColumnIndex(COLUMN_COURSE_STATUS));
            String newStatus = currentStatus.equals("active") ? "not active" : "active";

            ContentValues values = new ContentValues();
            values.put(COLUMN_COURSE_STATUS, newStatus);

            int rowsAffected = db.update(TABLE_COURSE, values, COLUMN_COURSE_ID + " = ?", new String[]{String.valueOf(courseId)});
            cursor.close();
            return rowsAffected > 0;
        }
        if (cursor != null) {
            cursor.close();
        }
        return false;
    }
    public Cursor getCourseById(long courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_COURSE,
                null,
                COLUMN_COURSE_ID + " = ?",
                new String[]{String.valueOf(courseId)},
                null,
                null,
                null
        );
    }

    public Cursor getActiveCourses() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COURSE + " WHERE " + COLUMN_COURSE_STATUS + " = 'active'";
        return db.rawQuery(query, null);
    }


    // Check if attendance has already been recorded for the student, course, and date
    public boolean isAttendanceRecorded(long studentId, long courseId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_ATTENDANCE +
                " WHERE " + COLUMN_STUDENT_ID + " = ? AND " +
                COLUMN_COURSE_ID + " = ? AND " + COLUMN_ATTENDANCE_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId), String.valueOf(courseId), date});

        boolean isRecorded = false;
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            isRecorded = count > 0;
        }
        cursor.close();
        return isRecorded;
    }

    // Method to mark attendance
    public boolean markAttendance(long studentId, long courseId) {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (isAttendanceRecorded(studentId, courseId, todayDate)) {
            return false; // Attendance already recorded for today
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_ID, studentId);
        values.put(COLUMN_COURSE_ID, courseId);
        values.put(COLUMN_ATTENDANCE_DATE, todayDate);
        long result = db.insert(TABLE_ATTENDANCE, null, values);

        return result != -1; // Return true if insert was successful
    }

    public Cursor getAttendanceByCourse(long courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ATTENDANCE + " WHERE " + COLUMN_COURSE_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(courseId)});
    }

    public Cursor getAttendanceByStudent(long studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ATTENDANCE + " WHERE " + COLUMN_STUDENT_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(studentId)});
    }

    private String getCurrentDate() {
        // Return the current date in a format of your choice, e.g., "YYYY-MM-DD"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public long getStudentIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COLUMN_STUDENT_ID };
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = db.query(
                TABLE_STUDENT,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        long studentId = -1;
        if (cursor.moveToFirst()) {
            studentId = cursor.getLong(cursor.getColumnIndex(COLUMN_STUDENT_ID));
        }

        cursor.close();
        return studentId;
    }
    // Get student ID by roll number
    public long getStudentIdByRollNo(String rollNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_STUDENT_ID + " FROM " + TABLE_STUDENT + " WHERE " + COLUMN_ROLL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{rollNo});

        long studentId = -1;
        if (cursor.moveToFirst()) {
            studentId = cursor.getLong(cursor.getColumnIndex(COLUMN_STUDENT_ID));
        }
        cursor.close();
        return studentId;
    }

    // Get attendance records for a student
    public Cursor getAttendanceForStudent(long studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ATTENDANCE + " WHERE " + COLUMN_STUDENT_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(studentId)});
    }

    // Get course name by course ID
    public String getCourseNameById(long courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_COURSE_NAME + " FROM " + TABLE_COURSE + " WHERE " + COLUMN_COURSE_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(courseId)});

        String courseName = null;
        if (cursor.moveToFirst()) {
            courseName = cursor.getString(cursor.getColumnIndex(COLUMN_COURSE_NAME));
        }
        cursor.close();
        return courseName;
    }
    // Get course ID by course name
    public long getCourseIdByName(String courseName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_COURSE_ID + " FROM " + TABLE_COURSE + " WHERE " + COLUMN_COURSE_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{courseName});

        long courseId = -1;
        if (cursor.moveToFirst()) {
            courseId = cursor.getLong(cursor.getColumnIndex(COLUMN_COURSE_ID));
        }
        cursor.close();
        return courseId;
    }

    // Get attendance records for a student in a specific course
    public Cursor getAttendanceForStudentInCourse(long studentId, long courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ATTENDANCE +
                " WHERE " + COLUMN_STUDENT_ID + " = ? AND " + COLUMN_COURSE_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(studentId), String.valueOf(courseId)});
    }

    // Get all students
    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_STUDENT;
        return db.rawQuery(query, null);
    }

    // Get all teachers
    public Cursor getAllTeachers() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TEACHER;
        return db.rawQuery(query, null);
    }

    // Toggle user status
    public boolean toggleUserStatus(String userType, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table = userType.equals("Student") ? TABLE_STUDENT : TABLE_TEACHER;
        String column = userType.equals("Student") ? COLUMN_STUDENT_STATUS : COLUMN_TEACHER_STATUS;

        // Get current status
        String query = "SELECT " + column + " FROM " + table + " WHERE " + (userType.equals("Student") ? COLUMN_STUDENT_ID : COLUMN_TEACHER_ID) + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            String currentStatus = cursor.getString(cursor.getColumnIndex(column));
            String newStatus = currentStatus.equals("active") ? "inactive" : "active";

            ContentValues values = new ContentValues();
            values.put(column, newStatus);

            int rowsUpdated = db.update(table, values, (userType.equals("Student") ? COLUMN_STUDENT_ID : COLUMN_TEACHER_ID) + " = ?", new String[]{String.valueOf(userId)});
            cursor.close();
            return rowsUpdated > 0;
        }
        cursor.close();
        return false;
    }

}