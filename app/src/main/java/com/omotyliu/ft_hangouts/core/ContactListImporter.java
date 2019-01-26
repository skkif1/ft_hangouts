package com.omotyliu.ft_hangouts.core;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class ContactListImporter {

    private Activity context;

    public ContactListImporter(Activity context) {
        this.context = context;
    }


    // select * form
    public List<Contact> getPhoneBookList()
    {
        List<Contact> phoneBook = new ArrayList<>();

            Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            while (cursor.moveToNext())
            {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String phoneNumber = getPhoneNumberByContactId(id);
                Contact ct = new Contact(name, phoneNumber, null);
                ct.setPhotoUri(photoUri);
                phoneBook.add(ct);
            }
            cursor.close();

        return phoneBook;
    }


    private String getPhoneNumberByContactId(String id)
    {
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",  new String[]{id}, null);

        if (cursor.moveToNext())
        {
            return cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        return "";
    }


    public void enableRuntimePermission(int code)
    {
        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SEND_SMS}, code);
    }

    public boolean havePermission()
    {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;

    }
}
