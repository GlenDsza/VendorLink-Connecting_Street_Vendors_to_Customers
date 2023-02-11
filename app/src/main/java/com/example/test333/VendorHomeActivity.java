package com.example.test333;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.facebook.login.LoginManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class VendorHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {
    TabLayout vhome_tablayout;
    ViewPager2 vhome_viewpager;
    VHomeVPAdapter vhome_adapter;
    private String[] titles = new String[]{"ITEMS", "GALLERY", "REVIEWS"};
    private final int MIN_TIME = 1000; // in milliseconds
    private final int MIN_DISTANCE = 1; // 1 meter
    //for navigation drawer
    ImageButton vhome_menubtn;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    //Switch in Drawer
    SwitchCompat vhome_switch69;
    Boolean vhome_switchState;
    Menu vhome_menu1;
    MenuItem vhome_nav_loc;

    private LocationManager manager;
    String username;
    public DatabaseReference reference;
    sqliteDB sdb;
    FirebaseFirestore userdb;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private Uri mImageUri = null;
    TextView tv_username,tv_name;
    CircleImageView cv_profile;
    Uri insert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_home);

        sdb = new sqliteDB(this);
        username = sdb.getDoc();
        mFirebaseAuth = FirebaseAuth.getInstance();
        /////for firestorage
        mStorage = FirebaseStorage.getInstance().getReference();
        userdb= FirebaseFirestore.getInstance();
        fillNavigation();
        //for tabLayout
        vhome_tablayout = findViewById(R.id.vhome_tablayout);
        vhome_viewpager = findViewById(R.id.vhome_viewpager);
        ///Set Details of the vendor
        tv_username = findViewById(R.id.vhome_tv1);
        tv_name = findViewById(R.id.vhome_tv2);
        cv_profile = findViewById(R.id.vhome_profile_image);
        fill_details();
        /////////////////////////////

        vhome_adapter = new VHomeVPAdapter(this);
        vhome_viewpager.setAdapter(vhome_adapter);
        reference = FirebaseDatabase.getInstance().getReference().child("USERS").child("HAWKERS").child(username).child("LOC");
        new TabLayoutMediator(vhome_tablayout, vhome_viewpager,
                ((tab, position) -> {tab.setText(titles[position]);setIco(tab, position);})).attach();

        View root = vhome_tablayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.com_facebook_likeview_text_color));
            drawable.setSize(5, 1);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }
        //end for tab layout

        //for nav drawer
        drawerLayout = findViewById(R.id.vhome_draw);
        navigationView = findViewById(R.id.vhome_nav_view);

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

        vhome_menubtn = findViewById(R.id.vhome_menubtn);
        navigationView.bringToFront();
        vhome_menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        vhome_menu1 = navigationView.getMenu();
        vhome_nav_loc = vhome_menu1.findItem(R.id.nav_location);
        vhome_switch69 = vhome_nav_loc.getActionView().findViewById(R.id.switch_id);
        if(VendorProfileActivity.status.equals("true")) {
            vhome_switch69.setChecked(true);
        }
        else
            vhome_switch69.setChecked(false);

        vhome_switch69.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vhome_switch69.isChecked()) {
                    vhome_switchState = true;
                    VendorProfileActivity.status = "true";
                    Intent intent = new Intent(getApplicationContext(), locationservice.class);
                    startService(intent);
                }
                else {
                    vhome_switchState = false;
                    VendorProfileActivity.status="false";
                    Intent intent = new Intent(getApplicationContext(), locationservice.class);
                    stopService(intent);
                    reference.child("latitude").removeValue();
                    reference.child("longitude").removeValue();
                }
                Toast.makeText(getApplicationContext(), vhome_switch69.isChecked()? "GPS turned on" : "GPS Turned off",Toast.LENGTH_SHORT).show();
            }
        });
        //end for nav drawer
    }

    // user-defined function to set icon in tab items
    public void setIco(TabLayout.Tab tab, Integer position){
        switch (position){
            case 0:
                tab.setIcon(R.drawable.vhome_menu);
                break;
            case 1:
                tab.setIcon(R.drawable.vhome_image);
                break;
            case 2:
                tab.setIcon(R.drawable.vhome_reviews);
                break;
        }
    }

    // For navigation drawer outside on create
    private void fill_details() {
        tv_username.setText(username);
        FirebaseFirestore.getInstance()
                .collection("HAWKERS")
                .document(username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        tv_name.setText(documentSnapshot.getString("Shopname"));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        StorageReference filepath = mStorage.child("USERS").child("HAWKERS").child(username).child("PROFILE");
        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(cv_profile);
            }
        });
        filepath.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
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
            case R.id.nav_prof:
                Intent intent1 = new Intent(getApplicationContext(), VendorProfileActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.nav_about:
                Intent intent2 = new Intent(getApplicationContext(), VendorAboutActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.nav_sign_out:
                signout();
                break;
            case R.id.nav_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
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
    /////////////////////////////////////////
    private void fillNavigation() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.vhome_nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) headerView.findViewById(R.id.nav_user);
        TextView nav_mail = (TextView) headerView.findViewById(R.id.nav_email);
        ImageView dp = (ImageView) headerView.findViewById(R.id.display_picture);
        dp.setClipToOutline(true); //added to set image clipped to border of imageview - Glen

        ///////profile picture
        StorageReference filepath = mStorage.child("USERS").child("HAWKERS").child(username).child("PROFILE");
        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(dp);
            }
        });
        //////////////////////////////////////////////////

        DocumentReference docRef = userdb.collection("HAWKERS").document(username);
        FirebaseFirestore.getInstance()
                .collection("HAWKERS")
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

    @Override
    public void onLocationChanged(@NonNull Location location) {
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 96) {
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                insert = data.getData();
                // Use Uri object instead of File to avoid storage permissions
                StorageReference filepath = mStorage.child("USERS").child("HAWKERS").child(username).child("gallery").child(UUID.randomUUID().toString());
                filepath.putFile(insert).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "DONE IMAGE", Toast.LENGTH_SHORT).show();
                        //ReviewFragment rf = (ReviewFragment) getFragmentManager().findFragmentById(R.id.vhome_viewpager);
                    }
                });
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getApplicationContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

}