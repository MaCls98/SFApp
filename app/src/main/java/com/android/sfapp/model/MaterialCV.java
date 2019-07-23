package com.android.sfapp.model;

public class MaterialCV {

    private int materialId;
    private int materialObraId;
    private String materialType;
    private String materialUnit;
    private String materialQuantity;
    private String materialProveedor;
    private String materialDate;
    private String materialPrice;

    public MaterialCV(int materialId, int materialObraId, String materialType, String materialUnit, String materialQuantity, String materialProveedor, String materialDate, String materialPrice) {
        this.materialId = materialId;
        this.materialObraId = materialObraId;
        this.materialType = materialType;
        this.materialUnit = materialUnit;
        this.materialQuantity = materialQuantity;
        this.materialProveedor = materialProveedor;
        this.materialDate = materialDate;
        this.materialPrice = materialPrice;
    }

    public MaterialCV(int materialObraId, String materialType, String materialUnit, String materialQuantity, String materialProveedor, String materialDate, String materialPrice) {
        this.materialObraId = materialObraId;
        this.materialType = materialType;
        this.materialUnit = materialUnit;
        this.materialQuantity = materialQuantity;
        this.materialProveedor = materialProveedor;
        this.materialDate = materialDate;
        this.materialPrice = materialPrice;
    }

    public int getMaterialId() {
        return materialId;
    }

    public int getMaterialObraId() {
        return materialObraId;
    }

    public String getMaterialType() {
        return materialType;
    }

    public String getMaterialUnit() {
        return materialUnit;
    }

    public String getMaterialQuantity() {
        return materialQuantity;
    }

    public String getMaterialProveedor() {
        return materialProveedor;
    }

    public String getMaterialDate() {
        return materialDate;
    }

    public String getMaterialPrice() {
        return materialPrice;
    }

    @Override
    public String toString() {
        return "MaterialCV{" +
                "materialId=" + materialId +
                ", materialObraId=" + materialObraId +
                ", materialType='" + materialType + '\'' +
                ", materialUnit='" + materialUnit + '\'' +
                ", materialQuantity='" + materialQuantity + '\'' +
                ", materialProveedor='" + materialProveedor + '\'' +
                ", materialDate='" + materialDate + '\'' +
                ", materialPrice='" + materialPrice + '\'' +
                '}';
    }
}
