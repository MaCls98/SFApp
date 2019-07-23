package com.android.sfapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.sfapp.R;
import com.android.sfapp.model.Materials;
import com.android.sfapp.model.Obra;

import java.util.ArrayList;

public class AddMaterialDialog extends DialogFragment {

    private Spinner spMaterials;
    private EditText etPrecio;
    private EditText etCantidad;
    private EditText etProveedor;
    private Button btnFecha;
    private TextView tvFecha;
    private Spinner spObras;

    private AddMaterialListener listener;

    private ArrayList<Obra> obras;
    private ArrayList<String> proveedores;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.home_nv_add_material, null);

        builder.setView(view)
        .setTitle("Agregar nuevo material")
        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
        .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int materialObraId = getObraId();
                String materialType = spMaterials.getSelectedItem().toString();
                String materialUnit = unidades(materialType);
                String materialQuantity = etCantidad.getText().toString();
                String materialProveedor = etProveedor.getText().toString();
                String materialDate = tvFecha.getText().toString();
                String materialPrice = etPrecio.getText().toString();
                listener.addMaterials(materialObraId, materialType, materialUnit, materialQuantity, materialProveedor, materialDate, materialPrice);
            }
        });

        spMaterials = view.findViewById(R.id.sp_add_mat_type);
        spMaterials.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, materiales()));
        etPrecio = view.findViewById(R.id.et_add_mat_precio);
        etCantidad = view.findViewById(R.id.et_add_mat_cantidad);
        etProveedor = view.findViewById(R.id.et_add_mat_proveedor);
        btnFecha = view.findViewById(R.id.btn_add_mat_fecha);
        tvFecha = view.findViewById(R.id.tv_add_mat_fecha);
        spObras = view.findViewById(R.id.sp_add_mat_obra);
        spObras.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, obras()));


        return builder.create();
    }

    public interface AddMaterialListener{
        void addMaterials(int materialObraId,
                          String materialType, String materialUnit,
                          String materialQuantity, String materialProveedor,
                          String materialDate, String materialPrice);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            listener = (AddMaterialListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public ArrayList<String> materiales(){
        ArrayList<String> tempM = new ArrayList<>();
        for (Materials m:
             Materials.values()) {
            tempM.add(m.getMaterialType());
        }
        return tempM;
    }

    public ArrayList<String> obras(){
        ArrayList<String> tempM = new ArrayList<>();
        for (Obra o:
                obras) {
            tempM.add(o.getObraName());
        }
        return tempM;
    }

    public String unidades(String material){
        for (Materials m:
             Materials.values()) {
            if (m.getMaterialType().equalsIgnoreCase(material)){
                return m.getMaterialUnit();
            }
        }
        return "";
    }

    public int getObraId(){
        for (Obra obra:
             obras) {
            if (obra.getObraName().equalsIgnoreCase(spObras.getSelectedItem().toString())){
                return obra.getObraId();
            }
        }
        return -1;
    }
}
