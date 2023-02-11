package com.example.test333;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.login.LoginManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageButton prof_menubtn;
    Button btn_save;
    TextInputEditText ed_name,ed_username,ed_mobile,ed_email,ed_pass;
    ImageView iv_name,iv_mobile,iv_email,iv_pass,dialog_iv;
    CircleImageView civ;
    // For navigation drawer
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FloatingActionButton fab;
    Uri profPic;
    String username;
    sqliteDB sdb;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private Uri mImageUri = null;
    FirebaseFirestore userdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        sdb = new sqliteDB(this);
        username = sdb.getDoc();
        mFirebaseAuth = FirebaseAuth.getInstance();
        /////for firestorage
        mStorage = FirebaseStorage.getInstance().getReference();
        // For navigation drawer inside on create
        drawerLayout = findViewById(R.id.prof_draw);
        navigationView = findViewById(R.id.prof_nav_view);

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

        prof_menubtn = findViewById(R.id.prof_menubtn);
        navigationView.bringToFront();
        userdb= FirebaseFirestore.getInstance();
        displayInfo();
        fillNavigation();
        prof_menubtn.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(this);
        //////////////////////////////////////
        ed_name = findViewById(R.id.ed_name);
        ed_name.setFilters(EmojiFilter.getFilter());
        ed_username = findViewById(R.id.ed_username);
        ed_username.setFilters(EmojiFilter.getFilter());
        ed_email = findViewById(R.id.ed_email);
        ed_email.setFilters(EmojiFilter.getFilter());
        ed_mobile = findViewById(R.id.ed_mobile);
        ed_pass = findViewById(R.id.ed_pass);
        iv_name = findViewById(R.id.iv_name);
        iv_email = findViewById(R.id.iv_email);
//        iv_mobile = findViewById(R.id.iv_mobile);
        iv_pass = findViewById(R.id.iv_pass);
        btn_save=findViewById(R.id.btn_save);
        civ = findViewById(R.id.vhome_profile_image);
        fab = findViewById(R.id.fab);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        iv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_name.setEnabled(true);
                ed_name.requestFocus();
                imm.showSoftInput(ed_name, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        iv_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_email.setEnabled(true);
                ed_email.requestFocus();
                imm.showSoftInput(ed_email, InputMethodManager.SHOW_IMPLICIT);
            }
        });
//        iv_mobile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed_mobile.setEnabled(true);
//                ed_mobile.requestFocus();
//                imm.showSoftInput(ed_mobile, InputMethodManager.SHOW_IMPLICIT);
//            }
//        });
        iv_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_pass.setEnabled(true);
                ed_pass.requestFocus();
                imm.showSoftInput(ed_pass, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ed_email.isEnabled()){
                    FirebaseFirestore.getInstance()
                            .collection("CUSTOMER")
                            .document(username)
                            .update("Email",ed_email.getText().toString());
                }
                if(ed_name.isEnabled()) {
                    FirebaseFirestore.getInstance()
                            .collection("CUSTOMER")
                            .document(username)
                            .update("Name",ed_name.getText().toString());
                }
                if(ed_mobile.isEnabled()) {
                    FirebaseFirestore.getInstance()
                            .collection("CUSTOMER")
                            .document(username)
                            .update("Mobile", ed_mobile.getText().toString());
                }
                if(ed_pass.isEnabled()) {
                    FirebaseFirestore.getInstance()
                            .collection("CUSTOMER")
                            .document(username)
                            .update("Password", ed_pass.getText().toString());
                }
                ed_username.setEnabled(false);
                ed_name.setEnabled(false);
                ed_mobile.setEnabled(false);
                ed_email.setEnabled(false);
                ed_pass.setEnabled(false);
                fillNavigation();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ProfileActivity.this)
                        .cropSquare()    			//Crop image(Optional), Check Customization for more
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start(69);


            }
        });
        civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            openDialog();
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
            case R.id.nav_home:
                Intent intent1 = new Intent(getApplicationContext(), HomeActivity.class);
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
            case R.id.nav_prof:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
        return true;
    }
    private void displayInfo() {
        showDP();
        FirebaseFirestore.getInstance()
                .collection("CUSTOMER")
                .document(username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ed_username.setText(documentSnapshot.getString("Username"));
                        ed_email.setText(documentSnapshot.getString("Email"));
                        ed_name.setText(documentSnapshot.getString("Name"));
                        ed_mobile.setText(documentSnapshot.getString("Mobile"));
                        ed_pass.setText(documentSnapshot.getString("Password"));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
    private void showDP() {
        StorageReference filepath = mStorage.child("USERS").child("CUSTOMER").child(username).child("PROFILE");
        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(civ);
            }
        });
    }
    //////////////////////////////////////
    private void signout() {
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
    /////////////////////////////////////////
    public void openDialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog_iv = dialog.findViewById(R.id.dialog_iv);
        StorageReference filepath = mStorage.child("USERS").child("CUSTOMER").child(username).child("PROFILE");
        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(dialog_iv);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.prof_dialog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 69) {
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                profPic = data.getData();
                // Use Uri object instead of File to avoid storage permissions
                civ.setImageURI(profPic);
                StorageReference filepath = mStorage.child("USERS").child("CUSTOMER").child(username).child("PROFILE");

                filepath.putFile(profPic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "DONE IMAGE", Toast.LENGTH_SHORT).show();
                        fillNavigation();
                    }
                });
            }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
            }
        }

    private void fillNavigation() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.prof_nav_view);
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
