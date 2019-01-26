package com.omotyliu.ft_hangouts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.omotyliu.ft_hangouts.core.Contact;
import com.omotyliu.ft_hangouts.core.ContactDao;
import com.omotyliu.ft_hangouts.core.ContactDb;
import com.omotyliu.ft_hangouts.core.ContactListImporter;
import com.omotyliu.ft_hangouts.core.ThemeEditor;
import com.omotyliu.ft_hangouts.core.sms.SmsDao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;


// contact list
public class ContactActivity extends AppCompatActivity {

    private View.OnClickListener listener;

    private static final int RequestPermissionCode = 1;

    private ContactDao dao;

    private View importText;

    private ContactListAdapter adapter;

    private Button addContactButton;

    private EditText searchContactBar;


    private ListView.OnItemClickListener itemListener =
            new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Contact contact = (Contact) parent.getItemAtPosition(position);
                    Intent intent = new Intent(getApplicationContext(), ContactItemActivity.class);
                    intent.putExtra(ContactItemActivity.CONTACT_ID, contact.getId());
                    getApplicationContext().startActivity(intent);
                }
            };

    @Override
    public void onResume() {
        super.onResume();
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().get("time") != null && ThemeEditor.isTimeFlagEnabled(this)) {
                long last = Long.parseLong((String) Objects.requireNonNull(getIntent().getExtras().get("time")));
                last = System.currentTimeMillis() - last;
                DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String dateFormatted = formatter.format(last);
                Toast.makeText(this, getString(R.string.background) + " " + dateFormatted, Toast.LENGTH_LONG).show();
            }
        }

        addContactButton = findViewById(R.id.addContactButton);
        addContactButton.setOnClickListener(listener);
        drowList();
        refreshButtons();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeEditor.getTheme(this));
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_contact);
        createListener();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        addContactButton = findViewById(R.id.addContactButton);
        addContactButton.setOnClickListener(listener);
        searchContactBar = findViewById(R.id.contactSearchBar);
        searchContactBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                addContactButton.setVisibility(View.GONE);
                addContactButton.invalidate();
            }
        });
        createList();
        refreshButtons();
    }


    private void refreshButtons()
    {
        int count = adapter == null ? 0 : adapter.getCount();
        showSearchBar(count);
        addContactButton.setVisibility(View.VISIBLE);
    }

    private void createList()
    {

        if (!checkPermissions())
            enablePermissions();
        else
        {
           drowList();
        }


    }


    private void drowList()
    {
        dao = ContactDb.getInst(getApplicationContext());
        List<Contact> contacts = dao.getAllContacts();

        if (contacts.isEmpty())
        {
            noContactsAction();
            if (adapter != null)
                adapter.clear();
        }
        else {
            createList(contacts);
            importText = findViewById(R.id.emptyContactListNotification);
            importText.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.importContacts:
                importList();
                createList();
                break;
            case R.id.darkTheme:
                ThemeEditor.setDark(this);
                recreate();
                break;
            case R.id.lightTheme:
                ThemeEditor.setLight(this);
                recreate();
                break;
            case R.id.backgroundTime:
                ThemeEditor.saveTimeFlag(this, true);
                break;
            case R.id.backgroundOff:
                ThemeEditor.saveTimeFlag(this, false);
                break;
                case R.id.delleteAllContacts:
               deleteAllContact();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void enablePermissions()
    {
        ContactListImporter importer = new ContactListImporter(this);
        importer.enableRuntimePermission(RequestPermissionCode);
    }

    private boolean checkPermissions()
    {
        ContactListImporter importer = new ContactListImporter(this);
        return importer.havePermission();
    }

    // create vie list with contacts
    private void createList(List<Contact> contacts)
    {
        SmsDao dao = new SmsDao();
        dao.getAllNewMessages(this, contacts);
        ListView contactList = (ListView) findViewById(R.id.contactsList);
        contactList.setOnItemClickListener(itemListener);
        adapter = new ContactListAdapter(this, contacts);
        EditText text = findViewById(R.id.contactSearchBar);
        addTextListener(text);
        contactList.setAdapter(adapter);
        contactList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(searchContactBar.getWindowToken(), 0);
                searchContactBar.clearFocus();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount >= totalItemCount - 2 && totalItemCount > 10) {
                    addContactButton.setVisibility(View.GONE);
                } else {
                    addContactButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void noContactsAction() {
        importText = findViewById(R.id.emptyContactListNotification);
        importText.setVisibility(View.VISIBLE);
        addContactButton.setVisibility(View.VISIBLE );
    }


    public void createListener() {

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.addContactButton:
                        Intent intent = new Intent(ContactActivity.this, AddContactActivity.class);
                        startActivity(intent);
                        break;

                }
            }
        };
    }

    private void importList() {
        if (dao.getAllContacts().isEmpty()) {

            ContactListImporter importer = new ContactListImporter(this);
            List<Contact> contacts = importer.getPhoneBookList();
            dao.addAllContacts(contacts);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                boolean enable = grantResults.length == 6 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[4] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[5] == PackageManager.PERMISSION_GRANTED;
                if (!enable)
                {
                    Toast.makeText(this, getString(R.string.permissions), Toast.LENGTH_SHORT).show();
                    finish();
                }else
                {
                    createList();
                }
                break;
        }
    }

    private void addTextListener(EditText text) {

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    disableSearchBar();
                    addContactButton.setVisibility(View.VISIBLE);
                }
                adapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        Bundle bundle = new Bundle();
        bundle.putString("time", String.valueOf(System.currentTimeMillis()));
        getIntent().putExtras(bundle);
    }

    private void showSearchBar(int contactsSize) {
        if (contactsSize < 10) {
            searchContactBar.setVisibility(View.GONE);
        } else {
            searchContactBar.setVisibility(View.VISIBLE);
        }

    }


    private void disableSearchBar() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchContactBar.getWindowToken(), 0);
        searchContactBar.clearFocus();
    }

    private void deleteAllContact()
    {
        for (Contact contact : dao.getAllContacts())
        {
            dao.removeContact(contact.getId());
        }
        createList(dao.getAllContacts());
    }


    // Runtime sms display
    // Sorting
    // Photo
    // display sms status (sms sent
    // import

}

// TODO: • The application works in portrait and landscape mode.
//TODO: • On receipt of an SMS, a contact with the number in name is directly created


