package edu.neu.madcourse.recoio.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// TODO: implement profile + private profile
public class ProfileViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Coolest User");
    }

    public LiveData<String> getText() {
        return mText;
    }
}