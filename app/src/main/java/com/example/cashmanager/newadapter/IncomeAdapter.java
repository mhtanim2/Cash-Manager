package com.example.cashmanager.newadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashmanager.Model.Data;
import com.example.cashmanager.R;
import com.example.cashmanager.databinding.IncomeSingleRowDesignBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IncomeAdapter extends FirebaseRecyclerAdapter<Data, IncomeAdapter.myIncome> {

    FirebaseAuth IncAuth = FirebaseAuth.getInstance();
    String getIncId = IncAuth.getUid();

    public IncomeAdapter(@NonNull FirebaseRecyclerOptions<Data> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myIncome holder, @SuppressLint("RecyclerView") final int position, @NonNull Data model) {
        holder.binding.dateTxtIncome.setText(model.getDate());
        holder.binding.typeTxtIncome.setText(model.getType());
        holder.binding.noteTxtIncome.setText(model.getNote());
        holder.binding.ammountTxtIncome.setText(model.getAmount());
        holder.binding.updateIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DialogPlus dialogPlusIncome = DialogPlus.newDialog(holder.binding.typeTxtIncome.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_alart))
                        .setExpanded(true, 1000)
                        .create();

                View myview = dialogPlusIncome.getHolderView();
                // final EditText purl=myview.findViewById(R.id.uimgurl);
                final EditText amount = myview.findViewById(R.id.ammount_update);
                final EditText type = myview.findViewById(R.id.type_update);
                final EditText note = myview.findViewById(R.id.note_update);
                final Button update = myview.findViewById(R.id.btnUpdate);

                amount.setText(model.getAmount());
                type.setText(model.getType());
                note.setText(model.getNote());
                dialogPlusIncome.show();
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("amount", amount.getText().toString());
                        map.put("type", type.getText().toString());
                        map.put("note", note.getText().toString());
                        FirebaseDatabase.getInstance().getReference().child("IncomeData")
                                .child(getIncId).child(Objects.requireNonNull(getRef(position)
                                        .getKey())).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialogPlusIncome.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialogPlusIncome.dismiss();
                                    }
                                });
                    }
                });
            }
        });
        holder.binding.deleteIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus incDltdialogPlus = DialogPlus.newDialog(holder.binding.noteTxtIncome.getContext()).
                        setContentHolder(new ViewHolder(R.layout.delt_alart)).
                        setExpanded(true, 600)
                        .create();
                View myIncDltView = incDltdialogPlus.getHolderView();
                Button yes = myIncDltView.findViewById(R.id.dltYes);
                Button no = myIncDltView.findViewById(R.id.dltNo);
                incDltdialogPlus.show();
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference().child("IncomeData")
                                .child(getIncId).child(getRef(position).getKey()).removeValue();
                        incDltdialogPlus.dismiss();

                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        incDltdialogPlus.dismiss();
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public myIncome onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .income_single_row_design, parent, false);
        return new myIncome(view);
    }

    public class myIncome extends RecyclerView.ViewHolder {
        IncomeSingleRowDesignBinding binding;

        public myIncome(@NonNull View itemView) {
            super(itemView);
            binding = IncomeSingleRowDesignBinding.bind(itemView);
        }
    }
}

    /*public class IncomeViewHolder extends RecyclerView.ViewHolder {
        IncomeSingleRowDesignBinding binding;
        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = IncomeSingleRowDesignBinding.bind(itemView);
        }
    }*/
 /*private List<Data> list;
    private Context context;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    String getId = auth.getUid();
    public IncomeAdapter(List<Data> list, Context context){
        this.list = list;
        this.context = context;
    }
    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.income_single_row_design,parent,false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        Data data = list.get(position);
        holder.binding.dateTxtIncome.setText(data.getDate());
        holder.binding.noteTxtIncome.setText(data.getNote());
        holder.binding.typeTxtIncome.setText(data.getType());
        holder.binding.ammountTxtIncome.setText(data.getAmount());
        holder.binding.updateIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.binding.noteTxtIncome.getContext()).
                        setContentHolder(new ViewHolder(R.layout.update_alart)).
                        setExpanded(true, 1000)
                        .create();
                View myUpView = dialogPlus.getHolderView();
                final EditText amount = myUpView.findViewById(R.id.ammount_update);
                final EditText type = myUpView.findViewById(R.id.type_update);
                final EditText note = myUpView.findViewById(R.id.note_update);
                final Button update = myUpView.findViewById(R.id.btnUpdate);

                amount.setText(data.getAmount());
                type.setText(data.getType());
                note.setText(data.getNote());
                dialogPlus.show();

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("money", amount.getText().toString());
                        map.put("type", type.getText().toString());
                        map.put("note", note.getText().toString());
                        String id = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(getId).push().getKey();
                        assert id != null;
                        FirebaseDatabase.getInstance().getReference().child("IncomeData").child(getId).child(id).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        holder.binding.deleteIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.binding.noteTxtIncome.getContext()).
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
                        String id = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(getId).push().getKey();
                        FirebaseDatabase.getInstance().getReference().child("ExpenseData")
                                .child(getId).child(id).removeValue();
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
    @Override
    public int getItemCount() {
        return list.size();
    }*/