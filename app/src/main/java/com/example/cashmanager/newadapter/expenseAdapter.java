package com.example.cashmanager.newadapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashmanager.Model.Data;
import com.example.cashmanager.R;
import com.example.cashmanager.databinding.ExpenseSingleRowDesignBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

public class expenseAdapter extends FirebaseRecyclerAdapter<Data, expenseAdapter.myExpenseHolder> {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    String getId = auth.getUid();

    public expenseAdapter(@NonNull FirebaseRecyclerOptions<Data> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myExpenseHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Data model) {
        holder.binding.dateTxtExpense.setText(model.getDate());
        holder.binding.typeTxtExpense.setText(model.getType());
        holder.binding.noteTxtExpense.setText(model.getNote());
        holder.binding.ammountTxtExpense.setText(model.getAmount());
        holder.binding.updateExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.binding.noteTxtExpense.getContext()).
                        setContentHolder(new ViewHolder(R.layout.update_alart)).
                        setExpanded(true, 1000)
                        .create();
                View myUpView = dialogPlus.getHolderView();
                final EditText amount = myUpView.findViewById(R.id.ammount_update);
                final EditText type = myUpView.findViewById(R.id.type_update);
                final EditText note = myUpView.findViewById(R.id.note_update);
                final Button update = myUpView.findViewById(R.id.btnUpdate);

                amount.setText(model.getAmount());
                type.setText(model.getType());
                note.setText(model.getNote());
                dialogPlus.show();

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("amount", amount.getText().toString());
                        map.put("type", type.getText().toString());
                        map.put("note", note.getText().toString());
                        FirebaseDatabase.getInstance().getReference().child("ExpenseData")
                                .child(getId).child(getRef(position).getKey()).
                                updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialogPlus.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialogPlus.dismiss();
                                    }
                                });
                    }
                });
            }
        });
        holder.binding.deleteExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.binding.noteTxtExpense.getContext()).
                        setContentHolder(new ViewHolder(R.layout.delt_alart)).
                        setExpanded(true, 600)
                        .create();
                View myDltView = dialogPlus.getHolderView();
                Button yes = myDltView.findViewById(R.id.dltYes);
                Button no = myDltView.findViewById(R.id.dltNo);
                dialogPlus.show();
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference().child("ExpenseData")
                                .child(getId).child(getRef(position).getKey()).removeValue();
                        dialogPlus.dismiss();

                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogPlus.dismiss();
                    }
                });

            }
        });
    }

    @NonNull
    @Override
    public myExpenseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.expense_single_row_design, parent, false);
        return new myExpenseHolder(view);
    }

    public class myExpenseHolder extends RecyclerView.ViewHolder {
        ExpenseSingleRowDesignBinding binding;

        public myExpenseHolder(@NonNull View itemView) {
            super(itemView);
            binding = ExpenseSingleRowDesignBinding.bind(itemView);
        }
    }
}
