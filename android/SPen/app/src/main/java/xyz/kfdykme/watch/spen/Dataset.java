package xyz.kfdykme.watch.spen;

import java.util.ArrayList;
import java.util.List;

//TODO:整理这些类
public class Dataset {
    public List<Double> time = new ArrayList<Double>();
    public List<Double> x = new ArrayList<Double>();
    public List<Double> y = new ArrayList<Double>();
    public List<Double> z= new ArrayList<Double>();
    public List<Double> gra_x = new ArrayList<Double>();
    public List<Double> gra_y = new ArrayList<Double>();
    public List<Double> gra_z = new ArrayList<Double>();
    public List<Double> acc_squar = new ArrayList<Double>();
    public List<Double> gra_squar = new ArrayList<Double>();
    public List<Double> gyro_x = new ArrayList<Double>();
    public List<Double> gyro_y = new ArrayList<Double>();
    public List<Double> gyro_z = new ArrayList<Double>();
    public List<Double> gyro_squar = new ArrayList<Double>();

    public void clear_Data(){
        time.clear();
        x.clear();
        y.clear();
        z.clear();
        gra_x.clear();
        gra_y.clear();
        gra_z.clear();
        acc_squar.clear();
        gra_squar.clear();
        gyro_x.clear();
        gyro_y.clear();
        gyro_z.clear();
        gyro_squar.clear();
    }
    public int size(){
        return time.size();
    }
}
