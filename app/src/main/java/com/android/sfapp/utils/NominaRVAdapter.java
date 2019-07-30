package com.android.sfapp.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.sfapp.R;
import com.android.sfapp.model.Nomina;

import java.util.ArrayList;

public class NominaRVAdapter extends RecyclerView.Adapter<NominaRVAdapter.NominaViewHolder> {

    private ArrayList<Nomina> nominas;

    private OnItemLongClickListener listener;
    private OnItemClickListener listener2;

    public interface OnItemLongClickListener{
        void assing(int position);
    }

    public interface OnItemClickListener{
        void uploadAssign(int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.listener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener2 = listener;
    }

    public static class NominaViewHolder extends RecyclerView.ViewHolder{

        private TextView tvNOmbre;
        private TextView tvDoc;
        private TextView tvCell;
        private TextView tvSalario;
        private Button btnAssign;

        public NominaViewHolder(@NonNull View itemView, final OnItemLongClickListener listener, final OnItemClickListener listener2) {
            super(itemView);
            tvNOmbre = itemView.findViewById(R.id.tv_nombre);
            tvDoc = itemView.findViewById(R.id.tv_doc);
            tvCell = itemView.findViewById(R.id.tv_cell);
            tvSalario = itemView.findViewById(R.id.tv_salario);
            btnAssign = itemView.findViewById(R.id.btn_assign);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.assing(position);
                            if (btnAssign.getVisibility() == View.VISIBLE){
                                btnAssign.setVisibility(View.GONE);
                            }else {
                                btnAssign.setVisibility(View.VISIBLE);
                            }
                            return true;
                        }
                    }
                    return false;
                }
            });

            btnAssign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnAssign.getVisibility() == View.VISIBLE){
                        if (listener2 != null){
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION){
                                listener2.uploadAssign(position);
                                btnAssign.setEnabled(false);
                            }
                        }
                    }
                }
            });
        }
    }

    public NominaRVAdapter(ArrayList<Nomina> nominas){
        this.nominas = nominas;
    }

    @NonNull
    @Override
    public NominaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nomina, parent, false);
        NominaViewHolder maquinasViewHolder = new NominaViewHolder(v, listener, listener2);
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
