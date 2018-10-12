package com.dylanmohsoftware.publictrans.Modules;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {
    public int index;
    public Distance distance;
    public Duration duration;
    public Duration startTime;
    public Duration endTime;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;
    public double price;

    public List<LatLng> points;
    public List<RouteStep> steps;
}
