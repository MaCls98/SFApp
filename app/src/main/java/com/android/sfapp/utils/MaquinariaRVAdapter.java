package com.android.sfapp.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        }
    }

    @NonNull
    @Override
    public MaquinasViewHoler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MaquinasViewHoler maquinasViewHoler, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
