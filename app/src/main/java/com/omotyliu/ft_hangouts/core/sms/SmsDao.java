package com.omotyliu.ft_hangouts.core.sms;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.omotyliu.ft_hangouts.core.Contact;
import com.omotyliu.ft_hangouts.core.Sms;

import java.util.ArrayList;
import java.util.List;

public class SmsDao
{

    private List<Sms> getAllSentMessages(List<Sms> all, String phone)
    {

        List<Sms> history = new ArrayList<>();
        phone = phone.replace(" ", "");

        for (Sms sms : all)
        {
            if (sms.getType().equals("send") && sms.getAddressNumber() != null && !sms.getAddressNumber() .equals("") && sms.getAddressNumber().equals(phone))
            {
                sms.setAuthorId(Sms.USER_ID);
                history.add(sms);
            }
        }

        return history;
    }


    private List<Sms> getAllMessages(Uri path, Activity context)
    {
        List<Sms> history = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(path, null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexTime = smsInboxCursor.getColumnIndex("DATE");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int readAddress = smsInboxCursor.getColumnIndex("read");

        while (smsInboxCursor.moveToNext())
        {
                String number = smsInboxCursor.getString(indexAddress);
                Sms sms = new Sms();
                sms.setMessage(smsInboxCursor.getString(indexBody));
                sms.setCreationTime(Long.parseLong(smsInboxCursor.getString(indexTime)));
                if (path.toString().endsWith("sent")) {
                    sms.setAddressNumber(number);
                    sms.setType("send");
                }
                else
                {
                    sms.setAuthorNumber(number);
                    sms.setType("incoming");
                }
                sms.setRead(smsInboxCursor.getString(readAddress).equals("1"));
                history.add(sms);
        }
        return history;
    }



    private List<Sms> getAllIncoming(List<Sms> all, String phone)
    {

        List<Sms> history = new ArrayList<>();
        phone = phone.replace(" ", "");

        for (Sms sms : all)
        {
            if (sms.getType().equals("incoming") && sms.getAuthorNumber() != null && !sms.getAuthorNumber().equals("") && sms.getAuthorNumber().equals(phone))
            {
                sms.setAuthorId(1);
                history.add(sms);
            }
        }

        return history;
    }



    public void getAllNewMessages(Activity context, List<Contact> contacts)
    {

        List<Sms> all = getAllMessages(Uri.parse("content://sms/inbox"), context);
        all.addAll(getAllMessages(Uri.parse("content://sms/sent"), context));

        for (Contact contact : contacts)
        {
            ArrayList<Sms> history = new ArrayList<>();
           history.addAll(getAllSentMessages(all, contact.getNumber()));
           history.addAll(getAllIncoming(all, contact.getNumber()));
           contact.setSms(history);
        }
    }

}
