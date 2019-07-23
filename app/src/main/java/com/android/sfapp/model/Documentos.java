package com.android.sfapp.model;

public enum Documentos {
    TARJETA_IDENTIDAD("Tarjeta de identidad"),
    CEDULA("Cedula"),
    PASAPORTE("Pasaporte")
    ;

    private String docType;

    Documentos(String docType){
        this.docType = docType;
    }

    @Override
    public String toString() {
        return docType;
    }
}
