package com.example.sightzapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDoB, textViewGender, textViewMobile;
    private ProgressBar progressBar;
    private String fullName, email, doB, gender, mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;
    private Toolbar toolbar;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Package Context

    public Profile() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        /*((AppCompatActivity)requireActivity()).getSupportActionBar().setTitle("Profile");*/
        /* ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        textViewWelcome = requireView().findViewById(R.id.textView_show_welcome);
        textViewFullName = requireView().findViewById(R.id.textView_show_fullname);
        textViewEmail =  requireView().findViewById(R.id.textView_show_email);
        textViewDoB =  requireView().findViewById(R.id.textView_show_dob);
        textViewGender =  requireView().findViewById(R.id.textView_show_gender);
        textViewMobile =  requireView().findViewById(R.id.textView_show_mobile);
        progressBar =  requireView().findViewById(R.id.progressBar);


        //Set OnClick Listener on the Imageview
        imageView = requireView().findViewById(R.id.imageView_profile_dp);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), UploadProfilePicActivity.class);
            startActivity(intent);

        });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(requireContext(), "Something went wrong! Users details not available yet",
                    Toast.LENGTH_LONG).show();
        }else {
            checkIfEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }
    //User Redirected to Profile After Successfully registering
    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        //Set Up alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireView().getContext());
        builder.setTitle("Email not Verified");
        builder.setMessage("Please verify your email now. You wont be logged in without verifying");

        //Open Email Apps if User Clicks Continue Button
        builder.setPositiveButton("Continue", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        //Create the Alertdialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //Extraxting User Reference From Database for Registered user
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails !=null){
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    doB = readUserDetails.doB;
                    gender = readUserDetails.gender;
                    mobile = readUserDetails.mobile;

                    textViewWelcome.setText(getString(R.string.welcome_head_profile, fullName));
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDoB.setText(doB);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);


                    //Set User  DP (After user has Upload)
                    Uri uri = firebaseUser.getPhotoUrl();

                    //ImageView setImageURi() Should not be used with regular URIs. So we are using Picaso
                    Picasso.with(requireContext()).load(uri).into(imageView);
                }else {
                    Toast.makeText(requireContext(), "Something went wrong! Users details not available yet",
                            Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Something went wrong! Users details not available yet",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    //Creating ActionBar Menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //Inflate menu items
        inflater.inflate(R.menu.common_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);


    }



    //When any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        int id = item.getItemId();

        switch (id) {
            case R.id.menu_update_profile:
                startActivity(new Intent(getActivity(), UpdateProfileActivity.class));
                return true;

            case R.id.menu_update_email:
                startActivity(new Intent(getActivity(), UpdateEmailActivity.class));
                return true;

            case R.id.menu_change_password:
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                return true;

            case R.id.menu_delete_profile:
                startActivity(new Intent(getActivity(), DeleteProfileActivity.class));
                return true;
            case
                    R.id.menu_logout:
                authProfile.signOut();
                Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent (getActivity(), FirstActivity.class);
                //Clear stack to prevent user coming back to Profile Activity after pressing back button
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(getActivity().getIntent());
                getActivity().finish();
        }

        return super.onOptionsItemSelected(item);
    }
}





    /*int id = item.getItemId();
  //ctionBar Tweaks
        if (id == android.R.id.home){
                NavUtils.navigateUpFromSameTask(requireActivity());
                }

                else if(id == R.id.menu_refresh){
                //Refresh activity
                startActivity(getActivity().getIntent());
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);

                //FragmentActivity FragmentActivity = this.getActivity()

                }else if (id == R.id.menu_update_profile){

                Intent intent = new Intent (requireContext(), UpdateProfileActivity.class);
        startActivity(requireActivity().getIntent());
        requireActivity().finish();
        }else if (id == R.id.menu_update_email) {
        Intent intent = new Intent (requireContext(), UpdateEmailActivity.class);
        startActivity(intent);
        } else if (id == R.id.menu_settings){
        Toast.makeText(requireContext(), "menu_settings", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_change_password) {
        Intent intent = new Intent (requireContext(), ChangePasswordActivity.class);
        startActivity(intent);
        }else if (id == R.id.menu_delete_profile) {
        Intent intent = new Intent (requireContext(), DeleteProfileActivity.class);
        startActivity(intent);
        }else if (id == R.id.menu_logout){
        authProfile.signOut();
        Toast.makeText(requireContext(), "Logged Out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent (requireContext(), MainActivity.class);
        //Clear stack to prevent user coming back to Profile Activity after pressing back button
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(requireActivity().getIntent());
        requireActivity().finish();
        } else {
        Toast.makeText(requireContext(), "Something Actually Went wrong", Toast.LENGTH_LONG).show();
        }*/