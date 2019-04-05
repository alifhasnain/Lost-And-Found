package com.testapp.lostfound;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    ImageView mProfilePhoto;

    TextView mName;

    TextView mUniversity;

    TextView mDepartment;

    TextView mUserEmail;

    TextView mFbLink;

    TextView mAbout;

    FirebaseFirestore mFirestoreDB;

    FirebaseAuth mAuth;

    Context mContext;

    FirebaseUser mCurrentUser;

    DocumentReference mUserProfileDocRef;

    ProfileObject userProfileObject;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        setHasOptionsMenu(true);
        initializeVariables(view);

        loadData();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mUserProfileDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null)   {
                    return;
                }
                if(documentSnapshot.exists())   {
                    userProfileObject = documentSnapshot.toObject(ProfileObject.class);
                    loadData();
                }
            }
        });
    }

    private void loadData() {

        try {
            String name = userProfileObject.getFirstName();
            name += " " + userProfileObject.getLastName();
            mName.setText(name);
            mUniversity.setText("Daffodil International University");
            mDepartment.setText(userProfileObject.getDepartment());
            mUserEmail.setText(mCurrentUser.getEmail());
            mFbLink.setText(userProfileObject.getFbProfileUrl());
            mAbout.setText(userProfileObject.getAboutMe());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(15f);
        circularProgressDrawable.setCenterRadius(70f);
        circularProgressDrawable.setColorSchemeColors(Color.parseColor("#D32F2F"), Color.parseColor("#009688") , Color.parseColor("#0288D1"));
        circularProgressDrawable.start();

        try {
            Glide.with(this)
                    .load(userProfileObject.getProfilePhotoUrl())
                    .placeholder(circularProgressDrawable)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(mProfilePhoto);
        }
        catch (Exception e) {
            mProfilePhoto.setImageResource(R.drawable.dummy_photo);
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.edit_profile_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.edit_profile:
                getActivity().getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container,new EditProfileFragment())
                        .addToBackStack(null).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeVariables(View view) {
        mContext = getActivity().getApplicationContext();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mFirestoreDB = FirebaseFirestore.getInstance();
        mUserProfileDocRef = mFirestoreDB.collection("profile_details").document(mCurrentUser.getUid());

        mProfilePhoto = view.findViewById(R.id.profile_photo);
        mName = view.findViewById(R.id.name);
        mUserEmail = view.findViewById(R.id.user_email);
        mUniversity = view.findViewById(R.id.university);
        mDepartment = view.findViewById(R.id.department);
        mFbLink = view.findViewById(R.id.fb_profile_link);
        mAbout = view.findViewById(R.id.about_me);
    }

}
