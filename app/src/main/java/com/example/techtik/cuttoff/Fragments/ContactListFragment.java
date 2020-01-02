package com.example.techtik.cuttoff.Fragments;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.techtik.cuttoff.Adapters.ContactsListAdapter;
import com.example.techtik.cuttoff.Models.Contact;
import com.example.techtik.cuttoff.R;
import com.example.techtik.cuttoff.databinding.FragmentContactListBinding;
import com.example.techtik.cuttoff.viewmodel.ContactsFragmentViewModel;

import java.util.ArrayList;
import java.util.List;


public class ContactListFragment extends Fragment {

    private FragmentContactListBinding contactListBinding;

    private ArrayList<Contact> contactList;

    private ContactsListAdapter contactsListAdapter;
    private ContactsFragmentViewModel contactsFragmentViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactListBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_contact_list,container,false);
        View view=contactListBinding.getRoot();
        contactsFragmentViewModel= ViewModelProviders.of(this).get(ContactsFragmentViewModel.class);

        init();

        contactsFragmentViewModel.contactList.observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                if(contacts!=null){
                    contactListBinding.contactRv.setAdapter(new ContactsListAdapter(getContext(), (ArrayList<Contact>) contacts));
                }
            }
        });

        contactListBinding.searchContactTxt.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement the seaching contact
            }
        });

        return view;
    }

    private void init(){

        //initialize recycler view
        contactList =new ArrayList<>();
        contactsListAdapter=new ContactsListAdapter(getContext(), (ArrayList<Contact>) contactsFragmentViewModel.contactList.getValue());
        contactListBinding.contactRv.setLayoutManager(new LinearLayoutManager(getContext()));
        contactListBinding.contactRv.setAdapter(contactsListAdapter);
        contactListBinding.contactRv.setItemAnimator(new DefaultItemAnimator());
    }


}
