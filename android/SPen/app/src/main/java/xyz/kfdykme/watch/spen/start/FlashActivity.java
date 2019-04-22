package xyz.kfdykme.watch.spen.start;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import xyz.kfdykme.watch.spen.R;
import xyz.kfdykme.watch.spen.start.login.LoginActivity;

public class FlashActivity extends WearableActivity {

    public static String TAG = "FlashActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(2000);
                    startActivity(
                            new Intent(FlashActivity.this,LoginActivity.class)
                    );
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    finish();
                }
            }
        }).start();

        // Enables Always-on
        setAmbientEnabled();
    }
}
