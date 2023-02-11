package com.example.test333;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // For navigation drawer
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton home_im1;
    String username;
    GoogleSignInClient signInClient;
    CardView c1_fruit,c2_food,c3_book, c4_cosmetics,c5_clothes,c6_accessory,c7_others,c8_all;
    sqliteDB sdb;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    private StorageReference mStorage;
    FirebaseFirestore userdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Window window = this.getWindow();

        window.setStatusBarColor(Color.BLACK);
        c1_fruit = findViewById(R.id.home_c1);
        c2_food = findViewById(R.id.home_c2);
        c3_book = findViewById(R.id.home_c3);
        c4_cosmetics = findViewById(R.id.home_c4);
        c5_clothes = findViewById(R.id.home_c5);
        c6_accessory = findViewById(R.id.home_c6);
        c7_others = findViewById(R.id.home_c7);
        c8_all = findViewById(R.id.home_c8);
        sdb = new sqliteDB(this);
        // For navigation drawer inside on create
        drawerLayout = findViewById(R.id.home_draw);
        navigationView = findViewById(R.id.home_nav_view);

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

        home_im1 = findViewById(R.id.home_menubtn);
        navigationView.bringToFront();
        username=sdb.getDoc();
        userdb= FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        fillNavigation();
//        Intent intent = new Intent(getApplicationContext(), notificationservice.class);
//        startService(intent);
        home_im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        //////////////////////////////////////

        c1_fruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Fruit", Toast.LENGTH_SHORT).show();
                Intent it1 = new Intent(HomeActivity.this,MapsActivity.class);
                it1.putExtra("type","Vegetable & Fruit");
                startActivity(it1);
                finish();
            }
        });
        c2_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "FOOD", Toast.LENGTH_SHORT).show();
                Intent it2 = new Intent(HomeActivity.this,MapsActivity.class);
                it2.putExtra("type","Food & Beverage");
                startActivity(it2);
                finish();
            }
        });
        c3_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "book", Toast.LENGTH_SHORT).show();
                Intent it3 = new Intent(HomeActivity.this,MapsActivity.class);
                it3.putExtra("type","Books & Toys");
                startActivity(it3);
                finish();
            }
        });
        c4_cosmetics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Cosmetics", Toast.LENGTH_SHORT).show();
                Intent it4 = new Intent(HomeActivity.this,MapsActivity.class);
                it4.putExtra("type","Beauty & Cosmetics");
                startActivity(it4);
                finish();
            }
        });
        c5_clothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Clothes", Toast.LENGTH_SHORT).show();
                Intent it4 = new Intent(HomeActivity.this,MapsActivity.class);
                it4.putExtra("type","Clothes & GARMENTS");
                startActivity(it4);
                finish();
            }
        });
        c6_accessory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Access", Toast.LENGTH_SHORT).show();
                Intent it4 = new Intent(HomeActivity.this,MapsActivity.class);
                it4.putExtra("type","Wearables & Accessories");
                startActivity(it4);
                finish();
            }
        });
        c7_others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "OTHERS", Toast.LENGTH_SHORT).show();
                Intent it4 = new Intent(HomeActivity.this,MapsActivity.class);
                it4.putExtra("type","Others");
                startActivity(it4);
                finish();
            }
        });
        c8_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "ALL", Toast.LENGTH_SHORT).show();
                Intent it4 = new Intent(HomeActivity.this,MapsActivity.class);
                it4.putExtra("type","All");
                startActivity(it4);
                finish();
            }
        });
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
            case R.id.nav_map:
                Intent intent = new  Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("type","All");
                startActivity(intent);
                finish();
                break;
            case R.id.nav_prof:
                Intent intent1 = new Intent(getApplicationContext(),ProfileActivity.class);
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
            case R.id.nav_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
        return true;
    }


    //////////////////////////////////////
    private void signout() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.addAuthStateListener(authStateListener);
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            for (UserInfo userInfo : firebaseUser.getProviderData()) {
                if (userInfo.getProviderId().equals("facebook.com")) {
                    LoginManager.getInstance().logOut();
                    Toast.makeText(getApplicationContext(), "Logged out of facebook", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent2);
                    finish();
                }
                else if (userInfo.getProviderId().equals("google.com")){
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getApplicationContext(), "Logged out of Google", Toast.LENGTH_SHORT).show();
                    mFirebaseAuth.removeAuthStateListener(authStateListener);
                    Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent2);
                    finish();
                }

            }

        }
        sdb = new sqliteDB(this);
        String doc = sdb.getDoc();
        sdb.deletedoc(doc);
        Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent2);
        finish();
    }

    private void fillNavigation() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.home_nav_view);
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

}