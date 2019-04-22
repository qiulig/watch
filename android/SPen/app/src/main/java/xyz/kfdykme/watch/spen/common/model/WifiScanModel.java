package xyz.kfdykme.watch.spen.common.model;

public class WifiScanModel {


    public String BSSID = "";
    public String SSID = "";
    public long level = 0;
    public long timestamp = 0;

    public WifiScanModel(String BSSID, String SSID, long level, long timestamp) {
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.level = level;
        this.timestamp = timestamp;
    }

}
