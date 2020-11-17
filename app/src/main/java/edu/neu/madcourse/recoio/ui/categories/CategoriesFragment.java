package edu.neu.madcourse.recoio.ui.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.recoio.R;

public class CategoriesFragment extends Fragment {

    private CategoriesViewModel categoriesViewModel;

    private RecyclerView categoriesRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CategoriesRecyclerViewAdapter adapter;

    String[] categories = {"Trending", "Shows/Movies", "Food", "Electronics", "Music"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        categoriesViewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createAdapter();
    }

    public void createAdapter() {
        categoriesRecyclerView = requireView().findViewById(R.id.categoriesRecyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        categoriesRecyclerView.setLayoutManager(layoutManager);
        adapter = new CategoriesRecyclerViewAdapter(categories);
        categoriesRecyclerView.setAdapter(adapter);
    }
}