package by.bsuir.relaxapp;

import static by.bsuir.relaxapp.MainActivity.CURR_USER_DB_INFO;
import static by.bsuir.relaxapp.MainActivity.DB_HELPER;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import org.intellij.lang.annotations.PrintFormat;

public class Mood {
    public int moodImageResourceID;
    public String name;
    public String tableColumnName;

    public Mood(int moodImageResourceID, String name, String tableColumnName, int clickedCount) {
        this.moodImageResourceID = moodImageResourceID;
        this.name = name;
        this.tableColumnName = tableColumnName;
    }


}
