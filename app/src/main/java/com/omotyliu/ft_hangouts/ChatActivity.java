package com.omotyliu.ft_hangouts;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.omotyliu.ft_hangouts.core.ContactDb;
import com.omotyliu.ft_hangouts.core.ThemeEditor;
import com.omotyliu.ft_hangouts.core.sms.AppSmsManager;
import com.omotyliu.ft_hangouts.core.Contact;
import com.omotyliu.ft_hangouts.core.ContactDao;
import com.omotyliu.ft_hangouts.core.Sms;
import com.omotyliu.ft_hangouts.core.sms.SmsDao;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ChatActivity extends AppCompatActivity
{

    private int layout = R.layout.activity_chat;

    private Contact currentContact;

    private ContactDao dao;

    private  PendingIntent sentPI;

    private View.OnClickListener listener;

    private AppSmsManager manager = new AppSmsManager();

    private EditText text;

    private static ChatActivity inst;

    private ArrayAdapter adapter;


    public static ChatActivity getInstance()
    {
        return inst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(ThemeEditor.getTheme(this));
        super.onCreate(savedInstanceState);
        inst = this;
        dao = ContactDb.getInst(getApplicationContext());
        setContentView(layout);
        text = findViewById(R.id.sendMessageField);
        createListener();
        createReceiver();
        Bundle extra = getIntent().getExtras();
        int contactId  = (int) extra.get(ContactItemActivity.CONTACT_ID);
        currentContact = dao.getContact(contactId);
        initChat();
    }

    private void initChat()
    {
        initContactView(currentContact);
        ListView smsList = findViewById(R.id.smsList);
        if (currentContact.getNumber() != null)
        {
            refreshChat();
        }
        smsList.setAdapter(adapter);
    }


    private void initContactView(Contact contact)
    {
        TextView textView  = findViewById(R.id.chatContactName);
        ImageView imageView  = findViewById(R.id.chatContactImage);
        if (contact.getPhotoUri() != null)
        {
            imageView.setImageURI(Uri.parse(contact.getPhotoUri()));
        }
        Button sendButton  = findViewById(R.id.chatSendSms);
        sendButton.setOnClickListener(listener);
        textView.setText(contact.getFullName());
        ListView smsList = findViewById(R.id.smsList);
//        smsList.setAdapter(new SmsListAdapter(this, dao.getAllContactMessages(currentContact.getId())));
    }

    private void sendSms()
    {

        if (manager.havePermission(this))
        {
            String messagesInput = text.getText().toString();
            if (!messagesInput.isEmpty())
            {
                Sms sms = new Sms();
                sms.setConsumerId(currentContact.getId());
                sms.setAuthorId(0);
                sms.setMessage(messagesInput);
                manager.sendMessage(sms, currentContact.getNumber(), sentPI, this);
                onMessageSent();
            }else
            {
                Toast.makeText(getBaseContext(), getString(R.string.emptyMessage), Toast.LENGTH_SHORT).show();
            }
        }else
        {
            manager.enableRuntimePermission(this, AppSmsManager.REQUEST_PERMISSION_CODE);
        }

    }


    private void createListener()
    {
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.chatSendSms:
                        sendSms();
                        break;
                }
            }
        };
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case AppSmsManager.REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                   sendSms();
                }else
                    finish();
                break;
        }
    }


    private void createReceiver()
    {
        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(""), 0);


        this.registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context arg0, Intent arg1)
            {
                if(getResultCode() == Activity.RESULT_OK)
                {
                    messageDelivered();
                }
                else
                {
                    Toast.makeText(getBaseContext(), getString(R.string.smsCouldNotSent), Toast.LENGTH_SHORT).show();
                }
            }
        }, new IntentFilter(""));
    }

    private void onMessageSent()
    {
       text.getText().clear();
    }

    private void messageDelivered()
    {
        Toast.makeText(getBaseContext(), getString(R.string.message_delivered), Toast.LENGTH_SHORT).show();
        refreshChat();
    }


    public void refreshChat()
    {

       new SmsDao().getAllNewMessages(this, Collections.singletonList(currentContact));
               Collections.sort(currentContact.getSms(), new Comparator<Sms>() {
            @Override
            public int compare(Sms o1, Sms o2)
            {
                return (Long.compare(o1.getCreationTime(), o2.getCreationTime()));
            }
        });

        if (adapter == null)
        {
            adapter = new SmsChatAdapter(this, currentContact.getSms(), currentContact);
        }
        else
        {
            adapter.clear();
            adapter.addAll(currentContact.getSms());
        }
    }

}
