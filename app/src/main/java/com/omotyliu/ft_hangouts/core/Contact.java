package com.omotyliu.ft_hangouts.core;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ListView;

import java.util.*;

public class Contact {
    private int id;

    private String fullName;

    private String photoUri;

    private String number;

    private Bitmap image;

    private List<Sms> sms = new ArrayList<>();

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }


    public Contact(String fullName, String number, Bitmap image) {
        this.fullName = fullName;
        this.number = number;
        this.image = image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPhotoUri()
    {
        return photoUri;
    }

    public void setPhotoUri(String photoUri)
    {
        this.photoUri = photoUri;
    }

    public List<Sms> getSms()
    {
        return sms;
    }

    public void setSms(List<Sms> sms) {
        this.sms = sms;
    }

    public int getNewMessagesCount()
    {
        int count = 0;
        for (Sms sm : sms)
        {
            if (!sm.isRead())
            count++;
        }
        return count;
    }
}


