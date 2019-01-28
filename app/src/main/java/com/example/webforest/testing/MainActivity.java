package com.example.webforest.testing;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.webforest.testing.BroadCastReciever.AlarmReceiver;
import com.example.webforest.testing.Common.Common;
import com.example.webforest.testing.Reset.Reset;
import com.example.webforest.testing.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;
public class MainActivity extends AppCompatActivity {
    private MaterialEditText editNewUser, editNewPassword, editNewEmail;
    private MaterialEditText editUser, editPassword;
    private Button btnSignUp, btnSignIn,btnReset;
    private DatabaseReference users;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make to run your application only in portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        btnSignIn = findViewById(R.id.sign_in);
        btnSignUp = findViewById(R.id.sign_up);
        editUser = findViewById(R.id.Username);
        editPassword = findViewById(R.id.password);
        btnReset = (Button)findViewById(R.id.Reset);

        //implement firebase authentication
        mAuth = FirebaseAuth.getInstance();
        //if user already sign in
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, Home.class));
            finish();
        }

        //if user already registered-27.01.19
//        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                if (user != null) {
//                    Intent intent = new Intent(MainActivity.this, Home.class);
//                    startActivity(intent);
//                    finish();
//                    return;
//                }
//            }
//        };


      users = FirebaseDatabase.getInstance().getReference("Users");
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        registerAlarm();
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(editUser.getText().toString()).exists()) {
                            User user = dataSnapshot.child(editUser.getText().toString()).getValue(User.class);
                            if (user.getPassword().equals(editPassword.getText().toString())) {
                                Intent intent = new Intent(MainActivity.this, Home.class);
                                Common.currentUser=user;
                                startActivity(intent);
                                finish();
                            } else
                                Toast.makeText(MainActivity.this, "Password Wrong", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(MainActivity.this, "Please Register", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this,Reset.class);
               startActivity(intent);
            }
        });
    }

    private void registerAlarm() {
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);// 9 hour
        calendar.set(Calendar.MINUTE,47);
        calendar.set(Calendar.SECOND,0);

        Intent intent=new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am= (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    ////////for registration dialog////////
    private void showDialog() {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(MainActivity.this);
        alertdialog.setTitle("Sign Up");
        alertdialog.setMessage("Please fill the credentials");
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.signup, null);
        editNewEmail = view.findViewById(R.id.newemail);
        editNewUser = view.findViewById(R.id.newUsername);
        editNewPassword = view.findViewById(R.id.newpassword);
        alertdialog.setView(view);
        alertdialog.setIcon(R.drawable.ic_account_circle_black_24dp);
        alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final User user = new User(editNewUser.getText().toString()
                        , editNewPassword.getText().toString()
                        , editNewEmail.getText().toString());


                //updated 27.01.19
//                final String name = editNewUser.getText().toString().trim();
//                final String email = editNewEmail.getText().toString().trim();
//                final String password = editNewPassword.getText().toString().trim();
//
//                mAuth.createUserWithEmailAndPassword(user.getEmail(),user.getPassword()).addOnCompleteListener(MainActivity.this,
//                        new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//
//                        if (task.isSuccessful()) {
//
//                            User user = new User(
//                                    name,
//                                    password,
//                                    email
//
//                            );
//
//                            String user_id = mAuth.getCurrentUser().getUid();
//                            users = FirebaseDatabase.getInstance().getReference("Users");
//                            users.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(MainActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
//                                    } else {
//                                        //display a failure message
//                                    }
//                                }
//
//
//                            });
//
//                    }
//                        else {
//                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                }


                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //replace get method 27.01.19
                        if (dataSnapshot.child(user.getUserName()).exists()) {
                            Toast.makeText(MainActivity.this, "User Already Exist", Toast.LENGTH_SHORT).show();
                        } else {
                            users.child(user.getUserName())
                                    .setValue(user);
                            Toast.makeText(MainActivity.this, "Registration Successfully", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
               });
                dialog.dismiss();
            }
        });
        alertdialog.show();

    }
}