package edu.neu.madcourse.recoio.ui.categories;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.neu.madcourse.recoio.R;
import android.widget.EditText;


public class CategoriesFragment extends Fragment {

    private CategoriesViewModel categoriesViewModel;

    private RecyclerView categoriesRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CategoriesRecyclerViewAdapter adapter;

    private Button searchButton;
    private EditText searchText;

    //TODO - add trending back to the categories
    String[] categories = {"Shows and Movies", "Food", "Electronics", "Music", "Books", "Other"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        categoriesViewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createAdapter();
        searchText = requireView().findViewById(R.id.SearchText);
        searchButton = requireView().findViewById(R.id.SearchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchText.getText().toString().equals("")){
                    Bundle searchStringBundle = new Bundle();
                    searchStringBundle.putString("searchText", searchText.getText().toString());
                    InputMethodManager inputMethodManager = (InputMethodManager)requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
                    NavHostFragment.findNavController(CategoriesFragment.this)
                            .navigate(R.id.action_navigation_categories_to_searchFragment,
                                    searchStringBundle);
                }
            }
        });

        adapter.setItemClickListener(new CategoriesRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, Context context) {
                Bundle categoryStringBundle = new Bundle();
                categoryStringBundle.putString("category", categories[position]);
                NavHostFragment.findNavController(CategoriesFragment.this)
                        .navigate(R.id.action_navigation_categories_to_categoryFragment,
                                categoryStringBundle);
            }
        });

        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void createAdapter() {
        categoriesRecyclerView = requireView().findViewById(R.id.categoriesRecyclerView);
        layoutManager = new GridLayoutManager(getActivity(), 1);
        categoriesRecyclerView.setLayoutManager(layoutManager);
        adapter = new CategoriesRecyclerViewAdapter(categories);
        categoriesRecyclerView.setAdapter(adapter);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}