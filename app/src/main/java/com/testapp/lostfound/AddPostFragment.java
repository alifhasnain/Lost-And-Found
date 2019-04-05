package com.testapp.lostfound;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddPostFragment extends Fragment implements View.OnClickListener,EasyPermissions.PermissionCallbacks{

    private static final int IMAGE_SELECT_REQUEST = 4656;
    private static final int ASK_PERMISSION_REQUEST = 2154;

    private ArrayList<Bitmap> mPhotoBitmapList = new ArrayList<>();

    private ArrayList<String> mPhotoUrlList = new ArrayList<>();

    private Context mContext;

    private Spinner mSpinner;

    private TextInputLayout mDescription;

    private ViewPager mViewPager;

    private ImageSliderAdapterForAddPost viewPagerAdapter;

    private DotsIndicator mDotsIndicator;

    private CollectionReference mPostsCollection;

    private StorageReference mPostPhotosRef;

    private SmoothProgressBar mSmoothProgressBar;

    ProfileObject mProfile;

    public AddPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        initializeVariablesAndListeners(view);

        initializeViewPager();

        isProfileCompleted();

        return view;
    }

    private void initializeViewPager() {
        viewPagerAdapter = new ImageSliderAdapterForAddPost(mContext,mPhotoBitmapList);
        mViewPager.setAdapter(viewPagerAdapter);
        mDotsIndicator.setViewPager(mViewPager);
    }

    private void initializeVariablesAndListeners(View view) {

        mContext = getActivity().getApplicationContext();

        mSpinner = view.findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mDescription = view.findViewById(R.id.post_description);
        mDescription.requestFocus();

        mPostsCollection = FirebaseFirestore.getInstance().collection("all_posts");

        mDotsIndicator = view.findViewById(R.id.worm_dots_indicator);

        mViewPager = view.findViewById(R.id.view_pager);

        Button postBtn = view.findViewById(R.id.post_btn);

        Button addPhoto = view.findViewById(R.id.add_photo);

        mSmoothProgressBar = view.findViewById(R.id.smooth_progress_bar);

        postBtn.setOnClickListener(this);
        addPhoto.setOnClickListener(this);
    }

    private void selectPhoto()  {
        //First Check Permissions
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(EasyPermissions.hasPermissions(mContext,perms))  {
            final String[] items = {"Camera","Gallery"};

            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
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

    private void uploadPhotoAndPost() {

        if(mProfile.getFirstName().isEmpty() || mProfile.getLastName().isEmpty() || mProfile.getFbProfileUrl().isEmpty() || mProfile.getProfilePhotoUrl().isEmpty())    {
            makeToast("Please complete your profile to post!");
            return;
        }

        final String description;

        try {
            if(mDescription.getEditText().getText().toString().trim().equals("") || mDescription.getEditText().getText().toString().trim().isEmpty())   {
                makeToast("Description can't be empty.");
                mDescription.requestFocus();
                return;
            }
        }
        catch (NullPointerException e)  {
            e.printStackTrace();
        }

        if(mPhotoBitmapList.size() == 0)    {
            makeToast("You must add minimum 1 sample photo.");
            return;
        }

        description = mDescription.getEditText().getText().toString().trim();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final String currentDate = sdf.format(new Date());

        final String uId = FirebaseAuth.getInstance().getUid();

        final String lostOrFound = mSpinner.getSelectedItem().toString();

        mSmoothProgressBar.setVisibility(View.VISIBLE);

        UploadTask uploadTask1;
        UploadTask uploadTask2;
        UploadTask uploadTask3;

        final ArrayList<StorageReference> storageReferenceArrayList = new ArrayList<>();

        for(int i = 0 ; i < mPhotoBitmapList.size() ; i++)  {

            mPostPhotosRef = FirebaseStorage.getInstance().getReference("/post_photos/"+generateRandomSting()+".jpg");

            storageReferenceArrayList.add(mPostPhotosRef);

            ConvertBitmapToByteArray imageByteArray = new ConvertBitmapToByteArray();

            byte[] temp = null ;

            try {
                temp = imageByteArray.execute(scaledBitmap(mPhotoBitmapList.get(i),1500f,true)).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            final int x = i;
            switch (x)
            {
                case 0:
                    uploadTask1 = mPostPhotosRef.putBytes(temp);
                    uploadTask1.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()) {
                                storageReferenceArrayList.get(x).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if(task.isSuccessful()) {
                                            mPhotoUrlList.add(task.getResult().toString());
                                            if(mPhotoUrlList.size() == mPhotoBitmapList.size()) {
                                                doPost(uId,currentDate,description,lostOrFound);
                                            }
                                        }
                                        else {
                                            makeToast("Failed!\nPlease try again.2");
                                            mSmoothProgressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                            else {
                                makeToast("Failed!\nPlease try again.1");
                                mSmoothProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                    break;
                case 1:
                    uploadTask2 = mPostPhotosRef.putBytes(temp);
                    uploadTask2.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()) {
                                storageReferenceArrayList.get(x).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if(task.isSuccessful()) {
                                            mPhotoUrlList.add(task.getResult().toString());
                                            if(mPhotoUrlList.size() == mPhotoBitmapList.size()) {
                                                doPost(uId,currentDate,description,lostOrFound);
                                            }
                                        }
                                        else {
                                            makeToast("Failed!\nPlease try again.2");
                                            mSmoothProgressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                            else {
                                makeToast("Failed!\nPlease try again.1");
                                mSmoothProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                    break;
                case 2:
                    uploadTask3 = mPostPhotosRef.putBytes(temp);
                    uploadTask3.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()) {
                                storageReferenceArrayList.get(x).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if(task.isSuccessful()) {
                                            mPhotoUrlList.add(task.getResult().toString());
                                            if(mPhotoUrlList.size() == mPhotoBitmapList.size()) {
                                                doPost(uId,currentDate,description,lostOrFound);
                                            }
                                        }
                                        else {
                                            makeToast("Failed!\nPlease try again.2");
                                            mSmoothProgressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                            else {
                                makeToast("Failed!\nPlease try again.1");
                                mSmoothProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                    break;
            }
        }
    }

    private void doPost(String uId,String currentDate,String description,String lostOrFound)   {
        PostObject postObject = new PostObject(uId,currentDate,description,mPhotoUrlList,lostOrFound);

        mPostsCollection.add(postObject).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()) {
                    makeToast("Post successful");
                    mPhotoUrlList.clear();
                    mSmoothProgressBar.setVisibility(View.GONE);
                }
                else {
                    makeToast("Failed!\nPlease Try Again");
                    mPhotoUrlList.clear();
                    mSmoothProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_photo:
                selectPhoto();
                break;
            case R.id.post_btn:
                uploadPhotoAndPost();
                break;
        }
    }

    private void makeToast(String txt) {
        Toast.makeText(mContext, txt, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        switch (requestCode)
        {
            case ASK_PERMISSION_REQUEST:
                selectPhoto();
                break;
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
                if(resultCode == RESULT_OK) {
                    Uri selectedPhoto = data.getData();
                    try {
                        if(mPhotoBitmapList.size() <= 3 )    {
                            mPhotoBitmapList.add(MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), selectedPhoto));
                            viewPagerAdapter.notifyDataSetChanged();
                        }
                        else {
                            makeToast("Max 3 photo is allowed.");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE:
                break;
        }
    }

    private boolean isProfileCompleted()    {
        DocumentReference profileDetail = FirebaseFirestore.getInstance().document("/profile_details/" +
                FirebaseAuth.getInstance().getCurrentUser().getUid());
        profileDetail.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())   {
                    mProfile = documentSnapshot.toObject(ProfileObject.class);
                    if(mProfile.getFirstName().isEmpty() || mProfile.getLastName().isEmpty() || mProfile.getFbProfileUrl().isEmpty() || mProfile.getProfilePhotoUrl().isEmpty())    {
                        makeToast("Please complete your profile to post!");
                    }
                }
                else {
                    makeToast("Please complete your profile to post!");
                }
            }
        });
        return false;
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

    public static String generateRandomSting()   {
        String ALPHABETS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        while (str.length()<=15)    {
            int index = (int)(random.nextFloat()*ALPHABETS.length());   //Used float for better random
            str.append(ALPHABETS.charAt(index));
        }
        return str.toString();
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
