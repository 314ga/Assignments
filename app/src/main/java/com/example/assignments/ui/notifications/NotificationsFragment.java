package com.example.assignments.ui.notifications;

import static com.example.assignments.utils.FileHandling.readSerializable;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.assignments.R;
import com.example.assignments.databinding.FragmentNotificationsBinding;
import com.example.assignments.services.ActivityDetectionService;
import com.example.assignments.services.LocationService;
import com.example.assignments.utils.CSVFile;
import com.example.assignments.utils.Constants;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.DetectedActivity;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NotificationsFragment extends Fragment {

    private static final String TAG = "Jakubapp";
    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    private TextView mTextARLabel;
    private TextView mTextConfidence;
    private String currentActivity;
    private int accuracy;
    private LocationActivity locationActivity;
    private ArrayList<Model> trackingList;
    private Button startTracking,stopTracking;
    List<ActivityTransition> transitions = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        currentActivity = "unknown";


        mTextARLabel = binding.textLabel;
        mTextConfidence = binding.textConfidence;
        startTracking = binding.startRacking;
        stopTracking = binding.stopRacking;
        locationActivity = new LocationActivity();
        trackingList = new ArrayList<>();
        ListView lview = binding.listview;
        listviewAdapter adapter = new listviewAdapter(getActivity(), trackingList);
        lview.setAdapter(adapter);

        //populateList();

        //adapter.notifyDataSetChanged();

        startTracking.setOnClickListener(view -> {
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(mActivityBroadcastReceiver,
                    new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(mActivityBroadcastReceiver,
                    new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY_LOCATION));
            if(!isMyServiceRunning(ActivityDetectionService.class))
            {
                getActivity().startService(new Intent(getContext(), ActivityDetectionService.class));
            }
            if(!isMyServiceRunning(LocationService.class))
            {
                getActivity().startService(new Intent(getContext(), LocationService.class));
            }
        });
       stopTracking.setOnClickListener(view -> {
           showCustomDialog();
           getActivity().stopService(new Intent(getContext(),ActivityDetectionService.class));
           getActivity().stopService(new Intent(getContext(),LocationService.class));
           LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mActivityBroadcastReceiver);
        });
        return root;
    }

    private void populateList() {
        Model model;
        LocationActivity loadLocationActivity = (LocationActivity) readSerializable(getContext(), "kubo");

        for (int i = 0; i < loadLocationActivity.activity.size(); i++)
        {
            model = new Model(loadLocationActivity.activity.get(i), loadLocationActivity.accuracy.get(i).toString(),
                    loadLocationActivity.latitude.get(i).toString(), loadLocationActivity.longitude.get(i).toString(),
                    loadLocationActivity.timestamp.get(i).toString());
            trackingList.add(model);
        }
    }

    private void showCustomDialog()
    {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_save_tracking);

        final EditText fileName = dialog.findViewById(R.id.filename);
        final Button save = dialog.findViewById(R.id.save_btn);

        save.setOnClickListener(view -> {
            writeCSVFile(fileName.getText().toString());
            dialog.dismiss();
        });

        dialog.show();
    }
    private  void writeCSVFile(String filename)
    {
        //read csv tracking file
        CSVFile csvFile = new CSVFile(null);
        try {
            csvFile.write(filename,locationActivity,getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver mActivityBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                int type = intent.getIntExtra("type", -1);
                int confidence = intent.getIntExtra("confidence", 0);
                handleUserActivity(type, confidence);
            }
            else if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY_LOCATION)) {
                double longitude = intent.getDoubleExtra("longitude", -1.0);
                double latitude = intent.getDoubleExtra("latitude", -1.0);
                mTextConfidence.setText("Lat is:" + latitude + "Long is:" + longitude + "\n" + "Activity: " + currentActivity);
                locationActivity.addLocationPoint(latitude,longitude,currentActivity,accuracy);
                Log.d("location", "Lat is:" + latitude + "Long is:" + longitude + "\n" + "Activity: " + currentActivity);
            }
        }
    };

    private void handleUserActivity(int type, int confidence) {
        String label = "Unknown";
        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = "In_Vehicle";
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = "On_Bicycle";
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = "On_Foot";
                break;
            }
            case DetectedActivity.RUNNING: {
                label = "Running";
                break;
            }
            case DetectedActivity.STILL: {
                label = "Still";
                break;
            }
            case DetectedActivity.TILTING: {
                label = "Tilting";
                break;
            }
            case DetectedActivity.WALKING: {
                label = "Walking";
                break;
            }
            case DetectedActivity.UNKNOWN: {
                break;
            }
        }
        if(confidence>60)
        {
            currentActivity = label;
            accuracy = confidence;
        }
        Log.d(TAG, "broadcast:onReceive(): Activity is " + label
                + " and confidence level is: " + confidence);

        mTextARLabel.setText( "type:" + label);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if(mActivityBroadcastReceiver != null){
            getActivity().stopService(new Intent(getContext(),ActivityDetectionService.class));
            getActivity().stopService(new Intent(getContext(),LocationService.class));
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mActivityBroadcastReceiver);

        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}