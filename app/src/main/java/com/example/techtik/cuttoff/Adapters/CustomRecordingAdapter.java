package com.example.techtik.cuttoff.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.techtik.cuttoff.R;
import com.example.techtik.cuttoff.Util.database.entity.CustomRecordings;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomRecordingAdapter extends RecyclerView.Adapter<CustomRecordingAdapter.RecordingsHolder> {

    private Context mContext;
    private List<CustomRecordings> customRecordingsList;


    public CustomRecordingAdapter(Context mContext, List<CustomRecordings> customRecordingsList) {
        this.mContext = mContext;
        this.customRecordingsList = customRecordingsList;
    }


    @NonNull
    @Override
    public RecordingsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.row_item_recordings,parent,false);

        return new RecordingsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingsHolder holder, int position) {
        holder.message.setText(customRecordingsList.get(position).getmCustomMessage());
    }

    @Override
    public int getItemCount() {
        //at initial it will be empty
        if( customRecordingsList==null||customRecordingsList.isEmpty())
            return 0;
        return customRecordingsList.size();
    }

    public class RecordingsHolder extends RecyclerView.ViewHolder{

        TextView message;
        public RecordingsHolder(@NonNull View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.recording_txt);
        }
    }

    public void setCustomRecordingsList(List<CustomRecordings> customRecordingsList) {
        this.customRecordingsList = customRecordingsList;
        notifyDataSetChanged();
    }

    public List<CustomRecordings> getCustomRecordingsList() {
        return customRecordingsList;
    }
}
