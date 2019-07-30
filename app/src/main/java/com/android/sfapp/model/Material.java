package com.android.sfapp.model;

public class Material {

    private int idOrder;
    private String priceUni;
    private String quantity;
    private String nameProvider;
    private String dateOrder;
    private String typeMaterial;
    private int idOeuvre;

    public Material(int idOrder, String priceUni, String quantity, String nameProvider, String dateOrder, String typeMaterial, int idOeuvre) {
        this.idOrder = idOrder;
        this.priceUni = priceUni;
        this.quantity = quantity;
        this.nameProvider = nameProvider;
        this.dateOrder = dateOrder;
        this.typeMaterial = typeMaterial;
        this.idOeuvre = idOeuvre;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public String getPriceUni() {
        return priceUni;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getNameProvider() {
        return nameProvider;
    }

    public String getDateOrder() {
        return dateOrder;
    }

    public String getTypeMaterial() {
        return typeMaterial;
    }

    public int getIdOeuvre() {
        return idOeuvre;
    }

    @Override
    public String toString() {
        return "Material{" +
                "idOrder=" + idOrder +
                ", priceUni='" + priceUni + '\'' +
                ", quantity='" + quantity + '\'' +
                ", nameProvider='" + nameProvider + '\'' +
                ", dateOrder='" + dateOrder + '\'' +
                ", typeMaterial='" + typeMaterial + '\'' +
                ", idOeuvre=" + idOeuvre +
                '}';
    }
}
