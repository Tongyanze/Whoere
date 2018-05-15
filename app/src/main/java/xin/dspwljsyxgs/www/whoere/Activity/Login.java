package xin.dspwljsyxgs.www.whoere.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import xin.dspwljsyxgs.www.whoere.R;
import xin.dspwljsyxgs.www.whoere.Util.Deal_Graphics;
import xin.dspwljsyxgs.www.whoere.Util.Getinfo;
import xin.dspwljsyxgs.www.whoere.Util.PersonalInfo;

public class Login extends AppCompatActivity implements View.OnClickListener{
    Context context=MyApplication.getContext();
    private Button btn;
    private TextView btn_reg,log_sdu;
    private EditText ed1,ed2;
    private ProgressDialog progressDialog;
    Handler handler = new Handler();
    String account="",password="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn=(Button) findViewById(R.id.login_btn);
        btn_reg=(TextView) findViewById(R.id.reg_ac);
        log_sdu=(TextView) findViewById(R.id.logwithsdu);
        ed1=(EditText)findViewById(R.id.log_account);
        ed2=(EditText)findViewById(R.id.log_passwd);
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setTitle("登录中");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(false);
        btn.setOnClickListener(this);
        log_sdu.setOnClickListener(this);
        btn_reg.setOnClickListener(this);

    }


    public void login(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    account=ed1.getText().toString();
                    password=ed2.getText().toString();

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

                        {
                            if (responseData.equals("nosuchid")) {
                                progressDialog.cancel();
                                Toast.makeText(MyApplication.getContext(), "该用户名不存在", Toast.LENGTH_LONG).show();
                                //Intent intent = new Intent(Signup2Activity.this, LoginActivity.class);
                                //startActivity(intent);
                                //finish();
                            }
                            else if (responseData.equals("false")) {
                                progressDialog.cancel();
                                Toast.makeText(MyApplication.getContext(), "密码错误", Toast.LENGTH_SHORT).show();
                            }
                            else  {
                                //Toast.makeText(Login.this,responseData.toString(), Toast.LENGTH_SHORT).show();
                                String id=responseData.toString().substring(4);
                                progressDialog.cancel();
                                parseJSONWITHGSON(responseData);
//                                String icon = Deal_Graphics.convertBitmapToString(BitmapFactory.decodeResource(
//                                        getResources(), R.drawable.headicon));
//                                PersonalInfo personalInfo =new PersonalInfo(account,password,id,icon);
//                                personalInfo.saveuserinfo();
//                                SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
//                                editor.putString("account",account);
//                                editor.putString("password",password);
//                                editor.putString("id",id);
//                                editor.apply();
                                Toast.makeText(MyApplication.getContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                finish();

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

    private void parseJSONWITHGSON(String jsonData){
        PersonalInfo personalInfo;
        Gson gson = new Gson();
        List<Getinfo> applist=gson.fromJson(jsonData,new TypeToken<List<Getinfo>>(){}.getType());
        for (Getinfo getinfo : applist){
            personalInfo = new PersonalInfo(getinfo.getAccount(),getinfo.getPassword(),getinfo.getId()+"",getinfo.getHeadicon());
            personalInfo.saveuserinfo();
            personalInfo.saveKind(getinfo.getKind()+"");
        }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.login_btn:
                progressDialog.show();
                login();
                break;
            case R.id.reg_ac:
                Intent intent = new Intent(Login.this,Register_Activity_nophone.class);
                startActivity(intent);
                finish();
                break;
            case R.id.logwithsdu:
                intent = new Intent(Login.this,Login_sdu.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
