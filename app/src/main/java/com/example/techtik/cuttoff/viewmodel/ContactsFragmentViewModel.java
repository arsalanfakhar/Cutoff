package com.example.techtik.cuttoff.viewmodel;

import android.app.Application;
import com.example.techtik.cuttoff.Models.Contact;
import com.example.techtik.cuttoff.repository.CutoffRepository;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class ContactsFragmentViewModel extends AndroidViewModel {

    private CutoffRepository repository;
    public MutableLiveData<List<Contact>> contactList=new MutableLiveData<>();

    public ContactsFragmentViewModel(@NonNull Application application) {
        super(application);
        repository=new CutoffRepository(application);
        getContacts();


    }

    public void getContacts(){
        Executor executor= Executors.newFixedThreadPool(1);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Contact> cc=repository.fetchContacts();
                contactList.postValue(cc);
            }
        });

    }


}
