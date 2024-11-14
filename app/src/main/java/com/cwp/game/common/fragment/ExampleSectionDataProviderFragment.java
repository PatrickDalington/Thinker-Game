
package com.cwp.game.common.fragment;


import android.os.Bundle;


import androidx.fragment.app.Fragment;

import com.cwp.game.common.data.AbstractDataProvider;
import com.cwp.game.common.data.ExampleSectionDataProvider;

public class ExampleSectionDataProviderFragment extends Fragment {
    private ExampleSectionDataProvider mDataProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);  // keep the mDataProvider instance
        mDataProvider = new ExampleSectionDataProvider();
    }

    public AbstractDataProvider getDataProvider() {
        return mDataProvider;
    }
}
