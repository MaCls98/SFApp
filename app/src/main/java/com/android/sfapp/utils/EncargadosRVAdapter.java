package com.android.sfapp.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.sfapp.R;
import com.android.sfapp.model.Encargado;

import java.util.ArrayList;

public class EncargadosRVAdapter extends RecyclerView.Adapter<EncargadosRVAdapter.EncargadosViewHolder>{

    private final ArrayList<Encargado> encargados;
    private int i;

    private OnItemLongClickListener listener;
    private OnItemClickListener listener2;

    public interface OnItemLongClickListener{
        void changeStatus(int position);
    }

    public interface OnItemClickListener{
        void uploadStatus(int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.listener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener2 = listener;
    }

    public static class EncargadosViewHolder extends RecyclerView.ViewHolder{

        private TextView tvNombre;
        private TextView tvStatus;
        private TextView tvCell;
        private TextView tvDoc;
        private ImageButton btnStatus;
        private Context c;

        public EncargadosViewHolder(@NonNull View itemView, Context c, final OnItemLongClickListener listener, final OnItemClickListener listener2) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvCell = itemView.findViewById(R.id.tv_cell);
            tvDoc = itemView.findViewById(R.id.tv_doc);
            btnStatus = itemView.findViewById(R.id.btn_status);

            this.c = c;

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.changeStatus(position);
                            btnStatus.setVisibility(View.VISIBLE);
                            return true;
                        }
                    }
                    return false;
                }
            });


            btnStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnStatus.getVisibility() == View.VISIBLE){
                        if (listener2 != null){
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION){
                                listener2.uploadStatus(position);
                            }
                        }
                    }
                }
            });
        }
    }

    public EncargadosRVAdapter(ArrayList<Encargado> encargados, int i){
        this.i = i;
        this.encargados = encargados;
    }

    @NonNull
    @Override
    public EncargadosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_encargado, parent, false);
        EncargadosViewHolder encargadosViewHolder = new EncargadosViewHolder(v, parent.getContext(), listener, listener2);
        return encargadosViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EncargadosViewHolder holder, int i) {
        Encargado encargado = encargados.get(i);

        Drawable active = holder.c.getResources().getDrawable(R.drawable.checked).mutate();
        Drawable unactive = holder.c.getResources().getDrawable(R.drawable.cancel).mutate();
        switch (encargado.getStatus()){
            case "H":
                if (encargado.getStatus().equals("H")){
                    holder.btnStatus.setImageDrawable(unactive);
                    holder.tvStatus.setText("Habilitado");
                }else {
                    holder.itemView.setVisibility(View.GONE);
                }

                holder.btnStatus.setVisibility(View.GONE);

                holder.tvNombre.setText(encargado.getFirstName() + " " + encargado.getLastName());
                holder.tvDoc.setText(String.valueOf(encargado.getNumberDoc()));
                holder.tvCell.setText(encargado.getPhone());
                break;

            case "D":

                if (encargado.getStatus().equals("D")){
                    holder.btnStatus.setImageDrawable(active);
                    holder.tvStatus.setText("Deshabilitado");
                }else {
                    holder.itemView.setVisibility(View.GONE);
                }

                holder.btnStatus.setVisibility(View.GONE);

                holder.tvNombre.setText(encargado.getFirstName() + " " + encargado.getLastName());
                holder.tvDoc.setText(String.valueOf(encargado.getNumberDoc()));
                holder.tvCell.setText(encargado.getPhone());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return encargados.size();
    }
}