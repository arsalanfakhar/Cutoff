package com.example.techtik.cuttoff.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.techtik.cuttoff.Models.Contact;
import com.example.techtik.cuttoff.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ContactViewHolder> {

    private Context mContext;
    private ArrayList<Contact> arrayList;

    public ContactsListAdapter(Context mContext, ArrayList<Contact> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.contact_item_rv,parent,false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.contactName.setText(arrayList.get(position).getName());
        holder.contactPhone.setText(arrayList.get(position).getPhones().get(0));
    }

    @Override
    public int getItemCount() {
        if(arrayList!=null && arrayList.size()>0)
            return arrayList.size();
        return 0;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder{

        TextView contactName,contactPhone;
        public ContactViewHolder(View itemView) {
            super(itemView);
            contactName=itemView.findViewById(R.id.contact_name);
            contactPhone=itemView.findViewById(R.id.contact_number);

        }
    }
}
