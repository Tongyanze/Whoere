package xin.dspwljsyxgs.www.whoere.Util;

import cn.smssdk.SMSSDK;



public class SMSUtil {

    public static final String APP_KEY = Getkeys.GetAppKey();

    public static final String APP_SECRET = Getkeys.GetApp_Secret();

    public static void getVerCode(String disCode, String phoneNum) {
        SMSSDK.getVerificationCode(disCode, phoneNum);
    }

    public static void submitVerCode(String disCode, String phoneNum, String verCode) {
        SMSSDK.submitVerificationCode(disCode, phoneNum, verCode);
    }
}
