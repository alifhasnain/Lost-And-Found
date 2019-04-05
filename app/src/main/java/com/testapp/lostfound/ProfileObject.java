package com.testapp.lostfound;

import com.google.firebase.firestore.Exclude;

public class ProfileObject {
    private String FirstName;
    private String LastName;
    private String university;
    private String department;
    private String fbProfileUrl;
    private String aboutMe;
    private String profilePhotoUrl;
    private String uID;
    private String eMail;

    public ProfileObject() {

    }

    public ProfileObject(String firstName, String lastName, String university, String department, String fbProfileUrl, String aboutMe, String profilePhotoUrl,String eMail) {
        FirstName = firstName;
        LastName = lastName;
        this.university = university;
        this.department = department;
        this.fbProfileUrl = fbProfileUrl;
        this.aboutMe = aboutMe;
        this.profilePhotoUrl = profilePhotoUrl;
        this.eMail = eMail;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFbProfileUrl() {
        return fbProfileUrl;
    }

    public void setFbProfileUrl(String fbProfileUrl) {
        this.fbProfileUrl = fbProfileUrl;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    @Exclude
    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }
}
