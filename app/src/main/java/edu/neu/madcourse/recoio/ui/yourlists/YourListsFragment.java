package edu.neu.madcourse.recoio.ui.yourlists;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.neu.madcourse.recoio.List;
import edu.neu.madcourse.recoio.R;
import edu.neu.madcourse.recoio.ui.categories.CategoriesRecyclerViewAdapter;
import edu.neu.madcourse.recoio.ui.categories.CategoryFragment;
import edu.neu.madcourse.recoio.ui.categories.CategoryRecyclerViewAdapter;

public class YourListsFragment extends Fragment {

    private RecyclerView listsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private YourListsRecyclerViewAdapter adapter;

    private final DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference listsReference = firebaseDatabase.child("lists");
    private final DatabaseReference usersReference = firebaseDatabase.child("users");

    private ArrayList<List> listsArrayList;

    Button newListButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_your_lists, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newListButton = requireView().findViewById(R.id.addListButton);

        listsArrayList = new ArrayList<>();

        newListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(YourListsFragment.this)
                        .navigate(R.id.action_navigation_your_lists_to_addListFragment);
            }
        });

        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference currentUserListRef = usersReference.child(currentUserUID).child("lists");

        currentUserListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    listsReference.child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List newList = new List(
                              snapshot.getKey(), (String) snapshot.child("name").getValue());
                            listsArrayList.add(newList);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        createAdapter();
        adapter.setItemClickListener(new YourListsRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, Context context) {
                Bundle listBundle = new Bundle();
                listBundle.putString("listUID", listsArrayList.get(position).getListUID());
                listBundle.putString("listName", listsArrayList.get(position).getListName());
                NavHostFragment.findNavController(YourListsFragment.this)
                        .navigate(R.id.action_navigation_your_lists_to_listFragment, listBundle);
            }
        });
    }

    public void createAdapter() {
        listsRecyclerView = requireView().findViewById(R.id.yourListsRecyclerView);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        listsRecyclerView.setLayoutManager(layoutManager);
        adapter = new YourListsRecyclerViewAdapter(listsArrayList);
        listsRecyclerView.setAdapter(adapter);
    }
}