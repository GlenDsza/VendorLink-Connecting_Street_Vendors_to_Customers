package com.example.test333;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;


import com.example.test333.databinding.ActivityMapsBinding;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, NavigationView.OnNavigationItemSelectedListener {

    GoogleMap mMap;
    public static int flag=1;
    private ActivityMapsBinding binding;
    private DatabaseReference reference, ref;
    private LocationManager manager;
    private final int MIN_TIME = 8000; // in milliseconds
    private final int MIN_DISTANCE = 1; // 1 meter
    static boolean count_search = false;
    SwitchCompat map_sw;
    Double cuslat, cuslon;
    LatLng cust;
    // For navigation drawer
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageButton imageButton, map_audio;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    //////////////////////////////////////
    private ArrayList<String> keys;
    ArrayList<String> shop = new ArrayList<>();
    Marker mymarker, marker;
    String username, shopname;
    sqliteDB sdb;
    private StorageReference mStorage;
    FirebaseFirestore userdb;
    String type, autostr;
    AutoCompleteTextView map_auto;
    List<Marker> markers = new ArrayList<Marker>();
    CircleOptions circleOptions;
    Circle circle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mFirebaseAuth = FirebaseAuth.getInstance();
        sdb = new sqliteDB(this);
        username = sdb.getDoc();
        circleOptions = new CircleOptions();
        reference = FirebaseDatabase.getInstance().getReference().child("USERS").child("CUSTOMER").child(username).child("username");
        reference.setValue(username);
        reference = FirebaseDatabase.getInstance().getReference().child("USERS").child("CUSTOMER").child(username).child("LOC");
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        userdb = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        Intent it_type = getIntent();
        type = it_type.getStringExtra("type");
        fillNavigation();
        // Search text enable
        ImageButton ig = findViewById(R.id.search_button);
        map_auto = findViewById(R.id.search_text);
        map_audio = findViewById(R.id.map_audio);
        map_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map_auto.setVisibility(View.VISIBLE);
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                try {
                    startActivityForResult(intent, 199);
                }
                catch (Exception e) {
                    Toast
                            .makeText(MapsActivity.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        map_sw = findViewById(R.id.map_theme);

        map_sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map_sw.isChecked())
                {
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style));
                    mymarker.setIcon(BitmapFromVector(getApplicationContext(), R.drawable.ic_map_cust_icon_d));
                    marker.setIcon(BitmapFromVector(getApplicationContext(), R.drawable.ic_map_vendor_icon_d));
                    flag=0;
                }
                else
                {
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style_light));
                    mymarker.setIcon(BitmapFromVector(getApplicationContext(), R.drawable.ic_map_cust_icon_l));
                    marker.setIcon(BitmapFromVector(getApplicationContext(), R.drawable.ic_map_vendor_icon_l));
                    flag=1;

                }
            }
        });
        map_auto.setVisibility(View.INVISIBLE);
        ig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count_search == false) {
//                    createnotif();
                    map_auto.setVisibility(View.VISIBLE);
                    count_search = true;
                } else {
                    if (!map_auto.getText().toString().equals("")) {
                        try {
                            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//                            check();
                            gotomarker();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    } else {
                        map_auto.setVisibility(View.INVISIBLE);
                        count_search = false;
                    }
                }
            }
        });

        keys = new ArrayList<>();
        getLocationUpdates();
        readChanges();
        getdata();
//
        // For navigation drawer inside on create
        drawerLayout = findViewById(R.id.map_draw);
        navigationView = findViewById(R.id.nav_view);

        //added for rounded drawer
        MaterialShapeDrawable navViewBackground = (MaterialShapeDrawable) navigationView.getBackground();
        navViewBackground.setShapeAppearanceModel(
                navViewBackground.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopRightCorner(CornerFamily.ROUNDED,100)
                        .setBottomRightCorner(CornerFamily.ROUNDED,100)
                        .build());
        //added for transparent status bar
        Utility.setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //end of rounded drawer

        imageButton = findViewById(R.id.menu_img);
        navigationView.bringToFront();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);



        //////////////////////////////////////
    }
/////////////////////gets marker data ////////////////////////
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
                    String user = dataSnapshot.child("HAWKERS").child(keys.get(i)).child("username").getValue(String.class);
                    Double lat = dataSnapshot.child("HAWKERS").child(keys.get(i)).child("LOC").child("latitude").getValue(Double.class);
                    Double lon = dataSnapshot.child("HAWKERS").child(keys.get(i)).child("LOC").child("longitude").getValue(Double.class);
//                    Toast.makeText(getApplicationContext(), "username= " + user + "  latitude: " + lat + " longitude: " + lon, Toast.LENGTH_SHORT).show();
                    if (lat != null || lon != null) {
                        LatLng sydneyy = new LatLng(lat, lon);
                        marker = mMap.addMarker(new MarkerOptions().position(sydneyy).visible(false));
                        marker.setIcon(BitmapFromVector(getApplicationContext(), R.drawable.ic_map_vendor_icon_l));
                        marker.setTag(user);
                        markers.add(marker);
                    }
                }
                getshopname();
                cuslat = dataSnapshot.child("CUSTOMER").child(username).child("LOC").child("latitude").getValue(Double.class);
                cuslon = dataSnapshot.child("CUSTOMER").child(username).child("LOC").child("longitude").getValue(Double.class);

                makecirle(cuslat,cuslon);
                check();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
/////////////////////////////////////////////
private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
    // below line is use to generate a drawable.
    Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

    // below line is use to set bounds to our vector drawable.
    vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

    // below line is use to create a bitmap for our
    // drawable which we have added.
    Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

    // below line is use to add bitmap in our canvas.
    Canvas canvas = new Canvas(bitmap);

    // below line is use to draw our
    // vector drawable in canvas.
    vectorDrawable.draw(canvas);

    // after generating our bitmap we are returning our bitmap.
    return BitmapDescriptorFactory.fromBitmap(bitmap);
}

//////////////////////////Making the circle on map////////////////////////////////////
    public void makecirle(Double lat ,Double lon)
    {
        cust = new LatLng(lat, lon);
        circleOptions.center(cust);
        circleOptions.radius(400).strokeColor(Color.rgb(0, 136, 255)).fillColor(Color.argb(20, 0, 136, 255));
        circle = mMap.addCircle(circleOptions);
    }
///////////////////////////////////////////////////////////////////////

//////////////////////getting shopname of hawkers//////////////////////////////
    public void getshopname() {
        for (Marker marker : markers) {
            FirebaseFirestore.getInstance()
                    .collection("HAWKERS")
                    .document(marker.getTag().toString())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String shopname = documentSnapshot.getString("Shopname");
                            marker.setTitle(shopname);
                            shop.add(shopname+"-( "+marker.getTag().toString()+" )");
//                            Toast.makeText(getApplicationContext(), "" + shop.get(0), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
        ArrayAdapter<String> map_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, shop);
        map_auto.setAdapter(map_adapter);
        map_auto.setThreshold(2);
    }
//////////////////////////////////////////////////////////////////////////////

/////////////////Finding the marker on map///////////////////////////////////////////
    public void gotomarker()
    {
        String textinauto = map_auto.getText().toString();
        String [] arr= textinauto.split("-");
//        for (Marker marker : markers) {
////            marker.setVisible(false);
//        }

        for (Marker marker : markers) {
        if(marker.getTitle().equals(arr[0]))
        {
            marker.setVisible(true);
            marker.showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        }
        }

    }
///////////////////////////////////////////////////////////////////////

////////////////////////////////displaying the markers according to the filters/////////////////
    public void check()
    {

        for (Marker marker : markers) {
//            marker.setVisible(false);
            String nn = marker.getTag().toString();
            FirebaseFirestore.getInstance()
                    .collection("HAWKERS")
                    .document(nn)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String haw_type = documentSnapshot.getString("Type");
                            if(type.equals("All")) {
                                if (SphericalUtil.computeDistanceBetween(cust, marker.getPosition()) < 400) {
                                    marker.setVisible(true);
                                    marker.showInfoWindow();
                                } else {
                                    marker.setVisible(false);
                                }
                            }
                            else
                            {
                                if (haw_type.equals(type)) {
                                    if (SphericalUtil.computeDistanceBetween(cust, marker.getPosition()) < 400) {
                                        marker.setVisible(true);
                                        marker.showInfoWindow();
                                    } else {
                                        marker.setVisible(false);
                                    }
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

 ///////////////////////////////////////////////////////////////////////////////////////

    private void createnotif()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Notification","My Notification",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MapsActivity.this,"Notification");
        builder.setContentTitle(" HAWKER IN YOUR AREA");
        builder.setContentText("Hawker has reached your locality dont forget to buy your favourite items");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MapsActivity.this);
        managerCompat.notify(1,builder.build());

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
                    markers.get(i).setPosition(new LatLng(lat,lon));
                }
//                check();

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
                        Toast.makeText(getApplicationContext(), " "+location.getLatitude(), Toast.LENGTH_SHORT).show();
                            mymarker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(mymarker.getPosition()));
                            circle.remove();
                            makecirle(location.getLatitude(),location.getLongitude());
                            check();
                        }
                    }catch (Exception e){
                        Log.d("hatim"," "+e);
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLocationUpdates() {
        if(manager != null) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                } else {
                    Toast.makeText(this, "No provider", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLocationUpdates();
        } else{
            Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (flag==0)
        {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style));
            map_sw.setChecked(true);
//            mymarker.setIcon(BitmapFromVector(getApplicationContext(), R.drawable.ic_map_cust_icon_d));
//            marker.setIcon(BitmapFromVector(getApplicationContext(), R.drawable.ic_map_vendor_icon_d));
        }
        else
        {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style_light));
            map_sw.setChecked(false);
//            mymarker.setIcon(BitmapFromVector(getApplicationContext(), R.drawable.ic_map_cust_icon_l));
//            marker.setIcon(BitmapFromVector(getApplicationContext(), R.drawable.ic_map_vendor_icon_l));
        }
        LatLng sydney = new LatLng(28.9679735,72.831369);
        mymarker =  mMap.addMarker(new MarkerOptions().position(sydney).title("YOU"));
        mymarker.setIcon(BitmapFromVector(getApplicationContext(), R.drawable.ic_map_cust_icon_l));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//added by glen for bottom sheet
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (!marker.getTitle().equals("YOU")) {
                    BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(marker.getTag().toString());
                    bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                }
                return false;
            }
        });
        //
        mMap.setMinZoomPreference(10);
        mMap.setMaxZoomPreference(25);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        readChanges();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location != null)
        {
            saveLocation(location);
        }
        else
        {
//            Toast.makeText(this, "Location Not available", Toast.LENGTH_SHORT).show();
        }


    }

    private void saveLocation(Location location) {

        reference.setValue(location);

    }

    // For navigation drawer outside on create

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                Intent intent = new  Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("user",username);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_prof:
                Intent intent1 = new Intent(getApplicationContext(), ProfileActivity.class);
                intent1.putExtra("user",username);
                startActivity(intent1);
                finish();
                break;
            case R.id.nav_fav:
                Intent intent2 = new Intent(getApplicationContext(),FavoriteActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.nav_about:
                Intent intent3 = new  Intent(getApplicationContext(),AboutActivity.class);
                startActivity(intent3);
                finish();
                break;
            case R.id.nav_sign_out:
                signout();
                break;
            case R.id.nav_map:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void signout() {
        mFirebaseAuth.addAuthStateListener(authStateListener);
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            for (UserInfo userInfo : firebaseUser.getProviderData()) {
                if (userInfo.getProviderId().equals("facebook.com")) {
                    LoginManager.getInstance().logOut();
                    Toast.makeText(getApplicationContext(), "Logged out of facebook", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent2);
                    finish();
                }
                else if (userInfo.getProviderId().equals("google.com")){
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getApplicationContext(), "Logged out of Google", Toast.LENGTH_SHORT).show();
                    mFirebaseAuth.removeAuthStateListener(authStateListener);
                    Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent2);
                    finish();
                }

            }

        }
        sdb = new sqliteDB(this);
        String doc = sdb.getDoc();
        sdb.deletedoc(doc);
        Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent2);
        finish();
    }

    //////////////////////////////////////
    private void fillNavigation() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) headerView.findViewById(R.id.nav_user);
        TextView nav_mail = (TextView) headerView.findViewById(R.id.nav_email);
        ImageView dp = (ImageView) headerView.findViewById(R.id.display_picture);
        dp.setClipToOutline(true); //added to set image clipped to border of imageview - Glen

        ///////profile picture
        StorageReference filepath = mStorage.child("USERS").child("CUSTOMER").child(username).child("PROFILE");
        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(dp);
            }
        });
        //////////////////////////////////////////////////

        DocumentReference docRef = userdb.collection("CUSTOMER").document(username);
        FirebaseFirestore.getInstance()
                .collection("CUSTOMER")
                .document(username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        nav_user.setText(documentSnapshot.getString("Username"));
                        nav_mail.setText(documentSnapshot.getString("Email"));
                        //Toast.makeText(getApplicationContext(), " "+  documentSnapshot.getString("Username"), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplicationContext(), "values filled", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
    //////////////////////////////////////
    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=YOUR_API_KEY");
        return urlString.toString();
    }
    /////////////////////////////////////////////////////////
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 199) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                map_auto.setText(Objects.requireNonNull(result).get(0));
            }
        }
    }

}