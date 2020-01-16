package com.example.techtik.cuttoff.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techtik.cuttoff.Adapters.ContactsListAdapter;
import com.example.techtik.cuttoff.Adapters.HorizontalScrollContactsAdapter;
import com.example.techtik.cuttoff.Adapters.listeners.OnItemClickListener;
import com.example.techtik.cuttoff.Models.Contact;
import com.example.techtik.cuttoff.R;
import com.example.techtik.cuttoff.Util.CallManager;
import com.example.techtik.cuttoff.Util.ContactsCursorLoader;
import com.example.techtik.cuttoff.Util.Utilities;
import com.example.techtik.cuttoff.database.entity.CustomRecordings;
import com.example.techtik.cuttoff.databinding.FragmentContactListBinding;
import com.example.techtik.cuttoff.viewmodel.ComfortFragmentViewModel;
import com.example.techtik.cuttoff.viewmodel.ContactsFragmentViewModel;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;


public class ContactListFragment extends Fragment implements
        DiscreteScrollView.OnItemChangedListener<HorizontalScrollContactsAdapter.ContactViewHolder>,
        LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener {

    private FragmentContactListBinding contactListBinding;

    private ContactsListAdapter mContactsListAdapter;
    private ContactsFragmentViewModel contactsFragmentViewModel;

    private HorizontalScrollContactsAdapter scrollContactsAdapter;

    private List<Contact> tempLatestList=new ArrayList<>();

    private final static String phoneNumberRegex = "^[\\d*#+]+$";

    private static final int LOADER_ID = 1;
    private static final String ARG_SEARCH_PHONE_NUMBER = "phone_number";
    private static final String ARG_SEARCH_CONTACT_NAME = "contact_name";


    //Viewmodel
    private ComfortFragmentViewModel comfortFragmentViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactListBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_contact_list,container,false);
        View view=contactListBinding.getRoot();

        init();


//        contactListBinding.searchContactTxt.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //implement the seaching contact
//            }
//        });

        return view;
    }

    private void init(){
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

        //Listeners

        contactListBinding.callBtn.setClickable(true);

        contactListBinding.messageBtn.setOnClickListener(v -> {
            int pos=contactListBinding.contactPicker.getCurrentItem();
            String phone=scrollContactsAdapter.getArrayList().get(pos).getPhones().get(0);
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.fromParts("sms",phone,null)));
        });

        //Intialize viewmodel
        comfortFragmentViewModel= ViewModelProviders.of(this).get(ComfortFragmentViewModel.class);

        contactListBinding.callBtn.setOnClickListener(v -> {
            int pos=contactListBinding.contactPicker.getCurrentItem();
            String phone=scrollContactsAdapter.getArrayList().get(pos).getPhones().get(0);
            makeCall(phone);
        });
    }

    //Methods
    public void makeCall(String normPhoneNumber) {
        if (normPhoneNumber == null) return;
        CallManager.call(this.getContext(), normPhoneNumber);
    }





    protected void onFragmentReady() {
        // The list adapter
        mContactsListAdapter =new ContactsListAdapter(getContext(), null,this);
        contactListBinding.contactRv.setLayoutManager(new LinearLayoutManager(getContext()));
        contactListBinding.contactRv.setAdapter(mContactsListAdapter);

        // Refresh Layout
        contactListBinding.refreshLayout.setOnRefreshListener(() -> {
            LoaderManager.getInstance(ContactListFragment.this).restartLoader(LOADER_ID, null, ContactListFragment.this);
            tryRunningLoader();
        });

    }

    // -- Overrides -- //

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onFragmentReady();

        init();

        tryRunningLoader();
    }

    @Override
    public void onResume() {
        super.onResume();
        tryRunningLoader();
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


    @Override
    public void onItemClick(RecyclerView.ViewHolder holder, Object data) {
        Contact contact= (Contact) data;


        showpopup(contact);
    }

    // -- Loader -- //

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String searchContactName = null;
        String searchPhoneNumber = null;
        if (args != null && args.containsKey(ARG_SEARCH_PHONE_NUMBER)) {
            searchPhoneNumber = args.getString(ARG_SEARCH_PHONE_NUMBER);
        }
        if (args != null && args.containsKey(ARG_SEARCH_CONTACT_NAME)) {
            searchContactName = args.getString(ARG_SEARCH_CONTACT_NAME);
        }

        boolean isSearchContactNameEmpty = searchContactName == null || searchContactName.isEmpty();
        boolean isSearchPhoneNumberEmpty = searchPhoneNumber == null || searchPhoneNumber.isEmpty();

        ContactsCursorLoader cursorLoader=new ContactsCursorLoader(getContext(), searchPhoneNumber, searchContactName);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        setData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mContactsListAdapter.changeCursor(null);
    }

    private void setData(Cursor data) {
        mContactsListAdapter.changeCursor(data);

        if (contactListBinding.refreshLayout.isRefreshing()) contactListBinding.refreshLayout.setRefreshing(false);
        if (data != null && data.getCount() > 0) {
            contactListBinding.contactRv.setVisibility(View.VISIBLE);

        } else {
            contactListBinding.contactRv.setVisibility(View.GONE);

        }

    }

    /**
     * Checks for the required permission in order to run the loader
     */
    private void tryRunningLoader() {
        if (!isLoaderRunning() && Utilities.checkPermissionsGranted(getContext(), Manifest.permission.READ_CONTACTS)) {
            runLoader();
        }
    }

    /**
     * Runs the loader
     */
    private void runLoader() {
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
    }

    /**
     * Checks whither the loader is currently running
     * (meaning the page is refreshing)
     *
     * @return boolean
     */
    private boolean isLoaderRunning() {
        Loader loader = LoaderManager.getInstance(this).getLoader(LOADER_ID);
        return loader != null;
    }

//    Show contact popup
    private void showpopup(Contact mContact){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        View view = layoutInflaterAndroid.inflate(R.layout.popup_edit_recording, null);
        TextView header= view.findViewById(R.id.header_txt);
        header.setText("Add a custom recording");

        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setView(view);

        EditText newMessage=view.findViewById(R.id.update_txt);

        builder.setCancelable(true)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Add message", (dialog, which) -> {

                });

        AlertDialog dialog=builder.create();
        dialog.show();

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if(TextUtils.isEmpty(newMessage.getText())){
                Toast.makeText(getContext(),"Enter message",Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                //update here
                CustomRecordings customRecordings=new CustomRecordings();
                customRecordings.setmCustomMessage(newMessage.getText().toString());
                customRecordings.setName(mContact.getName());
                customRecordings.setPhones(mContact.getPhones());
                customRecordings.setPhotoUri(mContact.getPhotoUri());

                comfortFragmentViewModel.addRecording(customRecordings);
                dialog.dismiss();
            }
        });

    }

}
