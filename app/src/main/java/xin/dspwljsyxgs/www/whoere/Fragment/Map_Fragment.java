package xin.dspwljsyxgs.www.whoere.Fragment;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import xin.dspwljsyxgs.www.whoere.Activity.MainActivity;
import xin.dspwljsyxgs.www.whoere.MyApplication;
import xin.dspwljsyxgs.www.whoere.R;
import xin.dspwljsyxgs.www.whoere.Service.Location;
import xin.dspwljsyxgs.www.whoere.Service.getLocation;
import xin.dspwljsyxgs.www.whoere.Util.Getinfo;
import xin.dspwljsyxgs.www.whoere.Util.PersonalInfo;


public class Map_Fragment extends Fragment {



//////////////////////////////////////////////////////////////////////////////
    private MapView mapView = null;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private  Location location = new Location();
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private double lat;
    int cnt=0;
    private double lon;
    Handler handler = new Handler();
    private getLocation getLoc = new getLocation();
    private  boolean OK=true;
    AMap aMap = null;
    MyLocationStyle myLocationStyle;
    private View view;
    List<MarkerOptions> markerOptionsList = new CopyOnWriteArrayList<MarkerOptions>();
    List<Marker> markerList = new CopyOnWriteArrayList<Marker>();
    //////////////////////////////////////////////////////////////
    public Map_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.mapfragment, container, false);
        myLocationStyle = new MyLocationStyle();
        //初始化定位
        mLocationClient = new AMapLocationClient(MyApplication.getContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //获取地图控件引用
        mapView = (MapView) view.findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);
        getLoc.startloc();
        ini();
        Handler handler = new Handler();
       handler.postDelayed(runnable,5000);

        //handler.postDelayed(runnable,10000);
        return view;
    }

    Runnable r = ()->{
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        while (true){
            //Looper.prepare();
            if (markerList != null && markerList.size() > 0)
                for (int i = 0;i < markerList.size();++i) {
                    markerList.get(i).remove();
                }
            markerList = null;
            markerList = new ArrayList<Marker>();

            //Toast.makeText(MyApplication.getContext(),"hh",Toast.LENGTH_SHORT).show();
            if (getLoc.getResponseData() != null) {

                        parseJSONWITHGSON(getLoc.getResponseData());

//                        for (int i = 0; i < markerOptionsList.size(); i++) {
//                            MarkerOptions marker = markerOptionsList.get(i);
//                            Marker now = aMap.addMarker(marker);
//                            markerList.add(now);
//                        }

            }

            try {
                Thread.sleep(10000);
            }catch (Exception e) {
                //Toast.makeText(MyApplication.getContext(),e.toString(),Toast.LENGTH_SHORT).show();
            }
            //Looper.loop();
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Thread t = new Thread(r);
            t.start();
        }

    };



    private void ini(){
        if (aMap == null){
            //aMap.setMyLocationStyle(myLocationStyle);
            aMap=mapView.getMap();
            setUpMap();
        }
    }
    private void setUpMap() {

        setupLocationStyle();
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();


    }

    private void setupLocationStyle(){
        // 自定义系统定位蓝点

        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.gps_point_new));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR);
        // 将自定义的 myLocationStyle 对象添加到地图上
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        aMap.setMyLocationStyle(myLocationStyle);
        //OK=false;
    }
    /**
     * 声明定位回调监听器
     */
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {

            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.getLatitude();//获取纬度
                    amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    df.format(date);//定位时间
                    amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    amapLocation.getCountry();//国家信息
                    amapLocation.getProvince();//省信息
                    amapLocation.getCity();//城市信息
                    amapLocation.getDistrict();//城区信息
                    amapLocation.getStreet();//街道信息
                    amapLocation.getStreetNum();//街道门牌号信息
                    amapLocation.getCityCode();//城市编码
                    amapLocation.getAdCode();//地区编码
                    amapLocation.getAoiName();//获取当前定位点的AOI信息
                    lat = amapLocation.getLatitude();
                    lon = amapLocation.getLongitude();
                    //Toast.makeText(MyApplication.getContext(),lat+"  "+lon,Toast.LENGTH_SHORT).show();
                    //Log.v("pcw", "lat : " + lat + " lon : " + lon);
                    //Log.v("pcw", "Country : " + amapLocation.getCountry() + " province : " + amapLocation.getProvince() + " City : " + amapLocation.getCity() + " District : " + amapLocation.getDistrict());
                    //清空缓存位置
                    //aMap.clear();
                    //设置当前地图显示为当前位置
                    if (OK) {aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 16));}
                    OK=false;
//                    MarkerOptions markerOptions = new MarkerOptions();
//                    markerOptions.position(new LatLng(lat, lon));
//                    markerOptions.title("当前位置");
//                    markerOptions.visible(false);
//                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round));
//                    markerOptions.icon(bitmapDescriptor);
//                    aMap.addMarker(markerOptions);

                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
        aMap.clear();
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }



    private void parseJSONWITHGSON(String jsonData){
        PersonalInfo personalInfo = new PersonalInfo();
        MarkerOptions markerOptions;
        Gson gson = new Gson();

        LatLng latLng_me = new LatLng(location.gety(),location.getx()),latLng2;
        List<Getinfo> applist=gson.fromJson(jsonData,new TypeToken<List<Getinfo>>(){}.getType());
        for (Getinfo getinfo : applist){
            if (getinfo.getId() != Integer.parseInt(personalInfo.getId())) {
                markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng((double)getinfo.getY(), (double) getinfo.getX()));
                latLng2 = new LatLng((double) getinfo.getY(),(double) getinfo.getX());
                markerOptions.title(getinfo.getAccount());
                markerOptions.snippet("距离您 "+AMapUtils.calculateLineDistance(latLng_me,latLng2)+" 米");
                markerOptionsList.add(markerOptions);
                Marker marker = aMap.addMarker(markerOptions);
                markerList.add(marker);
            }
            //Toast.makeText(MyApplication.getContext(),getinfo.getId()+"    Main",Toast.LENGTH_SHORT).show();
            //personalInfo = new PersonalInfo(getinfo.getAccount(),getinfo.getPassword(),getinfo.getId()+"",getinfo.getHeadicon());
            //personalInfo.saveuserinfo();
            //personalInfo.saveKind(getinfo.getKind()+"");
        }
    }
}
