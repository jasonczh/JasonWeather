package db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import model.City;
import model.County;
import model.Province;

/**
 * Created by Jason on 2016/3/13.
 */
public class JasonWeatherDB {
    /*
    * 数据库名
    * */
    public static final String DB_NAME="jason_weather";

    /*
    * 数据库版本
    * */
    private  static final int VERSION=1;

    private static JasonWeatherDB jasonWeatherDB;
    private SQLiteDatabase db;

    /*
    * 将构造方法私有化  单例设计模式  懒汉式
    * @param context 对应的上下文
    * */
    private JasonWeatherDB(Context context){
        JasonWeatherOpenHelper dbOpenHelper=new JasonWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db=dbOpenHelper.getWritableDatabase();//获得读写实例
    }

    public synchronized static JasonWeatherDB getInstance(Context context){
        if(jasonWeatherDB!=null){
            jasonWeatherDB=new JasonWeatherDB(context);
        }
        return jasonWeatherDB;
    }

    /*
    * 将Province实例存储到数据库
    * */

    public void saveProvince(Province province){
        if(province!=null)
        {
            db.execSQL("insert into Province(id,province_name,province_code) values(?,?,?)",new Object[]{province.getId(),province.getProvinceName(),province.getProvinceCode()});
        }
    }
    /*
    * 从数据库读取全国所有省份信息
    * */
    public List<Province> loadProvinces(){
        List<Province> provinces=new ArrayList<Province>();
        Cursor cursor=db.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Province province=new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provinces.add(province);
            }
            while (cursor.moveToNext());
        }
        return provinces;
    }
    /*
    * 将City实例存储到数据库
    * */
    public void saveCity(City city){
        if(city!=null){
            db.execSQL("insert into City(id,city_name,city_code,province_id) values(?,?,?,?)",
                    new Object[]{city.getId(), city.getCityName(),city.getCityCode(),city.getProvinceId()});
        }
    }

    /*
    * 从数据库读取某省的所有城市信息
    * */
    public List<City> loadCities(int provinceId){
        List<City> cities=new ArrayList<City>();
        Cursor cursor=db.query("City",null,"where province_id=?",new String[]{String.valueOf(provinceId)},null,null,null);
        /*
        * 判断是否为空
        * */
        if(cursor.moveToFirst()){
            do{
                City city=new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                cities.add(city);
            }
            while(cursor.moveToNext());
        }
        return cities;
    }
    /*
    * 将county实例存储到数据库
    * */

    public void saveCounty(County county){
        if(county!=null){
            db.execSQL("insert into County(id,county_name,county_code,city_id) values (?,?,?,?)",new Object[]{county.getId(),county.getCountyName(),county.getCountyCode(),county.getCityId()});

        }
    }

    /*
    * 从数据库读取County的信息
    * */
    public List<County> loadCounties(int cityId){
        List<County> counties=new ArrayList<County>();
        Cursor cursor=db.query("County",null,"where city_id=?",new String[]{String.valueOf(cityId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                County county=new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
            }
            while(cursor.moveToNext());
        }
        return counties;
    }
}
