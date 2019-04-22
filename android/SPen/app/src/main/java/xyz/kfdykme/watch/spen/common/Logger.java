package xyz.kfdykme.watch.spen.common;


import android.util.Log;

public class Logger {
    public static boolean isDeving = true;

    public static void i(String tag,String msg){
        if(isDeving)
            Log.i(tag,msg);
    }
}
