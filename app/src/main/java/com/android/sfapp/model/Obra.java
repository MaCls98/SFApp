package com.android.sfapp.model;

public class Obra {

    private int obraId;
    private String obraName;
    private String obraStartDate;
    private String obraEndDate;
    private String obraState;
    private String obraDireccion;

    public Obra(int obraId, String obraName, String obraStartDate, String obraEndDate, String obraState, String obraDireccion) {
        this.obraId = obraId;
        this.obraName = obraName;
        this.obraStartDate = obraStartDate;
        this.obraEndDate = obraEndDate;
        this.obraState = obraState;
        this.obraDireccion = obraDireccion;
    }

    public int getObraId() {
        return obraId;
    }

    public String getObraName() {
        return obraName;
    }

    public String getObraStartDate() {
        return obraStartDate;
    }

    public String getObraEndDate() {
        return obraEndDate;
    }

    public String getObraState() {
        return obraState;
    }

    public String getObraDireccion() {
        return obraDireccion;
    }

    @Override
    public String toString() {
        return "Obra{" +
                "obraId=" + obraId +
                ", obraName='" + obraName + '\'' +
                ", obraStartDate='" + obraStartDate + '\'' +
                ", obraEndDate='" + obraEndDate + '\'' +
                ", obraState='" + obraState + '\'' +
                ", obraDireccion='" + obraDireccion + '\'' +
                '}';
    }
}
