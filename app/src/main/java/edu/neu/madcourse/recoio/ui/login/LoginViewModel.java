package edu.neu.madcourse.recoio.ui.login;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import edu.neu.madcourse.recoio.R;

public class LoginViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();
    private FirebaseAuth mAuth;

    public LoginViewModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }

    public void setEmail(String username) {
        this.email.setValue(username);
    }

}