package com.android.sfapp.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.sfapp.R;
import com.android.sfapp.model.Nomina;

import java.util.ArrayList;

public class NominaRVAdapter extends RecyclerView.Adapter<NominaRVAdapter.NominaViewHolder> {

    private ArrayList<Nomina> nominas;

    public static class NominaViewHolder extends RecyclerView.ViewHolder{

        private TextView tvNOmbre;
        private TextView tvDoc;
        private TextView tvCell;
        private TextView tvSalario;

        public NominaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNOmbre = itemView.findViewById(R.id.tv_nombre);
            tvDoc = itemView.findViewById(R.id.tv_doc);
            tvCell = itemView.findViewById(R.id.tv_cell);
            tvSalario = itemView.findViewById(R.id.tv_salario);
        }
    }

    public NominaRVAdapter(ArrayList<Nomina> nominas){
        this.nominas = nominas;
    }

    @NonNull
    @Override
    public NominaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nomina, parent, false);
        NominaViewHolder maquinasViewHolder = new NominaViewHolder(v);
        return maquinasViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NominaViewHolder holder, int i) {
        Nomina nomina = nominas.get(i);

        holder.tvNOmbre.setText(nomina.getFirstName() + " " + nomina.getLastName());
        holder.tvDoc.setText(nomina.getNumberDoc() + "");
        holder.tvCell.setText(nomina.getPhone());
        holder.tvSalario.setText(nomina.getSalary());
    }

    @Override
    public int getItemCount() {
        return nominas.size();
    }
}
