package xin.dspwljsyxgs.www.whoere.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.MyLocationStyle;

import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.dspwljsyxgs.www.whoere.Activity.Login;
import xin.dspwljsyxgs.www.whoere.Activity.MainActivity;
import xin.dspwljsyxgs.www.whoere.MyApplication;

public class Location extends Service {
    Context context = MyApplication.getContext();

    private static double x,y;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    //初始化定位

    //设置定位回调监听
    SharedPreferences pref;
    int id;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    public double getx(){
        return x;
    }
    public double gety(){
        return y;
    }
    Handler handler = new Handler();
    public Location() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){

        mLocationClient = new AMapLocationClient(context);
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        pref=getSharedPreferences("data",MODE_PRIVATE);
        id=Integer.parseInt(pref.getString("id","0"));
        mLocationOption.setInterval(2000);
//设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
// 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
// 在定位结束后，在合适的生命周期调用onDestroy()方法
// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//启动定位
        mLocationClient.startLocation();
        handler.postDelayed(runnable,1000);



        return super.onStartCommand(intent,flags,startId);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

                    try {
                        double x = getx();
                        double y = gety();

                        //Toast.makeText(MyApplication.getContext(),id+" "+x+"  "+y,Toast.LENGTH_SHORT).show();

                        OkHttpClient client = new OkHttpClient();
                        //上传数据
                        //Toast.makeText(MyApplication.getContext(),id+"",Toast.LENGTH_SHORT).show();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("id",id+"")
                                .add("x", x+"")
                                .add("y", y+"")
                                .build();
                        Request request = new Request.Builder()
                                .url("http://39.106.126.202:8088/whoere/Location")//服务器网址
                                .post(requestBody)
                                .build();
                        try {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Looper.prepare();
                                try {

                                    Response response = client.newCall(request).execute();
                                    String responseData = response.body().string();
                                    //Toast.makeText(MyApplication.getContext(), responseData, Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e){
                                    //Toast.makeText(MyApplication.getContext(),"1   "+e.toString(),Toast.LENGTH_SHORT).show();
                                }
                                Looper.loop();
                                }
                            }).start();
                        }
                        catch (Exception e){

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.postDelayed(this,10000);



        }
    };
    AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    String s = "" + amapLocation.getLatitude();
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表

                    x=amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    y=amapLocation.getLatitude();//获取纬度
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

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());

                    //Toast.makeText(context, s, Toast.LENGTH_LONG).show();//获取纬度
                    df.format(date);//定位时间
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Toast.makeText(context,"网络异常",Toast.LENGTH_SHORT).show();
                    //Toast.makeText(context, amapLocation.getErrorCode() + "  " + amapLocation.getErrorInfo(), Toast.LENGTH_SHORT).show();
                    //Log.e("AmapError", "location Error, ErrCode:"
                          //  + amapLocation.getErrorCode() + ", errInfo:"
                           // + amapLocation.getErrorInfo());
                }
            }


        }
    };

    @Override
    public void onDestroy(){
        super.onDestroy();
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
