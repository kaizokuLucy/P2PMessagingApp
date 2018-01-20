package com.example.lucy.p2pmessagingapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lucy.p2pmessagingapp.Models.Contact;

import java.util.List;

/**
 * Created by Tomislav on 1/13/2018.
 */

public class ContactAdapter extends ArrayAdapter<Contact> {

    Context context;
    int layoutResourceId;
    List<Contact> data = null;

    public ContactAdapter(Context context, int layoutResourceId, List<Contact> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ContactsHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ContactsHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.contactName = (TextView)row.findViewById(R.id.txtTitle);
            holder.contactStatus = (ImageView) row.findViewById(R.id.statusIndicator);

            row.setTag(holder);
        }
        else
        {
            holder = (ContactsHolder)row.getTag();
        }

        Contact contact = data.get(position);
        holder.contactName.setText(contact.getFirstName());
        if(contact.getStatus().endsWith("1")){
            holder.contactStatus.setImageResource(android.R.drawable.presence_online);
        }else{
            holder.contactStatus.setImageResource(android.R.drawable.presence_invisible);
        }

        return row;
    }

    static class ContactsHolder
    {
        ImageView imgIcon;
        TextView contactName;
        ImageView contactStatus;
    }
}
