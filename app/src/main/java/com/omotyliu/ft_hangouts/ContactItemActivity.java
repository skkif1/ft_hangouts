package com.omotyliu.ft_hangouts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.omotyliu.ft_hangouts.core.Contact;
import com.omotyliu.ft_hangouts.core.ContactDao;
import com.omotyliu.ft_hangouts.core.ContactDb;
import com.omotyliu.ft_hangouts.core.ThemeEditor;

// contact list item view
public class ContactItemActivity extends AppCompatActivity
{


    private View.OnClickListener listener;

    public static final String CONTACT_ID  = "contactId";

    private ContactDao  dao;

    private int contactId;

    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(ThemeEditor.getTheme(this));
        super.onCreate(savedInstanceState);
        dao = ContactDb.getInst(getApplicationContext());
        setContentView(R.layout.activity_contact_item);
        Bundle extra = getIntent().getExtras();
        contactId  = (int) extra.get(CONTACT_ID);
        contact = dao.getContact(contactId);
        TextView fullName = findViewById(R.id.contactItemFullName);
        TextView phone = findViewById(R.id.contactItemPhoneNumber);
        ImageView image = findViewById(R.id.contactItemImage);
        fullName.setText(contact.getFullName());
        phone.setText(contact.getNumber());
        if (contact.getPhotoUri() != null)
        {
            image.setImageURI(Uri.parse(contact.getPhotoUri()));
        }
        createListener();
        findViewById(R.id.contactItemDeleteContactButton).setOnClickListener(listener);
        findViewById(R.id.contactItemEditContactButton).setOnClickListener(listener);
        findViewById(R.id.sendMessageButton).setOnClickListener(listener);
    }



    public void createListener() {

        listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.contactItemDeleteContactButton:
                        deleteContact();
                        break;

                    case R.id.contactItemEditContactButton:
                        editContact();
                        break;
                    case R.id.sendMessageButton:
                        sendMessage();
                        break;
                }
            }
        };
    }

    private void sendMessage()
    {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(CONTACT_ID, contactId);
        startActivity(intent);
        finish();
    }

    private void editContact()
    {
        Intent intent = new Intent(this, AddContactActivity.class);
        intent.putExtra(AddContactActivity.EDIT_COMMAND, contact.getId());
        startActivity(intent);
        finish();
    }

    private void deleteContact()
    {
        dao.removeContact(contactId);
        Toast.makeText(getApplicationContext(), getString(R.string.removeContact), Toast.LENGTH_SHORT).show();
        finish();
    }
}
