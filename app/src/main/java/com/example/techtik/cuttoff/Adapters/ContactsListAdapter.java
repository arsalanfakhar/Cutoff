package com.example.techtik.cuttoff.Adapters;


import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.techtik.cuttoff.Models.Contact;
import com.example.techtik.cuttoff.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ContactViewHolder> {

    private final String phoneNumberRegex = "^[\\d*#+]+$";
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
        String contactName=arrayList.get(position).getName();
        holder.contactName.setText(contactName);
        holder.contactPhone.setText(arrayList.get(position).getPhones().get(0));

        //For custom image
        String contact_image=arrayList.get(position).getPhotoUri();
        if(TextUtils.isEmpty(contact_image)){
            //For default image
            //fist remove whitespaces and then match with regex
            if(!contactName.replaceAll("\\s+","").matches(phoneNumberRegex)) {

                //to generate random colors
                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT


                //not a number in contact name
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(contactName.substring(0,2), generator.getRandomColor());

//                Glide.with(mContext).load(drawable).skipMemoryCache(true)
//                        .into(holder.contact_circle_img);
                holder.contact_circle_img.setImageDrawable(drawable);
            }
            else {
                //a number in contact name
                Glide.with(mContext).load(R.drawable.ic_user).skipMemoryCache(true)
                        .apply(RequestOptions.circleCropTransform()).into(holder.contact_circle_img);
            }
//            TextDrawable drawable = TextDrawable.builder()
//                        .buildRect("AA", Color.RED);
//            holder.contact_circle_img.setImageDrawable(drawable);
        }
        else {
            //For custom image
            Glide.with(mContext).load(contact_image).skipMemoryCache(true)
                    .apply(RequestOptions.circleCropTransform()).into(holder.contact_circle_img);
        }
    }

    @Override
    public int getItemCount() {
        if(arrayList!=null && arrayList.size()>0)
            return arrayList.size();
        return 0;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder{

        TextView contactName,contactPhone;
        ImageView contact_circle_img;
        //TODO apply two way data binding

        public ContactViewHolder(View itemView) {
            super(itemView);
            contactName=itemView.findViewById(R.id.contact_name);
            contactPhone=itemView.findViewById(R.id.contact_number);
            contact_circle_img=itemView.findViewById(R.id.contact_img);
        }
    }
}
