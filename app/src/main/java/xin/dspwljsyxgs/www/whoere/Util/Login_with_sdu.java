package xin.dspwljsyxgs.www.whoere.Util;

/**
 * Created by root on 18-4-9.
 */

import android.widget.Toast;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import org.jsoup.nodes.*;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.select.Elements;

import xin.dspwljsyxgs.www.whoere.MyApplication;


public class Login_with_sdu {
    private  String username,passwd;
    private  String res,name="";
    static Map<String, String>cookies = null;
    public Login_with_sdu(String username,String passwd){
        cookies=null;
        name="";
        this.username=username;
        this.passwd=passwd;
    }

    public  void startLog() throws IOException, NoSuchAlgorithmException {
        Response response = Jsoup.connect("http://bkjws.sdu.edu.cn/b/ajaxLogin")
                .method(Method.POST)
                .data("j_username", username, "j_password", md5(passwd))
                .ignoreContentType(true)
                //.timeout()超时,0为不超时
                //.ignoreContentType(true)忽略返回数据类型
                .userAgent("Mozilla/5.0 (X11; Linux x86_64…) Gecko/20100101 Firefox/59.0")
                .header("Referer", "http://bkjws.sdu.edu.cn/f/login").execute();
        if (response.statusCode() == 200) {
             res = response.body();
            if (res.contains("success")) {
                cookies = response.cookies();//这个cookies就可以拿去请求别的了.
                System.out.println("Login Success");
                //System.out.println(cookies);
                //getname();
                //getclass();
            } else {
                System.out.println(res);
            }
        }
    }
    public String getName(){
        return name;
    }
    public  void getname() throws IOException,NoSuchAlgorithmException{
        if (cookies == null) return;
        Document doc =Jsoup.connect("http://bkjws.sdu.edu.cn/f/common/main")
                //.data("query", "Java") //请求参数
                .userAgent("I’mjsoup") //设置User-Agent
                .cookies(cookies) //设置cookie
                .timeout(3000) //设置连接超时时间
                .header("Referer", "http://bkjws.sdu.edu.cn/f/login")
                .get(); //使用POST方法访问URL
        //System.out.println(doc);
        Elements masthead = doc.select("span.username");
        String temp=masthead.toString();
        //System.out.println(doc);
        //String name="";
        for (int i = 22;i < 27;++i) {
            if (temp.charAt(i) != '>' && temp.charAt(i) != '[' && temp.charAt(i) != '\"' && temp.charAt(i) != ' ' &&!(temp.charAt(i) >= '0' && temp.charAt(i) <= '9')){
                name+=temp.charAt(i);
            }
            //Toast.makeText(MyApplication.getContext(),name,Toast.LENGTH_SHORT).show();
        }
        //System.out.println(name);

//
//        Response response = Jsoup.connect("http://bkjws.sdu.edu.cn/f/common/main").method(Method.GET)
//                //.data("j_username", "201705301346", "j_password", md5("123369"))
//                .ignoreContentType(true)
//                .cookies(cookies)
//                .userAgent("Mozilla/5.0 (X11; Linux x86_64…) Gecko/20100101 Firefox/59.0")
//                .header("Referer", "http://bkjws.sdu.edu.cn/f/login").execute();
//                System.out.println(response.body());



    }

    public static void getclass() throws IOException,NoSuchAlgorithmException{

        Document doc =Jsoup.connect("http://bkjws.sdu.edu.cn/f/xk/xs/bxqkb")
                //.data("query", "Java") //请求参数
                .userAgent("I’mjsoup") //设置User-Agent
                .cookies(cookies) //设置cookie
                .timeout(3000) //设置连接超时时间
                .header("Referer", "http://bkjws.sdu.edu.cn/f/common/main")
                .get(); //使用POST/GET方法访问URL
        //System.out.println(doc);

        Elements masthead = doc.select("tr td:not(.1,.2,.3,.4,.5,.6,.7)");
        masthead=masthead.select(":not(:contains(周))");
        //String temp=masthead.toString();
        //System.out.println(masthead.text());

    }

    public static String md5(String str) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] res = digest.digest(str.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < res.length; i++) {
            int val = res[i] & 0xff;
            if (val < 16)
                sb.append("0" + Integer.toHexString(val));
            else
                sb.append(Integer.toHexString(val));
        }
        return sb.toString();

    }
}