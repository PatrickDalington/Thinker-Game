

package com.cwp.game.common.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.cwp.game.common.data.AbstractDataProvider;
import com.cwp.game.common.data.AlphabetsDataProvider;
import com.cwp.game.common.data.EmojisDataProvider;

public class EmojisDataProviderFragment extends Fragment {
    private AbstractDataProvider mDataProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);  // keep the mDataProvider instance
        mDataProvider = new EmojisDataProvider();
    }

    public AbstractDataProvider getDataProvider() {
        return mDataProvider;
    }
}
