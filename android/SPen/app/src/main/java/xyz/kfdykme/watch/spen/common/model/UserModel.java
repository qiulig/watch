package xyz.kfdykme.watch.spen.common.model;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;

import xyz.kfdykme.watch.spen.common.WatchConstant;

public class UserModel {

    public UserModel(String token) {
        this.token = token;
    }

    public String token ="";



    public static void saveUserModel2SP(Context context,UserModel userModel){
        context.getSharedPreferences(WatchConstant.SHAREDPREFERENCED_NAME,Activity.MODE_PRIVATE)
                .edit()
                .putString(WatchConstant.SHAREDPREFERENCED_KEY_JSON_USER_DATA,
                        new Gson().toJson(userModel))
                .commit();
    }

    public static UserModel getUserModelFromSP(Context context){
        String json = context.getSharedPreferences(WatchConstant.SHAREDPREFERENCED_NAME,Activity.MODE_PRIVATE)
                .getString(WatchConstant.SHAREDPREFERENCED_KEY_JSON_USER_DATA,"{}");
        return new Gson().fromJson(json,UserModel.class);
    }
}
