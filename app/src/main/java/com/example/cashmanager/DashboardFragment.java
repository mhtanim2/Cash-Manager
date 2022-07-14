package com.example.cashmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.cashmanager.Model.Data;
import com.example.cashmanager.databinding.FragmentDashBoardBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class DashboardFragment extends Fragment {
    FragmentDashBoardBinding binding;
    FirebaseAuth mAuth;
    String incValue = "", incResult = "", expResult = "";
    String setIncomeResult = "", setresult = "";
    Animation topAnim;
    Animation botAnim;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    public DashboardFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashBoardBinding.inflate(inflater, container, false);
        topAnim = AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.top_animation);
        botAnim = AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.bott_animation);
        //set up animation
        binding.dahboardBanner.setAnimation(topAnim);
        binding.netBalanceCard.setAnimation(botAnim);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        String uid = mUser.getUid();
        Activity activity = getActivity();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        binding.btIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertIncome();
            }
        });
        binding.btExpese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertExpense();
            }
        });
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalIncSum = 0;
                    for (DataSnapshot mySnapshot : snapshot.getChildren()) {
                        Data ob = mySnapshot.getValue(Data.class);
                        int intVal = Integer.valueOf(String.valueOf(ob.getAmount()));
                        totalIncSum += intVal;
                    }
                    setIncomeResult = String.valueOf(totalIncSum);
                    binding.incomeSetResult.setText(setIncomeResult);

                    netBalance(setIncomeResult);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
                    binding.expenseSetResult.setText(setresult);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        //receve data from another fragment
        /*getParentFragmentManager().setFragmentResultListener("incData", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                incResult=result.getString("incKey");
            }
        });
        binding.incomeSetResult.setText(incResult);
        getParentFragmentManager().setFragmentResultListener("expData", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                expResult=result.getString("expKey");
            }
        });
        binding.expenseSetResult.setText(expResult);*/
        // Toast.makeText(activity,setIncomeResult,Toast.LENGTH_SHORT).show();
        return binding.getRoot();
    }

    private void netBalance(String setIncomeResult) {

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
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
                    int incRes = Integer.parseInt(setIncomeResult);
                    int expRes = Integer.parseInt(setresult);
                    int sum = incRes - expRes;
                    if (setIncomeResult.length() > 0 && setresult.length() > 0) {
                        String totalRes = String.valueOf(sum);
                        binding.netBalance.setText(totalRes);
                    } else if (setresult.length() < 1) {
                        binding.netBalance.setText(setIncomeResult);
                    } else if (setIncomeResult.length() < 1) {
                        binding.netBalance.setText("-" + setresult);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void insertIncome() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.insert_layout, null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();
        dialog.getWindow().setWindowAnimations(R.style.animation);
        dialog.setCancelable(false);
        EditText newAmmount = myview.findViewById(R.id.ammount_edt);
        EditText newType = myview.findViewById(R.id.type_edt);
        EditText newNote = myview.findViewById(R.id.note_edt);
        Button save = myview.findViewById(R.id.btnSave);
        Button cancel = myview.findViewById(R.id.btnCancel);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amoutn = newAmmount.getText().toString().trim();
                //int inamount = Integer.parseInt(amoutn);
                String type = newType.getText().toString().trim();
                String note = newNote.getText().toString().trim();
                if (TextUtils.isEmpty(amoutn)) {
                    newAmmount.setError("Required Field..");
                    return;
                }

                if (TextUtils.isEmpty(type)) {
                    newType.setError("Required Field..");
                    return;
                }
                if (TextUtils.isEmpty(note)) {
                    newNote.setError("Required Field..");
                    return;
                }

                String mDate = DateFormat.getDateInstance().format(new Date());
                String id = mIncomeDatabase.push().getKey();
                Data ob = new Data(amoutn, type, note, mDate, id);
                assert id != null;


                mIncomeDatabase.child(id).setValue(ob).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
                        newAmmount.setText(" ");
                        newType.setText(" ");
                        newNote.setText(" ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error Occurs", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void insertExpense() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.insert_layout, null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();
        dialog.getWindow().setWindowAnimations(R.style.animation);
        dialog.setCancelable(false);
        EditText newAmmount = myview.findViewById(R.id.ammount_edt);
        EditText newType = myview.findViewById(R.id.type_edt);
        EditText newNote = myview.findViewById(R.id.note_edt);
        Button save = myview.findViewById(R.id.btnSave);
        Button cancel = myview.findViewById(R.id.btnCancel);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amoutn = newAmmount.getText().toString().trim();
                //int inamount = Integer.parseInt(amoutn);
                String type = newType.getText().toString().trim();
                String note = newNote.getText().toString().trim();
                if (TextUtils.isEmpty(amoutn)) {
                    newAmmount.setError("Required Field..");
                    return;
                }

                if (TextUtils.isEmpty(type)) {
                    newType.setError("Required Field..");
                    return;
                }
                if (TextUtils.isEmpty(note)) {
                    newNote.setError("Required Field..");
                    return;
                }

                String mDate = DateFormat.getDateInstance().format(new Date());
                String id = mExpenseDatabase.push().getKey();
                Data ob = new Data(amoutn, type, note, mDate, id);
                assert id != null;
                mExpenseDatabase.child(id).setValue(ob).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //    Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
                        newAmmount.setText(" ");
                        newType.setText(" ");
                        newNote.setText(" ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error Occurs", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}