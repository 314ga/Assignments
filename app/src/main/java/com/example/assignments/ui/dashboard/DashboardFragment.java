package com.example.assignments.ui.dashboard;

import static com.example.assignments.utils.FileHandling.readSerializable;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.assignments.R;
import com.example.assignments.databinding.FragmentDashboardBinding;
import com.example.assignments.ui.notifications.LocationActivity;
import com.example.assignments.utils.CSVFile;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private MapView map = null;
    private List loadedFile;
    private Polyline roadOverlay;
    private RoadManager roadManager;
    private Road road;
    private GeoPoint startPoint;
    private Marker startMarker;
    private IMapController mapController;
    private Button loadTracking;
    private int filterPoints = 30;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loadTracking = binding.btnLoadTrajectory;
        final RadioGroup projection = binding.radioGroup;
        final RadioButton raw = binding.rbRaw;
        raw.setChecked(true);
        loadTracking.setOnClickListener(view -> {
                    });


        projection.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
            boolean isChecked = checkedRadioButton.isChecked();
            if (isChecked)
            {
                if(checkedRadioButton.getText().equals("MEAN"))
                {
                    ArrayList<GeoPoint> path = createGeoPointListMeanFiltered(loadedFile);
                    map.getOverlays().remove(roadOverlay);
                    startPoint = path.get(0);
                    mapController.setCenter(startPoint);
                    startMarker = new Marker(map);
                    startMarker.setPosition(startPoint);
                    road = roadManager.getRoad(path);
                    roadOverlay = RoadManager.buildRoadOverlay(road);
                    map.getOverlays().add(roadOverlay);
                    map.invalidate();
                }
                else if(checkedRadioButton.getText().equals("MEDIAN"))
                {
                    ArrayList<GeoPoint> path = createGeoPointListMedianFiltered(loadedFile);
                    map.getOverlays().remove(roadOverlay);
                    startPoint = path.get(0);
                    mapController.setCenter(startPoint);
                    startMarker = new Marker(map);
                    startMarker.setPosition(startPoint);
                    road = roadManager.getRoad(path);
                    roadOverlay = RoadManager.buildRoadOverlay(road);
                    map.getOverlays().add(roadOverlay);
                    map.invalidate();
                }
                else
                {
                    ArrayList<GeoPoint> path = createGeoPointList(loadedFile);
                    map.getOverlays().remove(roadOverlay);
                    startPoint = path.get(0);
                    mapController.setCenter(startPoint);
                    startMarker = new Marker(map);
                    startMarker.setPosition(startPoint);
                    road = roadManager.getRoad(path);
                    roadOverlay = RoadManager.buildRoadOverlay(road,0xFFB74093,5.0f);
                    map.getOverlays().add(roadOverlay);
                    map.invalidate();
                }
            }
        });
        //inflate and create the map + allow zoom with fingers
        map = (MapView) binding.map;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        map.setMultiTouchControls(true);

        loadedFile = readCSVFile(R.raw.walking);
        //create road path
        ArrayList<GeoPoint> path = createGeoPointList(loadedFile);

        //Create map control + starting point
        mapController = map.getController();
        mapController.setZoom(13.0);
        startPoint = path.get(0);
        mapController.setCenter(startPoint);

        //Create marker for displaying starting points
        startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        //startMarker.setIcon(getResources().getDrawable(R.drawable.ic_launcher)); -changing icon
        // startMarker.setTitle("Start point");

        //Create road manager for creating path
        roadManager = new OSRMRoadManager(getContext(),"JAKUB_TRACK");
        ((OSRMRoadManager)roadManager).setMean(OSRMRoadManager.MEAN_BY_FOOT);
        road = roadManager.getRoad(path);
        roadOverlay = RoadManager.buildRoadOverlay(road, 0xFFB74093,8.0f);

        //add path into the map and refresh
        map.getOverlays().add(roadOverlay);
        map.invalidate();
        return root;
    }
    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
       map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
       map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private ArrayList<GeoPoint> createGeoPointList(List list)
    {
        ArrayList<GeoPoint> waypoints = new ArrayList<>();

        int i = 1;
        while(i < list.size())
        {
            String[] pointArray  = (String[])list.get(i);
            //3 and phone element for phone_lat and phone_long
            GeoPoint point = new GeoPoint(Double.parseDouble(pointArray[3]), Double.parseDouble(pointArray[4]));
            waypoints.add(point);
            i++;
        }
        return waypoints;
    }
    private ArrayList<GeoPoint> createGeoPointList(LocationActivity locationActivity)
    {
        ArrayList<GeoPoint> waypoints = new ArrayList<>();


        int i = 1;
        while(i < locationActivity.activity.size())
        {

            GeoPoint point = new GeoPoint(locationActivity.getLatitude().get(i),locationActivity.getLongitude().get(i));
            waypoints.add(point);
            i++;
        }
        return waypoints;
    }
    private ArrayList<GeoPoint> createGeoPointListMeanFiltered(List list)
    {
        ArrayList<GeoPoint> waypoints = new ArrayList<>();

        int i = 1;
        double[] longitudes = new double[filterPoints]; //4
        double[] latitudes = new double[filterPoints];
        int pointsCounter = 0;
        while(i < list.size())
        {
            if(i%(filterPoints+1)==0)
            {
                GeoPoint point = new GeoPoint(getAverageFromArray(latitudes), getAverageFromArray(longitudes));
                waypoints.add(point);
                longitudes = new double[filterPoints]; //4
                latitudes = new double[filterPoints];
                pointsCounter = 0;
            }
            else
            {
                String[] pointArray  = (String[])list.get(i);
                longitudes[pointsCounter] = Double.parseDouble(pointArray[4]);
                latitudes[pointsCounter] = Double.parseDouble(pointArray[3]);
                pointsCounter++;
            }
            i++;
        }
        return waypoints;
    }
    private ArrayList<GeoPoint> createGeoPointListMedianFiltered(List list)
    {
        ArrayList<GeoPoint> waypoints = new ArrayList<>();

        int i = 1;
        double[] longitudes = new double[filterPoints]; //4
        double[] latitudes = new double[filterPoints];
        int pointsCounter = 0;
        while(i < list.size())
        {
            if(i%(filterPoints+1)==0)
            {
                Arrays.sort(latitudes);
                Arrays.sort(longitudes);
                GeoPoint point = new GeoPoint(latitudes[(int)filterPoints/2],longitudes[(int)filterPoints/2]);
                waypoints.add(point);
                longitudes = new double[filterPoints]; //4
                latitudes = new double[filterPoints];
                pointsCounter = 0;
            }
            else
            {
                String[] pointArray  = (String[])list.get(i);
                longitudes[pointsCounter] = Double.parseDouble(pointArray[4]);
                latitudes[pointsCounter] = Double.parseDouble(pointArray[3]);
                pointsCounter++;
            }
            i++;
        }
        return waypoints;
    }

    private double getAverageFromArray(double[] array)
    {
        double sum = 0.0;
        for (double d : array) sum += d;
        double average = sum / array.length;
        return average;
    }

    private  List readCSVFile(int id)
    {
        //read csv tracking file
        InputStream inputStream = getResources().openRawResource(id);
        CSVFile csvFile = new CSVFile(inputStream);
        return csvFile.read();
    }
}