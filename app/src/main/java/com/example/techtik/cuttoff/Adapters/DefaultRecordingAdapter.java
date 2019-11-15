package com.example.techtik.cuttoff.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.techtik.cuttoff.R;
import com.example.techtik.cuttoff.database.entity.DefaultRecordings;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DefaultRecordingAdapter extends RecyclerView.Adapter<DefaultRecordingAdapter.RecordingsHolder> {

    private Context mContext;
    private List<DefaultRecordings> defaultRecordingsList;

    public DefaultRecordingAdapter(Context mContext, List<DefaultRecordings> defaultRecordingsList) {
        this.mContext = mContext;
        this.defaultRecordingsList = defaultRecordingsList;
    }

    @NonNull
    @Override
    public RecordingsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.row_item_recordings,parent,false);

        return new RecordingsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingsHolder holder, int position) {
        holder.message.setText(defaultRecordingsList.get(position).getmCustomMessage());
    }

    @Override
    public int getItemCount() {
        if(defaultRecordingsList==null ||defaultRecordingsList.isEmpty() )
            return 0;
        return defaultRecordingsList.size();
    }


    public class RecordingsHolder extends RecyclerView.ViewHolder{

        TextView message;
        public RecordingsHolder(@NonNull View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.recording_txt);
        }
    }

    public void setDefaultRecordingsList(List<DefaultRecordings> defaultRecordingsList) {
        this.defaultRecordingsList = defaultRecordingsList;
        notifyDataSetChanged();
    }
}
