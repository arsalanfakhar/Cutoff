package com.example.techtik.cuttoff.Adapters;


import android.content.Context;
import android.database.Cursor;
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
import com.example.techtik.cuttoff.Adapters.listeners.OnItemClickListener;
import com.example.techtik.cuttoff.Models.Contact;
import com.example.techtik.cuttoff.R;
import com.example.techtik.cuttoff.Util.Utilities;
import com.l4digital.fastscroll.FastScroller;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsListAdapter extends AbsFastScrollerAdapter<ContactsListAdapter.ContactViewHolder> implements FastScroller.SectionIndexer {

    private final String phoneNumberRegex = "^[\\d*#+]+$";
    // Click listeners
    private OnItemClickListener mOnItemClickListener;
    private String contactTitle;

    /**
     * Constructor
     *
     * @param context
     * @param cursor
     */
    public ContactsListAdapter(Context context, Cursor cursor,
                               OnItemClickListener onItemClickListener) {
        super(context, cursor);
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.contact_item_rv,parent,false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, Cursor cursor) {

        // Display the contact's information
        Contact contact = new Contact(cursor);
        String contactName=contact.getName();
        String contactNumber=contact.getPhones().get(0);
        String formattedNumber = Utilities.formatPhoneNumber(contactNumber);
        String contact_image=contact.getPhotoUri();

        holder.contactName.setText(contactName);
        holder.contactPhone.setText(formattedNumber);

        //For custom image
        if(TextUtils.isEmpty(contact_image)){
            //For default image
            //fist remove whitespaces and then match with regex
            if(!contactName.replaceAll("\\s+","").matches(HorizontalScrollContactsAdapter.phoneNumberRegex)) {

                //to generate random colors
                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT


                //not a number in contact name
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(contactName.substring(0,2), generator.getRandomColor());

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

        // Set click listeners
        if(mOnItemClickListener!=null){
            holder.contactStatusBtn.setOnClickListener(v -> mOnItemClickListener.onItemClick(holder,contact));
        }

        //Set contact tile
        this.contactTitle=contactName.substring(0,1);
    }




    @Override
    public String getHeaderString(int position) {
        return null;
    }

    @Override
    public void refreshHeaders() {

    }

    public class ContactViewHolder extends RecyclerView.ViewHolder{

        TextView contactName,contactPhone;
        ImageView contact_circle_img,contactStatusBtn;
        //TODO apply two way data binding

        public ContactViewHolder(View itemView) {
            super(itemView);
            contactName=itemView.findViewById(R.id.contact_name);
            contactPhone=itemView.findViewById(R.id.contact_number);
            contact_circle_img=itemView.findViewById(R.id.contact_img);
            contactStatusBtn=itemView.findViewById(R.id.status_btn);

//            contactStatusBtn.setOnClickListener(v -> {
//
//            });
        }

    }

//    @Override
//    public String getSectionTitle(int position) {
//        return contactTitle.toUpperCase();
//    }

    @Override
    public CharSequence getSectionText(int position) {
        return contactTitle.toUpperCase();
    }
}
