package xin.dspwljsyxgs.www.whoere.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.dspwljsyxgs.www.whoere.MyApplication;
import xin.dspwljsyxgs.www.whoere.R;
import xin.dspwljsyxgs.www.whoere.Util.Loginconf;

public class Login extends AppCompatActivity implements View.OnClickListener{
    private Button btn;
    private TextView btn_reg;
    private EditText ed1,ed2;
    private ProgressDialog progressDialog;
    String account="",password="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn=(Button) findViewById(R.id.login_btn);
        btn_reg=(TextView) findViewById(R.id.reg_ac);
        ed1=(EditText)findViewById(R.id.log_account);
        ed2=(EditText)findViewById(R.id.log_passwd);
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setTitle("登录中");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(false);
        btn.setOnClickListener(this);
        btn_reg.setOnClickListener(this);

    }
    public void onClick(View view){
        switch (view.getId()){
            case R.id.login_btn:
                new Thread(new Runnable(){
                    public void run(){
                        Looper.prepare();
                        progressDialog.show();
                        Looper.loop();
                    }
                }).start();
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
                                    .url("http://202.194.15.232:8088/whoere/login")//服务器网址
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
                                        progressDialog.cancel();
                                        SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
                                        editor.putString("account",account);
                                        editor.putString("password",password);
                                        editor.apply();
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
                break;
            case R.id.reg_ac:
                Intent intent = new Intent(Login.this,Register_Activity_nophone.class);
                startActivity(intent);
        }
    }
}
