package com.example.test333;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


public class MainActivity_register extends AppCompatActivity {

    LinearLayout l1;
    TextInputLayout t1,t2,t3,t4,t5,t6;
    TextInputEditText ed1,ed2,ed3,ed4,ed5,ed6;
    CountDownTimer cmd;
    long milli=30000;
    int flag=0;
    Button b1;
    TextView label1;
    FloatingActionButton back;
    EditText otp01,otp02,otp03,otp04,otp05,otp06;
    FirebaseFirestore userdatabse;
    sqliteDB sdb;
    //Toggle Button Group for User Type
    MaterialButtonToggleGroup register_toggleGroup;
    Integer register_UserType = 0;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userdatabse = FirebaseFirestore.getInstance();
        t1 = findViewById(R.id.t1);
        t2 = findViewById(R.id.t2);
        t3 = findViewById(R.id.t3);
        t4 = findViewById(R.id.t4);
        t5 = findViewById(R.id.t5);
        t6 = findViewById(R.id.t6);
        ed1 = findViewById(R.id.ed1);
        ed1.setFilters(EmojiFilter.getFilter());
        ed2 = findViewById(R.id.ed2);
        ed2.setFilters(EmojiFilter.getFilter());
        ed3 = findViewById(R.id.ed3);
        ed3.setFilters(EmojiFilter.getFilter());
        ed4 = findViewById(R.id.ed4);
        ed5 = findViewById(R.id.ed5);
        ed6 = findViewById(R.id.ed6);

        back = findViewById(R.id.back);

        sdb = new sqliteDB(this);

        //ToggleButton Group for login_UserType/////
        register_toggleGroup = findViewById(R.id.register_toggleGroup);
        register_toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(isChecked){
                    switch (checkedId){
                        case R.id.register_user_cust:
                            t1.setHint("Full name");
                            register_UserType = 1;
                            type="CUSTOMER";
                            break;
                        case R.id.register_user_vend:
                            t1.setHint("Shopname");
                            register_UserType = 2;
                            type="HAWKERS";
                            break;
                    }
                }else{
                    if (register_toggleGroup.getCheckedButtonId() == View.NO_ID){
                        t1.setHint("Full name / Shopname");
                        register_UserType=0;}
                }
            }
        });
        //End of ToggleButton Group for login_UserType/////

        b1 = findViewById(R.id.b1);
        l1 =(LinearLayout)findViewById(R.id.l1);
        l1.setOnClickListener(null);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "Redirecting...", Toast.LENGTH_SHORT).show();
                Intent it2 = new Intent(MainActivity_register.this, MainActivity.class);
                startActivity(it2);
                finish();
            }
        });
        ed1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ed1.getText().toString().isEmpty()){
                    t1.setHelperText("Required*");
                }else {t1.setHelperText(null);}}
            @Override
            public void afterTextChanged(Editable s) {}
        });
        ed2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ed2.getText().toString().isEmpty()){
                    t2.setHelperText("Required*");
                }else {t2.setHelperText(null);} }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        ed3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ed3.getText().toString().isEmpty()){
                    t3.setHelperText("Required*");
                }else {t3.setHelperText(null);} }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        ed4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ed4.getText().toString().isEmpty()){
                    t4.setHelperText("Required*");
                }else {t4.setHelperText(null);} }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        ed5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ed5.getText().toString().isEmpty()){
                    t5.setHelperText("Required*");
                }else {t5.setHelperText(null);} }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        ed6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ed6.getText().toString().isEmpty()){
                    t6.setHelperText("Reuired*");
                }else {t6.setHelperText(null);} }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (register_UserType == 0)
                    Toast.makeText(MainActivity_register.this, "Please select a User Type", Toast.LENGTH_SHORT).show();
                else if(ed1.getText().toString().isEmpty()) {
                    t1.setErrorEnabled(true);
                    if(register_UserType==1)
                        t1.setError("Full name can't be empty!!!");
                    else
                        t1.setError("Shopname can't be empty!!!");
                } else if (ed2.getText().toString().isEmpty()) {
                    t2.setErrorEnabled(true);
                    t2.setError("Username can't be empty!!!");
                } else if (ed3.getText().toString().isEmpty()) {
                    t3.setErrorEnabled(true);
                    t3.setError("Email ID can't be empty!!!");
                } else if (ed4.length() < 10) {
                    t4.setErrorEnabled(true);
                    t4.setError("Invalid Mobile No.!!!");
                } else if (ed5.getText().toString().isEmpty()) {
                    t5.setErrorEnabled(true);
                    t5.setError("Password can't be empty!!!");
                }else if (ed5.getText().length()<8) {
                    t5.setErrorEnabled(true);
                    t5.setError("Password must be greater than 8 characters");
                }else if(validatepass(ed5.getText().toString())){
                    t5.setErrorEnabled(true);
                    t5.setError("Password must contain an lowercase letter, uppercase letter and a number");
                } else if (ed6.getText().toString().isEmpty()) {
                    t6.setErrorEnabled(true);
                    t6.setError("Password can't be empty!!!");
                } else if (!(ed5.getText().toString().equals(ed6.getText().toString()))) {
                    Toast.makeText(MainActivity_register.this, "Password does not match", Toast.LENGTH_SHORT).show();
                }
                else {
                    check();
//                    insertUserData();

                }
            }
        });
        ////////////////////////////////////////////////
    }


//////OTP system outside oncreate///////
    public void GetOTP(String OTP){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_otp);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.backg);
        otp01 = dialog.findViewById(R.id.otp01);
        otp02 = dialog.findViewById(R.id.otp02);
        otp03 = dialog.findViewById(R.id.otp03);
        otp04 = dialog.findViewById(R.id.otp04);
        otp05 = dialog.findViewById(R.id.otp05);
        otp06 = dialog.findViewById(R.id.otp06);
        label1 = dialog.findViewById(R.id.label1);
         ImageButton close = (ImageButton) dialog.findViewById(R.id.close);
         Button submit_otp = (Button) dialog.findViewById(R.id.submit);
         Button resend_otp = (Button) dialog.findViewById(R.id.resend);
        new CountDownTimer(milli, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                label1.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                label1.setVisibility(View.GONE);
                resend_otp.setVisibility(View.VISIBLE);
            }
        }.start();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        submit_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!otp01.getText().toString().isEmpty() && !otp02.getText().toString().isEmpty()
                        && !otp03.getText().toString().isEmpty() && !otp04.getText().toString().isEmpty()
                        && !otp05.getText().toString().isEmpty() && !otp06.getText().toString().isEmpty()) {
                    String enteredOtp = otp01.getText().toString() + otp02.getText().toString() + otp03.getText().toString()
                            + otp04.getText().toString() + otp05.getText().toString() + otp06.getText().toString();
                    if (OTP != null) {
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(OTP, enteredOtp);
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = task.getResult().getUser();
                                            insertUserData();
                                            Intent it = new Intent(MainActivity_register.this, HomeActivity.class);
//                                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(it);
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "EMPTY", Toast.LENGTH_SHORT).show();
                }

            }
        });

        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + ed4.getText().toString(),
                        30,
                        TimeUnit.SECONDS,
                        MainActivity_register.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(MainActivity_register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String bOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(bOtp, forceResendingToken);
                                Toast.makeText(MainActivity_register.this, "OTP Code Sent", Toast.LENGTH_SHORT).show();
                                GetOTP(bOtp);
                            }
                        }
                );
            }
        });

//////////////////////////////////
        otp01.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
           if (!s.toString().trim().isEmpty()){
           otp02.requestFocus();}
        }
        @Override
        public void afterTextChanged(Editable s) {}
    });
    otp02.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().trim().isEmpty()){
                otp03.requestFocus();}
        }
        @Override
        public void afterTextChanged(Editable s) {}
    });
    otp03.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().trim().isEmpty()){
                otp04.requestFocus();}
        }
        @Override
        public void afterTextChanged(Editable s) {}
    });
    otp04.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().trim().isEmpty()){
                otp05.requestFocus();}
        }
        @Override
        public void afterTextChanged(Editable s) {}
    });
    otp05.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().trim().isEmpty()){
                otp06.requestFocus();}
        }
        @Override
        public void afterTextChanged(Editable s) {}
    });
    dialog.show();
}

///////User database insertion outside oncreate
    public void check()
    {
        DocumentReference docRef = userdatabse.collection(type).document(ed2.getText().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(MainActivity_register.this, "Username already exits", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        userdatabse.collection(type).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                for (DocumentSnapshot doc : value) {
                                    if (error != null) {
                                        Toast.makeText(MainActivity_register.this, "Error", Toast.LENGTH_SHORT).show();
                                    }

                                    if ((ed4.getText().toString().equals(doc.getString("Mobile"))) || (ed3.getText().toString().equals(doc.getString("Email"))))
                                    {
                                        if((ed4.getText().toString().equals(doc.getString("Mobile"))))
                                        {
                                            Toast.makeText(MainActivity_register.this, "This mobile number is already registered", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                        if((ed3.getText().toString().equals(doc.getString("Email"))))
                                        {
                                            Toast.makeText(MainActivity_register.this, "This Email is already registered", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    }
                                    else
                                    {
                                        /// Otp system ///////////
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+91" + ed4.getText().toString(),
                            60,
                            TimeUnit.SECONDS,
                            MainActivity_register.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    Toast.makeText(MainActivity_register.this, "Verification Completed", Toast.LENGTH_SHORT).show();
//                                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(
//                                            new OnCompleteListener<AuthResult>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                                    if (task.isSuccessful()) {
//                                                        Toast.makeText(MainActivity_register.this, "Registration Successful!!!", Toast.LENGTH_SHORT).show();
                                    Intent it001 = new Intent(getApplicationContext(), MapsActivity.class);
                                    it001.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(it001);
                                    finish();
//                                                    }
//                                                }
//                                            });
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    Toast.makeText(MainActivity_register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String bOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(bOtp, forceResendingToken);
                                    Toast.makeText(MainActivity_register.this, "OTP Code Sent", Toast.LENGTH_SHORT).show();
                                    GetOTP(bOtp);
                                }
                            }
                    );
//                                        insertUserData();
//                                        break;
                                    }
                                }
                            }
                        });

                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void insertUserData()
    {
        Map<String,String> items=new HashMap<>();
        if(type.equals("CUSTOMER")) {
            items.put("Name", ed1.getText().toString());
        }
        else
        {
            items.put("Shopname", ed1.getText().toString());
        }
        items.put("Username",ed2.getText().toString());
        items.put("Email",ed3.getText().toString());
        items.put("Mobile",ed4.getText().toString());
        items.put("Password",ed5.getText().toString());
        DocumentReference docRef = userdatabse.collection(type).document(ed2.getText().toString());
            docRef.set(items).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    sdb.insertDoc(ed2.getText().toString(),type);
                    if(type.equals("CUSTOMER")) {
                        Intent it = new Intent(MainActivity_register.this, HomeActivity.class);
                        startActivity(it);
                        finish();
                    }
                    else
                    {
                        Intent it = new Intent(MainActivity_register.this, VendorHomeActivity.class);
                        startActivity(it);
                        finish();

                    }
                }
            });

    }

////////////////////////////////////////////
//validate password
public boolean validatepass(String password) {

    Pattern uppercase = Pattern.compile("[A-Z]");
    Pattern lowercase = Pattern.compile("[a-z]");
    Pattern digit = Pattern.compile("[0-9]");

    if ((!lowercase.matcher(password).find()) || (!uppercase.matcher(password).find()) || (!digit.matcher(password).find()))
        return true;
    else
        return false;
}
//End of validate password

}
