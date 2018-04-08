package xin.dspwljsyxgs.www.whoere.Activity;

import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.dspwljsyxgs.www.whoere.MyApplication;
import xin.dspwljsyxgs.www.whoere.R;
import xin.dspwljsyxgs.www.whoere.Util.Loginconf;


import android.content.pm.ActivityInfo;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class EnterActivity extends Activity{
    private Handler handler = new Handler();
    Button btn;
    private int time=4;
    SharedPreferences pref;
    int val;
    String account,password;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        NO Title
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_enter);
        //延时
        pref=getSharedPreferences("data",MODE_PRIVATE);
        account=pref.getString("account","");
        password=pref.getString("password","");
        //Toast.makeText(this,account+"   "+password,Toast.LENGTH_SHORT).show();
        if ((account != null && password != null) && (!account.equals("") && !password.equals(""))) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    try {


                        OkHttpClient client = new OkHttpClient();
                        //上传数据
                        RequestBody requestBody = new FormBody.Builder()
                                .add("password", password)
                                .add("account", account)
                                .build();
                        Request request = new Request.Builder()
                                .url("http://202.194.15.232:8088/whoere/login")//服务器网址
                                .post(requestBody)
                                .build();
                        try {
                            //获得返回数据
                            Response response = client.newCall(request).execute();
                            String responseData = response.body().string();

                            {
                                if (responseData.equals("nosuchid")) {
                                    Toast.makeText(MyApplication.getContext(), "该用户名不存在", Toast.LENGTH_LONG).show();
                                    handler.postDelayed(new Runnable() {  //使用handler的postDelayed实现延时跳转

                                        public void run() {
                                            Intent intent = new Intent(EnterActivity.this, Login.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 3000);
                                    //Intent intent = new Intent(Signup2Activity.this, LoginActivity.class);
                                    //startActivity(intent);
                                    //finish();
                                }
                                else if (responseData.equals("false")) {
                                    Toast.makeText(MyApplication.getContext(), "密码错误", Toast.LENGTH_SHORT).show();
                                    handler.postDelayed(new Runnable() {  //使用handler的postDelayed实现延时跳转

                                        public void run() {
                                            Intent intent = new Intent(EnterActivity.this, Login.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 3000);
                                }
                                else  {
                                    val=3;
                                    String id=responseData.toString().substring(4);
                                    SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
                                    editor.putString("account",account);
                                    editor.putString("password",password);
                                    editor.putString("id",id);
                                    editor.apply();
                                    Toast.makeText(MyApplication.getContext(), "欢迎回来", Toast.LENGTH_SHORT).show();
                                    handler.postDelayed(new Runnable() {  //使用handler的postDelayed实现延时跳转

                                        public void run() {
                                            Intent intent = new Intent(EnterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 3000);
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

        }
        else{
            handler.postDelayed(new Runnable() {  //使用handler的postDelayed实现延时跳转

                public void run() {
                    Intent intent = new Intent(EnterActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        }



    }


    //目前暂时不启用短信验证
//    public  void login(){
//
//        String phonenum = pref.getString("phonenum","");
//        String password = pref.getString("password","");
//    }

}

