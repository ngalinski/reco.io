package edu.neu.madcourse.recoio.ui.newsfeed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewsfeedViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NewsfeedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}