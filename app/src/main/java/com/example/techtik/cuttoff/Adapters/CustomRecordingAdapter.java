package com.example.techtik.cuttoff.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.techtik.cuttoff.Fragments.ComfortFragment;
import com.example.techtik.cuttoff.R;
import com.example.techtik.cuttoff.database.entity.CustomRecordings;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomRecordingAdapter extends RecyclerView.Adapter<CustomRecordingAdapter.RecordingsHolder> {

    private Context mContext;
    private List<CustomRecordings> customRecordingsList;
    private ComfortFragment fragment;

    public CustomRecordingAdapter(Context mContext, List<CustomRecordings> customRecordingsList,ComfortFragment fragment) {
        this.mContext = mContext;
        this.customRecordingsList = customRecordingsList;
        this.fragment=fragment;
    }


    @NonNull
    @Override
    public RecordingsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.row_item_recordings_custom,parent,false);

        return new RecordingsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingsHolder holder, int position) {
        holder.message.setText(customRecordingsList.get(position).getmCustomMessage());
        String imageUri= customRecordingsList.get(position).getmContact().getPhotoUri();

        Glide.with(mContext).load(imageUri).skipMemoryCache(true)
                .apply(RequestOptions.circleCropTransform()).into(holder.contactImage);

        holder.itemView.setOnClickListener(v -> {
            fragment.makePopupDialog(customRecordingsList.get(position),position);
        });
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
        ImageView contactImage;
        public RecordingsHolder(@NonNull View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.recording_txt);
            contactImage=itemView.findViewById(R.id.recording_img);
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
