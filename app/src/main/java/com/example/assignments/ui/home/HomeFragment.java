package com.example.assignments.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;

import com.example.assignments.databinding.FragmentHomeBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    FusedLocationProviderClient fusedLocationProviderCLient;
    SensorManager sensorManager;
    Sensor sensor;
    TextView xAxis,yAxis,zAxis;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        final Button getLocation = binding.btnLocation;
        final TextView longitude, latitude, countryName, timestamp, address;
        longitude = binding.longitude;
        latitude = binding.latitude;
        countryName = binding.country;
        timestamp = binding.timestamp;
        address = binding.address;
        zAxis = binding.zAxis;
        xAxis = binding.xAxis;
        yAxis = binding.yAxis;
        fusedLocationProviderCLient = LocationServices.getFusedLocationProviderClient(getActivity());


        getLocation.setOnClickListener(view -> {

            if (!(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                fusedLocationProviderCLient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        //initialize location
                        Location location = task.getResult();
                        if (location != null) {

                            try {
                                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                Log.i("info", ""+addresses.get(0).getLocality());
                                longitude.setText(Html.fromHtml("<font color= '#6200EE'><b>Longitude:</b><br></font>" + addresses.get(0).getLongitude()));
                                latitude.setText(Html.fromHtml("<font color= '#6200EE'><b>Latitude:</b><br></font>" + addresses.get(0).getLatitude()));
                                countryName.setText(Html.fromHtml("<font color= '#6200EE'><b>Country:</b><br></font>" + addresses.get(0).getCountryName()));
                                address.setText(Html.fromHtml("<font color= '#6200EE'><b>Address:</b><br></font>" + addresses.get(0).getAddressLine(0)));
                                Date currentTime = Calendar.getInstance().getTime();
                                timestamp.setText(Html.fromHtml("<font color= '#6200EE'><b>Date and time:</b><br></font>" + currentTime.toString()));


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

            }

        });
        return root;
    }
    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(gyroListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(gyroListener);
    }

    public SensorEventListener gyroListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            xAxis.setText(Html.fromHtml("<font color= '#6200EE'><b>X Axis:</b><br></font>" + (int) x ));
            yAxis.setText(Html.fromHtml("<font color= '#6200EE'><b>X Axis:</b><br></font>" + (int) y ));
            zAxis.setText(Html.fromHtml("<font color= '#6200EE'><b>X Axis:</b><br></font>" + (int) z ));
        }
    };
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}