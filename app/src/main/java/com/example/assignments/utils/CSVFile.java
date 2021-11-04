package com.example.assignments.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import com.example.assignments.ui.notifications.LocationActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class CSVFile {
    InputStream inputStream;

    public CSVFile(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public List read(){
        List resultList = new ArrayList();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                resultList.add(row);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
        return resultList;
    }

    public void write(String fileNameVar, LocationActivity activity, Context ctx, boolean allActivities) throws IOException
    {
/*  ANDROID 11*/
    File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() +"/Assignment3");
        if (!directory.exists())
            directory.mkdir();
        File patternDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() +"/Assignment3/"+ fileNameVar + ".csv");
        FileOutputStream fos;/**/
        /*File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/Assignment3");
        if (!directory.exists())
            directory.mkdir();

        File patternDirectory;
        FileOutputStream fos;*/
        if(allActivities)
        {

            int incrementor = 0;
            while(true)
            {
                patternDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() +"/Assignment3/"+ fileNameVar + incrementor + ".csv");
                if (patternDirectory.exists ())
                {
                    incrementor++;
                }
                else
                {
                    patternDirectory.createNewFile();
                    break;
                }

            }
        }
        else
        {
            patternDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() +"/Assignment3/"+ fileNameVar + ".csv");


            if (patternDirectory.exists ())
                patternDirectory.delete ();
            patternDirectory.createNewFile();
        }


        try {
            fos = new FileOutputStream (new File(patternDirectory.getAbsolutePath().toString()), true);
            String TestString="activity,confidence,timestamp\n";
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            // Write the string to the file
            if(allActivities)
            {
                for( int i=0; i<activity.getActivity().size(); i++)
                {
                    TestString+=activity.getActivity().get(i) + ","
                            + activity.getAccuracy().get(i).toString() + ","
                            + activity.getTimestamp().get(i).toString()
                           + "\n";        // to pass in every widget a context of activity (necessary)
                }
            }
            else
            {
                for( int i=0; i<activity.getActivity().size(); i++)
                {
                    TestString+=activity.getActivity().get(i) + ","
                            + activity.getAccuracy().get(i).toString() + ","
                            + activity.getTimestamp().get(i).toString() + ","
                            + activity.getLatitude().get(i).toString() + ","
                            + activity.getLongitude().get(i).toString() + "\n";        // to pass in every widget a context of activity (necessary)
                }
            }

            Log.v("the string is",TestString);
            osw.write(TestString);
            osw.flush();
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void writeNewExcel(String data) throws IOException
    {
        /*  ANDROID 11*/
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/Assignment3");
        if (!directory.exists())
            directory.mkdir();
        File patternDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/Assignment3/fixedExcel.csv");
        FileOutputStream fos;/**/
        /*File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/Assignment3");
        if (!directory.exists())
            directory.mkdir();
    patternDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() +"/Assignment3/fixedExcel.csv");

        File patternDirectory;
        FileOutputStream fos;*/


            if (patternDirectory.exists ())
                patternDirectory.delete ();

            patternDirectory.createNewFile();

        try {
            fos = new FileOutputStream (new File(patternDirectory.getAbsolutePath().toString()), true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(data);
            osw.flush();
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
