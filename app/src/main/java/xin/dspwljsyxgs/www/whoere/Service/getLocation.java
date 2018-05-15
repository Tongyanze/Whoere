package xin.dspwljsyxgs.www.whoere.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.dspwljsyxgs.www.whoere.MyApplication;
import xin.dspwljsyxgs.www.whoere.Util.Getinfo;
import xin.dspwljsyxgs.www.whoere.Util.PersonalInfo;

public class getLocation extends Service {
    String responseData;
    Handler handler = new Handler();
    @Override
    public void onCreate(){
        super.onCreate();
    }
    public getLocation() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//               Looper.prepare();
//                OkHttpClient client = new OkHttpClient();
//                RequestBody requestBody = new FormBody.Builder()
//                        .build();
//                Request request = new Request.Builder()
//                        .url("http://39.106.126.202:8088/whoere/getLocation")//服务器网址
//                        .post(requestBody)
//                        .build();
//                try {
//                    Response response = client.newCall(request).execute();
//                    String responseData = response.body().string();
//                    //Toast.makeText(MyApplication.getContext(),responseData,Toast.LENGTH_SHORT).show();
//                    //System.out.print(responseData);
//                    //Log.d("infomation!!!!!!", responseData);
//                    parseJSONWITHGSON(responseData);
//                }catch (Exception e){
//
//                }
//                Looper.loop();
//            }
//        }).start();

        handler.postDelayed(runnable,0);
        return super.onStartCommand(intent,flags,startId);
    }

    public  void startloc(){
        handler.postDelayed(runnable,0);
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {


            OkHttpClient client = new OkHttpClient();
            //上传数据
            //Toast.makeText(MyApplication.getContext(),"123456",Toast.LENGTH_SHORT).show();
            RequestBody requestBody = new FormBody.Builder()
                    .build();
            Request request = new Request.Builder()
                    .url("http://39.106.126.202:8088/whoere/getLocation")//服务器网址
                    .post(requestBody)
                    .build();
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        try {
                            Response response = client.newCall(request).execute();
                            responseData = response.body().string();
                            //Toast.makeText(MyApplication.getContext(),responseData,Toast.LENGTH_SHORT).show();
                            //Log.d("infomation!!!!!!", responseData);
                            //parseJSONWITHGSON(responseData);
                        } catch (Exception e) {
                            //Toast.makeText(MyApplication.getContext(), e.toString(), Toast.LENGTH_SHORT);
                        }
                        Looper.loop();
                    }
                }).start();
            }catch (Exception e){

            }
            //Toast.makeText(MyApplication.getContext(), responseData, Toast.LENGTH_SHORT).show();
            handler.postDelayed(this,30000);
            }

    };

    public String getResponseData(){
        return responseData;
    }

    @Override
    public  void  onDestroy(){
        super.onDestroy();
        responseData="";
    }

}
