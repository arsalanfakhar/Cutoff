package com.example.techtik.cuttoff.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.techtik.cuttoff.R;

import com.example.techtik.cuttoff.Models.CustomRecordingsModel;

import java.util.ArrayList;

public class RecordingsListAdapter extends ArrayAdapter<CustomRecordingsModel>{

    Context context;
    ArrayList<CustomRecordingsModel> list;
    LayoutInflater inflater;


    public RecordingsListAdapter(@NonNull Context context,  ArrayList<CustomRecordingsModel> list) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_item_recordings, null);

        }

        return convertView;

    }
}
