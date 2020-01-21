package com.example.techtik.cuttoff.Adapters;

import android.content.Context;
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
import com.example.techtik.cuttoff.Models.RecentCall;
import com.example.techtik.cuttoff.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalScrollContactsAdapter extends RecyclerView.Adapter<HorizontalScrollContactsAdapter.ContactViewHolder> {

    private Context mContext;
    private ArrayList<RecentCall> arrayList;
    public final static String phoneNumberRegex = "^[\\d*#+]+$";

    public HorizontalScrollContactsAdapter(Context mContext, ArrayList<RecentCall> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }



    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.contact_scroll_rv,parent,false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        String photoUri=arrayList.get(position).getCallerPhoto();

        if(TextUtils.isEmpty(photoUri)){
            //set placeholder avatar
            Glide.with(mContext).load(R.drawable.ic_user).skipMemoryCache(true)
                    .apply(RequestOptions.circleCropTransform()).into(holder.contact_circle_img);
        }
        else {
            //set image
            Glide.with(mContext).load(photoUri).skipMemoryCache(true)
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

        //TextView contactName;
        ImageView contact_circle_img;
        //TODO apply two way data binding

        public ContactViewHolder(View itemView) {
            super(itemView);
            //contactName=itemView.findViewById(R.id.contact_name);
            contact_circle_img=itemView.findViewById(R.id.scroll_contact_img);
        }
    }

    //Methods
    public void setArrayList(ArrayList<RecentCall> arrayList) {
        this.arrayList = arrayList;
    }

    public ArrayList<RecentCall> getArrayList() {
        return arrayList;
    }

}
