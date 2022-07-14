package com.example.cashmanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cashmanager.Model.Data;
import com.example.cashmanager.databinding.FragmentExpenseBinding;
import com.example.cashmanager.newadapter.expenseAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExpenseFragment extends Fragment {

    FragmentExpenseBinding binding;
    String uid;
    expenseAdapter adapter;
    FirebaseAuth mAuth;
    DatabaseReference mExpenseDatabase;
    String setresult = "";
    Bundle argsExp;
    DashboardFragment dfExpense;

    public ExpenseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExpenseBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);


        dfExpense = new DashboardFragment();
        binding.recyclerIdExpense.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.
                Builder<Data>().setQuery(mExpenseDatabase, Data.class).build();

        adapter = new expenseAdapter(options);
        binding.recyclerIdExpense.setAdapter(adapter);
        binding.recyclerIdExpense.setHasFixedSize(true);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalSum = 0;
                    for (DataSnapshot mySnapshot : snapshot.getChildren()) {
                        Data ob = mySnapshot.getValue(Data.class);
                        int intVal = Integer.valueOf(String.valueOf(ob.getAmount()));
                        totalSum += intVal;
                    }
                    String setresult = String.valueOf(totalSum);
                    binding.expenseTxtResult.setText(setresult);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //getting data from the fragment
        /* argsExp = new Bundle();
        argsExp.putString("expKey",setresult);
        getParentFragmentManager().setFragmentResult("expData",argsExp);
*/
        /*DashboardFragment dashboardFragment = new DashboardFragment ();
        Bundle arg = new Bundle();
        arg.putString("expResult", setresult);
        dashboardFragment.setArguments(arg);
        getFragmentManager().beginTransaction().add(R.id.main_frame, dashboardFragment).commit();

*/

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}