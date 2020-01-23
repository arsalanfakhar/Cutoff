package com.example.techtik.cuttoff.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.techtik.cuttoff.Activity.CallScreenActivity;
import com.example.techtik.cuttoff.Activity.SplashActivity;
import com.example.techtik.cuttoff.Adapters.RecentsAdapter;
import com.example.techtik.cuttoff.Adapters.listeners.OnItemClickListener;
import com.example.techtik.cuttoff.Adapters.listeners.OnItemLongClickListener;
import com.example.techtik.cuttoff.R;
import com.example.techtik.cuttoff.Util.DBhelper;
import com.example.techtik.cuttoff.Util.PreferenceUtils;
import com.example.techtik.cuttoff.Util.RecentsCursorLoader;
import com.example.techtik.cuttoff.Util.Utilities;
import com.example.techtik.cuttoff.databinding.FragmentHomeBinding;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;

import java.io.IOException;

import static com.example.techtik.cuttoff.Util.DBhelper.TAG;


public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        OnItemClickListener, OnItemLongClickListener {

    private FragmentHomeBinding homeBinding;

    private static final int LOADER_ID = 1;
    private static final String ARG_PHONE_NUMBER = "phone_number";
    private static final String ARG_CONTACT_NAME = "contact_name";


    private RecentsAdapter mRecentsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = homeBinding.getRoot();
        homeBinding.setLifecycleOwner(this);
        setHasOptionsMenu(true);
        return view;
    }

    // -- Overrides -- //

    protected void onFragmentReady() {
        mRecentsAdapter = new RecentsAdapter(getContext(), null, this, this);

        // RecyclerView
        homeBinding.recentRv.setLayoutManager(new LinearLayoutManager(getContext()));
        homeBinding.recentRv.setAdapter(mRecentsAdapter);

        // RefreshLayout
        homeBinding.recentsRefreshLayout.setOnRefreshListener(() -> {
            LoaderManager.getInstance(HomeFragment.this).restartLoader(LOADER_ID, null, HomeFragment.this);
            tryRunningLoader();
        });

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        onFragmentReady();

        tryRunningLoader();
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder holder, Object data) {

    }

    @Override
    public void onItemLongClick(RecyclerView.ViewHolder holder, Object data) {

    }

    @Override
    public void onResume() {
        super.onResume();
        tryRunningLoader();
    }

    // -- Loader -- //
    private void tryRunningLoader() {
        if (!isLoaderRunning() && Utilities.checkPermissionsGranted(getContext(), Manifest.permission.READ_CALL_LOG)) {
            runLoader();
        }
    }

    private void runLoader() {
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
    }

    private boolean isLoaderRunning() {
        Loader loader = LoaderManager.getInstance(this).getLoader(LOADER_ID);
        return loader != null;
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String contactName = null;
        String phoneNumber = null;
        if (args != null && args.containsKey(ARG_PHONE_NUMBER)) {
            phoneNumber = args.getString(ARG_PHONE_NUMBER);
        }
        if (args != null && args.containsKey(ARG_CONTACT_NAME)) {
            contactName = args.getString(ARG_CONTACT_NAME);
        }
        RecentsCursorLoader recentsCursorLoader = new RecentsCursorLoader(getContext(), phoneNumber, contactName);
        return recentsCursorLoader;

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        setData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mRecentsAdapter.changeCursor(null);
    }

    private void setData(Cursor data) {
        mRecentsAdapter.changeCursor(data);
        if (homeBinding.recentsRefreshLayout.isRefreshing()) homeBinding.recentsRefreshLayout.setRefreshing(false);
        if (data != null && data.getCount() > 0) {
            homeBinding.recentRv.setVisibility(View.VISIBLE);

        } else {
            homeBinding.recentRv.setVisibility(View.GONE);

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_search_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search logs");
        searchView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(isLoaderRunning()){
                    Bundle args = new Bundle();
                    args.putString(ARG_PHONE_NUMBER, newText);
                    LoaderManager.getInstance(HomeFragment.this).restartLoader(LOADER_ID,args,HomeFragment.this);
                }
                return false;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }
}
