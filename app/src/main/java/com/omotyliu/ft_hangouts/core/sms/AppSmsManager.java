package com.omotyliu.ft_hangouts.core.sms;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;

import com.omotyliu.ft_hangouts.core.Sms;

public class AppSmsManager
{
    SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(3);

    public static final int REQUEST_PERMISSION_CODE = 1;


    public void sendMessage(Sms sms, String phoneNumber, PendingIntent receiver, Activity context)
    {
        smsManager.sendTextMessage(phoneNumber, null, sms.getMessage(), receiver, null);
    }


    public void enableRuntimePermission(Activity context, int code)
    {
        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE}, code);
    }

    public boolean havePermission(Activity context)
    {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

}
