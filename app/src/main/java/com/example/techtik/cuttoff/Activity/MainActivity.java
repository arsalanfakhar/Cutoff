package com.example.techtik.cuttoff.Activity;

import com.example.techtik.cuttoff.Adapters.listeners.OnItemClickListener;
import com.example.techtik.cuttoff.Fragments.Aboutfragment;
import com.example.techtik.cuttoff.Util.Utilities;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import hotchemi.android.rate.AppRate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techtik.cuttoff.Adapters.MainPagerAdapter;
import com.example.techtik.cuttoff.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TabLayout tabLayout;
    ViewPager viewPager;
    MainPagerAdapter mainPagerAdapter;
    BottomNavigationView bottomNavigationView;
    private int[] tabIcons = {
            R.drawable.home_holo,
            R.drawable.features,
            R.drawable.contacts,
    };
    MenuItem prevMenuItem;
    private DrawerLayout drawer;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        Utilities.setUpLocale(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.home:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.comforts:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.contacts:
                    viewPager.setCurrentItem(2);
                    break;
            }
            return false;
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                if (position <= 2) {
                    if (prevMenuItem != null) {
                        prevMenuItem.setChecked(false);
                    } else {
                        bottomNavigationView.getMenu().getItem(0).setChecked(false);
                    }

                    bottomNavigationView.getMenu().getItem(position).setChecked(true);
//                getSupportActionBar().setTitle(bottomNavigationView.getMenu().getItem(position).getTitle()) ;
                    setActionBarTitle(bottomNavigationView.getMenu().getItem(position).getTitle().toString());

                    prevMenuItem = bottomNavigationView.getMenu().getItem(position);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        tabLayout = (TabLayout) findViewById(R.id.tabs);
//        viewPager = (ViewPager)findViewById(R.id.pager);
//
//        mainPagerAdapter =  new MainPagerAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(mainPagerAdapter);
//        tabLayout.setupWithViewPager(viewPager);
//        setupTabIcons();
//        viewPager.setOffscreenPageLimit(2);
    }

    public void init() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.pager);


        drawer = findViewById(R.id.drawer_layout);

        //Setting up custom toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setActionBarTitle("Home");
//        toolbar.inflateMenu(R.menu.toolbar_search_menu);

        //Setting navigation drawer icon for toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), 3);
        viewPager.setAdapter(mainPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        //to disable swipe on About fragment
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (viewPager.getCurrentItem() == 3) {
                    viewPager.setCurrentItem(3, false);
                    return true;
                }
                return false;
            }
        });


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Get value from preferences
//        homeBinding.appStatusSwitch.setOn(SplashActivity.pref.getBoolean("app_state",false));

        //App state switch on and off
        LabeledSwitch userStatusSwitch = navigationView.getMenu().findItem(R.id.nav_btn_switch).getActionView().findViewById(R.id.app_status_switch);

        boolean app_state = getApplicationContext().getSharedPreferences("MyPref", 0).getBoolean("app_state", false);
        //Get value from preferences and set it
        userStatusSwitch.setOn(app_state);


        userStatusSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                //Toast.makeText(getApplicationContext(),"Switch dabaya",Toast.LENGTH_SHORT).show();
                addCurrentStateToPref(isOn);
            }
        });

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View view = layoutInflaterAndroid.inflate(R.layout.rate_us_dialog, null);
        //App rating dialog
        AppRate.with(this).setShowTitle(false).setShowNeverButton(false)
                .setShowLaterButton(false).setMessage("").setView(view)
                .setTextRateNow("").setCancelable(true)
                .setInstallDays(0).setLaunchTimes(2).setRemindInterval(2)
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);
        //To reset even if ignored


    }

    //    Methods
    public void addCurrentStateToPref(boolean state) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("MyPref", 0).edit();
        editor.putBoolean("app_state", state);
        editor.apply(); //apply writes the data in background process
    }

    public void setActionBarTitle(String title) {
        TextView textView = new TextView(this);
        textView.setText(title);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        getSupportActionBar().setCustomView(textView, layoutParams);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    //    Overides
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_about:
                updateAdapter();
                setActionBarTitle("About");
                viewPager.setCurrentItem(3, false);
//                Toast.makeText(this,"Mera dabaya",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_recent:
                resetAdapterDefault();
                viewPager.setCurrentItem(0);
                break;
            case R.id.nav_recording:
                resetAdapterDefault();
                viewPager.setCurrentItem(1);
                break;
            case R.id.nav_contact:
                resetAdapterDefault();
                viewPager.setCurrentItem(2);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        //TODO implement fragment for drawerclick
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar_search_menu,menu);
//        MenuItem menuItem=menu.findItem(R.id.action_search);
//        SearchView searchView= (SearchView) menuItem.getActionView();
//        searchView.setQueryHint("Type here to search");
    //        searchView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//
//
//        return super.onCreateOptionsMenu(menu);
//    }
    private void updateAdapter() {
        mainPagerAdapter.setCount(4);
        mainPagerAdapter.notifyDataSetChanged();
        bottomNavigationView.setVisibility(View.INVISIBLE);

    }

    private void resetAdapterDefault() {
        mainPagerAdapter.setCount(3);
        mainPagerAdapter.notifyDataSetChanged();
        bottomNavigationView.setVisibility(View.VISIBLE);

    }
}
