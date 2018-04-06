package xin.dspwljsyxgs.www.whoere.Activity;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.MobSDK;

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
import org.json.JSONObject;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.dspwljsyxgs.www.whoere.MyApplication;
import xin.dspwljsyxgs.www.whoere.R;
import xin.dspwljsyxgs.www.whoere.Util.Getkeys;
import xin.dspwljsyxgs.www.whoere.Util.SMSUtil;
import xin.dspwljsyxgs.www.whoere.Util.Utility;

public class Register_Activity extends AppCompatActivity implements View.OnClickListener{

    private EditText editPhone;

    private EditText editVerCode;

    private EditText editDisCode;

    private EditText editPassword;

    private EventHandler eventHandler;

    private Button getVerCode;

    private Button reg;

    private boolean checkVerCode = false;

    private ProgressDialog regDialog, verDialog;

    private Thread regThread;

    private TextView eula;

    private SMSHandler mHandler = new SMSHandler(this);
    private MyHandler handler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);

        //初始化短信SDK
        MobSDK.init(this, Getkeys.GetAppKey(), Getkeys.GetApp_Secret());

        //绑定控件
        editPhone = (EditText) findViewById(R.id.reg_phonenum);
        editVerCode = (EditText) findViewById(R.id.reg_vercode);
        editDisCode = (EditText) findViewById(R.id.reg_discode);
        editPassword = (EditText) findViewById(R.id.reg_password);

        getVerCode = (Button) findViewById(R.id.reg_get_vercode);
        reg = (Button) findViewById(R.id.reg_regbtn);

        eula = (TextView) findViewById(R.id.reg_eula);


        eula.setOnClickListener(this);

        //设置按钮监听器
        getVerCode.setOnClickListener(this);
        reg.setOnClickListener(this);

        //手机号码文本框获得焦点
        editPhone.requestFocus();


        // 创建EventHandler对象
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                mHandler.handleMessage(msg);
            }
        };

        // 注册短信监听器
        SMSSDK.registerEventHandler(eventHandler);

        //监听密码文本框长度
        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 20) {
                    Toast.makeText(Register_Activity.this, "密码不能超过20位",
                            Toast.LENGTH_SHORT).show();
                    editPassword.setText(charSequence.subSequence(0, 20));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        String disCode = nonspaceConvert(editDisCode.getText().toString()); //区号
        String phoneNum = nonspaceConvert(editPhone.getText().toString()); //手机号码

        switch (view.getId()) {
            case R.id.reg_get_vercode:
                SMSUtil.getVerCode(disCode, phoneNum);
                break;
            case R.id.reg_regbtn:
                //避免重复验证短信导致失败
                String verCode = editVerCode.getText().toString();
                if (!checkVerCode){
                    SMSUtil.submitVerCode(disCode, phoneNum, verCode);
                    //弹出正在验证对话框
                    startVerifyUIDialog();
                } else {
                    addUserOnServer("86", phoneNum, editPassword.getText().toString());
                }

                break;
            case R.id.reg_eula:
                //startActivity(new Intent(this, EULAActivity.class));
                break;
            default: break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //反注册短信监听器
        SMSSDK.unregisterEventHandler(eventHandler);
        //关闭所有对话框
        stopUIDialog();
    }


    /**
     * 去掉号码中的特殊字符
     * @param s
     * @return
     */
    private String nonspaceConvert(String s) {
        Scanner scanner = new Scanner(s);
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNext()) {
            builder.append(scanner.next());
        }
        return builder.toString();
    }

    /**
     * 检查密码格式
     * @return
     */
    private boolean checkPassword() {
        String passwd = editPassword.getText().toString();
        if (passwd.length() < 6) {
            Toast.makeText(this, "密码不能少于6位", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 开启获取验证码的一分钟冷却时间
     */
    private void startVerCodeCountDown() {
        VerCodeCountDownTimer timer = new
                VerCodeCountDownTimer(60000, 1000);
        timer.start();
    }

    /**
     * 显示正在验证对话框
     */
    private void startVerifyUIDialog() {
        if (verDialog == null) {
            verDialog = new ProgressDialog(this);
        }
        verDialog.setCancelable(false);
        verDialog.setCanceledOnTouchOutside(false);
        verDialog.setMessage("正在验证...");
        verDialog.setTitle("注册");
        verDialog.show();
    }

    /**
     * 显示正在注册对话框
     */
    private void startRegUIDialog() {
        if (regDialog == null) {
            regDialog = new ProgressDialog(this);
        }
        regDialog.setCancelable(false);
        regDialog.setCanceledOnTouchOutside(false);
        regDialog.setMessage("正在注册...");
        regDialog.setTitle("注册");
        regDialog.show();
    }

    /**
     * 关闭正在注册对话框
     */
    private void stopUIDialog() {
        if (regDialog != null) {
            regDialog.dismiss();
        }
        if (verDialog != null) {
            verDialog.dismiss();
        }
    }

    /**
     * 向服务器端注册用户
     * @param disCode
     * @param phoneNum
     * @param password
     */
    private String response = "";
    private void addUserOnServer(final String disCode, final String phoneNum, final String password) {
        //耗时操作，需要在子线程中完成
        startRegUIDialog();
        regThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try{
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10,TimeUnit.SECONDS)//设置连接超时时间
                            .build();
                    //上传数据
                RequestBody requestBody = new FormBody.Builder()
                        .add("phonenum", "1232345443422")
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

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
                        Looper.loop();

        }
    });
        regThread.start();
    }

    private static class SMSHandler extends Handler {
        WeakReference<Register_Activity> activityWeakReference;
        public SMSHandler(Register_Activity activity) {
            this.activityWeakReference = new WeakReference<Register_Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Register_Activity activity = activityWeakReference.get();
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;

            if (result == SMSSDK.RESULT_COMPLETE) {
                //正确进行
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "验证码已发送",
                                    Toast.LENGTH_SHORT).show();
                            activity.startVerCodeCountDown();
                        }
                    });

                } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //验证码正确
                    activity.checkVerCode = true;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.stopUIDialog();
                        }
                    });
                    //执行注册逻辑
                    if (activity.checkPassword() && activity.checkVerCode) {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //打开正在注册对话框
                                activity.startRegUIDialog();
                                String disCode = activity.nonspaceConvert(activity.editDisCode.getText().toString()); //区号
                                String phoneNum = activity.nonspaceConvert(activity.editPhone.getText().toString()); //手机号码
                                String password = activity.editPassword.getText().toString(); //密码

                                //开启网络请求
                                activity.addUserOnServer(disCode, phoneNum, password);
                            }
                        });


                    }

                }

            } else {
                //出现异常
                int status = 0;
                try{
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;

                    JSONObject object = new JSONObject(throwable.getMessage());
                    final String des = object.optString("detail");
                    status = object.optInt("status");
                    if (!TextUtils.isEmpty(des)) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.stopUIDialog();
                                Toast.makeText(activity, des, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (Exception e) {
                    SMSLog.getInstance().w(e);
                }
            }
        }
    }

    private static class MyHandler extends Handler {
        WeakReference<Register_Activity> activityWeakReference;
        public MyHandler(Register_Activity activity) {
            this.activityWeakReference = new WeakReference<Register_Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Register_Activity activity = activityWeakReference.get();
            switch (msg.what) {
                case 0:
                    String response = (String) msg.obj;
                    if (response.equals("")) {
                        Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.stopUIDialog();
                            }
                        });
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.stopUIDialog();
                            }
                        });
                        if (response.equals("false")) {
                            Toast.makeText(activity, "手机号已被注册", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "注册成功", Toast.LENGTH_SHORT).show();
                            activity.finish();
                        }

                    }
                    break;
                case 1:
                    Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.stopUIDialog();
                        }
                    });
                    break;
            }
        }
    }


    /**
     * 自定义计时器内部类
     */
    class VerCodeCountDownTimer extends CountDownTimer {
        public VerCodeCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            getVerCode.setEnabled(false);
            getVerCode.setText("获取验证码(" + l / 1000 + ")");
        }

        @Override
        public void onFinish() {
            getVerCode.setEnabled(true);
            getVerCode.setText("获取验证码");
        }
    }


}
