package xyz.kfdykme.watch.spen.common.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class TodayStepModel {

    public long previousStepCount = 0;
    public long lastUpdateTimeMillis = 0;
    public long todayStepCount = 0;

    public String getDay(){

        SimpleDateFormat f = new SimpleDateFormat("dd");
        return f.format(new Date(lastUpdateTimeMillis));
    }

}
