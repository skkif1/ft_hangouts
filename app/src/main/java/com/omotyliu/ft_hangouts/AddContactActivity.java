package com.omotyliu.ft_hangouts;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.omotyliu.ft_hangouts.core.Contact;
import com.omotyliu.ft_hangouts.core.ContactDao;
import com.omotyliu.ft_hangouts.core.ContactDb;
import com.omotyliu.ft_hangouts.core.ImageHelper;
import com.omotyliu.ft_hangouts.core.ThemeEditor;


public class AddContactActivity extends AppCompatActivity {

    private View.OnClickListener listener;

    private ContactDao dao;

    private final int PICK_IMAGE_REQUEST = 1;

    private final String CONTACT_IMAGE = "contactImage";

    private ImageView contactImage;

    public static final String EDIT_COMMAND = "edit";

    private boolean inEditMode = false;

    private Contact currentContact;

    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(ThemeEditor.getTheme(this));
        super.onCreate(savedInstanceState);
        dao = ContactDb.getInst(getApplicationContext());
        setContentView(R.layout.activity_add_contact);
        createListener();
        contactImage  = findViewById(R.id.newContactImage);
        findViewById(R.id.saveContact).setOnClickListener(listener);
        findViewById(R.id.newContactImage).setOnClickListener(listener);

        if (getIntent().getExtras() != null && getIntent().getExtras().get(EDIT_COMMAND) != null)
        {
            inEditMode = true;
            editContact();
        }else
        {
            if (savedInstanceState != null && savedInstanceState.getParcelable(CONTACT_IMAGE) != null)
            {
                ImageView imageView = findViewById(R.id.newContactImage);
                imageUri = Uri.parse(savedInstanceState.getString(CONTACT_IMAGE));
                imageView.setImageURI(imageUri);
            }
        }
    }

    private void editContact()
    {
        int id = (int) getIntent().getExtras().get(EDIT_COMMAND);
        currentContact = dao.getContact(id);
        EditText name = findViewById(R.id.addContactNameText);
        EditText number = findViewById(R.id.addContactPhoneText);
        ImageView image = findViewById(R.id.newContactImage);
        Button addButton = findViewById(R.id.saveContact);
        name.setText(currentContact.getFullName());
        number.setText(currentContact.getNumber());
        if (currentContact.getImage() != null)
            image.setImageBitmap(currentContact.getImage());
        addButton.setText(getString(R.string.addContactEditText));
    }


    private void saveContact() {
        if (isValidInput())
        {
            String name = ((EditText) findViewById(R.id.addContactNameText)).getText().toString();
            String phone = ((EditText) findViewById(R.id.addContactPhoneText)).getText().toString();

            if (inEditMode) {
                currentContact.setFullName(name);
                currentContact.setNumber(phone);
                if (imageUri != null)
                currentContact.setPhotoUri(ImageHelper.getPath(this, imageUri));
                dao.updateContact(currentContact);
                Toast.makeText(getApplicationContext(), getString(R.string.contactUpdated), Toast.LENGTH_SHORT).show();
                onSaveContactOk(currentContact);
            } else
                {
                Contact newContact = new Contact(name, phone, null);
                if (imageUri != null)
                newContact.setPhotoUri(ImageHelper.getPath(this, imageUri));
                int id = dao.addContact(newContact);
                newContact.setId(id);
                Toast.makeText(getApplicationContext(), getString(R.string.toast_contact_added), Toast.LENGTH_SHORT).show();
                onSaveContactOk(newContact);
            }
            finish();
        }
    }

    public void createListener() {

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.saveContact:
                        saveContact();

                        break;

                    case R.id.newContactImage:
                        getImage();
                        break;


                }
            }
        };
    }


    private void getImage()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.SelectPicture)), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();;
            contactImage.setImageURI(imageUri);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
        if (contactImage != null) {
            savedInstanceState.putString(CONTACT_IMAGE, contactImage.toString());
        }
    }


    private boolean isValidInput() {
        EditText name = findViewById(R.id.addContactNameText);
        EditText phone = findViewById(R.id.addContactPhoneText);
        String nameText = name.getText().toString();
        String phoneText = phone.getText().toString();

        if (nameText.isEmpty() || phoneText.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_contact_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void onSaveContactOk(Contact contact)
    {
        Intent intent = new Intent(this, ContactItemActivity.class);
        intent.putExtra(ContactItemActivity.CONTACT_ID, contact.getId());
        startActivity(intent);
    }


}
