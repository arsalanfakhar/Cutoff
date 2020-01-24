package com.example.techtik.cuttoff.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.techtik.cuttoff.Adapters.listeners.OnItemClickListener;
import com.example.techtik.cuttoff.Adapters.listeners.OnItemLongClickListener;
import com.example.techtik.cuttoff.Models.RecentCall;
import com.example.techtik.cuttoff.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecentsAdapter extends AbsFastScrollerAdapter<RecentsAdapter.RecentCallHolder> {


    // Click listeners
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    //counter for call times
    private static int called_counter;

    /**
     * Constructor
     *
     * @param context
     * @param cursor
     * @param onItemClickListener
     * @param onItemLongClickListener
     */

    public RecentsAdapter(Context context,
                          Cursor cursor,
                          OnItemClickListener onItemClickListener,
                          OnItemLongClickListener onItemLongClickListener) {
        super(context, cursor);
        mOnItemClickListener = onItemClickListener;
        mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public String getHeaderString(int position) {
        return null;
    }

    @Override
    public void refreshHeaders() {

    }

    @Override
    public void onBindViewHolder(RecentCallHolder holder, Cursor cursor) {

        // Get information
        RecentCall recentCall = new RecentCall(this.mContext, cursor);
        String callerName = recentCall.getCallerName();
        String phoneNumber = recentCall.getCallerNumber();
        String date = recentCall.getCallDateString();
        String time=recentCall.getCallTime();
        String contact_image=recentCall.getCallerPhoto();

        //Set information
        if (callerName != null) {
            holder.contactName.setText(callerName);
            holder.contactPhone.setText(phoneNumber);
        } else
            holder.contactName.setText(phoneNumber);

        String dateNTime=date+"\n"+time;

        holder.time.setText(dateNTime);


        //set caller photo
        if(TextUtils.isEmpty(contact_image)){
            Glide.with(mContext).load(R.drawable.ic_user).skipMemoryCache(true)
                    .apply(RequestOptions.circleCropTransform()).into(holder.contact_circle_img);
        }
        else {
            Glide.with(mContext).load(contact_image).skipMemoryCache(true)
                    .apply(RequestOptions.circleCropTransform()).into(holder.contact_circle_img);
        }

        //TODO set the call type for image and also the click listeners
        switch (recentCall.getCallType()){
            case RecentCall.mIncomingCall:
                holder.status.setImageResource(R.drawable.greenicon);
                break;
            case RecentCall.mOutgoingCall:
                holder.status.setImageResource(R.drawable.outgoing);

                break;
            case RecentCall.mMissedCall:
                holder.status.setImageResource(R.drawable.missed);
                break;
            default:
                break;
        }


    }

    @NonNull
    @Override
    public RecentCallHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.call_logs_rv, parent, false);
        return new RecentCallHolder(v);
    }

    class RecentCallHolder extends RecyclerView.ViewHolder {

        TextView contactName, contactPhone, time;
        ImageView contact_circle_img, status;


        public RecentCallHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.log_contact_name);
            contactPhone = itemView.findViewById(R.id.log_contact_number);
            contact_circle_img = itemView.findViewById(R.id.log_contact_img);
            time = itemView.findViewById(R.id.log_contact_time);
            status = itemView.findViewById(R.id.log_state_img);
        }
    }
}
