package xin.dspwljsyxgs.www.whoere.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import xin.dspwljsyxgs.www.whoere.R;
import xin.dspwljsyxgs.www.whoere.Util.Deal_Graphics;
import xin.dspwljsyxgs.www.whoere.Util.Login_with_sdu;
import xin.dspwljsyxgs.www.whoere.Util.PersonalInfo;

public class Login_sdu extends AppCompatActivity implements View.OnClickListener {
    private EditText ed1, ed2;
    private TextView btn2;
    private Button btn;
    private Login_with_sdu login_with_sdu;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sdu);
        ed1 = (EditText) findViewById(R.id.log_account);
        ed2 = (EditText) findViewById(R.id.log_passwd);
        btn = (Button) findViewById(R.id.login_btn);
        btn2 = (TextView) findViewById(R.id.reg_ac);
        progressDialog = new ProgressDialog(Login_sdu.this);
        progressDialog.setTitle("登录中");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(false);
        btn.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();

                        login_with_sdu = new Login_with_sdu(ed1.getText().toString(), ed2.getText().toString());
                        try {
                            login_with_sdu.startLog();
                            login_with_sdu.getname();
                            if (login_with_sdu.getName().length() > 0) {
                                PersonalInfo personalInfo = new PersonalInfo();
                                personalInfo.saveAccount(ed1.getText().toString());
                                personalInfo.savePassword(ed2.getText().toString());
                                personalInfo.saveKind("2");
                                personalInfo.saveIcon(Deal_Graphics.convertBitmapToString(BitmapFactory.decodeResource(
                                        getResources(), R.drawable.headicon)));
                                progressDialog.cancel();
                                Toast.makeText(Login_sdu.this, "欢迎您  " + login_with_sdu.getName(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login_sdu.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                progressDialog.cancel();
                                Toast.makeText(Login_sdu.this, "学号或密码错误", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(Login_sdu.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }

                        Looper.loop();
                    }

                }).start();
                break;
            case R.id.reg_ac:
                Intent intent = new Intent(Login_sdu.this,Register_Activity_nophone.class);
                startActivity(intent);
                break;
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(Login_sdu.this,Login.class);
        startActivity(intent);
        finish();
    }
}
