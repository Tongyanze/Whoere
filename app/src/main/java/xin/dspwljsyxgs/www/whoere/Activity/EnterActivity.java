package xin.dspwljsyxgs.www.whoere.Activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.os.Bundle;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.dspwljsyxgs.www.whoere.MyApplication;
import xin.dspwljsyxgs.www.whoere.R;
import xin.dspwljsyxgs.www.whoere.Service.getLocation;
import xin.dspwljsyxgs.www.whoere.Util.Deal_Graphics;
import xin.dspwljsyxgs.www.whoere.Util.Getinfo;
import xin.dspwljsyxgs.www.whoere.Util.Login_with_sdu;
import xin.dspwljsyxgs.www.whoere.Util.PersonalInfo;


import android.content.pm.ActivityInfo;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class EnterActivity extends Activity{
    private Handler handler = new Handler();
    Button btn;
    private int time=4;
    int val;
    private int ok = 0;
    String account,password;
    private int kind=0;
    PersonalInfo personalInfo = new PersonalInfo();
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        NO Title
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_enter);
        //延时





        account=personalInfo.getAccount();
        password=personalInfo.getPassword();
        kind = Integer.parseInt(personalInfo.getKind());
        //Toast.makeText(EnterActivity.this,account+"  "+password+" "+kind,Toast.LENGTH_SHORT).show();
        //Toast.makeText(this,account+"   "+password,Toast.LENGTH_SHORT).show();
        checkAuthority();



    }


    private void parseJSONWITHGSON(String jsonData){
        PersonalInfo personalInfo;
        Gson gson = new Gson();
        List<Getinfo> applist=gson.fromJson(jsonData,new TypeToken<List<Getinfo>>(){}.getType());
        for (Getinfo getinfo : applist){
            personalInfo = new PersonalInfo(getinfo.getAccount(),getinfo.getPassword(),getinfo.getId()+"",getinfo.getHeadicon());
            account=getinfo.getAccount();
            personalInfo.saveuserinfo();
        }
    }

    private void checkAuthority(){
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(EnterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(EnterActivity.this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(EnterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(EnterActivity.this ,permissions,1);
        }
        else {
            if ((account != null && password != null) && (!account.equals("") && !password.equals(""))) {
                LoginConfirm();
            }
            else{
                newUser();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode , String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(EnterActivity.this,"必须同意所有权限才能使用本程序!  qwq",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    //ok = 1;
                    if ((account != null && password != null) && (!account.equals("") && !password.equals(""))) {
                        LoginConfirm();
                    }
                    else{
                        newUser();
                    }
                    return;
                }
                else {
                    Toast.makeText(this,"发生未知错误 QAQ",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    public void LoginConfirm(){
        if (kind == 1) {
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
                                .url("http://39.106.126.202:8088/whoere/login")//服务器网址
                                .post(requestBody)
                                .build();
                        try {
                            //获得返回数据
                            Response response = client.newCall(request).execute();
                            String responseData = response.body().string();
                            //Toast.makeText(EnterActivity.this,responseData,Toast.LENGTH_SHORT).show();
                            {
                                if (responseData.equals("nosuchid")) {

                                    Toast.makeText(MyApplication.getContext(), "该用户名不存在", Toast.LENGTH_LONG).show();
                                    handler.postDelayed(new Runnable() {  //使用handler的postDelayed实现延时跳转

                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(EnterActivity.this, Login.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 2000);
                                    finish();

                                    //Intent intent = new Intent(Signup2Activity.this, LoginActivity.class);
                                    //startActivity(intent);
                                    //finish();
                                } else if (responseData.equals("false")) {
                                    Toast.makeText(MyApplication.getContext(), "密码错误", Toast.LENGTH_SHORT).show();
                                    handler.postDelayed(new Runnable() {  //使用handler的postDelayed实现延时跳转

                                        public void run() {
                                            Intent intent = new Intent(EnterActivity.this, Login.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 2000);

                                } else {
                                    val = 3;
                                    //String id=responseData.toString().substring(4);
                                    parseJSONWITHGSON(responseData);
//
// SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
//                                    editor.putString("account",account);
//                                    editor.putString("password",password);
//                                    editor.putString("id",id);
//                                    editor.apply();
                                    Toast.makeText(MyApplication.getContext(), "欢迎回来  " + account, Toast.LENGTH_SHORT).show();
                                    handler.postDelayed(new Runnable() {  //使用handler的postDelayed实现延时跳转

                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(EnterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 2000);


                                }

                            }
                        } catch (Exception e) {
                            //Toast.makeText(EnterActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Looper.loop();
                }
            }).start();
        }
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    try {
                        Login_with_sdu login_with_sdu;
                        PersonalInfo personalInfo1 = new PersonalInfo();
                        personalInfo.readuserinfo();
                        login_with_sdu = new Login_with_sdu(personalInfo.getAccount(), personalInfo.getPassword());
                        login_with_sdu.startLog();
                        login_with_sdu.getname();
                        if (login_with_sdu.getName().length() > 0) {
                            Toast.makeText(EnterActivity.this, "欢迎您  " + login_with_sdu.getName(), Toast.LENGTH_SHORT).show();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(EnterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            },2000);

                        }
                        else {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(EnterActivity.this,Login.class);
                                    startActivity(intent);
                                    finish();
                                }
                            },2000);

                        }
                    }
                    catch (Exception e){

                    }
                    Looper.loop();
                }
            }).start();

        }
    }

    public void newUser(){
        handler.postDelayed(new Runnable() {  //使用handler的postDelayed实现延时跳转

            @Override
            public void run() {
                Intent intent = new Intent(EnterActivity.this, Login.class);
                startActivity(intent);
                finish();

            }
        }, 3000);
    }
    //目前暂时不启用短信验证
//    public  void login(){
//
//        String phonenum = pref.getString("phonenum","");
//        String password = pref.getString("password","");
//    }

}

