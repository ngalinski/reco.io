package edu.neu.madcourse.recoio.ui.yourlists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import edu.neu.madcourse.recoio.R;

public class YourListsFragment extends Fragment {

    private YourListsViewModel yourListsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        yourListsViewModel =
                ViewModelProviders.of(this).get(YourListsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_your_lists, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        yourListsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}