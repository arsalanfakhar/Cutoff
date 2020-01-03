package com.example.techtik.cuttoff.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.techtik.cuttoff.Adapters.CustomRecordingAdapter;
import com.example.techtik.cuttoff.Adapters.DefaultRecordingAdapter;
import com.example.techtik.cuttoff.R;
import com.example.techtik.cuttoff.database.entity.CustomRecordings;
import com.example.techtik.cuttoff.databinding.FragmentComfortBinding;
import com.example.techtik.cuttoff.viewmodel.ComfortFragmentViewModel;

import java.util.ArrayList;


public class ComfortFragment extends Fragment {

    private FragmentComfortBinding fragmentComfortBinding;

    //Adapters
    private CustomRecordingAdapter customRecordingAdapter;
    private DefaultRecordingAdapter defaultRecordingAdapter;

    //Viewmodel
    private ComfortFragmentViewModel comfortFragmentViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentComfortBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_comfort,container,false);
        View view=fragmentComfortBinding.getRoot();

        //Intialize viewmodel
        comfortFragmentViewModel= ViewModelProviders.of(this).get(ComfortFragmentViewModel.class);
        fragmentComfortBinding.setLifecycleOwner(this);

        init();
        comfortFragmentViewModel.getListdefaultRecording().observe(this, defaultRecordings -> {
            if(defaultRecordings!=null)
                defaultRecordingAdapter.setDefaultRecordingsList(defaultRecordings);
        });

        comfortFragmentViewModel.getListcustomRecording().observe(this,customRecordings -> {
            if(customRecordings!=null)
                customRecordingAdapter.setCustomRecordingsList(customRecordings);
        });

        //Swipe left to delete recording
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //Write here
                CustomRecordings customRecordings=customRecordingAdapter.getCustomRecordingsList().get(viewHolder.getAdapterPosition());
                comfortFragmentViewModel.removeRecording(customRecordings);
            }
        }).attachToRecyclerView(fragmentComfortBinding.customRecordingsRv);



        return view;
    }

    private void init(){
        //Default recording rv
        defaultRecordingAdapter=new DefaultRecordingAdapter(getContext(),new ArrayList<>());
        fragmentComfortBinding.defaultRecordingsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentComfortBinding.defaultRecordingsRv.setItemAnimator(new DefaultItemAnimator());
        fragmentComfortBinding.defaultRecordingsRv.setAdapter(defaultRecordingAdapter);

        //Custom recording rv
        customRecordingAdapter=new CustomRecordingAdapter(getContext(),new ArrayList<>(),this);
        fragmentComfortBinding.customRecordingsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentComfortBinding.customRecordingsRv.setItemAnimator(new DefaultItemAnimator());
        fragmentComfortBinding.customRecordingsRv.setAdapter(customRecordingAdapter);
    }

    public void makePopupDialog(CustomRecordings recording,int position){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        View view = layoutInflaterAndroid.inflate(R.layout.popup_edit_recording, null);

        AlertDialog.Builder alertBuilder=new AlertDialog.Builder(getContext());
        alertBuilder.setView(view);

        EditText updateMessage=view.findViewById(R.id.update_txt);

        alertBuilder.setCancelable(true)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Update", (dialog, which) -> {

                });

        AlertDialog dialog=alertBuilder.create();
        dialog.show();

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if(TextUtils.isEmpty(updateMessage.getText())){
                Toast.makeText(getContext(),"Enter message",Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                //update here
                recording.setmCustomMessage(updateMessage.getText().toString());
                comfortFragmentViewModel.updateRecording(recording);
                dialog.dismiss();
            }
        });

    }
}
