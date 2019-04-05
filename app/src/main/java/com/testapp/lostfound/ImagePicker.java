package com.testapp.lostfound;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

public class ImagePicker extends AppCompatActivity {

    // Flag to indicate the request of the next task to be performed
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_SELECT_IMAGE = 1;

    File storageDir;

    // The URI of photo taken with camera
    private Uri uriPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imager_picker);

        new Thread(new ClearCache(this)).start();

        //storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        storageDir = new File(getExternalCacheDir(),"camera");
        if(!storageDir.exists())    {
            storageDir.mkdirs();
        }

        int n = getIntent().getIntExtra("choice", 4);
        if (n == 0) {
            takePhoto();
        } else if (n == 1) {
            selectImageInGallery();
        }
    }

    private void takePhoto() {
        Intent pickPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pickPhoto.resolveActivity(getPackageManager()) != null) {
            // Save the photo taken to a temporary file.
            try {
                File file = File.createTempFile("IMG_", ".jpg", storageDir);

                uriPhoto = FileProvider.getUriForFile(this, getPackageName()+".provider", file);
                pickPhoto.putExtra(MediaStore.EXTRA_OUTPUT, uriPhoto);
                startActivityForResult(pickPhoto, REQUEST_TAKE_PHOTO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void selectImageInGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE);
        }
    }

    // Deal with the result of selection of the photos and faces.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    //setResultDataAndFinish(uriPhoto,resultCode);
                    cropImage(uriPhoto);
                    break;
                case REQUEST_SELECT_IMAGE:
                    Uri imageUri = data.getData();
                    cropImage(imageUri);
                    //setResultDataAndFinish(imageUri, resultCode);
                    break;
                case UCrop.REQUEST_CROP:
                    Uri result = UCrop.getOutput(data);
                    setResultDataAndFinish(result, resultCode);
                    break;
                default:
                    break;
            }
        } else {
            setResultCancelled();
        }
    }

    private void setResultCancelled() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private void cropImage(Uri sourceUri) {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(this,R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        //queryName(getContentResolver(), sourceUri)
        Uri destinationUri = Uri.fromFile(new File(storageDir,generateRandomSting()+".jpg"));
        UCrop.of(sourceUri, destinationUri).withOptions(options).start(this);
    }

    /*private String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }*/

    private void setResultDataAndFinish(Uri imageUri, int resultCode) {
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.setData(imageUri);
            setResult(resultCode, intent);
            finish();
        } else {
            Intent intent = new Intent();
            setResult(420, intent);
            finish();
        }
    }

    public static String generateRandomSting()   {
        String ALPHABETS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        while (str.length()<=10)    {
            int index = (int)(random.nextFloat()*ALPHABETS.length());   //Used float for better random
            str.append(ALPHABETS.charAt(index));
        }
        return str.toString();
    }

    class ClearCache implements Runnable    {

        Context context;

        public ClearCache(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            File path = new File(context.getExternalCacheDir(), "camera");
            if (path.exists() && path.isDirectory()) {
                for (File child : path.listFiles()) {
                    child.delete();
                }
            }
        }
    }
}
