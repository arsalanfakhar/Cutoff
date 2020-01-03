package com.example.techtik.cuttoff.Activity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.techtik.cuttoff.Adapters.MainPagerAdapter;
import com.example.techtik.cuttoff.R;

public class MainActivity extends AppCompatActivity {

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


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        viewPager.setCurrentItem(0);
                    case R.id.comforts:
                        viewPager.setCurrentItem(1);
                    case R.id.contacts:
                        viewPager.setCurrentItem(2);
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }

                bottomNavigationView.getMenu().getItem(position).setChecked(true);
//                getSupportActionBar().setTitle(bottomNavigationView.getMenu().getItem(position).getTitle()) ;
                setActionBarTitle(bottomNavigationView.getMenu().getItem(position).getTitle().toString());

                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

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

    public void init(){
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        viewPager = (ViewPager)findViewById(R.id.pager);


        drawer=findViewById(R.id.drawer_layout);

        //Setting up custom toolbar
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setActionBarTitle("Home");
//        toolbar.inflateMenu(R.menu.toolbar_search_menu);

        //Setting navigation drawer icon for toolbar
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mainPagerAdapter =  new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
    }

    public void setActionBarTitle(String title){
        TextView textView=new TextView(this);
        textView.setText(title);
        ActionBar.LayoutParams layoutParams= new  ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.CENTER;
        getSupportActionBar().setCustomView(textView,layoutParams);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");
        searchView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        return super.onCreateOptionsMenu(menu);
    }
}
