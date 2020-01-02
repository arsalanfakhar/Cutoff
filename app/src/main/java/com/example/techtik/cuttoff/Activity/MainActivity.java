package com.example.techtik.cuttoff.Activity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        viewPager = (ViewPager)findViewById(R.id.pager);

        mainPagerAdapter =  new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainPagerAdapter);
        viewPager.setOffscreenPageLimit(2);

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


    private void setupTabIcons(){
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }
}
