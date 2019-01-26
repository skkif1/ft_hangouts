package com.omotyliu.ft_hangouts;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import com.omotyliu.ft_hangouts.core.Contact;
import java.util.ArrayList;
import java.util.List;

// contact item in view list
public class ContactListAdapter extends ArrayAdapter<Contact> {


    private List<Contact> list;

    private List<Contact> copy;

    private final Activity context;


    public ContactListAdapter(Activity context, List<Contact> list) {
        super(context, R.layout.contact_view, list);
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflator = context.getLayoutInflater();
        convertView = inflator.inflate(R.layout.contact_view, parent, false);
        holder = new ViewHolder(convertView);
        convertView.setTag(holder);
        Contact contact = list.get(position);
        holder.fullName.setText(contact.getFullName());
        holder.phoneNumber.setText(contact.getNumber());
        if (contact.getNewMessagesCount() > 0)
        {
            TextView count = convertView.findViewById(R.id.newMessagesCount);
            count.setText(String.valueOf(contact.getNewMessagesCount()));
            count.setVisibility(View.VISIBLE);
        }

        if (contact.getPhotoUri() != null)
        {
            holder.imageView.setImageURI(Uri.parse(contact.getPhotoUri()));
        }
        return convertView;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }


    @Override
    public Contact getItem(int position) {
        return list.get(position);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    private class ViewHolder
    {
        ImageView imageView;
        TextView fullName;
        TextView phoneNumber;

        ViewHolder(View view)
        {
            imageView = (ImageView) view.findViewById(R.id.contactImage);
            fullName = (TextView) view.findViewById(R.id.contactName);
            phoneNumber = (TextView) view.findViewById(R.id.contactNumber);
        }
    }

    @Override
    public Filter getFilter()
    {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                list = (ArrayList<Contact>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Contact> filteredArrList = new ArrayList<>();

                if (copy == null)
                {
                    copy = new ArrayList<>(list); // saves the original data in mOriginalValues
                }


                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = copy.size();
                    results.values = copy;
                } else
                    {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < copy.size(); i++)
                    {
                        String data = copy.get(i).getFullName();
                        String phone = copy.get(i).getNumber();
                        if (data.toLowerCase().startsWith(constraint.toString()) || phone.toLowerCase().startsWith(constraint.toString()))
                        {
                            filteredArrList.add(copy.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = filteredArrList.size();
                    results.values = filteredArrList;
                }
                return results;
            }
        };
        return filter;
    }


}
