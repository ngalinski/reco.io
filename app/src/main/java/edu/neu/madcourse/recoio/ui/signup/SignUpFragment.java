package edu.neu.madcourse.recoio.ui.signup;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.neu.madcourse.recoio.R;

public class SignUpFragment extends Fragment {

    private SignUpViewModel mViewModel;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    private Button signUpButton;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return inflater.inflate(R.layout.sign_up_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signUpButton = requireView().findViewById(R.id.signUpButton);
        usernameEditText = requireView().findViewById(R.id.signUpUsernameEditText);
        emailEditText = requireView().findViewById(R.id.signUpEmailEditText);
        passwordEditText = requireView().findViewById(R.id.signUpPasswordEditText);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              signUp();
            }
        });
    }

    public boolean signUp() {
        if(!emailEditText.getText().toString().equals("") &&
                !usernameEditText.getText().toString().equals("") &&
                !passwordEditText.getText().toString().equals("")) {
            getUsernameAvailability(usernameEditText.getText().toString());
        }
        return false;
    }

    private void registerUser() {
        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(),
                passwordEditText.getText().toString())
                .addOnCompleteListener(requireActivity(),
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            saveToDatabase();
                        } else {
                            Toast.makeText(getActivity(),
                                    "Error registering, please change the email or password.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void saveToDatabase() {
        DatabaseReference usersReference = mDatabase.child("users");
        DatabaseReference usernames = mDatabase.child("usernames");
        usernames.child(usernameEditText.getText().toString()).setValue(true);
        // we are going to use the UID to identify users in the database
        DatabaseReference newUser = usersReference.child(currentUser.getUid());
        newUser.child("username").setValue(usernameEditText.getText().toString());
        newUser.child("email").setValue(currentUser.getEmail());
        NavHostFragment.findNavController(SignUpFragment.this)
                .navigate(R.id.action_signUpFragment_to_app_navigation);
    }

    public void getUsernameAvailability(String newUsername) {
        DatabaseReference usernames = mDatabase.child("usernames");
        System.out.println(usernames.getKey());

        usernames.child(newUsername).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    System.out.println(snapshot.getValue());
                    registerUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}