package com.example.test333;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class FavoriteActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    // For navigation drawer
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton fav_menubtn, fav_audio;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    ///
    RecyclerView fav_recylerview;
    private RecyclerView.LayoutManager layoutManager;
    EditText fav_ed1;
    sqliteDB sdb;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    FirebaseFirestore userdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        fav_recylerview = findViewById(R.id.fav_recyclerview);
        fav_recylerview.setNestedScrollingEnabled(false);
        fav_recylerview.setHasFixedSize(false);
        fav_recylerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        layoutManager = new LinearLayoutManager(FavoriteActivity.this, RecyclerView.VERTICAL, false);
        fav_recylerview.setLayoutManager(layoutManager);


        fav_ed1 = findViewById(R.id.fav_ed1);
        fav_audio = findViewById(R.id.fav_audio);
        fav_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                try {
                    startActivityForResult(intent, 139);
                }
                catch (Exception e) {
                    Toast.makeText(FavoriteActivity.this, " " + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });


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

        fav_menubtn = findViewById(R.id.menu_img);
        navigationView.bringToFront();
        fav_menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        //////////////////////////////////////

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                Intent intent = new  Intent(getApplicationContext(), HomeActivity.class);
//                intent.putExtra("user",username);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_prof:
                Intent intent1 = new Intent(getApplicationContext(), ProfileActivity.class);
//                intent1.putExtra("user",username);
                startActivity(intent1);
                finish();
                break;
            case R.id.nav_map:
                Intent intent2 = new  Intent(getApplicationContext(),MapsActivity.class);
//                intent.putExtra("type","All");
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
            case R.id.nav_fav:
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

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 139) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                fav_ed1.setText(Objects.requireNonNull(result).get(0));
            }
        }
    }
}