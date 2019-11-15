package com.example.techtik.cuttoff.viewmodel;

import android.app.Application;

import com.example.techtik.cuttoff.database.entity.CustomRecordings;
import com.example.techtik.cuttoff.database.entity.DefaultRecordings;
import com.example.techtik.cuttoff.repository.CutoffRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ComfortFragmentViewModel extends AndroidViewModel {

    private CutoffRepository cutoffRepository;
    private LiveData<List<DefaultRecordings>> listdefaultRecording;
    private LiveData<List<CustomRecordings>> listcustomRecording;

    public ComfortFragmentViewModel(@NonNull Application application) {
        super(application);
        cutoffRepository=new CutoffRepository(application);

        listdefaultRecording=cutoffRepository.getAllDefaultRecordings();

        listcustomRecording=cutoffRepository.getAllCustomRecordings();

    }

    public LiveData<List<DefaultRecordings>> getListdefaultRecording() {
        return listdefaultRecording;
    }

    public LiveData<List<CustomRecordings>> getListcustomRecording() {
        return listcustomRecording;
    }

    public void addRecording(CustomRecordings customRecordings){cutoffRepository.addRecording(customRecordings);}

    public void updateRecording(CustomRecordings customRecordings){cutoffRepository.updateRecording(customRecordings);}

    public void removeRecording(CustomRecordings customRecordings){cutoffRepository.deleteRecording(customRecordings);}

}
