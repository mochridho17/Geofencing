package com.example.geofencing.ui.parent.childs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.geofencing.Config;
import com.example.geofencing.adapter.ChildAdapter;
import com.example.geofencing.databinding.FragmentChildsBinding;
import com.example.geofencing.dialog.ChildOptionDialog;
import com.example.geofencing.dialog.EnterChildPairCodeDialog;
import com.example.geofencing.helper.StringHelper;
import com.example.geofencing.model.ChildPairCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChildsFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FragmentChildsBinding binding;
    private DatabaseReference DB;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChildsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setupRecyclerView();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupEventListener();
    }

    private void setupEventListener() {
        binding.fabAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_addChildFragment);
                EnterChildPairCodeDialog dialog = new EnterChildPairCodeDialog(view, getContext());
                dialog.show(getParentFragmentManager(), dialog.getTag());
            }
        });
    }

    private void setupRecyclerView() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get data from db
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("users/" + uid + "/childs");
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!isAdded()){
                    return;
                }
                List<ChildPairCode> childList = new ArrayList<>();

                int i = 0;
                for (DataSnapshot clidSnapshot: dataSnapshot.getChildren()) {
                    i++;

//                    Double lat = clidSnapshot.child("latitude").getValue(Double.class);
//                    Double lng = clidSnapshot.child("longitude").getValue(Double.class);
                    String childId = clidSnapshot.child("childId").getValue(String.class);
                    String email = clidSnapshot.child("email").getValue(String.class);
                    String username = clidSnapshot.child("username").getValue(String.class);

//                    if (lat != null || lng != null) {
//                        Log.d(TAG, "onDataChange: have lat lng" + i + " " + clidSnapshot.getKey() + " " + clidSnapshot.child("name").getValue(String.class) + " " + lat + " " + lng);
//                    }

                    childList.add(new ChildPairCode(clidSnapshot.getKey(), username, childId));
                }

                ChildAdapter adapter = new ChildAdapter(childList);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL));
                binding.recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener((view, i1) -> {
                    final Bundle bundle = new Bundle();
                    bundle.putString("id", childList.get(i1).getChildId());
                    bundle.putString("name", childList.get(i1).getUsername());
                    Log.d(TAG, "onDataChange: "+childList.get(i1).getUsername());
//                    ChildCodeDialog childCodeDialog = new ChildCodeDialog(childList.get(i1).getPairkey());
//                    childCodeDialog.show(getParentFragmentManager(), "child_code");
//                    Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_trackChildMapsFragment, bundle);
                    ChildOptionDialog childOptionDialog = new ChildOptionDialog(
                            view,
                            childList.get(i1).getChildId(),
                            StringHelper.usernameFromEmail(childList.get(i1).getEmail()),
                            childList.get(i1).getUsername()
                    );
                    childOptionDialog.show(getParentFragmentManager(), "child_option");
                });

//                adapter.setOnItemLongClickListener((view, i12) -> {
//                    String id = childList.get(i12).getId();
//                    String name = childList.get(i12).getName();
//
//                    DeleteChildDialog deleteChildDialog = new DeleteChildDialog(id, name);
//                    deleteChildDialog.show(getParentFragmentManager(), "delete_child");
//                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage());
            }
        });
    }

    private void showChildOptionDialog() {
        Toast.makeText(requireContext(), "Option", Toast.LENGTH_SHORT).show();
    }

}