package xyz.kfdykme.watch.spen.main;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import xyz.kfdykme.demo.myapplication.HttpResult;
import xyz.kfdykme.watch.spen.Dataset;
import xyz.kfdykme.watch.spen.R;
import xyz.kfdykme.watch.spen.common.Logger;
import xyz.kfdykme.watch.spen.common.WatchConstant;
import xyz.kfdykme.watch.spen.common.model.PulseModel;
import xyz.kfdykme.watch.spen.common.model.TodayStepModel;
import xyz.kfdykme.watch.spen.common.model.WifiScanModel;
import xyz.kfdykme.watch.spen.common.model.WifiTableModel;
import xyz.kfdykme.watch.spen.common.retrofit.PensionClient;

public class MainActivity extends WearableActivity {



    //Var
    public static final String TAG = "MainActivity";

    public boolean hasRecordStep = false;//app启动以来是否获取过过步数
    public long appStartStep = 0;//app启动时第一次获取的步数,
    public TodayStepModel toDayStepModel = null;
    SensorManager mgr;


    //TODO:整理这些变量
    Dataset data = new Dataset();
    double acc_avg_max;
    double gra_avg_max;
    int count=0;
    /*是否正在进行数据处理的标识*/
    private double[] gravity = new double[3];
    private double[] motion = new double[3];
    boolean flag=true;

    public LocationClient mLocationClient = null;

    public WifiManager mWifiManager = null;

    public BroadcastReceiver mWifiBroadcastREceiver = null;

    //Interface
    SensorEventListener sensorlistener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            MainActivity.this.onSensorChanged(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    Observer pensionObservable = new Observer<HttpResult>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(HttpResult httpResult) {
            Toast.makeText(MainActivity.this,
                    httpResult.toString(),
                    Toast.LENGTH_SHORT)
                    .show();
            Logger.i(TAG,httpResult.toString());
        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText(MainActivity.this,
                    e.getMessage(),
                    Toast.LENGTH_SHORT)
                    .show();

            e.printStackTrace();
        }

        @Override
        public void onComplete() {

        }
    };



    //function
    private double GetMaxflag(List<Double> gra_x) {
        double max=0;
        for(int i=0;i<gra_x.size();i++)
        {
            if(max<Math.abs(gra_x.get(i)))
            {
                max=gra_x.get(i);
            }
        }
        return  max;
    }


    private double getMax (List < Double > y) {
        double max = 0;
        for (int i = 0; i < y.size(); i++) {
            if (max < y.get(i)) {
                max = y.get(i);

            }
        }
        return max;
    }


    public void init() {

        initPermission();

        initTodayStep();

        initSensor();

        initLocation();
    }


    public void initPermission(){
        String[] needPermissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                Manifest.permission.BODY_SENSORS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
        };

        List<String> reqPermissions = new ArrayList<>();

        for(int i =0 ; i<needPermissions.length;i++){
            if(ActivityCompat.checkSelfPermission(
                    this,
                    needPermissions[i]
            ) != PackageManager.PERMISSION_GRANTED)
                reqPermissions.add(needPermissions[i]);

        }

        ActivityCompat.requestPermissions(
                this,
                reqPermissions.toArray(new String[]{}),
                1
        );
    }


    public void initTodayStep(){
        SharedPreferences sp = getSharedPreferences(WatchConstant.SHAREDPREFERENCED_NAME,Activity.MODE_PRIVATE);
        String jsonTodayStepModel= sp.getString(WatchConstant.SHAREDPREFERENCED_KEY_TODAY_STEP,"{}");
        toDayStepModel = new Gson()
                            .fromJson(
                                    jsonTodayStepModel,
                                    TodayStepModel.class
                            );


    }


    public void initSensor(){
            //init sensor
            mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
            Sensor  septSensor = mgr.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            Sensor  septCountSensor = mgr.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            Sensor  heartRateSensor = mgr.getDefaultSensor(Sensor.TYPE_HEART_RATE);
            Sensor accelSensor = mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor gyroSensor = mgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            mgr.registerListener(sensorlistener,heartRateSensor,SensorManager.SENSOR_DELAY_NORMAL);
            mgr.registerListener(sensorlistener,septSensor,SensorManager.SENSOR_DELAY_FASTEST);
            mgr.registerListener(sensorlistener,septCountSensor,SensorManager.SENSOR_DELAY_FASTEST);
            mgr.registerListener(sensorlistener, accelSensor, SensorManager.SENSOR_DELAY_GAME);
            mgr.registerListener(sensorlistener, gyroSensor, SensorManager.SENSOR_DELAY_GAME);

    }

    public void initLocation(){

        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {

            //TODO:需要优化
            long lastUpdataPosTimeMillis = 0;

            @Override
            public void onReceiveLocation(BDLocation location) {


                if(System.currentTimeMillis() - lastUpdataPosTimeMillis < 10 * 1000) return;
                lastUpdataPosTimeMillis = System.currentTimeMillis();

                double latitude = location.getLatitude();    //获取纬度信息
                double longitude = location.getLongitude();    //获取经度信息
                float radius = location.getRadius();    //获取定位精度，默认值为0.0f

                String coorType = location.getCoorType();
                //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

                int errorCode = location.getLocType();
                Logger.i(Thread.currentThread().getName()+" Loc",latitude +" .... "+longitude+"..."+radius+"..."+coorType+"..."+errorCode);

                PensionClient.getInstace().uploadLoaction(longitude+"",latitude+"")
                .subscribe(new Observer<HttpResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult httpResult) {
                        Logger.i("Location result",new Gson().toJson(httpResult));

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

                //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            }
        });
        //注册监听函数

        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setScanSpan(1000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5*60*1000);
        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    }

    public void onBodyDown(){
        mLocationClient.start();
        PensionClient.getInstace().postStatus("0","后摔")
        .subscribe(pensionObservable);
        Logger.i("Location","location client start when bodydown");
        requestCall();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAmbientEnabled();
        init();


        // Enables Always-on
    }


    public void onDealData(){
        acc_avg_max = getMax(data.acc_squar);
        gra_avg_max = getMax(data.gra_squar);

        double max_gra_x_flag=GetMaxflag(data.gra_x);//小于0
        double max_gra_z_flag=GetMaxflag(data.gra_z);///大于0

        if (acc_avg_max>30&&gra_avg_max>30&&max_gra_x_flag<-5&&max_gra_z_flag>5) {


            Logger.i(TAG,"on body down");
            //TODO:跌倒英文是啥来着?
            onBodyDown();
        } else {
            Logger.i(TAG,"on null thing");
            //onNotTing();
        }
        data.clear_Data();


    }


    public void onHearEvent(SensorEvent event){


        long cT =System.currentTimeMillis();
        PulseModel pulseMode = null;
        int hearRate = (int)event.values[0];
        if(pulseMode==null)
            pulseMode = new PulseModel(hearRate,hearRate,hearRate);

        if (hearRate < pulseMode.pulseMin)
            pulseMode.pulseMin = hearRate;
        if (hearRate > pulseMode.pulseMax)
            pulseMode.pulseMax = hearRate;
        pulseMode.list.add(hearRate);

        renderHeartText(hearRate);

        //Logger.i(TAG,"hearRate "+hearRate+"");
        //如果与上一次获取步数间隔的时间小于十分钟,则不上传
        if(cT-WatchConstant.lastUploadHeartRateTime< WatchConstant.HEART_RATE_UPLOAD_TIME) return;

        int sum = 0;
        for(int i =0  ; i < pulseMode.list.size();i++){
            sum+= pulseMode.list.get(i);
        }
        pulseMode.pulseArg = sum/pulseMode.list.size();

        PensionClient.getInstace().uploadHeartRate(
                System.currentTimeMillis()+"",
                pulseMode,
                hearRate+"")
                .subscribe(pensionObservable);

        WatchConstant.lastUploadHeartRateTime = cT;

        //note: 重置pulsemode
        pulseMode = null;


        //TODO:到时再改位置

        mLocationClient.start();
        Logger.i("Location","location client start");

        //TODO:如上
        final WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mWifiBroadcastREceiver =  new BroadcastReceiver() {
            WifiManager wifiManager = mWifiManager;
            @Override
            public void onReceive(Context context, Intent intent) {
                if(mWifiManager == null) return;
                if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                    List<ScanResult> list = wifiManager.getScanResults();

                    WifiTableModel wifiTableModel = new WifiTableModel();
                    for(int i =0; i < list.size() ; i++){

                        wifiTableModel.wifitable
                                .add(new WifiScanModel(
                                        list.get(i).BSSID,
                                        list.get(i).SSID,
                                        list.get(i).level,
                                        list.get(i).timestamp
                                ));
                    }
                    Logger.i(TAG,new Gson().toJson(wifiTableModel));

                    PensionClient.getInstace().uploadWifiTable(
                            wifiTableModel)
                            .subscribe(pensionObservable);
                }

            }
        };
        registerReceiver(mWifiBroadcastREceiver, filter);

        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        mWifiManager.startScan();

    }


    public void onStepEvent(SensorEvent event){


        Logger.i("STEP","ONSTEPEVENT");

        long cT =System.currentTimeMillis();

        long stepCount = (long)event.values[0];

        String preToday = toDayStepModel.getDay();

        String toDay = new SimpleDateFormat("dd")
                .format(System.currentTimeMillis());

        if(!preToday.equals(toDay)){
            Logger.i("STEP","不等于");
            toDayStepModel.todayStepCount = 0;
            toDayStepModel.lastUpdateTimeMillis = System.currentTimeMillis();
        }

        //如果该app开启至今没有记录过步数,就记录一下当前信息
        if(!hasRecordStep){
            appStartStep = stepCount;
            hasRecordStep = true;
        } else{
            //否则,记录数据
            long thisStepCount = stepCount - appStartStep;//这一次总步数
            long thisUsefulStepCount = thisStepCount - toDayStepModel.previousStepCount;
            toDayStepModel.todayStepCount += thisUsefulStepCount;
            toDayStepModel.previousStepCount = thisStepCount;
            toDayStepModel.lastUpdateTimeMillis = System.currentTimeMillis();

//            Logger.i(TAG,"on sensor thisStepCount " +thisStepCount+"");
//            Logger.i(TAG,"on sensor previousStepCount " +toDayStepModel.previousStepCount+"");
//            Logger.i(TAG,"on sensor toDayStepModel.todayStepCount " +  toDayStepModel.todayStepCount);
        }

        Logger.i(TAG,"on sensor appStartStep " +appStartStep+"");


        //如果与上一次获取步数间隔的时间小于十分钟,则不上传
        if(cT- WatchConstant.lastUploadStepTime<WatchConstant.STEP_UPLOAD_DEVILE_TIME)
            return;

        Log.i(TAG,"Step "+stepCount);

        PensionClient.getInstace().uploadStep(
                System.currentTimeMillis()+"",
                stepCount+"")
                .subscribe(
                        pensionObservable
                );


//        Logger.i(TAG,"on sensor step " +toDayStepModel.todayStepCount+"");


        renderStepText(stepCount);

        WatchConstant.lastUploadStepTime = cT;
    }


    public void onAccEvent(SensorEvent event){
        for (int i = 0; i < 3; i++) {
            gravity[i] = (float) (0.2 * event.values[i] + 0.8 * gravity[i]); //for gravity Data
            motion[i] = event.values[i] - gravity[i];// for motion accel data
        }
        double acc_squar = Math.sqrt(Math.pow(motion[0], 2) + Math.pow(motion[1], 2) + Math.pow(motion[2], 2));
        double gra_squar = Math.sqrt(Math.pow(gravity[0], 2) + Math.pow(gravity[1], 2) + Math.pow(gravity[2], 2));
        data.time.add((double)System.currentTimeMillis());
        data.x.add(motion[0]);
        data.y.add(motion[1]);
        data.z.add(motion[2]);
        data.gra_x.add(gravity[0]);
        data.gra_y.add(gravity[1]);
        data.gra_z.add(gravity[2]);
        data.acc_squar.add(acc_squar);
        data.gra_squar.add(gra_squar);
    }


    public void onGyroEvent(SensorEvent event){
        double gyro_squar = Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2));
        data.gyro_x.add((double) event.values[0]);
        data.gyro_y.add((double) event.values[1]);
        data.gyro_z.add((double) event.values[2]);
        data.gyro_squar.add(gyro_squar);
    }


    //重写按键用于开发
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        Logger.i("Key Down",keyCode+"");
        switch (keyCode){
            case
                KeyEvent.KEYCODE_STEM_1://===265是huawei watch2 除返回首页的按键外的另一个按键的keycode
                    requestCall();
                return false;

        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        init();
    }


    public void onSensorChanged(SensorEvent event){
        int type = event.sensor.getType();
        switch(type){
            case  Sensor.TYPE_HEART_RATE:
                onHearEvent(event);
                break;

            case  Sensor.TYPE_STEP_COUNTER:

                onStepEvent(event);

                break;

            case Sensor.TYPE_GYROSCOPE://陀螺仪
                onGyroEvent(event);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                onAccEvent(event);
                break;
        }

        if(data.x.size()!=0 && data.x.size()%20==0)

        {
            //TODO:这个function后面再改名字
            onDealData();

        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        //解除监听感应器
        mgr.unregisterListener(sensorlistener);

        if(mWifiBroadcastREceiver!= null)
        {
            unregisterReceiver(mWifiBroadcastREceiver);
            mWifiBroadcastREceiver = null;
        }

        //保存cache
        saveTodayStepModel();

    }


    public void renderHeartText(int heartRate){
        TextView tvHeartRate = findViewById(R.id.tvHeartRate);
       // Logger.i(TAG,"render heartrate" +heartRate);
        tvHeartRate.setText("心率 : "+heartRate);
    }


    public void renderStepText(long stepcount){
        TextView tvStepCount = findViewById(R.id.tvStepCount);
        Logger.i(TAG,"render stepcount" +stepcount);
        tvStepCount.setText("最近步数 : "+stepcount);
    }


    public void requestCall(){
        Intent intent = new  Intent(Intent.ACTION_CALL);
        Uri data =   Uri.parse("tel:"+WatchConstant.DEFAULT_PHONE_NUMBER);
        intent.setData(data);
        try {
            Toast.makeText(this,"call",Toast.LENGTH_LONG).show();
            startActivity(intent);
        } catch (SecurityException e){

            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void saveTodayStepModel(){
        getSharedPreferences(WatchConstant.SHAREDPREFERENCED_NAME,Activity.MODE_PRIVATE)
                .edit()
                .putString(WatchConstant.SHAREDPREFERENCED_KEY_TODAY_STEP,
                        new Gson().toJson(toDayStepModel))
                .commit();
    }

}
