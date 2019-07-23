package com.android.sfapp.model;

public enum Materials {
    ARENA_RIO("Arena de rio", "Metros cubicos"),
    ARENA_DE_POZO("Arena de pozo", "Metros cubicos"),
    ARENA_SUCIA("Arena sucia", "Metros cubicos"),
    TRITURADO_1_2("Triturado de 1/2", "Metros cubicos"),
    TRITURADO_3_4("Triturado de 3/4", "Metros cubicos"),
    PIEDRA_RAJON("Piedra rajon", "Metros cubicos"),
    BASE("Base", "Metros cubicos"),
    SUB_BASE("Sub-base", "Metros cubicos"),
    RECEBO("Recebo", "Metros cubicos"),
    CEMENTO("Cemento", "Kilos"),
    TUBERIA("Tuberia", "Metro lineal"),
    MANGUERA_TIPO_FILTRO("Manguera tipo filtro", "Metro lineal"),
    LADRILLO("Ladrillo", "Unidad"),
    BLOQUE("Bloque", "Unidad"),
    LADRILLO_ESTRUCTURAL("Ladrillo estructural", "Unidad")
    ;

    private String materialType;
    private String materialUnit;

    private Materials(String materialType, String materialUnit){
        this.materialType = materialType;
        this.materialUnit = materialUnit;
    }

    public String getMaterialType() {
        return materialType;
    }

    public String getMaterialUnit() {
        return materialUnit;
    }

    @Override
    public String toString() {
        return materialType;
    }
}
