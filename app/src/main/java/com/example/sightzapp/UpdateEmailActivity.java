package com.example.sightzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

public class UpdateEmailActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView textViewAuthenticated;
    private String userOldEmail, userNewEmail, userPwd;
    private Button buttonUpdateEmail;
    private EditText editTextNewEmail, editTextPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        if(getSupportActionBar() !=null) {
            getSupportActionBar().setTitle("Update Email");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressBar = findViewById(R.id.progressBar);
        editTextPwd = findViewById(R.id.editText_update_email_verify_password);
        editTextNewEmail = findViewById(R.id.editText_update_email_new);
        textViewAuthenticated = findViewById(R.id.textView_update_email_authenticated);
        buttonUpdateEmail = findViewById(R.id.button_update_email);

        //Button disabled Until User is verified
        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        //Set Old Email ID on TextView
        userOldEmail = firebaseUser.getEmail();
        TextView textViewOldEmail = findViewById(R.id.textView_update_email_old);
        textViewOldEmail.setText(userOldEmail);

        if(firebaseUser.equals("")) {
            Toast.makeText(UpdateEmailActivity.this, "Something went wrong User details not found",
                    Toast.LENGTH_LONG).show();

        }else {
            reAuthenticate(firebaseUser);
        }

    }

    private void reAuthenticate(FirebaseUser firebaseUser) {
        //Reauthenticate User before Updating email
        Button buttonVerifyUser = findViewById(R.id.button_authenticate_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtain Password for authentication
                userPwd = editTextPwd.getText().toString();

                if (TextUtils.isEmpty(userPwd)){
                    Toast.makeText(UpdateEmailActivity.this, "Password is needed to proceed",
                            Toast.LENGTH_LONG).show();
                    editTextPwd.setError("Please provide password");
                    editTextPwd.requestFocus();

                }else {
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                   progressBar.setVisibility(View.GONE);

                   Toast.makeText(UpdateEmailActivity.this, "Password has not been verified" +
                           "You can update email now", Toast.LENGTH_SHORT).show();

                   //Set TextView To show user is now Authenticated
                   textViewAuthenticated.setText("You are updated and now fine");
                   //Disable EditText for Password button to verify user and enable Edit Text for a change
                   editTextNewEmail.setEnabled(true);
                   editTextPwd.setEnabled(false);
                   buttonVerifyUser.setEnabled(false);
                   buttonUpdateEmail.setEnabled(true);

                   //Change color of update Email button
                   buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this,
                           R.color.dark_green));

                   buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           userNewEmail = editTextNewEmail.getText().toString();
                           if(TextUtils.isEmpty(userNewEmail)){
                               Toast.makeText(UpdateEmailActivity.this, "New email is required",
                                       Toast.LENGTH_SHORT).show();
                           } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()){
                               Toast.makeText(UpdateEmailActivity.this, "Please Enter Valid email",
                                       Toast.LENGTH_SHORT).show();
                               editTextNewEmail.setError("Please provide a correct email");
                               editTextNewEmail.requestFocus();
                           }else if (userOldEmail.matches(userNewEmail)){
                               Toast.makeText(UpdateEmailActivity.this, "New email must not be the same as Old",
                                       Toast.LENGTH_SHORT).show();
                               editTextNewEmail.setError("Please enter a new Mail");
                               editTextNewEmail.requestFocus();
                           }else {
                               progressBar.setVisibility(View.VISIBLE);
                               updateEmail(firebaseUser);
                           }
                       }
                   });

                   } else{
                     try {
                         throw task.getException();
                     }catch (Exception e) {
                         Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                     }
               }


                }
            });
            }

            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
                    firebaseUser.sendEmailVerification();

                    Toast.makeText(UpdateEmailActivity.this, "Email has now been updated",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateEmailActivity.this, Profile.class);
                    startActivity(intent);
                    finish();
                }else {
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

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
            NavUtils.navigateUpFromSameTask(UpdateEmailActivity.this);

        } else if(id == R.id.menu_refresh){
            NavUtils.navigateUpFromSameTask(UpdateEmailActivity.this);
            //Refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);

            /*FragmentActivity FragmentActivity = this.getActivity();*/

        }else if (id == R.id.menu_update_profile){

            Intent intent = new Intent (UpdateEmailActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.menu_update_email) {
            Intent intent = new Intent (UpdateEmailActivity.this, UploadProfilePicActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_settings){
            Toast.makeText(UpdateEmailActivity.this, "menu_settings", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent (UpdateEmailActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent (UpdateEmailActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(UpdateEmailActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent (UpdateEmailActivity.this, MainActivity.class);
            //Clear stack to prevent user coming back to Profile Activity after pressing back button
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(UpdateEmailActivity.this, "Something Actually Went wrong", Toast.LENGTH_LONG).show();
        }


        return super.onOptionsItemSelected(item);
    }

}