package com.example.sightzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDoB,editTextRegisterMobile,
            editTextRegisterPwd, editTextRegisterConfirmPwd;


    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private DatePickerDialog picker;
    private static final String TAG= "RegisterActivity";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if(getSupportActionBar() !=null) {
        getSupportActionBar().setTitle("Register Here");

        }

        Toast.makeText(RegisterActivity.this,"You can register here", Toast.LENGTH_LONG).show();

        progressBar = findViewById(R.id.progressBar);
        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDoB= findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);

        //Radio Button For Gender
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        //Setting Up Date Picker
        editTextRegisterDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //Date Picker Dialog
                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    editTextRegisterDoB.setText(dayOfMonth +"/"+(month + 1)+"/"+year);
                    }
                }, year, month, day);
                picker.show();
            }

        });

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

                //Obtain the entered data

                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDoB = editTextRegisterDoB.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString();
                String textPwd = editTextRegisterPwd.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();
                String textGender; //Cant Obtain the value until button is selected

                //Verify Mobile Number using Matcher and Pattern
                String mobileRegex = "[1-9][0-9]{9}"; //First number must be (1 to 9)
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);




                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Full Name", Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full Name is required");
                    editTextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("valid Email is required");
                    editTextRegisterEmail.requestFocus();

                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(RegisterActivity.this, "Please Select Your Date Of Born", Toast.LENGTH_LONG).show();
                    editTextRegisterDoB.setError("Date Of Born is Needed");
                    editTextRegisterDoB.requestFocus();

                } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Please Select Your Gender", Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError("Gender is needed");
                    radioButtonRegisterGenderSelected.requestFocus();

                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(RegisterActivity.this, "Please put in your Contact", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile Number is Needed");
                    editTextRegisterMobile.requestFocus();

                } else if (textMobile.length() != 10) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your contact", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile Number should be 10 digits");
                    editTextRegisterMobile.requestFocus();

                } else if(!mobileMatcher.find()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your contact", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile Number not valid");
                    editTextRegisterMobile.requestFocus();

                }else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is needed");
                    editTextRegisterPwd.requestFocus();

                } else if (textPwd.length() < 8) {
                    Toast.makeText(RegisterActivity.this, "Password should be at least 8 Characters", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is too weak");
                    editTextRegisterPwd.requestFocus();

                } else if (TextUtils.isEmpty(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Confirmation Of Password is needed");
                    editTextRegisterConfirmPwd.requestFocus();

                } else if (!textPwd.equals(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please repeat same password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Confirmation Of Password is needed");
                    editTextRegisterConfirmPwd.requestFocus();

                    //Clear Entered Password
                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                } else {
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDoB, textGender, textMobile, textPwd);

                }
            }


        });


    }
    //Register User using Credentials
    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textMobie, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser firebaserUser = auth.getCurrentUser();

                    //Update Display Name of User
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaserUser.updateProfile(profileChangeRequest);



                    //Enter User Data into the Firebaase Realtime Database
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB, textGender, textMobie);

                    //Extracting User Reference from Database for "Registered Users"
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaserUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(task1 -> {

                        if (task1.isSuccessful()) {
                            //Send Verification Email
                            firebaserUser.sendEmailVerification();


                            Toast.makeText(RegisterActivity.this, "User Registered Successfully verify email",
                                    Toast.LENGTH_LONG).show();

                             //Open User Profile after succesful registration
                            Intent intent = new Intent (RegisterActivity.this, MainActivity.class);

                             //To Prevent User from returnng back to Registration Screen on pressing back button
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);

                                 startActivity(intent);
                                 finish(); //to close Registration Process

                        }else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed try again",
                                    Toast.LENGTH_LONG).show();
                        }
                        //Hide Progress Bar
                        progressBar.setVisibility(View.GONE);

                    });


                } else {
                    try{
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthWeakPasswordException e) {

                        editTextRegisterPwd.setError("Your password is too weak, use a mix of alphabets, numbers and special characters");
                        editTextRegisterPwd.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextRegisterPwd.setError("Your email is invalid or already in use");
                        editTextRegisterPwd.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        editTextRegisterPwd.setError("User Email is Already registered");
                        editTextRegisterPwd.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    //Hide Progress Bar
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }
}