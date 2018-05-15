package xin.dspwljsyxgs.www.whoere.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.List;

import xin.dspwljsyxgs.www.whoere.Fragment.Map_Fragment;
import xin.dspwljsyxgs.www.whoere.Fragment.Message_Fragment;
import xin.dspwljsyxgs.www.whoere.MyApplication;
import xin.dspwljsyxgs.www.whoere.R;
import xin.dspwljsyxgs.www.whoere.Service.getLocation;
import xin.dspwljsyxgs.www.whoere.Util.Deal_Graphics;
import xin.dspwljsyxgs.www.whoere.Util.Getinfo;
import xin.dspwljsyxgs.www.whoere.Util.PersonalInfo;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,View.OnClickListener{
    private int nowfragment = 1;
    private final int MAP = 1, MESSEGE = 2;
    private TextView tx,t1,t2;
    private ImageView imageView;
    private Intent intentLocation,intentgetLocation;
    private Map_Fragment map_Fragment;
    private Message_Fragment message_fragment;
    private ImageView btn1,btn2;
    private Resources myresources;
    Handler handler = new Handler();
    getLocation getLoc = new getLocation();
    PersonalInfo personalInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        intentLocation=new Intent(MainActivity.this, xin.dspwljsyxgs.www.whoere.Service.Location.class);
        startService(intentLocation);
        personalInfo=new PersonalInfo();
        String account = personalInfo.getAccount();
        String headicon=personalInfo.getIcon();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.inflateHeaderView(R.layout.nav_header_main);


        tx=(TextView) view.findViewById(R.id.account_name);
        tx.setText(account);
        t1=(TextView) findViewById(R.id.t1);
        t2=(TextView) findViewById(R.id.t2);
        t1.setOnClickListener(this);
        t2.setOnClickListener(this);
        imageView = (ImageView) view.findViewById(R.id.headicon);
        imageView.setImageBitmap(Deal_Graphics.convertStringToBitmap(headicon));
        myresources = getResources();
        btn1=(ImageView) findViewById(R.id.pic1);
        btn2=(ImageView) findViewById(R.id.pic2);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.frg_right,0);
        if (map_Fragment == null) {
            map_Fragment = new Map_Fragment();
            transaction.add(R.id.fragment_layout, map_Fragment);
        } else {
            transaction.show(map_Fragment);
        }
        transaction.commit();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        //mLocationClient.stopLocation();
    }





//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            xin.dspwljsyxgs.www.whoere.Service.Location Loc = new xin.dspwljsyxgs.www.whoere.Service.Location();
//            double x=Loc.getx();
//            double y=Loc.gety();
//            Toast.makeText(MainActivity.this,x+"  "+y,Toast.LENGTH_SHORT).show();
//            handler.postDelayed(this,10000);
//        }
//    };











    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.nav_exit:
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
                break;
            case R.id.pic1:
            case R.id.t1:
                if (nowfragment == MAP) {return;}
                showFragment(MAP);
                nowfragment = 1;
                break;
            case R.id.pic2:
            case R.id.t2:
                if (nowfragment == MESSEGE) {return;}
                showFragment(MESSEGE);
                nowfragment = 2;
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.user_info) {
            Intent intent = new Intent(MainActivity.this,ChangeUserinfo.class);
            startActivity(intent);
        }  else if (id == R.id.nav_exit) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.exit){
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopService(intentLocation);

    }


    private void parseJSONWITHGSON(String jsonData){
        PersonalInfo personalInfo;
        Gson gson = new Gson();
        List<Getinfo> applist=gson.fromJson(jsonData,new TypeToken<List<Getinfo>>(){}.getType());
        for (Getinfo getinfo : applist){
            //Toast.makeText(MyApplication.getContext(),getinfo.getId()+" Main",Toast.LENGTH_SHORT).show();
            //personalInfo = new PersonalInfo(getinfo.getAccount(),getinfo.getPassword(),getinfo.getId()+"",getinfo.getHeadicon());
            //personalInfo.saveuserinfo();
            //personalInfo.saveKind(getinfo.getKind()+"");
        }
    }

    private void showFragment(int x){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragment(transaction);
        orignalImage();
        switch (x){
            case MAP:
                if (map_Fragment == null){
                    map_Fragment = new Map_Fragment();
                    transaction.add(R.id.fragment_layout, map_Fragment);
                }else {
                    transaction.show(map_Fragment);
                }
                btn1.setImageDrawable(myresources.getDrawable(R.drawable.fuxiaojin2,null));
                t1.setTextColor(myresources.getColor(R.color.colorPrimary,null));
                break;
            case  MESSEGE:
                if (message_fragment == null){
                    message_fragment = new Message_Fragment();

                    transaction.add(R.id.fragment_layout, message_fragment);
                }else {

                    transaction.show(message_fragment);
                }
                btn2.setImageDrawable(myresources.getDrawable(R.drawable.quanxiaozi2,null));
                t2.setTextColor(myresources.getColor(R.color.colorPrimary,null));
                break;
            default:
                break;
        }
        transaction.commit();
    }
    private void hideFragment(FragmentTransaction transaction){
        if(map_Fragment!=null){
            transaction.hide(map_Fragment);
        }
        if (message_fragment!=null) {
            transaction.hide(message_fragment);
        }
    }

    private void orignalImage(){
        btn1.setImageDrawable(myresources.getDrawable(R.drawable.fuxiaojin1,null));
        btn2.setImageDrawable(myresources.getDrawable(R.drawable.quanxiaozi1,null));
        t1.setTextColor(Color.parseColor("#a1a1a1"));
        t2.setTextColor(Color.parseColor("#a1a1a1"));
    }

}
