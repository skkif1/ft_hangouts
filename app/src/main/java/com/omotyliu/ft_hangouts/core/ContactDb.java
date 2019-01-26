package com.omotyliu.ft_hangouts.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.omotyliu.ft_hangouts.core.sms.SmsDao;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ContactDb implements ContactDao{

    private static final String 		CONTACTS =		"contacts";
    private static final String 		ID =			"id";
    private static final String 		PHONE =		    "phone";
    private static final String 		FULL_NAME = 	"full_name";
    private static final String 		PHOTO_URI = 	"photo_uri";
    private static final String 		PHOTO = 	    "photo";

    private static final String 		K_IMAGE =		"image";

    private static final String 		CREATE_TABLE =	"CREATE TABLE IF NOT EXISTS " + CONTACTS + " (" + ID + " INTEGER PRIMARY KEY, " + FULL_NAME + " TEXT, " + PHONE + " TEXT ," +  PHOTO_URI + " TEXT ," + PHOTO + " BLOB " + ")";

    private static final String 		INSERT =        "INSERT INTO " + CONTACTS + " (" + PHONE + ", " + FULL_NAME + "," + PHOTO_URI + ", " + PHOTO + ") VALUES (?,?,?,?)";

    private static final String 		SELECT_ALL =	"SELECT * FROM " + CONTACTS;

    private static final String 		SELECT_BY_ID =	"SELECT * FROM " + CONTACTS + " WHERE " + ID + " = ?";

    private static final String 		UPDATE =	"UPDATE " + CONTACTS + " SET " + FULL_NAME + " = ? ," + PHONE + "= ?," + PHOTO_URI + "=?" + " WHERE " + ID + " = ?";

    private static final String 		DELETE =	"DELETE FROM " + CONTACTS + " WHERE " + ID + " = ?";

    private static final String			DELETE_TABLE =	"DROP TABLE IF EXISTS " + CONTACTS;

    private static SQLiteDatabase       Db;


    private static ContactDb inst;


    private ContactDb(Context context)
    {
        createDB(context);
    }

    public static ContactDb getInst(Context context)
    {
        if (inst == null)
        {
            inst = new ContactDb(context);
            return inst;
        }
        return inst;
    }

    public static void createDB(Context context)
    {
        Db = context.openOrCreateDatabase(CONTACTS, Context.MODE_PRIVATE, null);
        Db.execSQL(CREATE_TABLE);
    }

    public static void deleteContact(String number, Context context)
    {

    }


    @Override
    public List<Contact> getAllContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor c = Db.rawQuery(SELECT_ALL, null);

        while (c.moveToNext())
        {
            String phone = c.getString(c.getColumnIndex(PHONE));
            String name  = c.getString(c.getColumnIndex(FULL_NAME));
            String photoUri  = c.getString(c.getColumnIndex(PHOTO_URI));
            String id  = c.getString(c.getColumnIndex(ID));
            Contact contact = new Contact(name, phone, null);
            contact.setPhotoUri(photoUri);
            contact.setId(Integer.parseInt(id));
            contacts.add(contact);
        }
        c.close();

        return contacts;
    }


    @Override
    public int addContact(Contact contact)
    {
        SQLiteStatement st = Db.compileStatement(INSERT);
        st.bindString(1, contact.getNumber());
        st.bindString(2, contact.getFullName());
        if (contact.getPhotoUri() != null)
        {
            st.bindString(3, contact.getPhotoUri());
        }
        if (contact.getImage() != null)
        {

            st.bindBlob(4, bitMapToArray(contact.getImage()));
        }

        return (int) st.executeInsert();
    }

    @Override
    public void addAllContacts(List<Contact> contact)
    {
        for (Contact contact1 : contact)
        {
             addContact(contact1);
        }
    }


    @Override
    public void updateContact(Contact contact)
    {

        SQLiteStatement statement = Db.compileStatement(UPDATE);
        statement.bindString(1, contact.getFullName());
        statement.bindString(2, contact.getNumber());
        if (contact.getPhotoUri() != null)
        {
            statement.bindString(3, String.valueOf(contact.getPhotoUri()));
        }
        statement.bindString(4, String.valueOf(contact.getId()));

        statement.executeUpdateDelete();
    }

    @Override
    public Contact getContact(int id) {
        Cursor c = Db.rawQuery(SELECT_BY_ID, new String[]{String.valueOf(id)});
        c.moveToFirst();

        if (c.getCount() > 0)
        {
            String phone = c.getString(c.getColumnIndex(PHONE));
            String name  = c.getString(c.getColumnIndex(FULL_NAME));
            String photoUri  = c.getString(c.getColumnIndex(PHOTO_URI));
            byte[] image = c.getBlob(c.getColumnIndex(PHOTO));
            Bitmap photo  = null;
            if (image != null)
            {
                photo  = BitmapFactory.decodeByteArray(image, 0, image.length);
            }
            Contact contact = new Contact(name, phone, null);
            contact.setId(id);
            contact.setPhotoUri(photoUri);
            contact.setImage(photo);
            return contact;
        }

        c.close();
        return null;
    }

    @Override
    public Contact getContactByNumber(String id)
    {
        return null;
    }

    @Override
    public void removeContact(int id) {
        SQLiteStatement statement = Db.compileStatement(DELETE);
        statement.bindString(1, String.valueOf(id));
        statement.executeUpdateDelete();
    }


    private byte[] bitMapToArray(Bitmap bmp)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
