package com.example.techtik.cuttoff.Fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.techtik.cuttoff.Adapters.ContactsListAdapter;
import com.example.techtik.cuttoff.Adapters.HorizontalScrollContactsAdapter;
import com.example.techtik.cuttoff.Models.Contact;
import com.example.techtik.cuttoff.R;
import com.example.techtik.cuttoff.databinding.FragmentContactListBinding;
import com.example.techtik.cuttoff.viewmodel.ContactsFragmentViewModel;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;


public class ContactListFragment extends Fragment implements
        DiscreteScrollView.OnItemChangedListener<HorizontalScrollContactsAdapter.ContactViewHolder>{

    private FragmentContactListBinding contactListBinding;

    private ContactsListAdapter contactsListAdapter;
    private ContactsFragmentViewModel contactsFragmentViewModel;

    private HorizontalScrollContactsAdapter scrollContactsAdapter;

    private List<Contact> tempLatestList=new ArrayList<>();

    private final static String phoneNumberRegex = "^[\\d*#+]+$";
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
                    //contactListBinding.contactPicker.setAdapter(new HorizontalScrollContactsAdapter(getContext(), (ArrayList<Contact>) contacts));
                }
            }
        });

//        contactListBinding.searchContactTxt.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //implement the seaching contact
//            }
//        });

        return view;
    }

    private void init(){

        //initialize recycler view
        contactsListAdapter=new ContactsListAdapter(getContext(), (ArrayList<Contact>) contactsFragmentViewModel.contactList.getValue());
        contactListBinding.contactRv.setLayoutManager(new LinearLayoutManager(getContext()));
        contactListBinding.contactRv.setAdapter(contactsListAdapter);
        contactListBinding.contactRv.setHasFixedSize(true);

        //initialize horizontal scroll view
//        scrollContactsAdapter=new HorizontalScrollContactsAdapter(getContext(), (ArrayList<Contact>) contactsFragmentViewModel.contactList.getValue());
//        contactListBinding.contactPicker.setAdapter(scrollContactsAdapter);


        tempLatestList.add(new Contact("wasif","454535",Uri.parse("android.resource://com.example.techtik.cuttoff/drawable/avatar1").toString()));
        tempLatestList.add(new Contact("wasi","454535",Uri.parse("android.resource://com.example.techtik.cuttoff/drawable/avatar1").toString()));
        tempLatestList.add(new Contact("wasf","454535",Uri.parse("android.resource://com.example.techtik.cuttoff/drawable/avatar1").toString()));
        tempLatestList.add(new Contact("wsif","454535",Uri.parse("android.resource://com.example.techtik.cuttoff/drawable/avatar1").toString()));
        tempLatestList.add(new Contact("f","454535",Uri.parse("android.resource://com.example.techtik.cuttoff/drawable/avatar1").toString()));
        tempLatestList.add(new Contact("wsif","454535",Uri.parse("android.resource://com.example.techtik.cuttoff/drawable/avatar1").toString()));
        tempLatestList.add(new Contact("asif","454535",Uri.parse("android.resource://com.example.techtik.cuttoff/drawable/avatar1").toString()));
        tempLatestList.add(new Contact("wif","454535",Uri.parse("android.resource://com.example.techtik.cuttoff/drawable/avatar1").toString()));
        tempLatestList.add(new Contact("wasf","454535",Uri.parse("android.resource://com.example.techtik.cuttoff/drawable/avatar1").toString()));


        scrollContactsAdapter=new HorizontalScrollContactsAdapter(getContext(), (ArrayList<Contact>) tempLatestList);
        contactListBinding.contactPicker.setSlideOnFling(true);
//        contactListBinding.contactPicker.setOffscreenItems(2);
        contactListBinding.contactPicker.setItemTransitionTimeMillis(500);
        contactListBinding.contactPicker.setAdapter(scrollContactsAdapter);
        contactListBinding.contactPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        contactListBinding.contactPicker.addOnItemChangedListener(this);
    }

    @Override
    public void onCurrentItemChanged(@Nullable HorizontalScrollContactsAdapter.ContactViewHolder viewHolder, int position) {
        //setting the current selected contact name
        ArrayList<Contact> contactArrayList= scrollContactsAdapter.getArrayList();
        if(contactArrayList!=null && contactArrayList.size()>0) {
            String currentContactName = contactArrayList.get(position).getName();

            if(!currentContactName.replaceAll("\\s+","").matches(phoneNumberRegex)){
                //not a number
                contactListBinding.scrollContactName.setText(currentContactName);
            }

        }
    }

}
