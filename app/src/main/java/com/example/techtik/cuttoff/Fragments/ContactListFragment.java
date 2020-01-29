package com.example.techtik.cuttoff.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techtik.cuttoff.Adapters.ContactsListAdapter;
import com.example.techtik.cuttoff.Adapters.HorizontalScrollContactsAdapter;
import com.example.techtik.cuttoff.Adapters.listeners.OnItemClickListener;
import com.example.techtik.cuttoff.Models.Contact;
import com.example.techtik.cuttoff.Models.RecentCall;
import com.example.techtik.cuttoff.R;
import com.example.techtik.cuttoff.Util.CallManager;
import com.example.techtik.cuttoff.Util.ContactsCursorLoader;
import com.example.techtik.cuttoff.Util.RecentsCursorLoader;
import com.example.techtik.cuttoff.Util.Utilities;
import com.example.techtik.cuttoff.database.entity.CustomRecordings;
import com.example.techtik.cuttoff.databinding.FragmentContactListBinding;
import com.example.techtik.cuttoff.viewmodel.ComfortFragmentViewModel;
import com.example.techtik.cuttoff.viewmodel.ContactsFragmentViewModel;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class ContactListFragment extends Fragment implements
        DiscreteScrollView.OnItemChangedListener<HorizontalScrollContactsAdapter.ContactViewHolder>,
        LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener {

    private FragmentContactListBinding contactListBinding;

    private ContactsListAdapter mContactsListAdapter;
    private ContactsFragmentViewModel contactsFragmentViewModel;

    private HorizontalScrollContactsAdapter scrollContactsAdapter;

    private final static String phoneNumberRegex = "^[\\d*#+]+$";

    private static final int LOADER_CONTACTS_ID = 1;
    private static final int LOADER_RECENT_CONTACTS_ID = 2;


    private static final String ARG_SEARCH_PHONE_NUMBER = "phone_number";
    private static final String ARG_SEARCH_CONTACT_NAME = "contact_name";


    //Viewmodel
    private ComfortFragmentViewModel comfortFragmentViewModel;

    private TextView mEmptyTitle;
    private TextView mEmptyDesc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_list, container, false);
        View view = contactListBinding.getRoot();
//        mEmptyTitle=view.findViewById(R.id.empty_title);
//        mEmptyDesc=view.findViewById(R.id.empty_desc);

        init();
        setHasOptionsMenu(true);

//        contactListBinding.searchContactTxt.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //implement the seaching contact
//            }
//        });

        return view;
    }

    private void init() {
        //initialize horizontal scroll view
//        scrollContactsAdapter=new HorizontalScrollContactsAdapter(getContext(), (ArrayList<Contact>) contactsFragmentViewModel.contactList.getValue());
//        contactListBinding.contactPicker.setAdapter(scrollContactsAdapter);



        scrollContactsAdapter = new HorizontalScrollContactsAdapter(getContext(), new ArrayList<>());
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
            int pos = contactListBinding.contactPicker.getCurrentItem();
            String phone = scrollContactsAdapter.getArrayList().get(pos).getCallerNumber();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null)));
        });

        //Intialize viewmodel
        comfortFragmentViewModel = ViewModelProviders.of(this).get(ComfortFragmentViewModel.class);

        contactListBinding.callBtn.setOnClickListener(v -> {
            int pos = contactListBinding.contactPicker.getCurrentItem();
            String phone = scrollContactsAdapter.getArrayList().get(pos).getCallerNumber();
            makeCall(phone);
        });


    }

    //Methods
    private void makeCall(String normPhoneNumber) {
        if (normPhoneNumber == null) return;
        String dial = "tel:" + normPhoneNumber;
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        //        CallManager.call(this.getContext(), normPhoneNumber);
    }


    protected void onFragmentReady() {
        // The list adapter
        mContactsListAdapter = new ContactsListAdapter(getContext(), null, this);
        contactListBinding.contactRv.setLayoutManager(new LinearLayoutManager(getContext()));
        contactListBinding.contactRv.setAdapter(mContactsListAdapter);


        //Fast scroller
       // contactListBinding.fastscroll.setRecyclerView(contactListBinding.contactRv);

        // Refresh Layout
        contactListBinding.refreshLayout.setOnRefreshListener(() -> {
            LoaderManager.getInstance(ContactListFragment.this).restartLoader(LOADER_CONTACTS_ID, null, ContactListFragment.this);
            tryRunningLoader();

        });

//        mEmptyTitle.setText(R.string.empty_contact_title);
//        mEmptyDesc.setText(R.string.empty_contact_desc);
    }

    // -- Overrides -- //

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onFragmentReady();

        init();

        tryRunningLoader();
        tryRunningRecentLoader();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("resume", "true");
        tryRunningLoader();
        tryRunningRecentLoader();
    }

    @Override
    public void onCurrentItemChanged(@Nullable HorizontalScrollContactsAdapter.ContactViewHolder viewHolder, int position) {
        //setting the current selected contact name
        ArrayList<RecentCall> recentCalls = scrollContactsAdapter.getArrayList();
        if (recentCalls != null && recentCalls.size() > 0) {
            String currentContactName = recentCalls.get(position).getCallerName();
            contactListBinding.scrollContactName.setText(currentContactName);

            String lastcall = recentCalls.get(position).getLastcalled();
            lastcall = "last call: " + lastcall;
            contactListBinding.scrollContactLastCall.setText(lastcall);
        }

    }


    @Override
    public void onItemClick(RecyclerView.ViewHolder holder, Object data) {
        Contact contact = (Contact) data;

        showpopup(contact);
    }

    // -- Loader -- //

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loader_id, @Nullable Bundle args) {

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


        switch (loader_id) {
            case LOADER_CONTACTS_ID:
                ContactsCursorLoader cursorLoader = new ContactsCursorLoader(getContext(), searchPhoneNumber, searchContactName);
                return cursorLoader;
            case LOADER_RECENT_CONTACTS_ID:
                String timestamp = String.valueOf(getTodayTimestamp());
                RecentsCursorLoader recentsCursorLoader = new RecentsCursorLoader(getContext(), searchPhoneNumber, searchContactName, timestamp);
                return recentsCursorLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_CONTACTS_ID:
                setData(data);
                break;
            case LOADER_RECENT_CONTACTS_ID:
                Log.v("malam", "here");
//                if (data.moveToFirst()) {
//                    do {
//                        StringBuilder sb = new StringBuilder();
//                        int columnsQty = data.getColumnCount();
//                        for (int idx=0; idx<columnsQty; ++idx) {
//                            sb.append(data.getString(idx));
//                            if (idx < columnsQty - 1)
//                                sb.append("; ");
//                        }
//                        Log.v("malam", String.format("Row: %d, Values: %s", data.getPosition(),
//                                sb.toString()));
//                    } while (data.moveToNext());
//                }
                setRecentData(sortRecentCalls(data));
                break;
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        mContactsListAdapter.changeCursor(null);
//        scrollContactsAdapter.setArrayList(null);
//        scrollContactsAdapter.notifyDataSetChanged();
    }

    private void setData(Cursor data) {
        mContactsListAdapter.changeCursor(data);

        if (contactListBinding.refreshLayout.isRefreshing())
            contactListBinding.refreshLayout.setRefreshing(false);
        if (data != null && data.getCount() > 0) {
            contactListBinding.contactRv.setVisibility(View.VISIBLE);
           // contactListBinding.emptyState.setVisibility(View.GONE);

        } else {
            contactListBinding.contactRv.setVisibility(View.GONE);
            //contactListBinding.emptyState.setVisibility(View.VISIBLE);

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
        LoaderManager.getInstance(this).initLoader(LOADER_CONTACTS_ID, null, this);
    }

    /**
     * Checks whither the loader is currently running
     * (meaning the page is refreshing)
     *
     * @return boolean
     */
    private boolean isLoaderRunning() {
        Loader loader = LoaderManager.getInstance(this).getLoader(LOADER_CONTACTS_ID);

        return loader != null;
    }

    //    Show contact popup
    private void showpopup(Contact mContact) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        View view = layoutInflaterAndroid.inflate(R.layout.popup_edit_recording, null);
        TextView header = view.findViewById(R.id.header_txt);
        header.setText("Add a custom recording");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        EditText newMessage = view.findViewById(R.id.update_txt);

        builder.setCancelable(true)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Add message", (dialog, which) -> {

                });


        AlertDialog dialog = builder.create();
        dialog.show();


        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (TextUtils.isEmpty(newMessage.getText())) {
                Toast.makeText(getContext(), "Enter message", Toast.LENGTH_SHORT).show();
                return;
            } else {
                //update here
                CustomRecordings customRecordings = new CustomRecordings();
                customRecordings.setmCustomMessage(newMessage.getText().toString());
                customRecordings.setName(mContact.getName());
                customRecordings.setPhones(mContact.getPhones());
                customRecordings.setPhotoUri(mContact.getPhotoUri());

                comfortFragmentViewModel.addRecording(customRecordings);
                dialog.dismiss();
            }
        });

    }

    private void tryRunningRecentLoader() {
        if (!isRecentLoaderRunning() && Utilities.checkPermissionsGranted(getContext(), Manifest.permission.READ_CONTACTS)) {
            runRecentLoader();
        }
    }

    private void runRecentLoader() {
        LoaderManager.getInstance(this).initLoader(LOADER_RECENT_CONTACTS_ID, null, this);
    }

    private boolean isRecentLoaderRunning() {
        Loader loader = LoaderManager.getInstance(this).getLoader(LOADER_RECENT_CONTACTS_ID);

        return loader != null;
    }

    private void setRecentData(ArrayList<RecentCall> recentData) {

        scrollContactsAdapter.setArrayList(recentData);
        scrollContactsAdapter.notifyDataSetChanged();

        if (recentData != null && recentData.size() > 0) {
            contactListBinding.contactPicker.setVisibility(View.VISIBLE);
            contactListBinding.emptyLogState.setVisibility(View.GONE);

        } else {
            contactListBinding.contactPicker.setVisibility(View.GONE);
            contactListBinding.emptyLogState.setVisibility(View.VISIBLE);

        }

//        scrollContactsAdapter.setArrayList(new ArrayList<>());
//        scrollContactsAdapter.notifyDataSetChanged();


    }

    //With this method you will get the timestamp of today at midnight
    private long getTodayTimestamp() {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(new Date());

        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.YEAR, c1.get(Calendar.YEAR));
        c2.set(Calendar.MONTH, c1.get(Calendar.MONTH));
        c2.set(Calendar.DAY_OF_MONTH, c1.get(Calendar.DAY_OF_MONTH));
        c2.set(Calendar.HOUR_OF_DAY, 0);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.SECOND, 0);

        return c2.getTimeInMillis();
    }

    private ArrayList<RecentCall> sortRecentCalls(Cursor data) {
        ArrayList<RecentCall> callArrayList = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                RecentCall recentCall = new RecentCall(getContext(), data);

                //if name is null put its number in name
                if (TextUtils.isEmpty(recentCall.getCallerName()))
                    recentCall.setmCallerName(recentCall.getCallerNumber());

                int currentPos = data.getPosition();
                if (currentPos == 0) {
                    callArrayList.add(recentCall);
                } else {
                    //compare
                    if (checkArrList(recentCall.getCallerName(), callArrayList))
                        callArrayList.add(recentCall);

                }
            } while (data.moveToNext());
        }
        return callArrayList;
    }

    private boolean checkArrList(String name, ArrayList<RecentCall> callArrayList) {
        if (!callArrayList.isEmpty()) {
            for (int i = 0; i < callArrayList.size(); i++) {
                if (callArrayList.get(i).getCallerName().equals(name)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search contacts");
        searchView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (isLoaderRunning()) {
                    Bundle args = new Bundle();
                    args.putString(ARG_SEARCH_PHONE_NUMBER, newText);
                    LoaderManager.getInstance(ContactListFragment.this).restartLoader(LOADER_CONTACTS_ID, args, ContactListFragment.this);
                }
                return false;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }

}
