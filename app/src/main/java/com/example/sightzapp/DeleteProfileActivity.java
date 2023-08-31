package com.example.sightzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteProfileActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private EditText editTextUserPwd;
    private ProgressBar progressBar;
    private String userPwd;
    private TextView textViewAuthenticated;
    private Button buttonReAuthenticate, buttonDeleteUser;
    private static final String TAG = "DeleteProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        if(getSupportActionBar() !=null) {
            getSupportActionBar().setTitle("Delete Your Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressBar = findViewById(R.id.progressBar);
        editTextUserPwd = findViewById(R.id.editText_delete_user_pwd);
        textViewAuthenticated = findViewById(R.id.textView_delete_user_authenticated);
        buttonDeleteUser = findViewById(R.id.button_delete_user);
        buttonReAuthenticate = findViewById(R.id.button_delete_user_authenticate);

        //Disable Delete User Button Until Authenticated
        buttonDeleteUser.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser.equals("")) {
            Toast.makeText(DeleteProfileActivity.this, "Something went wrong"
                            + "User Details are not available at the moment",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(DeleteProfileActivity.this, FirstActivity.class);
            startActivity(intent);
            finish();
        }else {
            reAuthenticateUser(firebaseUser);
        }

    }

    //ReAuthenitcate User Before Changing password
    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwd = editTextUserPwd.getText().toString();
                if (TextUtils.isEmpty(userPwd)) {
                    Toast.makeText(DeleteProfileActivity.this, "Password is needed",
                            Toast.LENGTH_SHORT).show();
                    editTextUserPwd.setError("Please enter your current password to authenticate");
                    editTextUserPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    //ReAuthenticate Again
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                //Disable editText Passwprd
                                editTextUserPwd.setEnabled(true);


                                //Enable Change
                                buttonReAuthenticate.setEnabled(false);
                                buttonDeleteUser.setEnabled(true);

                                //Set TextView to show User is Now Verfied
                                textViewAuthenticated.setText("You are Verified."+"You can Delete");
                                Toast.makeText(DeleteProfileActivity.this, "You can now delete Profile", Toast.LENGTH_SHORT).show();

                                //Update color Of Change Password
                                buttonDeleteUser.setBackgroundTintList(ContextCompat.getColorStateList(
                                        DeleteProfileActivity.this, R.color.dark_green));

                                buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAlertDialog();
                                    }
                                });

                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void showAlertDialog() {
        //Set Up alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteProfileActivity.this);
        builder.setTitle("Delete User And Related Data");
        builder.setMessage("Please verify you want to complete as all User data will be erased");

        //Open Email Apps if User Clicks Continue Button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUserdata(firebaseUser);
            }
        });
        //Return to user Profile if User Cancelled
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DeleteProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //Create the Alertdialog
        AlertDialog alertDialog = builder.create();

        //Change the button color of contnue
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
            }
        });


        //Show Alert dialog
        alertDialog.show();
    }

    private void deleteUser() {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    authProfile.signOut();
                    Intent intent = new Intent(DeleteProfileActivity.this, FirstActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    try{
                        throw task.getException();

                    } catch (Exception e){
                        Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    //Deleting User Data and Object
    private void deleteUserdata(FirebaseUser firebaseUser) {
        //Delete the Display Picture But Check if any Pic Exist First

        if(firebaseUser.getPhotoUrl() !=null){

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "On Success: Photo is Deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, e.getMessage());
                    Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


        //Delete Data from Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused)
            {
                Log.d(TAG, "On Success: Photo is Deleted");
                //Finally Delete User after related data
                deleteUser();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    //Creating Action Bar menu
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
            NavUtils.navigateUpFromSameTask(DeleteProfileActivity.this);

        }else if (id == R.id.menu_refresh) {

            //Refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);

            /*FragmentActivity FragmentActivity = this.getActivity();*/

        } else if (id == R.id.menu_update_profile) {

            Intent intent = new Intent(DeleteProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(DeleteProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_settings) {
            Toast.makeText(DeleteProfileActivity.this, "menu_settings", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(DeleteProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(DeleteProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(DeleteProfileActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteProfileActivity.this, MainActivity.class);
            //Clear stack to prevent user coming back to Profile Activity after pressing back button
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(DeleteProfileActivity.this, "Something Actually Went wrong", Toast.LENGTH_LONG).show();
        }


        return super.onOptionsItemSelected(item);
    }

}

