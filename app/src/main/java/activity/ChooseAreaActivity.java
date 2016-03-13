package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jason.jasonweather.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import db.JasonWeatherDB;
import model.City;
import model.County;
import model.Province;
import util.HttpCallBackListener;
import util.HttpUtil;
import util.Utility;

/**
 * Created by Jason on 2016/3/13.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private JasonWeatherDB jasonWeatherDB;
    private List<String> dataList=new ArrayList<String>();

    /*
    * 省列表
    * */
    private List<Province> provinceList;
    /*
    * 市列表
    * */
    private List<City> cityList;
    /*
    * 县列表
    * */
    private List<County> countyList;
    /*
    * 选中的省份
    * */
    private Province selectedProvince;
    /*
    * 选中的城市
    * */
    private City selectCity;
    /*
    * 选择的县
    * */
    private County selectCounty;

    /*
    * 选择的级别
    * */
    private int currentLevel;

    private long currentTime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView= (ListView) findViewById(R.id.list_view);
        titleText= (TextView) findViewById(R.id.title_text);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        jasonWeatherDB=JasonWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }
                else if(currentLevel==LEVEL_CITY){
                    selectCity=cityList.get(position);
                    queryCounties();
                }
            }
        });
        queryProvinces();
    }
    /*
    * 查询全国所有的省，优先从数据库查询，如果没有查询到，再去服务器查询
    * */
    private void queryProvinces(){
        provinceList=jasonWeatherDB.loadProvinces();
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }else{
            queryFromServer(null,"province");
        }
    }
    /*
    * 查询选中的省内所有的市，优先从数据库中查询，如果没有在去服务器上查询
    * */

    private void queryCities(){
        cityList=jasonWeatherDB.loadCities(selectedProvince.getId());
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }else{
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }

    /*
    * 查询选中市内的所有县，优先从数据库查询，如果没有查询到再去服务器上查询
    * */
    private void queryCounties(){
        countyList=jasonWeatherDB.loadCounties(selectCity.getId());
        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectCity.getCityName());
            currentLevel=LEVEL_COUNTY;
        }
        else
        {
            queryFromServer(selectCity.getCityCode(),"county");
        }
    }
    /*
    * g根据传入的代号和类型从服务器上查询省县市数据
    * */
    private void queryFromServer(final String code,final String type){
        String address="";
        if(!TextUtils.isEmpty(code)){
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else
        {
            address="http://www.weather.com.cn/data/city3jdata/china.html";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handleProvincesResponse(jasonWeatherDB,response);
                }else if("city".equals(type)){
                    result=Utility.handleCitiesResponse(jasonWeatherDB,response,selectedProvince.getId());
                }else if("county".equals(type)){
                    result=Utility.hanldeCountyResponse(jasonWeatherDB,response,selectCity.getId());
                }
                if(result){
                    //通过runOnUiThread方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("provice".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                //通过runOnUiThread方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    /*
    * 显示进度对话框
    * */
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /*
    * 关闭进度对话框
    * */
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    /*
    * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
    * */

    @Override
    public void onBackPressed() {
        if(currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel==LEVEL_CITY)
        {
            queryProvinces();
        }
        else{
            if(System.currentTimeMillis()-currentTime>2000){
                currentTime=System.currentTimeMillis();
                Toast.makeText(ChooseAreaActivity.this,"再按一次Back键就退出程序",Toast.LENGTH_SHORT).show();
            }else {
                finish();
            }
        }
    }
}

