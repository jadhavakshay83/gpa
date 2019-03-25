package in.hatcube.gpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "GPA.db";
    public static final String USERS_TABLE_NAME = "users";
    public static final String USERS_COLUMN_NAME = "name";
    public static final String USERS_COLUMN_EMAIL = "email";
    public static final String USERS_COLUMN_PHONE = "phone";
    public static final String USERS_COLUMN_PASSWORD = "password";
    public static final String USERS_COLUMN_AUTH = "auth";
    public static final String USERS_COLUMN_GPA = "gpa";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table users " +
                        "(email string primary key, name text,phone text, password text,auth text,gpa text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public boolean insertUser (String name, String phone, String email, String password,String auth, String gpa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERS_COLUMN_NAME, name);
        contentValues.put(USERS_COLUMN_PHONE, phone);
        contentValues.put(USERS_COLUMN_EMAIL, email);
        contentValues.put(USERS_COLUMN_PASSWORD, password);
        contentValues.put(USERS_COLUMN_AUTH, auth);
        contentValues.put(USERS_COLUMN_GPA, gpa);

        db.insert(USERS_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateGPA (String email, String gpa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERS_COLUMN_GPA, gpa);

        db.update(USERS_TABLE_NAME, contentValues,"email = ?",new String[] { email });
        return true;
    }

    public Cursor getUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM users WHERE email='"+email+"'", null );
        return res;
    }
}