package com.example.cashmanager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cashmanager.Model.Data;
import com.example.cashmanager.databinding.FragmentIncomeBinding;
import com.example.cashmanager.newadapter.IncomeAdapter;
import com.example.cashmanager.newadapter.expenseAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IncomeFragment extends Fragment {
    FragmentIncomeBinding binding;
    String setIncomeResult="";
    IncomeAdapter adapter;
    FirebaseAuth mAuth;
    DatabaseReference mIncomeDatabase;
    FirebaseUser mUser;

    Bundle argsInc;
    //DashboardFragment dfIncome;
    public IncomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentIncomeBinding.inflate(inflater, container, false);
        mAuth=FirebaseAuth.getInstance();
        String uid=mAuth.getUid();
        assert uid != null;


        //dfIncome = new DashboardFragment ();
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        binding.recyclerIdIncome.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseRecyclerOptions<Data> options=new FirebaseRecyclerOptions.
                Builder<Data>().setQuery(mIncomeDatabase,Data.class).build();
        adapter=new IncomeAdapter(options);
        binding.recyclerIdIncome.setAdapter(adapter);
        binding.recyclerIdIncome.setHasFixedSize(true);
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int totalIncSum=0;
                    for (DataSnapshot mySnapshot:snapshot.getChildren()){
                        Data ob=mySnapshot.getValue(Data.class);
                        int intVal = Integer.valueOf(String.valueOf(ob.getAmount()));
                        totalIncSum+=intVal;
                    }
                    setIncomeResult=String.valueOf(totalIncSum);
                    binding.incomeTxtResult.setText(setIncomeResult);
                   }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
       //Passing data to another fragment
        /* argsInc = new Bundle();
        argsInc.putString("incKey",setIncomeResult);
        getParentFragmentManager().setFragmentResult("incData",argsInc);
       */         /*
        DashboardFragment dashboardFragment = new DashboardFragment ();
        Bundle args = new Bundle();
        args.putString("incResult", setIncomeResult);
        dashboardFragment.setArguments(args);
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









      /*  mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                list.clear();
               // int incomeSum=0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Data myData = dataSnapshot.getValue(Data.class);
                    list.add(0,myData);
                    //int incVal = Integer.valueOf(String.valueOf(myData.getAmount()));
                   // incomeSum+=incVal;
                }
                //setIncomeResult=String.valueOf(incomeSum);
                //binding.incomeTxtResult.setText(setIncomeResult);
                incomeAdapter = new IncomeAdapter(list,getContext());
                binding.recyclerIdIncome.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.recyclerIdIncome.setHasFixedSize(true);
                binding.recyclerIdIncome.setAdapter(incomeAdapter);
           }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });*/
        /*mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int totalSum=0;
                    for (DataSnapshot mySnapshot:snapshot.getChildren()){
                        Data ob=mySnapshot.getValue(Data.class);
                        int intVal = Integer.valueOf(String.valueOf(ob.getAmount()));
                        totalSum+=intVal;
                    }
                    setresult=String.valueOf(totalSum);
                    binding.incomeTxtResult.setText(setresult);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });*/
