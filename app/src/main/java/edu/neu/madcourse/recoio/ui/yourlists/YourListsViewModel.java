package edu.neu.madcourse.recoio.ui.yourlists;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class YourListsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public YourListsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is your lists fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}