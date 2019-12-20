package com.example.techtik.cuttoff.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.techtik.cuttoff.Activity.CallScreenActivity;
import com.example.techtik.cuttoff.Activity.SplashActivity;
import com.example.techtik.cuttoff.R;
import com.example.techtik.cuttoff.Util.DBhelper;
import com.example.techtik.cuttoff.Util.PreferenceUtils;
import com.example.techtik.cuttoff.databinding.FragmentHomeBinding;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;

import java.io.IOException;

import static com.example.techtik.cuttoff.Util.DBhelper.TAG;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding homeBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false);
        View view=homeBinding.getRoot();
        homeBinding.setLifecycleOwner(this);

        //Get value from preferences
        homeBinding.appStatusSwitch.setOn(SplashActivity.pref.getBoolean("app_state",false));


        homeBinding.appStatusSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                addCurrentState(isOn);
            }
        });

        homeBinding.testBtn.setOnClickListener(v -> {
            //launch intent to show call screen
            Intent intent=new Intent(getActivity(), CallScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void addCurrentState(boolean state){
        SharedPreferences.Editor editor = SplashActivity.pref.edit();
        editor.putBoolean("app_state",state);
        editor.commit();
    }

}
