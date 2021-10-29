package com.example.assignments.utils;

public class Constants
{
    public static final String BROADCAST_DETECTED_ACTIVITY = "activity_intent";
    public static final String BROADCAST_DETECTED_ACTIVITY_LOCATION = "location_activity_intent";
    // the desired time between activity detections. Larger values will result in fewer activity
    // detections while improving battery life. A value of 0 will result in activity detections
    // at the fastest possible rate.
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 2000; // every N seconds
}
