package com.testapp.lostfound;

import java.util.ArrayList;

public class PostObject {

    private String mUid;

    private String mPostDate;

    private String mDescription;

    private ArrayList<String> mPhotoUrlList;

    private String mLostOrFound;

    public PostObject() {
        //Required empty constructor for Cloud Firestore
    }

    public PostObject(String mUid, String mPostDate, String mDescription, ArrayList<String> mPhotoUrlList, String mLostOrFound) {
        this.mUid = mUid;
        this.mPostDate = mPostDate;
        this.mDescription = mDescription;
        this.mPhotoUrlList = mPhotoUrlList;
        this.mLostOrFound = mLostOrFound;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getmPostDate() {
        return mPostDate;
    }

    public void setmPostDate(String mPostDate) {
        this.mPostDate = mPostDate;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public ArrayList<String> getmPhotoUrlList() {
        return mPhotoUrlList;
    }

    public void setmPhotoUrlList(ArrayList<String> mPhotoUrlList) {
        this.mPhotoUrlList = mPhotoUrlList;
    }

    public String getmLostOrFound() {
        return mLostOrFound;
    }

    public void setmLostOrFound(String mLostOrFound) {
        this.mLostOrFound = mLostOrFound;
    }
}
