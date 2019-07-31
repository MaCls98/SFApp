package com.android.sfapp.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.sfapp.R;
import com.android.sfapp.model.Machine;

import java.util.ArrayList;

public class MaquinariaRVAdapter extends RecyclerView.Adapter<MaquinariaRVAdapter.MaquinasViewHoler> {

    private ArrayList<Machine> maquinas;

    private OnItemLongClickListener listener;
    private OnItemClickListener listener2;

    public void setMaquinarias(ArrayList<Machine> actualMachines) {
        maquinas = actualMachines;
    }

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

    public static class MaquinasViewHoler extends RecyclerView.ViewHolder{

        private TextView tvName;
        private TextView tvDate;
        private TextView tvStatus;
        private Button btnAssign;

        public MaquinasViewHoler(@NonNull View itemView, final OnItemLongClickListener listener, final OnItemClickListener listener2) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_nombre);
            tvDate = itemView.findViewById(R.id.tv_date);
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

    public MaquinariaRVAdapter(ArrayList<Machine> machines){
        this.maquinas = machines;
    }

    @NonNull
    @Override
    public MaquinasViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_maquinaria, parent, false);
        MaquinasViewHoler maquinasViewHolder = new MaquinasViewHoler(v, listener, listener2);
        return maquinasViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MaquinasViewHoler holder, int position) {
        Machine machine = maquinas.get(position);

        holder.tvName.setText(machine.getName());
        holder.tvDate.setText("Fecha: " + machine.getDate().substring(0, 10));
    }

    @Override
    public int getItemCount() {
        return maquinas.size();
    }
}
