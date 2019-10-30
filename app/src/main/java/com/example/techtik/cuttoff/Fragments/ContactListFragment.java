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


//        new LoadContactAsync().execute();

        contactsFragmentViewModel.contactList.observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                if(contacts!=null){

                    contactListBinding.contactRv.setAdapter(new ContactsListAdapter(getContext(), (ArrayList<Contact>) contacts));

                }

            }
        });


        return view;
    }

    private void init(){
        contactList =new ArrayList<>();
        contactsListAdapter=new ContactsListAdapter(getContext(), (ArrayList<Contact>) contactsFragmentViewModel.contactList.getValue());
        contactListBinding.contactRv.setLayoutManager(new LinearLayoutManager(getContext()));
        contactListBinding.contactRv.setAdapter(contactsListAdapter);
        contactListBinding.contactRv.setItemAnimator(new DefaultItemAnimator());
    }


    public class LoadContactAsync extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            LoadContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contactsListAdapter.notifyDataSetChanged();

        }
    }


    public void LoadContacts(){

        StringBuilder builder=new StringBuilder();

        Cursor cursor= getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,null,null,ContactsContract.Data.DISPLAY_NAME);


        if(cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {
                    Cursor cursor1 = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ "= ?",
                            new String[]{id}, null);

                    while (cursor1.moveToNext()){
                        String phoneNum=cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //contactList.add("Contact:"+name+"\nPhoneNum:"+phoneNum+"\n");

                    }
                    cursor1.close();
                }
            }

        }
        cursor.close();

        }

}
