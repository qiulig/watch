package xyz.kfdykme.watch.spen.common;

public class WatchConstant {


    public static long  TYPE_BREATH_MONITOR = 65550;

    public static long lastUploadStepTime= 0;

    public static long  lastUploadHeartRateTime = 0;

    //步数上传间隔
    public static long STEP_UPLOAD_DEVILE_TIME =5*1000;// 60 * 1000 * 5;

    //心率上传间隔
    public static long  HEART_RATE_UPLOAD_TIME = 60 * 1000;

    public static String  TAG = "WatchConstant";

    public static String  DEFAULT_PHONE_NUMBER = "10086";

    public static String BASEURL = "http://119.27.187.141:8080/";

    public static String POST_BY = "watch";
    public static String DEFAULT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZGVudCI6MSwiaWQiOjE0NCwiZXhwIjoxNTY1ODA3ODU3LCJpYXQiOjE1MzQyNzE4NTd9.sPnO3G9ZLEfa44cUkJM9Fx406Or_HOqJbMECKzU-M8c";
    public static String SHAREDPREFERENCED_NAME ="spen";
    public static String SHAREDPREFERENCED_KEY_TODAY_STEP = "sharedpreference_key_today_step";

    public static String SHAREDPREFERENCED_KEY_JSON_USER_DATA = "sharedpreference_key_json_user";
}
