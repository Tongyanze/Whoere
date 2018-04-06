package xin.dspwljsyxgs.www.whoere.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.VectorEnabledTintResources;
import android.util.Log;
import android.util.LogPrinter;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.dspwljsyxgs.www.whoere.MyApplication;
import xin.dspwljsyxgs.www.whoere.R;
import xin.dspwljsyxgs.www.whoere.Util.Utility;


import org.apache.http.params.HttpConnectionParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class Servlet_test extends AppCompatActivity implements View.OnClickListener{
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servlet_test);
        Button btn=(Button) findViewById(R.id.btn);
        btn.setOnClickListener(this);
    }
    Thread reg;
    hh x = new hh();
    Exception e;
    public void onClick(View view){

    String responsedata;

        switch (view.getId()){
            case R.id.btn:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        try {


                            OkHttpClient client = new OkHttpClient();
                            //上传数据
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("phonenum", "12323454422")
                                    .add("password", "32424224")
                                    .add("username", "dfd1222213zzz")
                                    .build();
                            Request request = new Request.Builder()
                                    .url("http://202.194.15.232:8088/whoere/register")//服务器网址
                                    .post(requestBody)
                                    .build();
                            try {
                                //获得返回数据
                                Response response = client.newCall(request).execute();
                                String responseData = response.body().string();

                                {
                                    if (responseData.equals("true")) {
                                        Toast.makeText(MyApplication.getContext(), "注册成功", Toast.LENGTH_LONG).show();
                                        //Intent intent = new Intent(Signup2Activity.this, LoginActivity.class);
                                        //startActivity(intent);
                                        finish();
                                    }
                                    if (responseData.equals("false")) {
                                        Toast.makeText(MyApplication.getContext(), "用户名已被注册", Toast.LENGTH_SHORT).show();
                                    }
                                    if (responseData.equals("used")) {
                                        Toast.makeText(MyApplication.getContext(), "手机号已被注册", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Looper.loop();

                    }
                }).start();
                break;
        }



    }

    static String  response="";


}

class hh{
    int a;
}