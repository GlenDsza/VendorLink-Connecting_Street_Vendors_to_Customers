package com.example.test333;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

///////////////
public class MainActivity extends AppCompatActivity {
    int flag,click=0;
    TextInputLayout tt1,tt2,tt3;
    TextInputEditText eed1,eed2,eed3;
    Button bb1;
    LinearLayout ll1;
    TextView tv_regi;
    FirebaseAuth mFirebaseAuth;
    //Facebook login
    CallbackManager mCallbackManager;
    LoginButton loginButton;
    ImageButton google,facebook;
    FirebaseAuth.AuthStateListener authStateListener;
    AccessTokenTracker accessTokenTracker;
    GoogleSignInOptions gso;
    GoogleSignInClient signInClient;
    GoogleSignInAccount signInAcc;
    ActivityResultLauncher<Intent> cl;
    GoogleSignInAccount acct;

    ///////Firestore declearation
    String personName,personGivenName,personEmail,personId;
    FirebaseFirestore userdb;
    /////////////

    ////sqlite database
    sqliteDB sdb;
    //////////////////////////

    //Toggle Button Group for User Type
    MaterialButtonToggleGroup login_toggleGroup;
    Integer login_UserType = 0;

    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        ll1 =(LinearLayout)findViewById(R.id.l1_login);
        ll1.setOnClickListener(null);
        tt1 = findViewById(R.id.t1_login);
        tt2 = findViewById(R.id.t2_login);
        tt3 = findViewById(R.id.t3_login);
        eed1 = findViewById(R.id.ed1_login);
        eed1.setFilters(EmojiFilter.getFilter());
        eed2 = findViewById(R.id.ed2_login);
        eed3 = findViewById(R.id.ed3_login);
        bb1 = findViewById(R.id.b1_login);
        google = findViewById(R.id.google);
        tv_regi = findViewById(R.id.tv_regi);
        facebook = findViewById(R.id.facebook);

        ////////Firestore initialization
        userdb= FirebaseFirestore.getInstance();

        ///////////////////////////
        sdb = new sqliteDB(this);

        //ToggleButton Group for login_UserType/////
        login_toggleGroup = findViewById(R.id.login_toggleGroup);
        login_toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(isChecked){
                    switch (checkedId){
                        case R.id.login_user_cust:
                            type="CUSTOMER";
                            login_UserType = 1;
                            break;
                        case R.id.login_user_vend:
                            type="HAWKERS";
                            login_UserType = 2;
                            break;
                    }
                }else{
                    if (login_toggleGroup.getCheckedButtonId() == View.NO_ID)
                        login_UserType=0;
                }
            }
        });

        //End of ToggleButton Group for login_UserType/////

        eed1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (eed1.getText().toString().isEmpty()){
                    tt1.setHelperText("Required*");
                }else {tt1.setHelperText(null);}}
            @Override
            public void afterTextChanged(Editable s) {}
        });
        eed2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (eed2.getText().toString().isEmpty()){
                    tt2.setHelperText("Required*");
                }else {tt2.setHelperText(null);} }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        eed3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (eed3.getText().toString().isEmpty()){
                    tt3.setHelperText("Required*");
                }else {tt3.setHelperText(null);} }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        bb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login_UserType==0){
                    Toast.makeText(MainActivity.this, "Please Select a login_UserType", Toast.LENGTH_SHORT).show();
                }
                else if(eed1.getText().toString().isEmpty()){
                    tt1.setErrorEnabled(true);
                    tt1.setError("Username can't be empty!!!"); }
                else if(eed2.getText().toString().isEmpty()){
                    tt2.setErrorEnabled(true);
                    tt2.setError("Mobile No. can't be empty!!!"); }
                else if(eed2.length()<10){
                    tt2.setErrorEnabled(true);
                    tt2.setError("Invalid Mobile No.!!!"); }
                else if(eed3.getText().toString().isEmpty()){
                    tt3.setErrorEnabled(true);
                    tt3.setError("Password can't be empty!!!"); }
                else
                {
                    check1(v);
                }
            }
        });
        tv_regi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "Redirecting...", Toast.LENGTH_SHORT).show();
                Intent it1 = new Intent(MainActivity.this, MainActivity_register.class);
                startActivity(it1);
                finish();
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });
        //For facebook
        mFirebaseAuth = FirebaseAuth.getInstance();
//        FacebookSdk.sdkInitialize(getApplicationContext());
        loginButton = findViewById(R.id.login_button);
        mCallbackManager =  CallbackManager.Factory.create();
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook","OnSuccess" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());
                Intent it = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(it);
                finish();
                //loginButton.setPermissions(Arrays.asList("email","Public_profile"));
            }

            @Override
            public void onCancel() {
                Log.d("Facebook","onCancel");

            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Log.d("Facebook","onError" + e);

            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

            }
        };
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(@Nullable AccessToken accessToken, @Nullable AccessToken accessToken1) {
                if (accessToken1 == null)
                {
                    mFirebaseAuth.signOut();
                }
            }
        };
        //////////////////////////
//google signin inside oncreate
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("324908161910-56e7pjvhvvkogl9mpnk2o5rcmpkhsd1r.apps.googleusercontent.com")
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(MainActivity.this,gso);

        acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        acct =null;
        if (acct != null) {
            Toast.makeText(this, "Already Logged in", Toast.LENGTH_SHORT).show();
            Intent it = new Intent(MainActivity.this,MapsActivity.class);
            startActivity(it);
            finish();
        }

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cl.launch(new Intent(signInClient.getSignInIntent()));
                insertUserData();
            }
        });


        cl = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if(result.getResultCode() == Activity.RESULT_OK)
                        {
                            Intent intent = result.getData();

                            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(intent);
                            try{
                                signInAcc = signInTask.getResult(ApiException.class);
                                op();
                            }
                            catch (ApiException e)
                            {
                                op1();
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
        ////////////////////////
    }

    // for facebook outside oncreate
    private void handleFacebookToken(AccessToken accessToken) {
        Log.d("Facebook","handleFacebooktoken" + accessToken);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Log.d("Facebook","sign in with credential: successful");
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    Toast.makeText(MainActivity.this, "Authentication Sucessful", Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(MainActivity.this , HomeActivity.class);
                    startActivity(it);
                    finish();
                }
                else
                {
                    Log.d("Facebook","sign in with credential: unsuccessful",task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);

        /*if(requestCode == Google_sign_code);
        {
            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount signInAcc = signInTask.getResult(ApiException.class);
                Toast.makeText(this, "Your Successfully Signed in with Google", Toast.LENGTH_SHORT).show();
            }
            catch (ApiException e)
            {
                Toast.makeText(this, "Your UNSuccessfully Signed in with Google", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }*/


    }
//    public void onStart()
//    {
//        super.onStart();
////        String check = sdb.getDoc();
////        if(check != null)
////        {
////            Intent it2 = new Intent(MainActivity.this,MapsActivity.class);
////            startActivity(it2);
////            finish();
////        }
//    }
//    public void onStop()
//    {
//        super.onStop();
//        if(authStateListener != null)
//        {
//            mFirebaseAuth.removeAuthStateListener(authStateListener);
//        }
//    }
    //////////////////////////////////////////////////

    //google signin outside oncreate
    public void op()
    {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAcc.getIdToken(),null);

        mFirebaseAuth.signInWithCredential(authCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                Toast.makeText(getApplicationContext(), "Account Connected", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void op1()
    {
        Toast.makeText(this, "Your UnSuccessfully Signed in with Google", Toast.LENGTH_SHORT).show();
    }
    ////////////////////////////


    ///////////////////retreive data outside oncreate
    public void check1(View v)
    {
        flag=0;
            userdb.collection(type).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for (DocumentSnapshot doc : value) {
                        if (error != null) {
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }

                        if ((eed1.getText().toString().equals(doc.getString("Username"))) && (eed2.getText().toString().equals(doc.getString("Mobile"))) && (eed3.getText().toString().equals(doc.getString("Password")))) {

                            storedata(eed1.getText().toString());
                            flag = 1;
                            final Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);
                            View customSnackView = getLayoutInflater().inflate(R.layout.custom_snack, null);
                            snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
                            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                            snackbarLayout.setPadding(0, 0, 0, 0);
                            Button ok = customSnackView.findViewById(R.id.ok);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    click=1;
                                    if(type.equals("CUSTOMER")) {
                                        Intent it = new Intent(MainActivity.this, HomeActivity.class);
                                        startActivity(it);
                                        finish();
                                    }
                                    else
                                    {
                                        Intent it = new Intent(MainActivity.this, VendorHomeActivity.class);
                                        startActivity(it);
                                        finish();

                                    }
                                    snackbar.dismiss();
                                }
                            });
                            snackbarLayout.addView(customSnackView, 0);
                            snackbar.show();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    if (click!=1){
                                        if(type.equals("CUSTOMER")) {
                                            Intent it = new Intent(MainActivity.this, HomeActivity.class);
                                            startActivity(it);
                                            finish();
                                        }
                                        else
                                        {
                                            Intent it = new Intent(MainActivity.this, VendorHomeActivity.class);
                                            startActivity(it);
                                            finish();

                                        }
                                    }
                                }
                            }, 1500);
                            break;
                        }


                    }
                    if (flag == 0) {
                        final Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);
                        View customSnackView = getLayoutInflater().inflate(R.layout.custom_snack1, null);
                        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
                        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                        snackbarLayout.setPadding(0, 0, 0, 0);
                        Button retry = customSnackView.findViewById(R.id.retry);
                        retry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbarLayout.addView(customSnackView, 0);
                        snackbar.show();

                    }
                }
            });

    }
    //////////////////////////////


    ///////User database insertion outside oncreate
    public void insertUserData()
    {
        acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if (acct != null) {
            personName = acct.getDisplayName();
            personGivenName = acct.getGivenName();
            personEmail = acct.getEmail();
            personId = acct.getId();
        }

        Map<String,String> items=new HashMap<>();
        items.put("Username",personName);
//        items.put("Username",personGivenName);
        items.put("Email",personEmail);
        items.put("personal ID",personId);
        items.put("Type",type);
        DocumentReference docRef = userdb.collection(type).document(personId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    }
                    else {
                        userdb.collection(type).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                for (DocumentSnapshot doc : value) {
                                    if (error != null) {
                                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }

                                    if ((personEmail.equals(doc.getString("Email")))) {
                                        Toast.makeText(MainActivity.this, "This Email is already registered", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    else
                                    {
                                        docRef.set(items).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                sdb.insertDoc(personGivenName,type);
                                                Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                    }
                                }
                    }});
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    private void storedata(String user) {
        Boolean checkforinsertion = sdb.insertDoc(user,type);
        if(checkforinsertion == true){
            Toast.makeText(getApplicationContext(),user, Toast.LENGTH_SHORT).show();
        }
        else{
        }
    }
    public void onStart()
    {
        super.onStart();
        String check = sdb.getType();
        if(check != null)
        {
            if(check.equals("CUSTOMER")) {
                Intent it = new Intent(this, HomeActivity.class);
                startActivity(it);
                finish();
            }
            else
            {
                Intent it = new Intent(this, VendorHomeActivity.class);
                startActivity(it);
                finish();
            }
        }

    }
    public void onStop()
    {
        super.onStop();
        if(authStateListener != null)
        {
            mFirebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
////////////////////////////////////////////

}