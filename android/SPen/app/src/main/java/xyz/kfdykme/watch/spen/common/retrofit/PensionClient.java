package xyz.kfdykme.watch.spen.common.retrofit;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import xyz.kfdykme.demo.myapplication.HttpResult;
import xyz.kfdykme.demo.myapplication.PensionApi;
import xyz.kfdykme.watch.spen.common.WatchConstant;
import xyz.kfdykme.watch.spen.common.model.PulseModel;
import xyz.kfdykme.watch.spen.common.model.WifiTableModel;

public class PensionClient {


    private static PensionClient instance;

    Retrofit retrofit;


    PensionApi pension;

    public static PensionClient getInstace(){
        if(instance == null)
            instance = new PensionClient();
        return instance;
    }

    private PensionClient(){

        //init retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(WatchConstant.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();


        pension = retrofit.create(PensionApi.class);

    }

    public Observable<HttpResult> uploadStep(
            String time,
            String stepcount){
        return pension.uploadStep(time,stepcount,WatchConstant.POST_BY,
                WatchConstant.DEFAULT_TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public  Observable<HttpResult> uploadHeartRate(
        String time,
        PulseModel model,
        String pulsecount){
        return pension.uploadHeartRate(
                time,
                model.pulseMax+"",
                model.pulseMin+"",
                model.pulseArg+"",
                WatchConstant.POST_BY,
                WatchConstant.DEFAULT_TOKEN)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<HttpResult> uploadLoaction(String longitude,  String latitude){
        return pension.uploadLocation(
                System.currentTimeMillis()+"",
                longitude,
                latitude,
                WatchConstant.POST_BY,
                WatchConstant.DEFAULT_TOKEN
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<HttpResult> uploadWifiTable(WifiTableModel wifiTableModel){
        return pension.uploadWifiTable(
                System.currentTimeMillis()+"",
                new Gson().toJson(wifiTableModel),
                WatchConstant.POST_BY,
                WatchConstant.DEFAULT_TOKEN
        )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<HttpResult> postStatus(String status,
                                             String message){
        return pension.postStatus(
                System.currentTimeMillis()+"",
                status,
                message,
                WatchConstant.POST_BY,
                WatchConstant.DEFAULT_TOKEN
        );
    }
}
