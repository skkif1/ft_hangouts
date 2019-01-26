package com.omotyliu.ft_hangouts.core;

import java.util.*;

public interface ContactDao
{
    List<Contact> getAllContacts();

    int addContact(Contact contact);

    void addAllContacts(List<Contact> contact);


    void updateContact(Contact contact);

    public Contact getContact(int id);

    public Contact getContactByNumber(String id);

    void removeContact(int id);


}
