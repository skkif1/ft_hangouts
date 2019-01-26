package com.omotyliu.ft_hangouts.core;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class PhoneBookBridge
{

    private Activity context;


    public PhoneBookBridge(Activity context)
    {
        this.context = context;
    }

    public void deleteContact(String rawContactId)
    {
        ContentResolver contentResolver = context.getContentResolver();

        //******************************* delete data table related data ****************************************
        // Data table content process uri.
        Uri dataContentUri = ContactsContract.Data.CONTENT_URI;

        // Create data table where clause.
        StringBuffer dataWhereClauseBuf = new StringBuffer();
        dataWhereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
        dataWhereClauseBuf.append(" = ");
        dataWhereClauseBuf.append(rawContactId);
        contentResolver.delete(dataContentUri, dataWhereClauseBuf.toString(), null);


        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

        StringBuffer rawContactWhereClause = new StringBuffer();
        rawContactWhereClause.append(ContactsContract.RawContacts._ID);
        rawContactWhereClause.append(" = ");
        rawContactWhereClause.append(rawContactId);

        contentResolver.delete(rawContactUri, rawContactWhereClause.toString(), null);

        Uri contactUri = ContactsContract.Contacts.CONTENT_URI;

        StringBuffer contactWhereClause = new StringBuffer();
        contactWhereClause.append(ContactsContract.Contacts._ID);
        contactWhereClause.append(" = ");
        contactWhereClause.append(rawContactId);

        contentResolver.delete(contactUri, contactWhereClause.toString(), null);

    }


    public void removeContact(String id)
    {
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.RawContacts.CONTENT_URI, null, null, null, null);
        while (cur.moveToNext())
        {
            try{
                if (cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID)).equals(id))
                {
                    String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                    cr.delete(uri, null, null);
                }
            }
            catch(Exception e)
            {
                System.out.println(e.getStackTrace());
            }
        }
    }

    private long getRawContactIdByName(String givenName, String familyName)
    {
        ContentResolver contentResolver = context.getContentResolver();

        // Query raw_contacts table by display name field ( given_name family_name ) to get raw contact id.

        // Create query column array.
        String queryColumnArr[] = {ContactsContract.RawContacts._ID};

        // Create where condition clause.
        String displayName = givenName;
        String whereClause = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " = '" + displayName + "'";

        // Query raw contact id through RawContacts uri.
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

        // Return the query cursor.
        Cursor cursor = contentResolver.query(rawContactUri, queryColumnArr, whereClause, null, null);

        long rawContactId = -1;

        if(cursor!=null)
        {
            // Get contact count that has same display name, generally it should be one.
            int queryResultCount = cursor.getCount();
            // This check is used to avoid cursor index out of bounds exception. android.database.CursorIndexOutOfBoundsException
            if(queryResultCount > 0)
            {
                // Move to the first row in the result cursor.
                cursor.moveToFirst();
                // Get raw_contact_id.
                rawContactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
            }
        }

        return rawContactId;
    }


}
