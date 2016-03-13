package util;

import android.text.TextUtils;

import db.JasonWeatherDB;
import model.City;
import model.County;
import model.Province;

/**
 * Created by Jason on 2016/3/13.
 */
public class Utility {
    /*
    *解析和处理服务器返回的省级数据
    * */
    public synchronized static boolean handleProvincesResponse(JasonWeatherDB jasonWeatherDB,String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces=response.split(",");
            if(allProvinces!=null && allProvinces.length>0){
                for(String p:allProvinces){
                    String[] array=p.split("\\|");
                    Province province=new Province();
                    province.setProvinceName(array[1]);
                    province.setProvinceCode(array[0]);
                    //将解析的数据存储到数据表Province中
                    jasonWeatherDB.saveProvince(province);
                }
            }
            return true;
        }
        return false;
    }

    /*
    * 解析和处理服务器返回的市级数据
    * */
    public static boolean handleCitiesResponse(JasonWeatherDB jasonWeatherDB,String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            String [] allCities=response.split(",");
            if(allCities!=null && allCities.length>0){
                for(String c: allCities){
                    City city=new City();
                    String [] array=c.split("\\|");
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    jasonWeatherDB.saveCity(city);
                }
            }
            return true;
        }
        return false;
    }

    /*
    * 解析和处理服务器返回的县级数据
    * */

    public static boolean hanldeCountyResponse(JasonWeatherDB jasonWeatherDB,String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            String [] allCounties=response.split(",");
            if(response!=null && response.length()>0)
            {
                for(String c:allCounties){
                    County county=new County();
                    String[] array=c.split("\\|");
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    jasonWeatherDB.saveCounty(county);
                }
            }
            return true;
        }
        return false;
    }
}
