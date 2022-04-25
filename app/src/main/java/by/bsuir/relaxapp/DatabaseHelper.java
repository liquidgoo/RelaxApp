package by.bsuir.relaxapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dbRelaxApp";
    public static final String TABLE_USERS = "users";

    //=============================================================================

    public static String KEY_id = "_id";
    public static String KEY_weight = "weight";
    public static String KEY_height = "height";
    public static String KEY_sysPressure = "sysPressure";
    public static String KEY_diaPressure = "diaPressure";
    public static String KEY_age = "age";
    public static String KEY_zodiac = "zodiac";


    public static String KEY_profilePic = "profilePic";
    public static String SUPP_pic_str = "pic";
    public static String KEY_pic0 = "pic0";
    public static String KEY_pic1 = "pic1";
    public static String KEY_pic2 = "pic2";
    public static String KEY_pic3 = "pic3";
    public static String KEY_pic4 = "pic4";
    public static String KEY_pic5 = "pic5";
    public static String KEY_realImageCount = "realImageCount";

    //=============================================================================


    public static final String TABLE_MOODS = "moods";
    public static String KEY_calm = "calm";
    public static String KEY_relax = "relax";
    public static String KEY_focus = "focus";
    public static String KEY_excited = "excited";
    public static String KEY_authentic = "authentic";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_USERS + "(" +
                KEY_id + " text primary key," +
                KEY_weight + " integer," +
                KEY_height + " integer," +
                KEY_sysPressure + " integer," +
                KEY_diaPressure + " integer," +
                KEY_age + " integer," +
                KEY_zodiac + " integer," +
                KEY_profilePic + " blob," +
                KEY_pic0 + " blob," +
                KEY_pic1 + " blob," +
                KEY_pic2 + " blob," +
                KEY_pic3 + " blob," +
                KEY_pic4 + " blob," +
                KEY_pic5 + " blob," +
                KEY_realImageCount + " integer"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_USERS);
        onCreate(sqLiteDatabase);
    }
}
