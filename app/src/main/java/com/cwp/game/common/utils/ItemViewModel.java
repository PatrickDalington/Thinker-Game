package com.cwp.game.common.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ItemViewModel extends ViewModel {
    private final MutableLiveData<Model> selectedItem = new MutableLiveData<Model>();

    public void selectItem(Model model)
    {
        selectedItem.setValue(model);
    }

    public LiveData<Model> getSelectedItem()
    {
        return selectedItem;
    }
}
