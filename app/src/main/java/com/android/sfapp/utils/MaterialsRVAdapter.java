package com.android.sfapp.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.sfapp.R;
import com.android.sfapp.model.MaterialCV;

import java.util.ArrayList;

public class MaterialsRVAdapter extends RecyclerView.Adapter<MaterialsRVAdapter.MaterialsViewHolder> {

    private ArrayList<MaterialCV> materials;

    public static class MaterialsViewHolder extends  RecyclerView.ViewHolder{

        public TextView tvMaterialType;
        public TextView tvQuantity;
        public TextView tvDate;
        public TextView tvPrice;

        public MaterialsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaterialType = itemView.findViewById(R.id.tv_material_type);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }

    public MaterialsRVAdapter(ArrayList<MaterialCV> materials){
        this.materials = materials;
    }

    @NonNull
    @Override
    public MaterialsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_materials, parent, false);
        MaterialsViewHolder materialsViewHolder = new MaterialsViewHolder(v);
        return materialsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialsViewHolder holder, int position) {
        MaterialCV material = materials.get(position);

        holder.tvMaterialType.setText(material.getMaterialType());
        holder.tvDate.setText(material.getDate());
        holder.tvPrice.setText(material.getPrice());
        holder.tvQuantity.setText(material.getPrice());
    }

    @Override
    public int getItemCount() {
        return materials.size();
    }
}
