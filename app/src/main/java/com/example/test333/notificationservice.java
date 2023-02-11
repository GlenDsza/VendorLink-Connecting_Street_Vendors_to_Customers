package com.example.test333;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class notificationservice extends Service implements LocationListener {
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LatLng cust;
    sqliteDB sdb;
    LatLng cusloc;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 1000;
    private ArrayList<String> keys;
    List<LatLng> markers = new ArrayList<LatLng>();
    String username;
    NotificationCompat.Builder builder;
    public DatabaseReference reference,ref;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public notificationservice()
    {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        sdb = new sqliteDB(this);
        username=sdb.getDoc();
        reference = FirebaseDatabase.getInstance().getReference().child("USERS").child("HAWKERS").child(username).child("username");
        reference.setValue(username);
        reference = FirebaseDatabase.getInstance().getReference().child("USERS").child("HAWKERS").child(username).child("LOC");
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), 6, notify_interval);
        keys = new ArrayList<>();
       fn_getlocation();
        readChanges();
        getdata();
        createnotif();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    private void createnotif()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Notification","My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        builder = new NotificationCompat.Builder(this,"Notification");
        builder.setContentTitle(" HAWKER IN YOUR AREA");
        builder.setContentText("Hawker has reached your locality dont forget to buy your favourite items");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);



    }
    private void getdata() {
        Query rref = FirebaseDatabase.getInstance().getReference().child("USERS");
        rref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.child("HAWKERS").getChildren()) {
                    String key = childSnapshot.getKey();
                    keys.add(key);
                }

                for (int i = 0; i < keys.size(); i++) {
                    Double lat = dataSnapshot.child("HAWKERS").child(keys.get(i)).child("LOC").child("latitude").getValue(Double.class);
                    Double lon = dataSnapshot.child("HAWKERS").child(keys.get(i)).child("LOC").child("longitude").getValue(Double.class);
//                    Toast.makeText(getApplicationContext(), "username= " + user + "  latitude: " + lat + " longitude: " + lon, Toast.LENGTH_SHORT).show();
                    if (lat != null || lon != null) {
                        LatLng sydneyy = new LatLng(lat, lon);
                        markers.add(sydneyy);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readChanges() {
        ref = FirebaseDatabase.getInstance().getReference().child("USERS").child("HAWKERS");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(int i=0;i<keys.size();i++) {
                    Double lat = dataSnapshot.child(keys.get(i)).child("LOC").child("latitude").getValue(Double.class);
                    Double lon = dataSnapshot.child(keys.get(i)).child("LOC").child("longitude").getValue(Double.class);
//                    Toast.makeText(getApplicationContext(), "changed", Toast.LENGTH_SHORT).show();
                    LatLng sydneyy = new LatLng(lat, lon);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        ref.addValueEventListener(eventListener);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    try {
                        MyLocation location = snapshot.getValue(MyLocation.class);
                        if (location != null) {
                            cusloc = new LatLng(location.getLatitude(),location.getLongitude());
                        }
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
            public void check()
            {

                for (LatLng marker : markers) {
                    if (SphericalUtil.computeDistanceBetween(cust, marker) < 50)
                    {
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
                        managerCompat.notify(1,builder.build());
                    }

                }
            }
    private void fn_getlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {

        } else {

            if (isNetworkEnable) {
                location = null;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        saveLocation(location);
                    }
                }

            }


            if (isGPSEnable) {
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        Log.e("latitude", location.getLatitude() + "");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        saveLocation(location);
                    }
                }
            }


        }

    }
    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    fn_getlocation();
                    readChanges();
                    check();
                }
            });

        }
    }

    private void saveLocation(Location location) {

        reference.setValue(location);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mTimer!=null){
            mTimer.cancel();
        }
        stopSelf();

    }
}

