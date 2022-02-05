package com.builditcreative.todo;

import android.app.AlertDialog;
import android.app.MediaRouteButton;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private final Timer _timer = new Timer();
    private final FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private ChildEventListener _FDB_child_listener;

    private LinearLayout loginlayout;
    private LinearLayout intro;
    private LinearLayout signinlayout;
    private EditText edittext1;
    private EditText edittext2;
    private TextView resetpassword;
    private TextView newuser;
    private TextView olduser;
    private EditText gmailsignin;
    private EditText namesignin;
    private EditText usernamesignin;
    private EditText accesscode;
    private EditText mobilesignin;
    private EditText passwordsignin;
    private Button signin;
    private Button loginin;
    private ListView listview;
    private TextInputLayout passwordloginlayout;
    private TextInputLayout namelayout;
    private TextInputLayout moblielayout;
    private TextInputLayout gmaillayout;
    private TextInputLayout usernamelayout;
    private TextInputLayout accseclayout;


    private final Intent intent = new Intent();
    private FirebaseAuth FA;
    private HashMap<String, Object> Map = new HashMap<>();
    private OnCompleteListener<AuthResult> _FA_sign_in_listener;
    private OnCompleteListener<AuthResult> _FA_create_user_listener;
    private OnCompleteListener<Void> _FA_reset_password_listener;

    private TimerTask T;
    private AlertDialog.Builder D;
    private final DatabaseReference Ver = _firebase.getReference("version");
    private final DatabaseReference FDB = _firebase.getReference("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        signinlayout = findViewById(R.id.signinlayout);
        intro = findViewById(R.id.intro);



        signin = findViewById(R.id.signin);
        loginin = findViewById(R.id.loginin);
        newuser = findViewById(R.id.newuser);
        olduser = findViewById(R.id.olduser);
        resetpassword = findViewById(R.id.resetpassword);
        gmailsignin = findViewById(R.id.gmailaddress);
        namesignin = findViewById(R.id.namesignin);


        namelayout = findViewById(R.id.namelayout);

        moblielayout = findViewById(R.id.moblielayout);
        gmaillayout = findViewById(R.id.gmaillayout);

        mobilesignin = findViewById(R.id.mobliesignin);
        passwordsignin = findViewById(R.id.passwordsignin);
        FA = FirebaseAuth.getInstance();
        D = new AlertDialog.Builder(this);


        loginin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if ("".equals(gmailsignin.getText().toString()) || passwordsignin.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter Details", Toast.LENGTH_SHORT).show();
                }
                else {
                    FA.signInWithEmailAndPassword(gmailsignin.getText().toString(), passwordsignin.getText().toString()).addOnCompleteListener(MainActivity.this, _FA_sign_in_listener);
                    Map.put("Email", gmailsignin.getText().toString());
                }
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if ("".equals(gmailsignin.getText().toString()) || "".equals(passwordsignin.getText().toString()) || "".equals(mobilesignin.getText().toString())  || "".equals(namesignin.getText().toString()) ) {
                    Toast.makeText(getApplicationContext(), "Enter Details", Toast.LENGTH_SHORT).show();
                }else {
                    FA.createUserWithEmailAndPassword(gmailsignin.getText().toString(),passwordsignin.getText().toString()).addOnCompleteListener(MainActivity.this,_FA_create_user_listener);
                    Map.put("Email",gmailsignin.getText().toString());
                    Map.put("Name",namesignin.getText().toString());
                    Map.put("Moblie",mobilesignin.getText().toString());
                }
            }
        });

        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                namelayout.setVisibility(View.VISIBLE);

                moblielayout.setVisibility(View.VISIBLE);
                olduser.setVisibility(View.VISIBLE);
                signin.setVisibility(View.VISIBLE);
                resetpassword.setVisibility(View.GONE);
                newuser.setVisibility(View.GONE);
                loginin.setVisibility(View.GONE);
            }
        });

        olduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                namelayout.setVisibility(View.GONE);
                moblielayout.setVisibility(View.GONE);
                olduser.setVisibility(View.GONE);
                signin.setVisibility(View.GONE);
                loginin.setVisibility(View.VISIBLE);
                resetpassword.setVisibility(View.VISIBLE);
                newuser.setVisibility(View.VISIBLE);
            }
        });
        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (("").equals(gmailsignin.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Enter Your Email For Reset Password", Toast.LENGTH_SHORT).show();
                }else {
                    FA.sendPasswordResetEmail(gmailsignin.getText().toString()).addOnCompleteListener(_FA_reset_password_listener);

                }
            }
        });

        mobilesignin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String tempMoblie= String.valueOf(mobilesignin.getText());
                if (!tempMoblie.isEmpty() && Patterns.PHONE.matcher(charSequence).matches()) {
                    moblielayout.setError(null);
                    signin.setEnabled(true);
                    loginin.setEnabled(true);
                } else {
                    moblielayout.setError("Invaild Moblie NUmber");
                    signin.setEnabled(false);
                    loginin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        gmailsignin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String tempMail= String.valueOf(gmailsignin.getText());
                if (!tempMail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(charSequence).matches()) {
                    gmaillayout.setError(null);
                    signin.setEnabled(true);
                    loginin.setEnabled(true);
                } else {
                    gmaillayout.setError("Invaild Mail Address");
                    signin.setEnabled(false);
                    loginin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final ChildEventListener _FDB_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                T = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if ((FirebaseAuth.getInstance().getCurrentUser() != null)) {
                                    String UID = _childKey;
                                    intent.putExtra("Name", _childValue.get("Name").toString());
                                    intent.putExtra("UID", UID);
                                    intent.setClass(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                } else {
                                    signinlayout.setVisibility(View.VISIBLE);

                                    intro.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                };
                _timer.schedule(T, 150);
            }


            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);

            }

            @Override
            public void onChildMoved(DataSnapshot _param1, String _param2) {

            }

            @Override
            public void onChildRemoved(DataSnapshot _param1) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);

            }

            @Override
            public void onCancelled(DatabaseError _param1) {
                final int _errorCode = _param1.getCode();
                final String _errorMessage = _param1.getMessage();

            }
        };
        FDB.addChildEventListener(_FDB_child_listener);

        _FA_create_user_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> _param1) {
                final boolean _success = _param1.isSuccessful();
                final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
                if (_success){


                    Map.put("Email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    Map.put("UID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Map.put("Name",namesignin.getText().toString());
                    Map.put("Moblie",mobilesignin.getText().toString());
                    FDB.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(Map);
                    FDB.addChildEventListener(_FDB_child_listener);

                }else {
                    if (_errorMessage.equals("The email address is already in use by another account.")){
                        gmaillayout.setError("You have Already Account.");
                        signin.setEnabled(false);
                    }else {
                        Toast.makeText(getApplicationContext(), _errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        _FA_sign_in_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> _param1) {
                final boolean _success = _param1.isSuccessful();
                final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
                if (_success) {

                    Map.put("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    Map.put("UID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    FDB.addChildEventListener(_FDB_child_listener);

                }
                else {
                    Toast.makeText(getApplicationContext(), _errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        };

        _FA_reset_password_listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> _param1) {
                final boolean _success = _param1.isSuccessful();
                if (_success){
                    Toast.makeText(getApplicationContext(), "Reset Password Mail Sended", Toast.LENGTH_SHORT).show();
                }
            }
        };


    }
}