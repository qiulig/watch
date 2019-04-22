package xyz.kfdykme.demo.myapplication

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface PensionApi{

    @FormUrlEncoded
    @POST("elder/step")
    fun uploadStep(@Field("time") time:String,
               @Field("stepcount") stepcount:String,
               @Field("postby") postby:String,
               @Field("token") token:String): Observable<HttpResult>

    @FormUrlEncoded
    @POST("elder/pulse")
    fun uploadHeartRate(@Field("time") time:String,
                        @Field("pulsemax") pulseMax:String,
                        @Field("pulsemin") pulseMin:String,
                        @Field("pulseaverage") pulseArg:String,
                        @Field("postby") postby:String,
                        @Field("token") token:String)
    :Observable<HttpResult>

    @FormUrlEncoded
    @POST("elder/gpslocation")
    fun uploadLocation(@Field("time") time:String,
                       @Field("longitude") longitude:String,
                       @Field("latitude")latitude:String,
                       @Field("postby")postby:String,
                       @Field("token") token :String
    )
            :Observable<HttpResult>


    @FormUrlEncoded
    @POST("elder/wifilocation")
    fun uploadWifiTable(@Field("time") time:String,
                        @Field("wifitable") wifitable:String,
                        @Field("postby")postby:String,
                        @Field("token") token :String
    )
            :Observable<HttpResult>
    @FormUrlEncoded
    @POST("elder/status")
    fun postStatus(@Field("time") time:String,
                        @Field("status") status:String,
                        @Field("message") message:String,
                        @Field("postby")postby:String,
                        @Field("token") token :String
    )
    :Observable<HttpResult>

}