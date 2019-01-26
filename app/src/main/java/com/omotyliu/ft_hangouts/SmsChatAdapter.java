package com.omotyliu.ft_hangouts;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.omotyliu.ft_hangouts.core.Contact;
import com.omotyliu.ft_hangouts.core.Sms;

import java.util.List;

public class SmsChatAdapter extends ArrayAdapter<Sms>
{
        private final List<Sms> list;

        private Contact contact;

        private final Activity context;


        public SmsChatAdapter(Activity context, List<Sms> list, Contact contact)
        {
            super(context, R.layout.activity_chat, list);
            this.context = context;
            this.list = list;
            this.contact = contact;
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            Sms sms = list.get(position);

                LayoutInflater inflator = context.getLayoutInflater();
                if (sms.getAuthor() == Sms.USER_ID)
                {
                    convertView = inflator.inflate(R.layout.sms_row, parent, false);
                }
                else
                {
                    convertView = inflator.inflate(R.layout.sms_row_left, parent, false);
                }

                holder = new ViewHolder(convertView);
                convertView.setTag(holder);


            holder.text.setText(sms.getMessage());
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public Sms getItem(int position)
        {
            return list.get(position);
        }


        @Override
        public int getCount() {
            return list.size();
        }

        private class ViewHolder
        {

            TextView text;
            ImageView photo;

            ViewHolder(View view)
            {
                text = view.findViewById(R.id.smsText);
            }
        }


}
