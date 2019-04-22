package xyz.kfdykme.watch.spen.common.model;

import java.util.ArrayList;
import java.util.List;

public class PulseModel {
    public PulseModel(int pulseMax, int pulseMin, int pulseArg) {
        this.pulseMax = pulseMax;
        this.pulseMin = pulseMin;
        this.pulseArg = pulseArg;
    }

    public List<Integer> list = new ArrayList<>();
    public int pulseMax = 0;
    public int pulseMin = 0;
    public int pulseArg = 0;
}
