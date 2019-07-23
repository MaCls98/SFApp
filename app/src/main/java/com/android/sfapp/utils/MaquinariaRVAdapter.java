package com.android.sfapp.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.sfapp.R;
import com.android.sfapp.model.Machine;

import java.util.ArrayList;

public class MaquinariaRVAdapter extends RecyclerView.Adapter<MaquinariaRVAdapter.MaquinasViewHoler> {

    private ArrayList<Machine> maquinas;

    public static class MaquinasViewHoler extends RecyclerView.ViewHolder{

        private TextView tvName;
        private TextView tvDate;
        private TextView tvStatus;

        public MaquinasViewHoler(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_nombre);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvStatus = itemView.findViewById(R.id.tv_doc);
        }
    }

    public MaquinariaRVAdapter(ArrayList<Machine> machines){
        this.maquinas = machines;
    }

    @NonNull
    @Override
    public MaquinasViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_maquinaria, parent, false);
        MaquinasViewHoler maquinasViewHolder = new MaquinasViewHoler(v);
        return maquinasViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MaquinasViewHoler holder, int position) {
        Machine machine = maquinas.get(position);

        holder.tvName.setText(machine.getName());
        holder.tvDate.setText(machine.getDate());
        holder.tvStatus.setText(machine.getStatus());
    }

    @Override
    public int getItemCount() {
        return maquinas.size();
    }
}
