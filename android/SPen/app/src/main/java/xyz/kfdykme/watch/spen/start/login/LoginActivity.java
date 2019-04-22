package xyz.kfdykme.watch.spen.start.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import xyz.kfdykme.watch.spen.common.WatchConstant;
import xyz.kfdykme.watch.spen.common.model.UserModel;
import xyz.kfdykme.watch.spen.main.MainActivity;
import xyz.kfdykme.watch.spen.R;

public class LoginActivity extends WearableActivity {

    EditText etUserName;
    EditText etPassword;
    Button btDoLogin;

    public View.OnClickListener clickListenr = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            onClickEvent(v);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String jsonUserData = getSharedPreferences(WatchConstant.SHAREDPREFERENCED_NAME, Activity.MODE_PRIVATE)
                .getString(WatchConstant.SHAREDPREFERENCED_KEY_JSON_USER_DATA, "{}");

        UserModel userModel = UserModel.getUserModelFromSP(this);

        if (userModel.token != "")
        {
            //进入主页面
            startActivity(
                    new Intent(LoginActivity.this, MainActivity.class)
            );
           finish();
        }
        else{

            setContentView(R.layout.activity_login);


            initView();

            // Enables Always-on
            setAmbientEnabled();
        }

    }

    public void onClickEvent(View v){
        switch (v.getId()){
            case R.id.btDoLogin:
                //TODO: 确认账号密码,进行登陆请求

                //TODO:if 登陆成功,获取token,保存登陆状态信息

                UserModel.saveUserModel2SP(this,
                        new UserModel(WatchConstant.DEFAULT_TOKEN));

                //进入主页面
                startActivity(
                        new Intent(LoginActivity.this,MainActivity.class)
                );
                finish();

                break;
            default:
                break;
        }
    }

    public void initView(){
        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        btDoLogin = findViewById(R.id.btDoLogin);

        btDoLogin.setOnClickListener(clickListenr);

    }
}
