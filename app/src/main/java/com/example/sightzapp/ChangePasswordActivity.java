package com.example.sightzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private EditText editTextPwdCurr, editTextPwdNew;
    private Button buttonChangePwd, buttonReAuthenticate;
    private TextView textViewAuthenticated;
    private ProgressBar progressBar;
    private String userPwdCurr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        if(getSupportActionBar() !=null) {
            getSupportActionBar().setTitle("Change Password");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editTextPwdNew = findViewById(R.id.editText_change_pwd_new);
        editTextPwdCurr = findViewById(R.id.editText_change_pwd_current);
        textViewAuthenticated = findViewById(R.id.textView_change_pwd_authenticated);
        progressBar = findViewById(R.id.progressBar);
        buttonReAuthenticate = findViewById(R.id.button_change_pwd_authenticate);
        buttonChangePwd = findViewById(R.id.button_change_pwd);

        //Disable editText for New Password and Make Change Pwd Button unclickable until after verification
        editTextPwdNew.setEnabled(false);
        buttonChangePwd.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser.equals("")) {
            Toast.makeText(ChangePasswordActivity.this, "Something went wrong User Details",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }


    }

    //ReAuthenitcate User Before Changing password
    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwdCurr = editTextPwdCurr.getText().toString();
                if (TextUtils.isEmpty(userPwdCurr)) {
                    Toast.makeText(ChangePasswordActivity.this, "Password is needed",
                            Toast.LENGTH_SHORT).show();
                    editTextPwdCurr.setError("Please enter your current password to authenticate");
                    editTextPwdCurr.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    //ReAuthenticate Again
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                //Disable editText for Current Passwprd & Enable for New One
                                editTextPwdNew.setEnabled(true);
                                editTextPwdCurr.setEnabled(false);

                                //Enable Change
                                buttonReAuthenticate.setEnabled(false);
                                buttonChangePwd.setEnabled(true);

                                //Set TextView to show User is Now Verfied
                                textViewAuthenticated.setText("You are Verified."+"You can Change Password now");
                                Toast.makeText(ChangePasswordActivity.this, "Password has been Verified", Toast.LENGTH_SHORT).show();

                                //Update color Of Change Password
                                buttonChangePwd.setBackgroundTintList(ContextCompat.getColorStateList(
                                        ChangePasswordActivity.this, R.color.dark_green));

                                buttonChangePwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePwd(firebaseUser);
                                    }
                                });

                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editTextPwdNew.getText().toString();

        if (TextUtils.isEmpty(userPwdNew)) {
            Toast.makeText(ChangePasswordActivity.this, "New Password is needed",
                    Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please Enter Your New Password");
            editTextPwdNew.requestFocus();

        } else if (userPwdCurr.matches(userPwdNew)) {
            Toast.makeText(ChangePasswordActivity.this, "New Password must not be as the old one",
                    Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Provide a different password not similar as old one");
            editTextPwdNew.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChangePasswordActivity.this, "New Password has now been changed",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

    }




    //Creating ActionBar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    //When any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(ChangePasswordActivity.this);

        }else if(id == R.id.menu_refresh){

            //Refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);

            /*FragmentActivity FragmentActivity = this.getActivity();*/

        }else if (id == R.id.menu_update_profile){

            Intent intent = new Intent (ChangePasswordActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.menu_update_email) {
            Intent intent = new Intent (ChangePasswordActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_settings){
            Toast.makeText(ChangePasswordActivity.this, "menu_settings", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent (ChangePasswordActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent (ChangePasswordActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(ChangePasswordActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent (ChangePasswordActivity.this, MainActivity.class);
            //Clear stack to prevent user coming back to Profile Activity after pressing back button
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(ChangePasswordActivity.this, "Something Actually Went wrong", Toast.LENGTH_LONG).show();
        }


        return super.onOptionsItemSelected(item);
    }

}