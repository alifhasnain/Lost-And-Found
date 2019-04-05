package com.testapp.lostfound;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment implements View.OnClickListener,EasyPermissions.PermissionCallbacks   {

    private static final int IMAGE_SELECT_REQUEST = 4656;
    private static final int ASK_PERMISSION_REQUEST = 2154;


    private Context mContext;

    private Uri mSelectedImageUri;

    private FirebaseAuth mAuth;

    private FirebaseUser mCurrentUser;

    private StorageReference mUserPhotoStorageRef;

    private DocumentReference mProfileDocRef;

    private TextInputLayout mFirstName;
    private TextInputLayout mLastName;
    private TextInputLayout mFbProfileUrl;
    private TextInputLayout mDepartment;
    private TextInputLayout mAbout;

    private CircleImageView mCircleProfilePhoto;
    private String mPhotoUrl;

    private SmoothProgressBar smoothProgressBar;

    private ProfileObject mUserProfile;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        //For setting extra menu on fragment
        setHasOptionsMenu(true);

        initializeVariables(view);

        loadUserData();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mProfileDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e!=null)    {
                    return;
                }
                if(documentSnapshot.exists())   {
                    mUserProfile = documentSnapshot.toObject(ProfileObject.class);
                    loadUserData();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.save_btn_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.save_profile_info:
                saveProfileData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeVariables(View view) {

        mContext = getContext();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mUserPhotoStorageRef = FirebaseStorage.getInstance().getReference("/profile_photos/"+mCurrentUser.getUid()+".jpg");
        mProfileDocRef = FirebaseFirestore.getInstance().collection("profile_details").document(mCurrentUser.getUid());

        mFirstName = view.findViewById(R.id.first_name);
        mLastName = view.findViewById(R.id.last_name);
        mFbProfileUrl = view.findViewById(R.id.fb_profile_url);
        mDepartment = view.findViewById(R.id.department);
        mAbout = view.findViewById(R.id.about_me);
        smoothProgressBar = view.findViewById(R.id.smooth_progress_bar);

        mCircleProfilePhoto = view.findViewById(R.id.profile_photo);
        mCircleProfilePhoto.setOnClickListener(this);
    }

    private void loadUserData() {

        /*mProfileDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())   {
                    mUserProfile = documentSnapshot.toObject(ProfileObject.class);
                }
            }
        });*/

        try {
            mFirstName.getEditText().setText(mUserProfile.getFirstName());
            mLastName.getEditText().setText(mUserProfile.getLastName());
            mFbProfileUrl.getEditText().setText(mUserProfile.getFbProfileUrl());
            mDepartment.getEditText().setText(mUserProfile.getDepartment());
            mAbout.getEditText().setText(mUserProfile.getAboutMe());
            mPhotoUrl = mUserProfile.getProfilePhotoUrl();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Glide.with(this)
                    .load(mUserProfile.getProfilePhotoUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            smoothProgressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            smoothProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(mCircleProfilePhoto);
        }
        catch (Exception e) {
            mCircleProfilePhoto.setImageResource(R.drawable.dummy_photo);
            e.printStackTrace();
        }
    }

    private void selectPhoto() {
        //First Check Permissions
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(EasyPermissions.hasPermissions(mContext,perms))  {
            final String[] items = {"Camera","Gallery"};

            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle("Choose an option ");
            dialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    Intent intent = new Intent(getActivity(),ImagePicker.class);
                    if(items[i].equals("Camera"))   {
                        intent.putExtra("choice",0);
                    }
                    else    {
                        intent.putExtra("choice",1);
                    }
                    startActivityForResult(intent,IMAGE_SELECT_REQUEST);
                }
            });
            dialog.create().show();
        }
        else {
            EasyPermissions.requestPermissions(this, "We need permission to access Camera and Gallery", ASK_PERMISSION_REQUEST, perms);
        }
    }

    private void uploadProfilePhoto()   {

        ConvertBitmapToByteArray byteArray = new ConvertBitmapToByteArray();
        try {
            smoothProgressBar.setVisibility(View.VISIBLE);
            Bitmap selectedPhotoBitmap = scaledBitmap(MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), mSelectedImageUri), 1500f, true);
            mCircleProfilePhoto.setImageBitmap(selectedPhotoBitmap);
            byte[] imageInByteArray = byteArray.execute(selectedPhotoBitmap).get();
            UploadTask photoUploadTask = mUserPhotoStorageRef.putBytes(imageInByteArray);


            photoUploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()) {
                        mUserPhotoStorageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful()) {
                                    mPhotoUrl = task.getResult().toString();
                                    saveProfileData();
                                    loadUserData();
                                }
                            }
                        });
                        makeToast("Profile photo has changed.");
                    }
                    else {
                        makeToast("Failed to upload photo");
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void saveProfileData()  {

        String firstName = "";
        String lastName  = "";
        String department  = "";
        String fbUrl = "" ;
        String about = "";

        try {
            firstName = mFirstName.getEditText().getText().toString().trim();
            lastName = mLastName.getEditText().getText().toString().trim();
            department = mDepartment.getEditText().getText().toString().trim();
            fbUrl = mFbProfileUrl.getEditText().getText().toString().trim();
            about = mAbout.getEditText().getText().toString().trim();
        }
        catch (NullPointerException e)  {
            e.printStackTrace();
        }

        ProfileObject profileObject = new ProfileObject();

        profileObject.setFirstName(firstName);
        profileObject.setLastName(lastName);
        profileObject.setUniversity("Daffodil International University");
        profileObject.setDepartment(department);
        profileObject.setFbProfileUrl(fbUrl);
        profileObject.setAboutMe(about);
        profileObject.setProfilePhotoUrl(mPhotoUrl);
        profileObject.seteMail(mCurrentUser.getEmail());

        mProfileDocRef.set(profileObject).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    makeToast("Info Updated");
                }
                else {
                    makeToast("Failed to update");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.profile_photo:
                selectPhoto();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if(requestCode == ASK_PERMISSION_REQUEST)   {
            selectPhoto();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case IMAGE_SELECT_REQUEST:
                if(resultCode==RESULT_OK)   {
                    mSelectedImageUri = data.getData();
                    uploadProfilePhoto();
                }  else if (resultCode == 420) {
                    makeToast("No Image was selected!");
                } else if (resultCode == RESULT_CANCELED) {
                    makeToast("Canceled!");
                }
                break;
            case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE:
                break;
        }
    }

    private void makeToast(String text)    {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    private Bitmap scaledBitmap(Bitmap realImage, Float maxImageSizeInKb, boolean filter) {
        float ratio = Math.min(
                maxImageSizeInKb / realImage.getWidth(),
                maxImageSizeInKb / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        return Bitmap.createScaledBitmap(realImage, width,
                height, filter);
    }

    private static class ConvertBitmapToByteArray extends AsyncTask<Bitmap, Void, byte[]> {

        @Override
        protected byte[] doInBackground(Bitmap... bitmaps) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        }
    }
}
