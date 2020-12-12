package edu.neu.madcourse.recoio.ui.welcome;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.neu.madcourse.recoio.R;
import edu.neu.madcourse.recoio.ui.login.LoginFragment;


public class WelcomeFragment extends Fragment {


    BottomNavigationView bottomNavigationView;


    private TextView getStartedTextView;
    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomNavigationView = requireActivity().findViewById(R.id.nav_view);
        bottomNavigationView.setVisibility(View.GONE);
        getStartedTextView = requireView().findViewById(R.id.getStartedTextView);

        getStartedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(WelcomeFragment.this)
                        .navigate(R.id.action_welcomeFragment_to_loginFragment);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            bottomNavigationView = requireActivity().findViewById(R.id.nav_view);
            bottomNavigationView.setVisibility(View.VISIBLE);
            NavHostFragment.findNavController(WelcomeFragment.this)
                    .navigate(R.id.action_welcomeFragment_to_app_navigation);
        }
    }
}