package db;

import android.content.Context;
import android.database.sqlite.*;

/**
 * Created by Jason on 2016/3/13.
 */
public class JasonWeatherOpenHelper extends SQLiteOpenHelper {

    /*
    * Province表 建表语句
    * */
    public static final String CREATE_PROVINCE="create table Province("
            +"id integer primary key autoincrement, "
            +"province_name text,"
            +"province_code )";
    /*
    * City表 建表语句
    * */
    public static final String CREATE_CITY="create table City("
            +"id integer primary key autoincrement,"
            +"city_name text,"
            +"city_code text,"
            +"province_id integer)";

    /*
    * County表 建表语句
    * */
    public static final String CREATE_COUNTY="create table County("
            +"id integer primary key autoincrement,"
            +"county_name text,"
            +"county_code text,"
            +"city_id integer)";

    public JasonWeatherOpenHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
