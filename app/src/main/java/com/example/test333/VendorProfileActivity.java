package com.example.test333;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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
import com.google.android.material.textfield.TextInputLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class VendorProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageButton vprof_menubtn;
    Button vprof_btn_save;
    TextInputEditText vprof_ed_name, vprof_ed_username, vprof_ed_mobile, vprof_ed_email, vprof_ed_pass;
    ImageView vprof_iv_name, vprof_iv_mobile, vprof_iv_email, vprof_iv_pass, vprof_dialog_iv,vprof_iv_category;
    CircleImageView vprof_civ;
    TextInputLayout vprof_t0;

    // For navigation drawer
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    //Switch in Drawer
    SwitchCompat vprof_switch69;
    Boolean vprof_switchState;
    Menu vprof_menu1;
    MenuItem vprof_nav_loc;
    public DatabaseReference reference;
    public static String status = "";
    FloatingActionButton vprof_fab;
    Uri vprof_profPic;
    String username;
    sqliteDB sdb;
    AutoCompleteTextView vprof_atv;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    FirebaseFirestore userdb;
    private Uri mImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_profile);
        sdb = new sqliteDB(this);
        username = sdb.getDoc();
        mFirebaseAuth = FirebaseAuth.getInstance();
        userdb= FirebaseFirestore.getInstance();

        /////for firestorage
        mStorage = FirebaseStorage.getInstance().getReference();
        displayInfo();
        fillNavigation();
        // For navigation drawer inside on create
        drawerLayout = findViewById(R.id.vprof_draw);
        navigationView = findViewById(R.id.vprof_nav_view);

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

        vprof_menubtn = findViewById(R.id.vprof_menubtn);
        navigationView.bringToFront();
        vprof_menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        //////////////////////////////////////
        vprof_ed_name = findViewById(R.id.vprof_ed_name);
        vprof_ed_name.setFilters(EmojiFilter.getFilter());
        vprof_ed_username = findViewById(R.id.vprof_ed_username);
        vprof_ed_username.setFilters(EmojiFilter.getFilter());
        vprof_ed_email = findViewById(R.id.vprof_ed_email);
        vprof_ed_email.setFilters(EmojiFilter.getFilter());
        vprof_ed_mobile = findViewById(R.id.vprof_ed_mobile);
        vprof_ed_pass = findViewById(R.id.vprof_ed_pass);
        vprof_iv_name = findViewById(R.id.vprof_iv_name);
        vprof_iv_email = findViewById(R.id.vprof_iv_email);
//        vprof_iv_mobile = findViewById(R.id.vprof_iv_mobile);
        vprof_iv_pass = findViewById(R.id.vprof_iv_pass);
        vprof_btn_save =findViewById(R.id.vprof_btn_save);
        vprof_civ = findViewById(R.id.vprof_profile_image);
        vprof_fab = findViewById(R.id.vprof_fab);
        vprof_atv = findViewById(R.id.vprof_atv);
        vprof_iv_category = findViewById(R.id.vprof_iv_category);
        vprof_t0 = findViewById(R.id.vprof_t0);

        reference = FirebaseDatabase.getInstance().getReference().child("USERS").child("HAWKERS").child(username).child("LOC");
        //product category dropdown view

        String[] items = new String[]{"Vegetable & Fruit", "Food & Beverage", "Books & Toys", "Beauty & Cosmetics", "Clothes & GARMENTS", "Wearables & Accessories", "Others", "All"};
        ArrayAdapter<String> vprof_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        vprof_atv.setAdapter(vprof_adapter);
        //product category dropdown view
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        vprof_iv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vprof_ed_name.setEnabled(true);
                vprof_ed_name.requestFocus();
                imm.showSoftInput(vprof_ed_name, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        vprof_iv_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vprof_ed_email.setEnabled(true);
                vprof_ed_email.requestFocus();
                imm.showSoftInput(vprof_ed_email, InputMethodManager.SHOW_IMPLICIT);
            }
        });
//        vprof_iv_mobile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                vprof_ed_mobile.setEnabled(true);
//                vprof_ed_mobile.requestFocus();
//                imm.showSoftInput(vprof_ed_mobile, InputMethodManager.SHOW_IMPLICIT);
//            }
//        });
        vprof_iv_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vprof_ed_pass.setEnabled(true);
                vprof_ed_pass.requestFocus();
                imm.showSoftInput(vprof_ed_pass, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        vprof_iv_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vprof_t0.setEnabled(true);
                vprof_atv.setText("");
                vprof_atv.requestFocus();
            }
        });
        vprof_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!vprof_atv.equals("Product Category"))
                {
                    FirebaseFirestore.getInstance()
                            .collection("HAWKERS")
                            .document(username)
                            .update("Type",vprof_atv.getText().toString());
                }
                if(vprof_ed_email.isEnabled()){
                    FirebaseFirestore.getInstance()
                            .collection("HAWKERS")
                            .document(username)
                            .update("Email",vprof_ed_email.getText().toString());
                }
                if(vprof_ed_name.isEnabled()) {
                    FirebaseFirestore.getInstance()
                            .collection("HAWKERS")
                            .document(username)
                            .update("Name",vprof_ed_name.getText().toString());
                }
                if(vprof_ed_mobile.isEnabled()) {
                    FirebaseFirestore.getInstance()
                            .collection("HAWKERS")
                            .document(username)
                            .update("Mobile", vprof_ed_mobile.getText().toString());
                }
                if(vprof_ed_pass.isEnabled()) {
                    FirebaseFirestore.getInstance()
                            .collection("HAWKERS")
                            .document(username)
                            .update("Password", vprof_ed_pass.getText().toString());
                }
                vprof_ed_username.setEnabled(false);
                vprof_ed_name.setEnabled(false);
                vprof_ed_mobile.setEnabled(false);
                vprof_ed_email.setEnabled(false);
                vprof_ed_pass.setEnabled(false);
                vprof_t0.setEnabled(false);
                fillNavigation();
            }
        });
        vprof_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(VendorProfileActivity.this)
                        .cropSquare()	    			//Crop image(Optional), Check Customization for more
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start(69);
            }
        });
        vprof_civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        vprof_menu1 = navigationView.getMenu();
        vprof_nav_loc = vprof_menu1.findItem(R.id.nav_location);
        vprof_switch69 = vprof_nav_loc.getActionView().findViewById(R.id.switch_id);
        if(status.equals("true"))
            vprof_switch69.setChecked(true);

        else
            vprof_switch69.setChecked(false);
        vprof_switch69.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vprof_switch69.isChecked()) {
                    vprof_switchState = true;
                    status = "true";
                    Intent intent = new Intent(getApplicationContext(), locationservice.class);
                    startService(intent);
                }
                else {
                    vprof_switchState = false;
                    status="false";
                    Intent intent = new Intent(getApplicationContext(), locationservice.class);
                    stopService(intent);
                    reference.child("latitude").removeValue();
                    reference.child("longitude").removeValue();
                }
                Toast.makeText(getApplicationContext(), vprof_switch69.isChecked()? "GPS turned on" : "GPS Turned off",Toast.LENGTH_SHORT).show();
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
            case R.id.nav_home:
                Intent intent1 = new Intent(getApplicationContext(), VendorHomeActivity.class);
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
            case R.id.nav_prof:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
        return true;
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
        String doc = sdb.getDoc();
        sdb.deletedoc(doc);
        Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent2);
        finish();
    }
    ////////////////////////////////////
    private void displayInfo() {
        showDP();
        FirebaseFirestore.getInstance()
                .collection("HAWKERS")
                .document(username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        vprof_ed_username.setText(documentSnapshot.getString("Username"));
                        vprof_ed_email.setText(documentSnapshot.getString("Email"));
                        vprof_ed_name.setText(documentSnapshot.getString("Shopname"));
                        vprof_ed_mobile.setText(documentSnapshot.getString("Mobile"));
                        vprof_ed_pass.setText(documentSnapshot.getString("Password"));
                        vprof_atv.setText(documentSnapshot.getString("Type"));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
    private void showDP() {
        StorageReference filepath = mStorage.child("USERS").child("HAWKERS").child(username).child("PROFILE");
        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(vprof_civ);
            }
        });
    }
    /////////////////////////////////////////
    public void openDialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        vprof_dialog_iv = dialog.findViewById(R.id.dialog_iv);
        StorageReference filepath = mStorage.child("USERS").child("HAWKERS").child(username).child("PROFILE");
        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(vprof_dialog_iv);
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
                vprof_profPic = data.getData();
                // Use Uri object instead of File to avoid storage permissions
                vprof_civ.setImageURI(vprof_profPic);
                StorageReference filepath = mStorage.child("USERS").child("HAWKERS").child(username).child("PROFILE");

                filepath.putFile(vprof_profPic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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

    ////////////////////////
    private void fillNavigation() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.vprof_nav_view);
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "HELLO", Toast.LENGTH_SHORT).show();
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

}