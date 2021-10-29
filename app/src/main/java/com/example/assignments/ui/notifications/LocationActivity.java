package com.example.assignments.ui.notifications;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LocationActivity implements Serializable
{
    public List<Double> longitude;
    public List<Double> latitude;
    public List<String> activity;
    public List<Integer> accuracy;
    public List<Date> timestamp;

    public LocationActivity()
    {
        this.longitude = new ArrayList<>();
        this.latitude = new ArrayList<>();
        this.activity = new ArrayList<>();
        this.accuracy = new ArrayList<>();
        this.timestamp = new ArrayList<>();
    }

    public List<Double> getLongitude() {
        return longitude;
    }

    public void setLongitude(List<Double> longitude) {
        this.longitude = longitude;
    }

    public List<Double> getLatitude() {
        return latitude;
    }

    public void setLatitude(List<Double> latitude) {
        this.latitude = latitude;
    }

    public List<String> getActivity() {
        return activity;
    }

    public void setActivity(List<String> activity) {
        this.activity = activity;
    }

    public List<Integer> getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(List<Integer> accuracy) {
        this.accuracy = accuracy;
    }

    public List<Date> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(List<Date> timestamp) {
        this.timestamp = timestamp;
    }

    public void addLocationPoint(double latitude, double longitude, String activity, int accuracy)
    {
        this.longitude.add(longitude);
        this.latitude.add(latitude);
        this.activity.add(activity);
        this.accuracy.add(accuracy);
        this.timestamp.add(Calendar.getInstance().getTime());
    }
}
