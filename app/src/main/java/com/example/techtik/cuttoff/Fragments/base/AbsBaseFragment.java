package com.example.techtik.cuttoff.Fragments.base;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class AbsBaseFragment extends Fragment {
    @Override
    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onFragmentReady();
    }

    protected abstract void onFragmentReady();
}
