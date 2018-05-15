package xin.dspwljsyxgs.www.whoere.Util;

import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.dspwljsyxgs.www.whoere.Activity.Login;
import xin.dspwljsyxgs.www.whoere.Activity.MainActivity;
import xin.dspwljsyxgs.www.whoere.MyApplication;

/**
 * Created by root on 18-4-6.
 */

public class Loginconf {
    static volatile int val=4;
    public static int confirm(final String account, final String password){
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
                                val=1;
                                //Intent intent = new Intent(Signup2Activity.this, LoginActivity.class);
                                //startActivity(intent);
                                //finish();
                            }
                            else if (responseData.equals("false")) {
                                Toast.makeText(MyApplication.getContext(), "密码错误", Toast.LENGTH_SHORT).show();
                                val=2;
                            }
                            else  {
                                val=3;
                                Toast.makeText(MyApplication.getContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MyApplication.getContext(), MainActivity.class);


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
        return val;
    }
}
