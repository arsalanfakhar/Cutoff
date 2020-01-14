package com.example.techtik.cuttoff.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.techtik.cuttoff.Adapters.listeners.OnItemLongClickListener;
import com.example.techtik.cuttoff.R;
import com.example.techtik.cuttoff.database.entity.DefaultRecordings;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class DefaultRecordingAdapter extends RecyclerView.Adapter<DefaultRecordingAdapter.RecordingsHolder> {

    private Context mContext;
    private List<DefaultRecordings> defaultRecordingsList;
    private OnItemLongClickListener mOnItemLongClickListener;

    public DefaultRecordingAdapter(Context mContext, List<DefaultRecordings> defaultRecordingsList,
                                   OnItemLongClickListener onItemLongClickListener) {
        this.mContext = mContext;
        this.defaultRecordingsList = defaultRecordingsList;
        this.mOnItemLongClickListener=onItemLongClickListener;



    }


    @NonNull
    @Override
    public RecordingsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.row_item_recordings_default,parent,false);


        return new RecordingsHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecordingsHolder holder, int position) {
        //Check and set background color
        if(defaultRecordingsList.get(position).getId()==getPrefvalue()){
            holder.cardView.setCardBackgroundColor(Color.GREEN);
        }
        else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }


        //Set message
        holder.message.setText(defaultRecordingsList.get(position).getmCustomMessage());

        // Set  long click listeners
        if(mOnItemLongClickListener!=null){
            holder.itemView.setOnLongClickListener(v -> {
                mOnItemLongClickListener.onItemLongClick(holder,defaultRecordingsList.get(position));
                return true;
            });

        }

    }


    @Override
    public int getItemCount() {
        if(defaultRecordingsList==null ||defaultRecordingsList.isEmpty() )
            return 0;
        return defaultRecordingsList.size();
    }


    public class RecordingsHolder extends RecyclerView.ViewHolder{

        TextView message;
        CardView cardView;
        public RecordingsHolder(@NonNull View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.recording_txt);

            cardView=itemView.findViewById(R.id.card_default);
        }
    }

    public void setDefaultRecordingsList(List<DefaultRecordings> defaultRecordingsList) {
        this.defaultRecordingsList = defaultRecordingsList;
        notifyDataSetChanged();
    }

    public long getPrefvalue(){
        return this.mContext.getApplicationContext().getSharedPreferences("MyPref", 0).getLong("active_recording_id",1);
    }
}
