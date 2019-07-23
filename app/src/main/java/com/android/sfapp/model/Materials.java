package com.android.sfapp.model;

public enum Materials {
    ARENA_RIO("Arena de rio", "Metros cubicos", "ARR"),
    ARENA_DE_POZO("Arena de pozo", "Metros cubicos", "ARP"),
    ARENA_SUCIA("Arena sucia", "Metros cubicos", "ARS"),
    TRITURADO_1_2("Triturado de 1/2", "Metros cubicos", "T12"),
    TRITURADO_3_4("Triturado de 3/4", "Metros cubicos", "T34"),
    PIEDRA_RAJON("Piedra rajon", "Metros cubicos", " PRA"),
    BASE("Base", "Metros cubicos", "BAS"),
    SUB_BASE("Sub-base", "Metros cubicos", "SUB"),
    RECEBO("Recebo", "Metros cubicos", "REC"),
    CEMENTO("Cemento", "Kilos", "CEM"),
    TUBERIA("Tuberia", "Metro lineal", "MTL"),
    MANGUERA_TIPO_FILTRO("Manguera tipo filtro", "Metro lineal", "MTF"),
    LADRILLO("Ladrillo", "Unidad", "LAD"),
    BLOQUE("Bloque", "Unidad", "BLO"),
    LADRILLO_ESTRUCTURAL("Ladrillo estructural", "Unidad", "LAE")
    ;

    private String materialType;
    private String materialUnit;
    private String materialC;

    Materials(String materialType, String materialUnit, String materialC){
        this.materialType = materialType;
        this.materialUnit = materialUnit;
        this.materialC = materialC;
    }

    public String getMaterialType() {
        return materialType;
    }

    public String getMaterialUnit() {
        return materialUnit;
    }

    @Override
    public String toString() {
        return materialType + "," + materialC;
    }
}
