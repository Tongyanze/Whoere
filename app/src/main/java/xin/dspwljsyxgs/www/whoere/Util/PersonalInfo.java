package xin.dspwljsyxgs.www.whoere.Util;

import android.content.Context;
import android.content.SharedPreferences;

import xin.dspwljsyxgs.www.whoere.MyApplication;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by root on 18-4-8.
 */

public class PersonalInfo {
    Context context = MyApplication.getContext();
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    private String account,password,id,icon,kind;
    public PersonalInfo(){
        readuserinfo();
    }

    public PersonalInfo(String account, String password, String id,String icon){
        this.account=account;
        this.password=password;
        this.id=id;
        this.icon=icon;
    }

    public void saveAccount(String account){
        editor=context.getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("account",account);
        editor.apply();
    }
    public void saveKind(String kind){
        editor=context.getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("kind",kind);
        editor.apply();
    }
    public void saveIcon(String icon){
        editor=context.getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("icon",icon);
        editor.apply();
    }

    public void savePassword(String password){
        editor=context.getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("password",password);
        editor.apply();
    }
    public void saveuserinfo(){
        editor=context.getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("account",account);
        editor.putString("password",password);
        editor.putString("id",id);
        editor.putString("icon",icon);
        //editor.putString("kind",kind);
        editor.apply();
    }

    public void readuserinfo(){
        preferences=context.getSharedPreferences("data",MODE_PRIVATE);
        account=preferences.getString("account","");
        password=preferences.getString("password","");
        id=preferences.getString("id","0");
        icon=preferences.getString("icon","");
        kind=preferences.getString("kind","0");
    }
    public String getAccount(){
        return account;
    }
    public String getPassword(){
        return password;
    }
    public String getId(){
        return id;
    }
    public String getIcon(){
        return icon;
    }
    public String getKind(){return kind;}
}
