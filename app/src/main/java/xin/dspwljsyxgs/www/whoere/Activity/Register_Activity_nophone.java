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
import android.widget.Toast;
import android.widget.ViewAnimator;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.dspwljsyxgs.www.whoere.MyApplication;
import xin.dspwljsyxgs.www.whoere.R;
import xin.dspwljsyxgs.www.whoere.Util.Loginconf;

public class Register_Activity_nophone extends AppCompatActivity implements View.OnClickListener{
    private Button btn1;
    private EditText ed1,ed2,ed3;
    private ProgressDialog progressDialog;
    int val1=0,val2=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__nophone);
        btn1=(Button)findViewById(R.id.reg_regbtn);
        ed1=(EditText)findViewById(R.id.reg_account);
        ed2=(EditText)findViewById(R.id.reg_passwd);
        ed3=(EditText)findViewById(R.id.reg_confpasswd);
        progressDialog = new ProgressDialog(Register_Activity_nophone.this);
        progressDialog.setTitle("注册中");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(false);
        btn1.setOnClickListener(this);

    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.reg_regbtn:
                new Thread(new Runnable(){
                   public void run(){
                       Looper.prepare();
                       progressDialog.show();
                       Looper.loop();
                   }
                }).start();
                checkAccount();

                break;
        }
    }


    private void checkAccount() {





        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {

                    checkPassword();
                    if (val2 != 2) return;
                    OkHttpClient client = new OkHttpClient();
                    //上传数据
                    RequestBody requestBody = new FormBody.Builder()
                            .add("account", ed1.getText().toString())
                            .add("password",ed2.getText().toString())
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
                            //Toast.makeText(Register_Activity_nophone.this,responseData.toString(),Toast.LENGTH_SHORT).show();
                            if (responseData.equals("false")) {
                                progressDialog.cancel();
                                Toast.makeText(MyApplication.getContext(), "该用户名已被注册", Toast.LENGTH_LONG).show();
                                //Intent intent = new Intent(Signup2Activity.this, LoginActivity.class);
                                //startActivity(intent);
                                //finish();
                            }
                            else {
                                progressDialog.cancel();
                                Toast.makeText(Register_Activity_nophone.this,"注册成功",Toast.LENGTH_SHORT).show();
                                String account=ed1.getText().toString();
                                SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
                                editor.putString("account",account);
                                editor.putString("password",ed2.getText().toString());
                                editor.apply();
                                Intent intent=new Intent(Register_Activity_nophone.this,MainActivity.class);
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

    private void checkPassword() {
        String passwd = ed2.getText().toString();
        String confpasswd=ed3.getText().toString();
        if (passwd.length() < 6) {
            Toast.makeText(this, "密码不能少于6位", Toast.LENGTH_SHORT).show();
            val2 =1;
            return;
        }
        else if (!passwd.equals(confpasswd)){
            Toast.makeText(this,"两次密码不一致！",Toast.LENGTH_SHORT).show();
            val2=1;
            return;
        }
        else if (passwd.length() > 20 || confpasswd.length() > 20){
            Toast.makeText(this,"密码过长",Toast.LENGTH_SHORT).show();
            val2=1;
            return;
        }
        val2=2;
    }
}
